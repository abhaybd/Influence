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
import java.util.concurrent.atomic.AtomicBoolean;

public class Lobby {
    private static final Map<String,Lobby> lobbyMap = new ConcurrentHashMap<>();
    private static final int CODE_LEN = 3; // code is made up of these many nouns
    private static String[] nouns;
    private static final Object nounsLock = new Object();

    public static Lobby getLobby(String code) {
        return lobbyMap.get(code);
    }

    public static Lobby create() {
        if (nouns == null) {
            synchronized (nounsLock) {
                if (nouns == null) { // double check required for thread safety (prevents duplicate effort)
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(Lobby.class.getResourceAsStream("/nouns.txt")))) {
                        List<String> words = new ArrayList<>();
                        String line;
                        while ((line = in.readLine()) != null) {
                            words.add(line);
                        }
                        nouns = words.toArray(new String[0]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }

        String[] parts = new String[CODE_LEN];
        String code;
        do {
            for (int i = 0; i < parts.length; i++) {
                parts[i] = nouns[ThreadLocalRandom.current().nextInt(nouns.length)];
            }
            code = String.join("-", parts);
        } while (lobbyMap.containsKey(code));
        Lobby lobby = new Lobby(code);
        // Technically, this could cause issues due to multithreading.
        // If two threads simultaneously create the same code at the same time, one lobby will be put in an invalid state.
        // The chances of this are literally less than one in a million, so whatever
        lobbyMap.put(code, lobby);
        return lobby;
    }

    private final String code;
    private final List<PlayerEndpoint> players;
    private final Object playersLock = new Object();
    private final AtomicBoolean started;

    private Lobby(String code) {
        this.code = code;
        started = new AtomicBoolean();
        players = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void addPlayer(PlayerEndpoint player) {
        synchronized (playersLock) {
            if (!started.get()) {
                System.out.printf("Player %s connected to lobby %s!\n", player.getName(), code);
                players.add(player);
                String list = new Gson().toJson(players.stream().map(PlayerEndpoint::getName).toArray(String[]::new));
                players.forEach(p -> p.write(list));
            }
        }
    }

    public void removePlayer(PlayerEndpoint player) {
        synchronized (playersLock) {
            if (players.remove(player)) {
                System.out.printf("Player %s disconnected from lobby %s!\n", player.getName(), code);
                String list = new Gson().toJson(players.stream().map(PlayerEndpoint::getName).toArray(String[]::new));
                players.forEach(p -> p.write(list));
            }

            if (players.isEmpty()) {
                System.out.printf("Lobby %s terminated.\n", code);
                lobbyMap.remove(code);
            }
        }
    }

    public void startGame() {
        synchronized (playersLock) {
            if (players.size() > 2 && started.getAndSet(true)) return;
        }
        System.out.println("Starting lobby: " + code);
        players.forEach(p -> p.write("Start"));
        lobbyMap.remove(code);
        while (!Thread.interrupted()) {
            PlayerEndpoint[] players = this.players.toArray(new PlayerEndpoint[0]);
            // shuffle the players to randomize turn order
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

    public int numPlayers() {
        synchronized (playersLock) {
            return players.size();
        }
    }

    public boolean isStarted() {
        return started.get();
    }
}
