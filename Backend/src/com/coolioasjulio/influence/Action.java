package com.coolioasjulio.influence;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The different actions able to be taken, as defined by the rules.
 */
public enum Action {
    Block(null, true),
    Income(null),
    ForeignAid(null, Card.Duke),
    Coup(null, true),
    Tax(Card.Duke),
    Assassinate(Card.Assassin, true, Card.Contessa),
    Exchange(Card.Ambassador),
    Steal(Card.Captain, true, Card.Captain, Card.Ambassador);

    /**
     * The card associated with this action.
     */
    public final Card card;
    /**
     * True if this action targets another player, false otherwise.
     */
    public final boolean targeted;
    /**
     * A set of all cards that can block this action. If empty, this action is unblockable.
     */
    public final Set<Card> blockedBy;
    Action(Card card, Card... canBlock) {
        this(card, false, canBlock);
    }

    Action(Card card, boolean targeted, Card... canBlock) {
        this.card = card;
        this.targeted = targeted;
        Set<Card> objects = new HashSet<>(Arrays.asList(canBlock));
        blockedBy = Collections.unmodifiableSet(objects);
    }
}
