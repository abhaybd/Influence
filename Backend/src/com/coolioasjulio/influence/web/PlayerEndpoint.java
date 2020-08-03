package com.coolioasjulio.influence.web;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value="/join/{code}/{name}")
public class PlayerEndpoint {
    private Session session;
    private String name;

    @OnOpen
    public void onOpen(Session session, @PathParam("code") String code, @PathParam("name") String name) throws IOException {
        this.session = session;
        this.name = name;
        // TODO: join the appropriate lobby using the code
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // TODO: tell the lobby that this player disconnected
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        // TODO: parse the message and take action
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // TODO: not gonna lie, no idea what to do here
    }
}
