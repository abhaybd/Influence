package com.coolioasjulio.influence.web;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LobbyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
        if ("application/json".equalsIgnoreCase(httpReq.getContentType())) {
            Gson gson = new Gson();
            InfoReq infoReq = gson.fromJson(httpReq.getReader(), InfoReq.class);
            String info = infoReq.info;
            String code = infoReq.code;
            if (code != null) {
                Lobby lobby = Lobby.getLobby(code);
                httpResp.setContentType("application/json");
                httpResp.setStatus(200);
                InfoResp response = null;
                switch (info.toLowerCase()) {
                    case "started":
                        response = new InfoResp(String.valueOf(lobby != null && lobby.isStarted()));
                        break;

                    case "exists":
                        response = new InfoResp(String.valueOf(lobby != null));
                        break;

                    case "numPlayers":
                        if (lobby != null) {
                            response = new InfoResp(String.valueOf(lobby.numPlayers()));
                        } else {
                            httpResp.sendError(422, "Lobby doesn't exist");
                        }
                        break;

                    default:
                        httpResp.sendError(400, "Requests must define an info key");
                        break;
                }
                if (response != null) {
                    httpResp.getWriter().print(gson.toJson(response));
                    httpResp.flushBuffer();
                }
            } else {
                httpResp.sendError(400, "Requests must define an action key");
            }
        } else {
            httpResp.sendError(400, "Only JSON requests accepted!");
        }
    }

    @Override
    protected void doPost(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
        if ("application/json".equalsIgnoreCase(httpReq.getContentType())) {
            Gson gson = new Gson();
            ActionReq actionReq = gson.fromJson(httpReq.getReader(), ActionReq.class);
            String action = actionReq.action;
            if (action != null) {
                ActionResp response = new ActionResp();
                Lobby lobby;
                switch (action) {
                    case "create":
                        lobby = Lobby.create();
                        response.code = lobby.getCode();
                        System.out.println("Created lobby: " + lobby.getCode());
                        break;

                    case "start":
                        String code = actionReq.code;
                        lobby = Lobby.getLobby(code);
                        if (lobby != null && !lobby.isStarted()) {
                            lobby.startGame();
                        } else {
                            httpResp.sendError(422);
                        }
                        break;
                }
                String json = gson.toJson(response);
                httpResp.setStatus(200);
                httpResp.setContentType("application/json");
                httpResp.getWriter().print(json);
                httpResp.getWriter().flush();
            } else {
                httpResp.sendError(400, "Requests must define an action key");
            }
        } else {
            httpResp.sendError(400, "Only JSON requests accepted!");
        }
    }

    private static class InfoReq {
        public String info;
        public String code;
    }

    private static class ActionReq {
        public String action;
        public String code;
    }

    private static class InfoResp {
        public String content;

        public InfoResp(String content) {
            this.content = content;
        }
    }

    private static class ActionResp {
        public boolean success = true;
        public String code;

        public ActionResp() {

        }
    }
}
