package com.coolioasjulio.influence;

import java.util.*;

public class MockGame extends Game {

    public static void main(String[] args) {
        new MockGame().playGame();
    }

    private Scanner in;
    public MockGame() {
        super(new String[]{"Bob", "Joe"});
        in = new Scanner(System.in);
    }

    @Override
    protected void playerWon(Player player) {
        System.out.printf("%s won!\n", player.getName());
    }

    @Override
    protected void update() {
        System.out.println();
        for (Player player : players) {
            if (player != null) {
                Card[] cards = player.getCards();
                System.out.printf("%s - Coins=%d -  %s,%s\n", player.getName(), player.coins, cards[0], cards[1]);
            }
        }
    }

    @Override
    protected void playerDied(Player player) {
        System.out.printf("%s died!\n", player.getName());
    }

    @Override
    protected Player getTarget(Player player) {
        Player target;
        do {
            System.out.println("Pick target!");
            String name = in.nextLine();
            target = Arrays.stream(players).filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        } while (target == null);
        return target;
    }

    @Override
    protected Action getAction(Player player) {
        Action action = null;
        do {
            System.out.printf("%s, choose action!\n", player.getName());
            String line = in.nextLine();
            try {
                action = Action.valueOf(line);
            } catch(IllegalArgumentException e) {
                // ignored
            }
        } while (action == null);
        return action;
    }

    @Override
    protected Card[] doExchange(Player player, Card[] cards) {
        boolean success = false;
        List<Card> kept = new ArrayList<>();
        Set<Card> set = new HashSet<>(Arrays.asList(cards));
        do {
            System.out.println("Choose cards to keep: " + Arrays.toString(cards));
            String line = in.nextLine();
            try {
                String[] parts = line.split(",");
                if (parts.length != player.getInfluence()) continue;
                kept.clear();
                success = true;
                for (String s : parts) {
                    Card c = Card.valueOf(s);
                    kept.add(c);
                    if (!set.contains(c)) {
                        success = false;
                        break;
                    }
                }
            } catch (Exception e) {
                success = false;
            }
        } while (!success);

        return kept.toArray(new Card[0]);
    }

    @Override
    protected CounterAction getCounterAction(Action action, Card card, Player player, Player target) {
        CounterAction counterAction = new CounterAction();
        if (action == Action.ForeignAid) {
            counterAction.player = getObjectFromInput("Who blocks?", players);
            counterAction.card = Card.Duke;
            counterAction.isBlock = true;
        } else {
            if (card == null) {
                return null;
            }
            if (action.blockedBy.isEmpty()) {
                counterAction.player = getObjectFromInput("Who challenges?", players);
                counterAction.isBlock = false;
            } else {
                counterAction.player = getObjectFromInput("Who performs counter action?", players);
                if (counterAction.player == null) return null;
                Boolean block = null;
                do {
                    System.out.println("Block or challenge?");
                    String line = in.nextLine();
                    if (line.equalsIgnoreCase("block")) block = true;
                    else if (line.equalsIgnoreCase("challenge")) block = false;
                } while (block == null);
                counterAction.isBlock = block;
                if (block) {
                    counterAction.card = getObjectFromInput("What card do you use to block?", Card.values());
                }
            }
        }
        return counterAction.player == null ? null : counterAction;
    }

    @Override
    protected Card getCardToSacrifice(Player player) {
        Card[] cards = player.getCards();
        Card toSacrifice;
        do {
            toSacrifice = getObjectFromInput(
                    String.format("%s, sacrifice one card! Cards - %s", player.getName(), Arrays.toString(cards)), cards);
        } while (toSacrifice == null);
        return toSacrifice;
    }

    private <T> T getObjectFromInput(String prompt, T[] objects) {
        T ret;
        do {
            System.out.println(prompt);
            String name = in.nextLine();
            if (name.length() == 0) return null;
            ret = Arrays.stream(objects)
                    .filter(Objects::nonNull)
                    .filter(p -> p.toString().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        } while (ret == null);
        return ret;
    }
}
