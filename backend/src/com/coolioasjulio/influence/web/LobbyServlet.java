package com.coolioasjulio.influence.web;

import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LobbyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
        // Only json requests are accepted
        if ("application/json".equalsIgnoreCase(httpReq.getContentType())) {
            // Parse the json request
            Gson gson = new Gson();
            ActionReq actionReq = gson.fromJson(httpReq.getReader(), ActionReq.class);
            String type = actionReq.type;
            // All requests must define a request type
            if (type != null) {
                String code = actionReq.code;
                Response<?> response = null;
                // Depending on the request type, it's either an info request or an action request
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
                // If we got a response object, send it now
                // If an error was reported, this will no-op
                if (response != null) {
                    httpResp.setContentType("application/json");
                    String json = gson.toJson(response);
                    httpResp.getWriter().print(json);
                }
                // Send the status and message body. If there was an error, this will no-op
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
                // Create a lobby and reply with the lobby code
                lobby = Lobby.create();
                response = new Response<>(lobby.getCode());
                System.out.println("Created lobby: " + lobby.getCode());
                break;

            case "start":
                // Start an existing lobby
                lobby = Lobby.getLobby(code);
                // If the lobby exists and hasn't started, start it now
                if (lobby != null && !lobby.isStarted()) {
                    // Try to start the game on a new thread
                    if (!lobby.startGameAsync()) {
                        // If the game failed to start, report an error code
                        httpResp.sendError(422, "The game may have been already started, or not enough players in the lobby");
                    }
                } else {
                    // Report an error code if the lobby doesn't exist or has already started
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
            // Get the lobby referred to in the request. If this lobby is null, then it doesn't exist.
            Lobby lobby = Lobby.getLobby(code);
            switch (info) {
                case "started":
                    // Check if the lobby exists and if it has started
                    response = new Response<>(lobby != null && lobby.isStarted());
                    break;

                case "exists":
                    // Check if the lobby exists
                    response = new Response<>(lobby != null);
                    break;

                case "numPlayers":
                    // Check if the lobby exists, and if so, how many players
                    if (lobby != null) {
                        response = new Response<>(lobby.numPlayers());
                    } else {
                        // Report an error if the lobby doesn't exist
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
