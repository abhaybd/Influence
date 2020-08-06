package com.coolioasjulio.influence.web;

import com.coolioasjulio.influence.Action;
import com.coolioasjulio.influence.Card;
import com.coolioasjulio.influence.Game;
import com.coolioasjulio.influence.Player;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;

public class WebGame extends Game {
    private final Map<Player,PlayerEndpoint> endpointMap;
    private final ExecutorService executorService;
    private final Gson gson;

    public static void main(String[] args) {
        Message message = new Message("update");
        Gson gson = new Gson();
        Player[] players = new Player[3];
        players[0] = new Player("Joe", Card.Duke, Card.Ambassador);
        players[1] = new Player("Bob", Card.Duke, Card.Ambassador);
        players[2] = new Player("Carol", Card.Captain, null);
        message.content = gson.toJsonTree(players);
        System.out.println(gson.toJson(message));

    }

    public WebGame(PlayerEndpoint[] endpoints) {
        super(Arrays.stream(endpoints).map(PlayerEndpoint::getName).toArray(String[]::new));
        endpointMap = new HashMap<>();
        for (int i = 0; i < players.length; i++) {
            endpointMap.put(players[i], endpoints[i]);
        }

        gson = new Gson();
        executorService = Executors.newFixedThreadPool(8);
    }

    private void broadcast(String type, Object content) {
        Message message = new Message(type);
        message.content = gson.toJsonTree(content);
        String json = gson.toJson(message);
        for (PlayerEndpoint endpoint : endpointMap.values()) {
            endpoint.write(json);
        }
    }

    private <T> Future<T> getFirst(Collection<Future<T>> futures, Predicate<T> predicate) {
        while (!Thread.interrupted() && !futures.isEmpty()) {
            List<Future<T>> toRemove = new LinkedList<>();
            for (Future<T> future : futures) {
                if (future.isDone()) {
                    try {
                        if (predicate.test(future.get())) {
                            return future;
                        } else {
                            toRemove.add(future);
                        }
                    } catch (InterruptedException e) {
                        return null;
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            futures.removeAll(toRemove);
            Thread.yield();
        }
        return null;
    }

    private String sanitize(String s) {
        return s.replaceAll("(?<=[a-z])(?=[A-Z])", " ").toLowerCase();
    }

    private <T> Future<T> getChoiceAsync(Player player, T[] choices, String prompt) {
        return executorService.submit(() -> getChoice(player, choices, prompt));
    }

    private <T> T getChoice(Player player, T[] choices, String prompt) {
        String[] choicesStr = Arrays.stream(choices).map(Object::toString).toArray(String[]::new);
        PlayerEndpoint endpoint = endpointMap.get(player);
        Message message = new Message("choice", prompt);
        message.content = gson.toJsonTree(choicesStr);
        String json = gson.toJson(message);
        endpoint.write(json);
        String responseJson = endpoint.readLine();
        String response = gson.fromJson(responseJson, String.class);
        for (int i = 0; i < choices.length; i++) {
            if (choicesStr[i].equals(response)) return choices[i];
        }
        return null;
    }

    @Override
    protected void log(String format, Object... args) {
        broadcast("log", String.format(format, args));
    }

    @Override
    protected void playerWon(Player player) {
        broadcast("info", String.format("%s wins!", player.getName()));
    }

    @Override
    protected void update() {
        broadcast("update", Arrays.stream(players).filter(Objects::nonNull).toArray(Player[]::new));
    }

    @Override
    protected void playerDied(Player player) {
        broadcast("info", String.format("%s died!", player.getName()));
    }

    @Override
    protected Player getTarget(Player player) {
        List<Player> options = new ArrayList<>(Arrays.asList(players));
        options.removeIf(Objects::isNull);
        options.remove(player);

        return getChoice(player, options.toArray(new Player[0]), "Choose a target");
    }

    @Override
    protected Action getAction(Player player) {
        List<Action> options = new ArrayList<>();
        options.add(Action.Income);
        options.add(Action.ForeignAid);
        if (player.coins >= 7) options.add(Action.Coup);
        options.add(Action.Tax);
        if (player.coins >= 3) options.add(Action.Assassinate);
        options.add(Action.Exchange);
        options.add(Action.Steal);

        return getChoice(player, options.toArray(new Action[0]), "Choose an action");
    }

    @Override
    protected Card[] doExchange(Player player, Card[] cards) {
        int influence = player.getInfluence();
        Card[] ret = new Card[influence];
        List<Card> cardList = new ArrayList<>(Arrays.asList(cards));
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getChoice(player, cardList.toArray(new Card[0]), "Choose a card to keep");
            cardList.remove(ret[i]);
        }

        return ret;
    }

    @Override
    protected CounterAction getCounterAction(Action action, Card card, Player player, Player target) {
        if (action != Action.ForeignAid && card == null) {
            return null;
        }

        Map<Future<String>,Player> futureMap = new HashMap<>();
        Map<String,Card> cardMap = new HashMap<>();
        String message;
        if (card != null) {
            message = String.format("%s is claiming to have a %s in order to %s", player, card, sanitize(action.name()));
            if (target != null) message += " from " + target;
        } else {
            message = String.format("%s is trying to do %s", player, sanitize(action.name()));
        }

        if (action == Action.ForeignAid) {
            for (Player p : players) {
                if (p != player) {
                    Future<String> f = getChoiceAsync(p, new String[]{"Block (Duke)", "Pass"}, message);
                    futureMap.put(f, p);
                }
            }
        } else {
            List<String> choices = new ArrayList<>();
            for (Card c : action.blockedBy) {
                String s = String.format("Block (%s)", c);
                choices.add(s);
                cardMap.put(s, c);
            }
            choices.add("Challenge");
            choices.add("Pass");
            for (Player p : players) {
                if (p != player && p != target) {
                    Future<String> f = getChoiceAsync(p, new String[]{"Challenge", "Pass"}, message);
                    futureMap.put(f, p);
                } else if (p == target) {
                    Future<String> f = getChoiceAsync(p, choices.toArray(new String[0]), message);
                    futureMap.put(f, p);
                }
            }
        }
        Future<String> f = getFirst(futureMap.keySet(), s-> !s.equalsIgnoreCase("pass"));
        for (Map.Entry<Future<String>,Player> entry : futureMap.entrySet()) {
            endpointMap.get(futureMap.get(entry.getKey())).write(gson.toJson(new Message("stopChoice")));
        }
        if (f != null) {
            try {
                String choice = f.get();
                Card c = cardMap.get(choice);
                return new CounterAction(c != null, futureMap.get(f), c);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    @Override
    protected Card getCardToSacrifice(Player player) {
        Card[] hand = new Card[player.getInfluence()];
        System.arraycopy(player.getCards(), 0, hand, 0, hand.length);
        return getChoice(player, hand, "Choose a card to sacrifice");
    }

    private static class Message {
        public String type;
        public String message;
        public JsonElement content;

        public Message(String type) {
            this.type = type;
        }

        public Message(String type, String message) {
            this.type = type;
            this.message = message;
        }
    }
}
