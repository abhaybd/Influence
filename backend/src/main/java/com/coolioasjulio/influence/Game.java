package com.coolioasjulio.influence;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Game {
    protected final Player[] players;
    private final List<Card> deck;

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
        try {
            int playerIndex = 0;
            update();

            while (playersAlive() > 1) {
                Player player = players[playerIndex];
                Action action = getAction(player);
                // If this action has a target, prompt frontend for the target
                Player target = action.targeted ? getTarget(player) : null;
                if (target != null) {
                    if (action.card != null) {
                        log("%s uses a(n) %s to do %s on %s", player, action.card, action, target);
                    } else {
                        log("%s does %s on %s", player, action, target);
                    }
                } else {
                    if (action.card != null) {
                        log("%s uses a(n) %s to do %s", player, action.card, action);
                    } else {
                        log("%s does %s", player, action);
                    }
                }
                // Execute this action, which may have a target
                doAction(action, player, target);

                // Mark all players with 0 influence as dead
                for (int i = 0; i < players.length; i++) {
                    if (players[i] != null && players[i].getInfluence() == 0) {
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
        } catch (InterruptedException e) {
            // Re-interrupt the thread to signal callers that this game was interrupted
            Thread.currentThread().interrupt();
        }
    }

    private boolean doAction(Action action, Player player, Player target) throws InterruptedException {
        return doAction(action, action.card, player, target);
    }

    private boolean doAction(Action action, Card card, Player player, Player target) throws InterruptedException {
        // Pay the cost of the action first
        handleActionCost(action, player);

        CounterAction counterAction = getCounterAction(action, card, player, target);
        if (counterAction != null) {
            if (counterAction.isBlock) {
                log("%s blocks with a(n) %s", counterAction.player, counterAction.card);
                // This block can itself be challenged, so we can reuse the doAction() method
                // block.player refers to the player blocking this action
                // block.card refers to the card being used by the player to block this action
                if (doAction(Action.Block, counterAction.card, counterAction.player, player)) {
                    log("Block succeeded!");
                    return false;
                } else {
                    log("Block failed!");
                    // If the block was challenged and failed, the target may have died
                    // If that's the case, don't continue with this action
                    if (target != null && target.getInfluence() == 0) return false;
                }
            } else {
                log("%s challenges", counterAction.player);
                // If the action has been challenged, determine if the player was lying
                boolean lying = !player.hasCard(card);
                // If the player was lying, they sacrifice a card. Otherwise, the challenger does
                if (lying) {
                    log("Challenge succeeded!");
                    sacrificeCard(player);
                } else {
                    log("Challenge failed!");
                    sacrificeCard(counterAction.player);
                    // If the player wasn't lying, shuffle their card back into the deck and draw a new one
                    player.removeCard(card);
                    deck.add(card);
                    shuffleDeck();
                    player.addCard(getTopCard());
                }
                // Don't continue with this action if the player was lying, or if the target died during the challenge
                if (lying || (target != null && target.getInfluence() == 0)) return false;
            }
        }

        // Handle the results of the action
        handleAction(action, player, target);

        return true;
    }

    private void handleActionCost(Action action, Player player) {
        // Deduct the appropriate amount from the player
        // We can assume the player has enough money
        switch (action) {
            case Assassinate:
                player.coins -= 3;
                break;

            case Coup:
                player.coins -= 7;
                break;
        }
    }

    private void handleAction(Action action, Player player, Player target) throws InterruptedException {
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
    }

    private void exchange(Player player) throws InterruptedException {
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
        // Iterate through the deck, randomly swapping each element
        for (int i = 0; i < deck.size(); i++) {
            int swap = ThreadLocalRandom.current().nextInt(deck.size());
            Card temp = deck.get(i);
            deck.set(i, deck.get(swap));
            deck.set(swap, temp);
        }
    }

    private void sacrificeCard(Player player) throws InterruptedException {
        Card card = getCardToSacrifice(player);
        log("%s sacrifices a %s", player, card);
        player.removeCard(card);
    }

    /**
     * Report game events to the players. Override this method to show the messages appropriately.
     *
     * @param format A format string
     * @param args   Arguments referenced by the format string
     */
    protected void log(String format, Object... args) {
        System.out.printf(format + "\n", args); // Override me to show game events to players
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
    protected abstract Player getTarget(Player player) throws InterruptedException;

    /**
     * Get an action to do.
     *
     * @param player The player doing this action.
     * @return The Action this player is doing this turn.
     */
    protected abstract Action getAction(Player player) throws InterruptedException;

    /**
     * Do an exchange action.
     *
     * @param player The player doing the exchange action
     * @param cards  The cards to choose from.
     * @return The cards to keep. The player can only keep as many cards as it has influence.
     */
    protected abstract Card[] doExchange(Player player, Card[] cards) throws InterruptedException;

    /**
     * Prompt the appropriate players for a counter action, either a block or a challenge.
     * It may be possible that no counter action is possible.
     *
     * @param action The action being done, against which a counter action may be done.
     * @param card   The card being used to do said action. Use this instead of <code>action.card</code>
     * @param player The player doing the action.
     * @param target The target of the action. If the action is not targeted, this will be null.
     * @return The counter action done against this action. If no counter action is done, return null.
     */
    protected abstract CounterAction getCounterAction(Action action, Card card, Player player, Player target) throws InterruptedException;

    /**
     * Get a card to sacrifice from the hand of this player.
     *
     * @param player The player that must sacrifice a card.
     * @return A card from the hand of the supplied player.
     */
    protected abstract Card getCardToSacrifice(Player player) throws InterruptedException;

    protected static class CounterAction {
        /**
         * If true, this counter action is a block. Otherwise, it's a challenge.
         * If this action is a block, {@link CounterAction#card} is guaranteed to be non-null.
         */
        public boolean isBlock;
        /**
         * The player performing this counter action.
         */
        public Player player;
        /**
         * The card being used to perform this counter action.
         * Only non-null if this counter action is a block.
         */
        public Card card;

        public CounterAction(boolean isBlock, Player player, Card card) {
            this.isBlock = isBlock;
            this.player = player;
            this.card = card;
        }

        public CounterAction() {
        }
    }
}
