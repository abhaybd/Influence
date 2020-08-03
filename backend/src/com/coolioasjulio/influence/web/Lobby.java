package com.coolioasjulio.influence.web;

import com.google.gson.Gson;

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
        String list = new Gson().toJson(players.stream().map(PlayerEndpoint::getName).toArray(String[]::new));
        players.forEach(p -> p.write(list));
    }

    public void removePlayer(PlayerEndpoint player) {
        if (players.remove(player)) {
            String list = new Gson().toJson(players.stream().map(PlayerEndpoint::getName).toArray(String[]::new));
            players.forEach(p -> p.write(list));
        }
    }

    public void startGame() {
        players.forEach(p -> p.write("Start"));
        lobbyMap.remove(code);
        while (!Thread.interrupted()) {
            PlayerEndpoint[] players = this.players.toArray(new PlayerEndpoint[0]);
            for (int i = 0; i < players.length; i++) {
                int index = ThreadLocalRandom.current().nextInt(players.length);
                PlayerEndpoint temp = players[i];
                players[i] = players[index];
                players[index] = temp;
            }
            WebGame game = new WebGame(players);
            game.playGame();
        }
    }
}
