package com.coolioasjulio.influence.web;

import com.coolioasjulio.influence.Game;
import com.google.gson.Gson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LobbyServlet extends HttpServlet {

    private static final String TYPE_LOBBY_STARTED = "started";
    private static final String TYPE_LOBBY_EXISTS = "exists";
    private static final String TYPE_NUM_PLAYERS_IN_LOBBY = "numPlayers";
    private static final String TYPE_CAN_JOIN = "canJoin";
    private static final String TYPE_IS_PLAYER_IN_LOBBY = "playerInLobby";
    private static final String TYPE_CREATE_LOBBY = "create";
    private static final String TYPE_START_LOBBY = "start";

    @Override
    protected void doPost(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
        // Only json requests are accepted
        if ("application/json".equalsIgnoreCase(httpReq.getContentType())) {
            // Parse the json request
            Gson gson = new Gson();
            Request request = gson.fromJson(httpReq.getReader(), Request.class);
            String type = request.type;
            // All requests must define a request type
            if (type != null) {
                Response<?> response = null;
                // Depending on the request type, it's either an info request or an action request
                switch (type) {
                    case TYPE_LOBBY_STARTED:
                    case TYPE_LOBBY_EXISTS:
                    case TYPE_NUM_PLAYERS_IN_LOBBY:
                    case TYPE_IS_PLAYER_IN_LOBBY:
                    case TYPE_CAN_JOIN:
                        response = processInfo(httpResp, request);
                        break;

                    case TYPE_CREATE_LOBBY:
                    case TYPE_START_LOBBY:
                        response = processAction(httpResp, request);
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

    private Response<?> processAction(HttpServletResponse httpResp, Request request) throws IOException {
        Response<?> response = null;
        Lobby lobby;
        String action = request.type;
        String code = request.code;
        switch (action) {
            case TYPE_CREATE_LOBBY:
                // Create a lobby and reply with the lobby code
                lobby = Lobby.create();
                response = new Response<>(lobby.getCode());
                System.out.println("Created lobby: " + lobby.getCode());
                break;

            case TYPE_START_LOBBY:
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
                    httpResp.sendError(422, "The lobby may not exist, or it may have already started!");
                }
                break;

            default:
                httpResp.sendError(400, "Invalid action type");
                break;
        }
        return response;
    }

    private Response<?> processInfo(HttpServletResponse httpResp, Request request) throws IOException {
        Response<?> response = null;
        String info = request.type;
        String code = request.code;
        String content = request.content;
        if (info != null && code != null) {
            // Get the lobby referred to in the request. If this lobby is null, then it doesn't exist.
            Lobby lobby = Lobby.getLobby(code);
            switch (info) {
                case TYPE_LOBBY_STARTED:
                    // Check if the lobby exists and if it has started
                    response = new Response<>(lobby != null && lobby.isStarted());
                    break;

                case TYPE_IS_PLAYER_IN_LOBBY:
                    // We need the content key to be defined
                    if (content != null) {
                        // Check if the lobby exists and if it has the player
                        response = new Response<>(lobby != null && lobby.containsPlayer(content));
                    } else {
                        httpResp.sendError(400, "playerInLobby request must define a content key!");
                    }
                    break;

                case TYPE_LOBBY_EXISTS:
                    // Check if the lobby exists
                    response = new Response<>(lobby != null);
                    break;

                case TYPE_NUM_PLAYERS_IN_LOBBY:
                    // Check if the lobby exists, and if so, how many players
                    if (lobby != null) {
                        response = new Response<>(lobby.numPlayers());
                    } else {
                        // Report an error if the lobby doesn't exist
                        httpResp.sendError(422, "Lobby doesn't exist");
                    }
                    break;

                case TYPE_CAN_JOIN:
                    // We need the content key to be defined
                    if (content != null) {
                        // Check if the lobby exists and if it has the player
                        int responseCode = 0;
                        if (lobby != null) {
                            // 0b11 => success, 0b10 => lobby full, 0b01 => name taken, 0b00 => lobby doesn't exist
                            responseCode = ((lobby.containsPlayer(content) ? 0 : 1) << 1) | (lobby.numPlayers() < Game.MAX_PLAYERS ? 1 : 0);
                        }
                        response = new Response<>(responseCode);
                    } else {
                        httpResp.sendError(400, "canJoin request must define a content key!");
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

    private static class Request {
        public String type;
        public String code;
        public String content;
    }

    private static class Response<T> {
        public T content;

        public Response(T content) {
            this.content = content;
        }
    }
}
