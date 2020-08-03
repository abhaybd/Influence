package com.coolioasjulio.influence.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Lobby {
    private static final Map<String,Lobby> lobbyMap = new ConcurrentHashMap<>();
    private static final int CODE_LEN = 3; // code is made up of these many nouns

    public static Lobby getLobby(String code) {
        return lobbyMap.get(code);
    }

    public static Lobby create() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Lobby.class.getResourceAsStream("/nouns.txt")))) {
            List<String> words = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null) {
                words.add(line);
            }
            String[] parts = new String[CODE_LEN];
            String code;
            do {
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = words.get(ThreadLocalRandom.current().nextInt(words.size()));
                }
                code = String.join("-", parts);
            } while (lobbyMap.containsKey(code));
            Lobby lobby = new Lobby(code);
            lobbyMap.put(code, lobby);
            return lobby;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String code;
    private List<PlayerEndpoint> players;

    private Lobby(String code) {
        this.code = code;
        players = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void addPlayer(PlayerEndpoint player) {
        players.add(player);
    }

    public void startGame() {
        lobbyMap.remove(code);
        // TODO: start the game
    }
}
