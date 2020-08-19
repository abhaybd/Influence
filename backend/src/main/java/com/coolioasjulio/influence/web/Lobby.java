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
    public static final int CODE_LEN = 3; // code is made up of these many nouns
    private static final Map<String, Lobby> lobbyMap = new ConcurrentHashMap<>();
    private static String[] nouns; // the nouns from which to assemble lobby codes. Will be lazy-loaded from disk.
    private static final Object nounsLock = new Object();

    public static Lobby getLobby(String code) {
        return lobbyMap.get(code);
    }

    public static Lobby create() {
        if (nouns == null) {
            synchronized (nounsLock) {
                if (nouns == null) { // double check required for thread safety (prevents duplicate effort)
                    // Load the nouns list from resources
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(Lobby.class.getResourceAsStream("/nouns.txt")))) {
                        // Read it into a list
                        List<String> words = new ArrayList<>();
                        String line;
                        while ((line = in.readLine()) != null) {
                            words.add(line);
                        }
                        // Copy the list into an array
                        nouns = words.toArray(new String[0]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }

        // The code is made up of multiple words from the word list
        String[] parts = new String[CODE_LEN];
        String code;
        // Keep assembling random codes until we've generated one that doesn't belong to an active lobby
        do {
            // Select random words from the word list
            for (int i = 0; i < parts.length; i++) {
                parts[i] = nouns[ThreadLocalRandom.current().nextInt(nouns.length)];
            }
            code = String.join("-", parts);
        } while (lobbyMap.containsKey(code));
        // Create a new lobby with this code
        Lobby lobby = new Lobby(code);
        // Technically, this could cause issues due to multithreading.
        // If two threads simultaneously create the same code at the same time, one lobby will be invisible to any joining player.
        // The chances of this are literally less than one in a million, so whatever
        lobbyMap.put(code, lobby);
        return lobby;
    }

    private final String code;
    private final List<PlayerEndpoint> players;
    private final Object playersLock = new Object();
    private final AtomicBoolean started;
    private volatile WebGame game;
    private volatile boolean shouldClose = false;
    private Thread gameThread;

    private Lobby(String code) {
        this.code = code;
        started = new AtomicBoolean();
        players = new ArrayList<>();
    }

    public boolean shouldClose() {
        return shouldClose;
    }

    public String getCode() {
        return code;
    }

    /**
     * Get the number of players in this lobby.
     *
     * @return The number of connected players.
     */
    public int numPlayers() {
        synchronized (playersLock) {
            return players.size();
        }
    }

    /**
     * Checks if a player with the specified name is in this lobby.
     *
     * @param name The name to check for.
     * @return True if any player in this lobby has the specified name, false otherwise.
     */
    public boolean containsPlayer(String name) {
        synchronized (playersLock) {
            return players.stream().map(PlayerEndpoint::getName).anyMatch(s -> s.equals(name));
        }
    }

    /**
     * Check if this lobby has been started.
     *
     * @return True if the lobby has started, false otherwise.
     */
    public boolean isStarted() {
        return started.get();
    }

    /**
     * Try to add a player to this lobby.
     * Players cannot be added to a lobby once the game has started.
     * Additionally, players must have unique names.
     *
     * @param player The player to add.
     * @return True if the player was successfully added, false otherwise. If this operation failed, it's either because the lobby already started, or the name was already taken.
     */
    public boolean addPlayer(PlayerEndpoint player) {
        // synchronize to avoid threading issues
        synchronized (playersLock) {
            if (!started.get()) {
                // We're joining a game that hasn't started yet
                // Names must be unique
                // This isn't a great way to handle it, since it doesn't indicate to the user exactly why it failed
                if (containsPlayer(player.getName())) {
                    return false;
                }
                System.out.printf("Player %s connected to lobby %s!\n", player.getName(), code);
                // Add this player to the list of players
                players.add(player);
                // Transmit the list of all players in the lobby to all players
                String list = new Gson().toJson(players.stream().map(PlayerEndpoint::getName).toArray(String[]::new));
                players.forEach(p -> p.write(list));
                return true;
            } else {
                // Reconnecting to a game that already started
                System.out.printf("%s is trying to reconnect to %s...", player.getName(), code);
                // Iterate through the players and find one that matches this name
                for (int i = 0; i < players.size(); i++) {
                    PlayerEndpoint playerEndpoint = players.get(i);
                    if (game != null && !playerEndpoint.isConnected() && playerEndpoint.getName().equals(player.getName())) {
                        players.set(i, player); // Replace the old endpoint with the new one
                        game.playerReconnected(player); // signal to the game that this player has reconnected
                        System.out.println("Success!");
                        return true;
                    }
                }
                System.out.println("Failed!");
                return false;
            }
        }
    }

    /**
     * Remove a player from the lobby, usually when a player disconnects.
     *
     * @param player The player to remove.
     */
    public void removePlayer(PlayerEndpoint player) {
        // synchronize to avoid threading issues
        synchronized (playersLock) {
            // unlike addPlayer(), we won't no-op if the lobby has started. This is because disconnects can happen during the game
            if (players.contains(player)) {
                System.out.printf("Player %s disconnected from lobby %s!\n", player.getName(), code);
                // If the game has already started, then tell the game that this player disconnected
                if (started.get() && game != null) {
                    game.onPlayerDisconnected(player);
                } else {
                    // Otherwise, update the players in the lobby that this player disconnected
                    String list = new Gson().toJson(players.stream().map(PlayerEndpoint::getName).toArray(String[]::new));
                    players.forEach(p -> p.write(list));
                }
            }

            // If all players have disconnected, end the lobby
            if (players.stream().noneMatch(PlayerEndpoint::isConnected)) {
                System.out.printf("Lobby %s terminated.\n", code);
                // Remove from the global lobby map
                lobbyMap.remove(code);
                shouldClose = true;
                // Terminate the game thread, if it exists
                if (gameThread != null) {
                    System.out.printf("Interrupting game thread for %s\n", code);
                    gameThread.interrupt();
                }
            }
        }
    }

    /**
     * Start the game on a new thread. This will no-op if there are not enough players, or if the game has already started.
     *
     * @return True if the game was started, false otherwise.
     */
    public boolean startGameAsync() {
        synchronized (playersLock) {
            // If there aren't enough players or the game has already started, no-op.
            if (players.size() < 2 || started.getAndSet(true)) return false;
            // Start a new game on a new thread
            gameThread = new Thread(() -> startGame(false));
            gameThread.start();
            return true;
        }
    }

    /**
     * Start the lobby, playing games on repeat forever, until the thread is interrupted.
     *
     * @param checkPreconditions If true, check for the the number of players and if the game has started before continuing. If false, assume the conditions are correct.
     */
    public void startGame(boolean checkPreconditions) {
        // Only check the preconditions if we're supposed to.
        // This is because when we're using startGameAsync(), the checks are performed before starting this thread
        if (checkPreconditions) {
            synchronized (playersLock) {
                if (players.size() < 2 || started.getAndSet(true)) return;
            }
        }
        System.out.println("Starting lobby: " + code);
        // Write to each player, telling them the game has started
        String message = new Gson().toJson("Start");
        synchronized (playersLock) {
            players.forEach(p -> p.write(message));
        }
        // Play forever until the thread is interrupted
        while (!Thread.interrupted()) {
            PlayerEndpoint[] players = this.players.toArray(new PlayerEndpoint[0]);
            // shuffle the players to randomize turn order
            for (int i = 0; i < players.length; i++) {
                int index = ThreadLocalRandom.current().nextInt(players.length);
                PlayerEndpoint temp = players[i];
                players[i] = players[index];
                players[index] = temp;
            }
            // Play a new game
            game = new WebGame(this, players);
            game.playGame();
        }
        // Close all websockets. This will no-op for already closed sockets.
        for (PlayerEndpoint player : players) {
            try {
                player.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lobbyMap.remove(code); // Remove the lobby from the map
        System.out.printf("Thread for lobby %s terminated!\n", code);
    }
}
