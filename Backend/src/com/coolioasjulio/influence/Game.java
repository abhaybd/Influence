package com.coolioasjulio.influence;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Game {
    protected Player[] players;
    private List<Card> deck;

    public Game(String[] names) {
        if (names.length < 2 || names.length > 6) throw new IllegalArgumentException("There must be 2-6 players!");

        players = new Player[names.length];
        deck = new ArrayList<>(3 * Card.values().length);

        // Initialize the deck, consisting of 3 of each card
        for (Card card : Card.values()) {
            for (int i = 0; i < 3; i++) {
                deck.add(card);
            }
        }
        // Shuffle for randomness
        shuffleDeck();

        // Create each player, giving it cards off the top of the deck
        for (int i = 0; i < names.length; i++) {
            players[i] = new Player(names[i], getTopCard(), getTopCard());
        }
    }

    /**
     * Plays a full game. Blocks until the game is complete.
     */
    public void playGame() {
        int playerIndex = 0;
        update();
        while (playersAlive() > 1) {
            Player player = players[playerIndex];
            Action action = getAction(player);
            // If this action has a target, prompt frontend for the target
            Player target = action.targeted ? getTarget(player) : null;
            // Execute this action, which may have a target
            doAction(action, player, target);

            // Mark all players with 0 influence as dead
            for (int i = 0; i < players.length; i++) {
                if (players[i].getInfluence() == 0) {
                    playerDied(players[i]);
                    players[i] = null;
                }
            }

            // Update the frontend to reflect the new state
            update();

            // Advance the playerIndex to point to the next player that's alive (looping over from the end back to the start)
            do {
                playerIndex = (playerIndex + 1) % players.length;
            } while (players[playerIndex] == null);
        }

        // The only remaining player is the winner
        playerWon(Arrays.stream(players).filter(Objects::nonNull).findFirst().orElseThrow(IllegalStateException::new));
    }

    private boolean doAction(Action action, Player player, Player target) {
        return doAction(action, action.card, player, target);
    }

    private boolean doAction(Action action, Card card, Player player, Player target) {
        // If there is no card associated with this action, it cannot be challenged
        Player challenge = card == null ? null : getChallenge(action, card, player);
        if (challenge != null) {
            // If the action has been challenged, determine if the player was lying
            boolean lying = !player.hasCard(card);
            // If the player was lying, they sacrifice a card. Otherwise, the challenger does
            sacrificeCard(lying ? player : challenge);
            // Don't continue with this action if the player was lying, or if the target died during the challenge
            if (lying || (target != null && target.getInfluence() == 0)) return false;
        }

        // If this action can be blocked by anything, prompt the frontend for any blocks
        Block block = action.blockedBy.isEmpty() ? null : getBlock(action, player);
        if (block != null) {
            // This block can itself be challenged, so we can reuse the doAction() method
            // block.player refers to the player blocking this action
            // block.card refers to the card being used by the player to block this action
            if (doAction(Action.Block, block.card, block.player, player)) {
                return false;
            }
        }

        // These are implemented according to the rules
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
                // Make sure not to go into the negative
                int removed = Math.min(Objects.requireNonNull(target).coins, 2);
                target.coins -= removed;
                player.coins += removed;
                break;
        }

        return true;
    }

    private void exchange(Player player) {
        // If the player has 1 influence, they can select from 3 cards. If 2, they can select from 4 cards.
        int influence = player.getInfluence();
        Card[] available = new Card[influence + 2];
        // Copy the existing hand into the available cards array
        System.arraycopy(player.getCards(), 0, available, 0, influence);
        // Draw 2 more cards from the top of the deck
        available[influence] = getTopCard();
        available[influence + 1] = getTopCard();

        // Prompt the frontend to see which cards to keep
        Card[] toKeep = doExchange(player, available);
        // Save this into a new hand for the player
        Card[] newHand = new Card[2];
        System.arraycopy(toKeep, 0, newHand, 0, influence);
        player.setCards(newHand);

        // Shuffle the unused cards back into the deck
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
        for (int i = 0; i < deck.size(); i++) {
            int swap = ThreadLocalRandom.current().nextInt(deck.size());
            Card temp = deck.get(i);
            deck.set(i, deck.get(swap));
            deck.set(swap, temp);
        }
    }

    private void sacrificeCard(Player player) {
        Card card = getCardToSacrifice(player);
        player.removeCard(card);
    }

    /**
     * Called at the end of a game, when a player wins.
     *
     * @param player The player that won this game.
     */
    protected abstract void playerWon(Player player);

    /**
     * Update the frontend UI.
     */
    protected abstract void update();

    /**
     * Called when a player dies.
     *
     * @param player The player that just died this turn.
     */
    protected abstract void playerDied(Player player);

    /**
     * Get the target for the last supplied targeted action.
     *
     * @param player The player doing the targeted action.
     * @return The player being targeted by this action.
     */
    protected abstract Player getTarget(Player player);

    /**
     * Get an action to do.
     *
     * @param player The player doing this action.
     * @return The Action this player is doing this turn.
     */
    protected abstract Action getAction(Player player);

    /**
     * Do an exchange action.
     *
     * @param player The player doing the exchange action
     * @param cards  The cards to choose from.
     * @return The cards to keep. The player can only keep as many cards as it has influence.
     */
    protected abstract Card[] doExchange(Player player, Card[] cards);

    /**
     * Get any challenger for an action.
     *
     * @param action The action being done.
     * @param card   The card being used to do said action.
     * @param player The player doing said action.
     * @return The player that is blocking this action. If no player is blocking this action, then null.
     */
    protected abstract Player getChallenge(Action action, Card card, Player player);

    /**
     * Get any block to this action.
     *
     * @param action The blockable action being done.
     * @param player The player doing this action.
     * @return A block object representing the player blocking this action and the card being used to block.
     */
    protected abstract Block getBlock(Action action, Player player);

    /**
     * Get a card to sacrifice from the hand of this player.
     *
     * @param player The player that must sacrifice a card.
     * @return A card from the hand of the supplied player.
     */
    protected abstract Card getCardToSacrifice(Player player);

    /**
     * Represents a block made by a player against another player.
     * A block consists of the player making the block, and the card being used to do the block.
     */
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
