package com.coolioasjulio.influence.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LobbyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Lobby lobby = Lobby.create();
        resp.getWriter().println(lobby.getCode());
    }
}
