package com.coolioasjulio.influence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
        List<Integer> indexes = null;
        do {
            System.out.println("Choose indexes to keep:");
            for (int i = 0; i < cards.length; i++) {
                System.out.printf("%d - %s\n", i, cards[i]);
            }
            String line = in.nextLine();
            try {
                String[] parts = line.split(",");
                if (parts.length != player.getInfluence()) continue;
                indexes = new ArrayList<>();
                for (String p : parts) {
                    indexes.add(Integer.parseInt(p));
                }
                success = true;
            } catch (Exception e) {
                // ignored
            }
        } while (!success);

        return indexes.stream().map(i -> cards[i]).toArray(Card[]::new);
    }

    @Override
    protected Player getChallenge(Action action, Card card, Player player) {
        Player challenger;
        do {
            System.out.println("Who challenges?");
            String name = in.nextLine();
            if (name.length() == 0) return null;
            challenger = Arrays.stream(players).filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        } while (challenger == null);
        return challenger;
    }

    @Override
    protected Block getBlock(Action action, Player player) {
        Block b = new Block();

        do {
            System.out.println("Who blocks?");
            String line = in.nextLine();
            if (line.length() == 0) return null;
            b.player = Arrays.stream(players).filter(p -> p.getName().equalsIgnoreCase(line)).findFirst().orElse(null);
        } while (b.player == null);

        do {
            System.out.println("Which card?");
            String line = in.nextLine();
            if (line.length() == 0) return null;
            try {
                b.card = Card.valueOf(line);
            } catch(IllegalArgumentException e) {
                // ignored
            }
        } while (b.card == null);

        return b;
    }

    @Override
    protected Card getCardToSacrifice(Player player) {
        Card toSacrifice = null;
        do {
            Card[] cards = player.getCards();
            System.out.printf("%s, sacrifice one card! Cards - %s\n", player.getName(), Arrays.toString(cards));
            int influence = player.getInfluence();
            String line = in.nextLine();
            try {
                int i = Integer.parseInt(line);
                if (i >= 0 && i < influence) {
                    toSacrifice = cards[i];
                }
            } catch (Exception e) {
                // ignored
            }
        } while (toSacrifice == null);
        return toSacrifice;
    }
}
