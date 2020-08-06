package com.coolioasjulio.influence;

import java.util.Arrays;
import java.util.Objects;

public class Player {
    public int coins;
    private final String name;
    private Card[] cards;

    /**
     * Create a new player object. The order of the cards does not matter.
     *
     * @param name The name of this player. This should be unique within a game.
     * @param card1 One of the cards in the player's hand.
     * @param card2 The other card in the player's hand.
     */
    public Player(String name, Card card1, Card card2) {
        this.name = name;
        cards = new Card[]{card1, card2};
        coins = 2;
    }

    /**
     * Get the hand of this player.
     * This returns the underlying array, so modifying it will modify the hand of the player.
     *
     * @return The hand of this player.
     */
    public Card[] getCards() {
        return cards;
    }

    /**
     * Set the hand of this player.
     * No checks are made as to the length of the array, so ensure it's correct.
     *
     * @param cards The new hand of this player.
     */
    public void setCards(Card[] cards) {
        this.cards = cards;
    }

    /**
     * Get the name of this player. This should be unique to a game.
     *
     * @return The name of this player.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the influence of this player. This is the number of cards in their hand.
     *
     * @return The influence of this player.
     */
    public int getInfluence() {
        return (int) Arrays.stream(cards).filter(Objects::nonNull).count();
    }

    /**
     * Check if this player has the supplied card in their hand.
     *
     * @param card The card to check for.
     * @return True if this card is in this player's hand, false otherwise.
     */
    public boolean hasCard(Card card) {
        return Arrays.stream(cards).anyMatch(c -> c == card);
    }

    /**
     * Add a card to this player's hand.
     *
     * @param card The card to add.
     * @throws IllegalStateException If there is no room in this player's hand for a new card.
     */
    public void addCard(Card card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) {
                cards[i] = card;
                return;
            }
        }
        throw new IllegalStateException(String.format("Player %s does not have room in their hand for a new card!", name));
    }

    /**
     * Remove a card from this player's hand.
     *
     * @param card The card to remove.
     * @throws IllegalArgumentException If the supplied card is not in the players hand.
     */
    public void removeCard(Card card) throws IllegalArgumentException {
        if (cards[0] == card) {
            cards[0] = cards[1];
            cards[1] = null;
        } else if (cards[1] == card) {
            cards[1] = null;
        } else {
            throw new IllegalArgumentException(String.format("Player %s does not have card %s in their hand!", name, card));
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
