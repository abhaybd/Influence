package com.coolioasjulio.influence;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Game {
    protected Player[] players;
    private List<Card> deck;

    public Game(String[] names) {
        if (names.length > 6) throw new IllegalArgumentException("No more than 6 players in a game!");

        players = new Player[names.length];
        deck = new ArrayList<>(3 * Card.values().length);

        for (Card card : Card.values()) {
            for (int i = 0; i < 3; i++) {
                deck.add(card);
            }
        }
        shuffleDeck();

        for (int i = 0; i < names.length; i++) {
            players[i] = new Player(names[i], getTopCard(), getTopCard());
        }
    }

    public void playGame() {
        int playerIndex = 0;
        update();
        while (playersAlive() > 1) {
            Player player = players[playerIndex];
            Action action = getAction(player);
            Player target = action.targeted ? getTarget(player) : null;
            doAction(action, player, target);

            for (int i = 0; i < players.length; i++) {
                if (players[i].getInfluence() == 0) {
                    playerDied(players[i]);
                    players[i] = null;
                }
            }

            update();

            do {
                playerIndex = (playerIndex + 1) % players.length;
            } while (players[playerIndex] == null);
        }

        playerWon(Arrays.stream(players).filter(Objects::nonNull).findFirst().orElseThrow(IllegalStateException::new));
    }

    public boolean doAction(Action action, Player player, Player target) {
        return doAction(action, action.card, player, target);
    }

    public boolean doAction(Action action, Card card, Player player, Player target) {
        Player challenge = card == null ? null : getChallenge(action, card, player);
        if (challenge != null) {
            boolean lying = !player.hasCard(card);
            sacrificeCard(lying ? player : challenge);
            if (lying || target.getInfluence() == 0) return false;
        }

        Block block = action.blockedBy.isEmpty() ? null : getBlock(action, player);
        if (block != null) {
            if (doAction(Action.Block, block.card, block.player, player)) {
                return false;
            }
        }

        switch (action) {
            case Income:
                player.coins++;
                break;

            case ForeignAid:
                player.coins += 2;
                break;

            case Tax:
                player.coins += 3;
                break;

            case Assassinate:
            case Coup:
                sacrificeCard(target);
                break;

            case Exchange:
                exchange(player);
                break;

            case Steal:
                int removed = Math.min(target.coins, 2);
                target.coins -= removed;
                player.coins += removed;
                break;
        }

        return true;
    }

    private void exchange(Player player) {
        int influence = player.getInfluence();
        Card[] available = new Card[influence + 2];
        System.arraycopy(player.getCards(), 0, available, 0, influence);
        available[influence] = getTopCard();
        available[influence + 1] = getTopCard();

        Card[] toKeep = doExchange(player, available);
        Card[] newHand = new Card[2];
        System.arraycopy(toKeep, 0, newHand, 0, influence);
        player.setCards(newHand);

        Set<Card> set = new HashSet<>(Arrays.asList(toKeep));
        for (Card c : available) {
            if (!set.contains(c)) deck.add(c);
        }
        shuffleDeck();
    }

    private int playersAlive() {
        return (int) Arrays.stream(players).filter(Objects::nonNull).count();
    }

    private Card getTopCard() {
        return deck.remove(deck.size() - 1);
    }

    private void shuffleDeck() {
        Random r = ThreadLocalRandom.current();
        for (int i = 0; i < deck.size(); i++) {
            int swap = r.nextInt(deck.size());
            Card temp = deck.get(i);
            deck.set(i, deck.get(swap));
            deck.set(swap, temp);
        }
    }

    private void sacrificeCard(Player player) {
        Card card = getCardToSacrifice(player);
        player.removeCard(card);
    }

    protected abstract void playerWon(Player player);

    protected abstract void update();

    protected abstract void playerDied(Player player);

    protected abstract Player getTarget(Player player);

    protected abstract Action getAction(Player player);

    /**
     * @param player
     * @param cards
     * @return The cards to keep.
     */
    protected abstract Card[] doExchange(Player player, Card[] cards);

    protected abstract Player getChallenge(Action action, Card card, Player player);

    protected abstract Block getBlock(Action action, Player player);

    protected abstract Card getCardToSacrifice(Player player);

    protected static class Block {
        public Player player;
        public Card card;

        public Block(Player player, Card card) {
            this.player = player;
            this.card = card;
        }

        public Block() {
        }
    }
}
