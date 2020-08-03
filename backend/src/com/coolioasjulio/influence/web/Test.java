package com.coolioasjulio.influence.web;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value="/echo")
public class Test {
    private Session session;

    @OnOpen
    public void start(Session session) {
        this.session = session;
        try {
            session.getBasicRemote().sendText("Hello!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void incoming(String message) {
        System.out.println(message);
        send(message);
    }

    public void send(String text) {
        try {
            session.getBasicRemote().sendText(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void close(Session session) throws IOException {

    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }
}
