package com.coolioasjulio.influence.web;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@ServerEndpoint(value = "/ws/join/{code}/{name}")
public class PlayerEndpoint {
    private Session session;
    private String name;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private final Set<Thread> readingThreads = Collections.synchronizedSet(new HashSet<>());
    private Lobby lobby;
    private volatile boolean connected;

    @OnOpen
    public void onOpen(Session session, @PathParam("code") String code, @PathParam("name") String name) throws IOException {
        System.out.printf("Websocket from %s to lobby %s\n", name, code);
        this.session = session;
        this.name = name;
        lobby = Lobby.getLobby(code);
        // Check if the asked for lobby exists
        if (lobby != null) {
            // Try to add this player to the lobby
            connected = true;
            if (!lobby.addPlayer(this)) {
                // If unsuccessful, close the player session
                connected = false;
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid lobby or name already taken!"));
            }
        } else {
            // If the lobby doesn't exist doesn't, close the session
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid lobby code!"));
        }
    }

    @OnClose
    public void onClose() {
        // If this player has joined a lobby, signal to the lobby that it disconnected
        connected = false;
        if (lobby != null) {
            lobby.removePlayer(this);
        }

        readingThreads.forEach(Thread::interrupt);
    }

    @OnMessage
    public void onMessage(String message) {
        // Add this message to the end of the message queue, to be read later
        messageQueue.add(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // TODO: not gonna lie, no idea what to do here
    }

    public synchronized void close() throws IOException {
        session.close();
    }

    public boolean isConnected() {
        return connected;
    }

    public String readLine() throws InterruptedException {
        if (!connected) return null;
        Thread thread = Thread.currentThread();
        try {
            readingThreads.add(thread);
            return messageQueue.take();
        } finally {
            readingThreads.remove(thread);
        }
    }

    public synchronized boolean write(String message) {
        if (isConnected()) {
            try {
                session.getBasicRemote().sendText(message);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }
}
