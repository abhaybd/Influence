package com.coolioasjulio.influence.web;

import com.coolioasjulio.influence.Action;
import com.coolioasjulio.influence.Card;
import com.coolioasjulio.influence.Game;
import com.coolioasjulio.influence.Player;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebGame extends Game {
    private Map<Player,PlayerEndpoint> endpointMap;
    private ExecutorService executorService;
    private Gson gson;

    public static void main(String[] args) {
        Message message = new Message("info");
        Gson gson = new Gson();
        Player[] players = new Player[3];
        players[0] = new Player("Joe", Card.Duke, Card.Ambassador);
        players[1] = new Player("Bob", Card.Duke, Card.Ambassador);
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

    private <T> Future<T> getChoiceAsync(Player player, T[] choices, Class<T> clazz) {
        return executorService.submit(() -> getChoice(player, choices, clazz));
    }

    private <T> T getChoice(Player player, T[] choices, Class<T> clazz) {
        PlayerEndpoint endpoint = endpointMap.get(player);
        Message message = new Message("choice");
        message.content = gson.toJsonTree(choices);
        String json = gson.toJson(message);
        endpoint.write(json);
        String responseJson = endpoint.readLine();
        return gson.fromJson(responseJson, clazz);
    }

    @Override
    protected void playerWon(Player player) {
        broadcast("info", String.format("%s wins!", player.getName()));
    }

    @Override
    protected void update() {
        broadcast("update", players);
    }

    @Override
    protected void playerDied(Player player) {
        broadcast("info", String.format("%s died!", player.getName()));
    }

    @Override
    protected Player getTarget(Player player) {
        List<Player> options = new ArrayList<>(Arrays.asList(players));
        options.remove(player);

        return getChoice(player, options.toArray(new Player[0]), Player.class);
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

        return getChoice(player, options.toArray(new Action[0]), Action.class);
    }

    @Override
    protected Card[] doExchange(Player player, Card[] cards) {
        int influence = player.getInfluence();
        Card[] ret = new Card[influence];
        List<Card> cardList = new ArrayList<>(Arrays.asList(cards));
        for (int i = 0; i < ret.length; i++) {
            ret[i] = getChoice(player, cardList.toArray(new Card[0]), Card.class);
            cardList.remove(ret[i]);
        }

        return ret;
    }

    @Override
    protected CounterAction getCounterAction(Action action, Card card, Player player, Player target) {
        CounterAction counterAction = new CounterAction();
        if (action == Action.ForeignAid) {

        } else {
            if (card == null) {
                return null;
            }

            if (action.blockedBy.isEmpty()) {
                counterAction.isBlock = false;
                // get challenges
            } else {
                // get challenges and blocks
            }
        }
    }

    @Override
    protected Card getCardToSacrifice(Player player) {
        Card[] hand = new Card[player.getInfluence()];
        System.arraycopy(player.getCards(), 0, hand, 0, hand.length);
        return getChoice(player, hand, Card.class);
    }

    private static class Message {
        public String type;
        public JsonElement content;

        public Message(String type) {
            this.type = type;
        }
    }
}
