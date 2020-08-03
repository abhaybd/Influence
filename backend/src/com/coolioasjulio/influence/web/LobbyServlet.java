package com.coolioasjulio.influence.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LobbyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getParameter("start") != null) {
            String code = req.getParameter("code");
            Lobby lobby = Lobby.getLobby(code);
            if (lobby != null) {
                lobby.startGame();
            } else {
                resp.sendError(422);
            }
        } else {
            Lobby lobby = Lobby.create();
            resp.getWriter().println(lobby.getCode());
        }
    }
}
