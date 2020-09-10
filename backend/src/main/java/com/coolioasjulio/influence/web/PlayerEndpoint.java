package com.coolioasjulio.influence.web;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
            Lobby.RejectionReason reason = lobby.addPlayer(this);
            if (reason != Lobby.RejectionReason.NONE) {
                // If unsuccessful, close the player session
                connected = false;
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Could not join lobby! Error: " + reason));
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

        // Interrupt all threads that are currently waiting on the message queue
        readingThreads.forEach(Thread::interrupt);
    }

    @OnMessage
    public void onMessage(String message) {
        // Add this message to the end of the message queue, to be read later
        messageQueue.add(message);
    }

    @OnError
    public void onError(Throwable throwable) {
        System.err.printf("Error with %s in lobby %s: %s\n", name, lobby.getCode(), throwable.getMessage());
    }

    public synchronized void close(String reason) throws IOException {
        session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, reason));
    }

    public boolean isConnected() {
        return connected;
    }

    public String readLine() throws InterruptedException, IOException {
        // No-op if this endpoint is disconnected
        if (!connected) return null;
        // We'll cache the threads that are waiting on the queue
        // That way, if this socket closes we can interrupt those threads, which would otherwise sleep forever
        Thread thread = Thread.currentThread();
        try {
            // Cache the thread and wait for data from the queue to become available
            readingThreads.add(thread);
            return messageQueue.take();
        } catch (InterruptedException e) {
            // If the player is still connected, this interruption was external, so rethrow it
            if (connected) {
                throw e;
            } else {
                // If the player is disconnected, this exception was because they just disconnected
                throw new IOException("Client " + name + " disconnected!");
            }
        } finally {
            // The thread is no longer waiting, so remove this thread from the cache
            readingThreads.remove(thread);
        }
    }

    public synchronized boolean write(String message) {
        // No-op if this endpoint is disconnected
        if (isConnected()) {
            try {
                // Send the message
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
