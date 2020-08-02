package com.coolioasjulio.influence;

import java.util.Arrays;
import java.util.Objects;

public class Player {
    public int coins;
    private String name;
    private Card[] cards;

    public Player(String name, Card card1, Card card2) {
        this.name = name;
        cards = new Card[]{card1, card2};
        coins = 2;
    }

    public Card[] getCards() {
        return cards;
    }

    public void setCards(Card[] cards) {
        this.cards = cards;
    }

    public String getName() {
        return name;
    }

    public int getInfluence() {
        return (int) Arrays.stream(cards).filter(Objects::nonNull).count();
    }

    public boolean hasCard(Card card) {
        return Arrays.stream(cards).anyMatch(c -> c == card);
    }

    public boolean addCard(Card card) {
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) {
                cards[i] = card;
                return true;
            }
        }
        return false;
    }

    public void removeCard(Card card) {
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
