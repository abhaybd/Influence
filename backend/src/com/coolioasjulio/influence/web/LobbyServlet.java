package com.coolioasjulio.influence.web;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LobbyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
        if ("application/json".equalsIgnoreCase(httpReq.getContentType())) {
            Gson gson = new Gson();
            ActionReq actionReq = gson.fromJson(httpReq.getReader(), ActionReq.class);
            String type = actionReq.type;
            if (type != null) {
                String code = actionReq.code;
                Response<?> response = null;
                switch (type) {
                    case "started":
                    case "exists":
                    case "numPlayers":
                        response = processInfo(httpResp, type, code);
                        break;

                    case "create":
                    case "start":
                        response = processAction(httpResp, type, code);
                        break;

                    default:
                        httpResp.sendError(400, "Invalid request type");
                        break;
                }
                httpResp.setStatus(200);
                if (response != null) {
                    httpResp.setContentType("application/json");
                    String json = gson.toJson(response);
                    httpResp.getWriter().print(json);
                }
                httpResp.getWriter().flush();
            } else {
                httpResp.sendError(400, "Requests must define a type key");
            }
        } else {
            httpResp.sendError(400, "Only JSON requests accepted!");
        }
    }

    private Response<?> processAction(HttpServletResponse httpResp, String action, String code) throws IOException {
        Response<?> response = null;
        Lobby lobby;
        switch (action) {
            case "create":
                lobby = Lobby.create();
                response = new Response<>(lobby.getCode());
                System.out.println("Created lobby: " + lobby.getCode());
                break;

            case "start":
                lobby = Lobby.getLobby(code);
                if (lobby != null && !lobby.isStarted()) {
                    lobby.startGame();
                } else {
                    httpResp.sendError(422);
                }
                break;

            default:
                httpResp.sendError(400, "Invalid action type");
                break;
        }
        return response;
    }

    private Response<?> processInfo(HttpServletResponse httpResp, String info, String code) throws IOException {
        Response<?> response = null;
        if (info != null && code != null) {
            Lobby lobby = Lobby.getLobby(code);
            switch (info.toLowerCase()) {
                case "started":
                    response = new Response<>(lobby != null && lobby.isStarted());
                    break;

                case "exists":
                    response = new Response<>(lobby != null);
                    break;

                case "numPlayers":
                    if (lobby != null) {
                        response = new Response<>(lobby.numPlayers());
                    } else {
                        httpResp.sendError(422, "Lobby doesn't exist");
                    }
                    break;

                default:
                    httpResp.sendError(400, "Requests must define a valid info key");
                    break;
            }
        } else {
            httpResp.sendError(400, "Requests must define an info and code key");
        }

        return response;
    }

    private static class ActionReq {
        public String type;
        public String code;
    }

    private static class Response<T> {
        public T content;

        public Response(T content) {
            this.content = content;
        }
    }
}
