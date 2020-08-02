package com.coolioasjulio.influence;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum Action {
    Block(null),
    Income(null),
    ForeignAid(null, Card.Duke),
    Coup(null, true),
    Tax(Card.Duke),
    Assassinate(Card.Assassin, true, Card.Contessa),
    Exchange(Card.Ambassador),
    Steal(Card.Captain, true, Card.Captain, Card.Ambassador);

    public final Card card;
    public final boolean targeted;
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

    public boolean isBlockedBy(Card card) {
        return blockedBy.contains(card);
    }
}
