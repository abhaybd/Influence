package com.coolioasjulio.influence.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {
    private PlayerEndpoint createPlayer(String name) {
        PlayerEndpoint player = Mockito.mock(PlayerEndpoint.class);
        Mockito.when(player.getName()).thenReturn(name);
        try {
            // The readLine() method will just block indefinitely, then bubble up an exception
            Mockito.when(player.readLine()).then(e -> {
                while (!Thread.interrupted()) Thread.sleep(100);
                throw new InterruptedException();
            });
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return player;
    }

    @Test
    public void addPlayer() {
        Lobby lobby = Lobby.create();
        String name = "Player";

        assertEquals(Lobby.RejectionReason.NONE, lobby.addPlayer(createPlayer(name)));
        assertEquals(1, lobby.numPlayers());

        assertEquals(Lobby.RejectionReason.NAME_TAKEN, lobby.addPlayer(createPlayer(name)));
        assertEquals(1, lobby.numPlayers());

        assertFalse(lobby.startGameAsync());

        assertEquals(Lobby.RejectionReason.NONE, lobby.addPlayer(createPlayer("Player2")));
        assertTrue(lobby.startGameAsync());
    }

    @Test
    public void startGameAsync() {
        Lobby lobby = Lobby.create();
        PlayerEndpoint player1 = createPlayer("Player1");
        PlayerEndpoint player2 = createPlayer("Player2");

        lobby.addPlayer(player1);
        assertFalse(lobby.startGameAsync());
        assertFalse(lobby.isStarted());
        lobby.addPlayer(player2);
        assertTrue(lobby.startGameAsync());
        assertTrue(lobby.isStarted());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNotEquals(Lobby.RejectionReason.NONE, lobby.addPlayer(createPlayer("Player3")));
        lobby.removePlayer(player1);
        lobby.removePlayer(player2);
    }

    @Test
    public void create() {
        assertNotNull(Lobby.create());

        String regex = String.format("(\\w+-){%d}\\w+", Lobby.CODE_LEN - 1);
        assertTrue(Lobby.create().getCode().matches(regex));
    }

    @Test
    public void getLobby() {
        assertNull(Lobby.getLobby(""));

        Lobby lobby = Lobby.create();
        assertEquals(lobby, Lobby.getLobby(lobby.getCode()));
    }

    @Test
    public void removePlayer() {
        Lobby lobby = Lobby.create();
        PlayerEndpoint player = createPlayer("p1");
        assertEquals(Lobby.RejectionReason.NONE, lobby.addPlayer(player));
        lobby.removePlayer(player);
        assertEquals(0, lobby.numPlayers());
    }
}
