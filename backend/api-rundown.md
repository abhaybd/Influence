# Influence API

This is a rundown on the API the backend uses to communicate with the frontend. This should make it easier to create new frontends, and also will help me remember how to maintain this in the future.

## Lobby Requests

You can make POST requests to `/lobby` in order to either retrieve information or perform actions related to lobbies. There are 2 broad types of requests, `action` requests and `info` requests.

The content type of all requests must be `application/json`, and they must define a `type` key, and possibly a `code` key.

If a request doesn't define the appropriate keys, the server will respond with HTTP 400.

### Action Requests

Action requests tell the server to do something related to lobbies. There are multiple types of action requests:

- `create`
  - Of the form `{"type":"create"}`
  - Used to create a new lobby. Only the `type` key needs to be defined.
  - Will respond with HTTP 200, and the body of the response will be of the form `{"content":"EXAMPLE-CODE"}`, where EXAMPLE_CODE is the code of the newly created lobby
- `start`
  - Of the form `{"type":"start", "code":"EXAMPLE-CODE"}`
  - Used to start the game in an existing lobby
  - If the game cannot start (if already started, not enough players, lobby doesn't exist) will return HTTP 422
  - If successful, will return HTTP 200

### Info Requests

Info requests retrieve information about lobbies.

- `started`
  - Of the form `{"type":"started", "code":"EXAMPLE-CODE"}`
  - Checks if the lobby with the specified code exists and has already started
  - Will respond with HTTP 200
  - If both conditions are true, will respond with `{"content":true}`, otherwise response will be `{"content":false}`
- `exists`
  - Of the form `{"type":"exists", "code":"EXAMPLE-CODE"}`
  - Checks if the lobby with the specified code exists
  - Will respond with HTTP 200
  - If the lobby exists, will respond with `{"content":true}`, otherwise response will be `{"content":false}`
- `numPlayers`
  - Of the form `{"type":"numPlayers", "code":"EXAMPLE-CODE"}`
  - Gets the number of players in the lobby with the specified code
  - If the lobby exists, will respond with HTTP 200, and the body will be in the form `{"content":numPlayers}`, where `numPlayers` is the number of players
  - If the lobby doesn't exist, will respond with HTTP 422

## Player WebSocket

Communication with the player happens through a WebSocket connection. The server expects the connection on port 8080.

There are 2 phases of the socket lifecycle: `lobby` and `game`. Once a lobby has started the game, it will keep playing new games forever until the lobby is closed by all players disconnecting.

If a player's socket disconnects, the game must be restarted and a new lobby must be created.

### Lobby phase

During the `lobby` phase, all players are in the lobby, waiting for the game to start. In this phase, whenever a player joins or leaves the lobby, the server will transmit the names of all players in the lobby to everyone in the lobby, as a JSON array. 

The message will look like this: `["Player1", "Player2", "Player3"]`

When the game starts, the game signals all players to start by sending the string `Start` (prior to `0.3` this is not a JSON-formatted string, it's just raw text. From `0.3` onwards this is a JSON-formatted string) to each player. After this, the players are permanently in the `game` phase.

### Game phase

During the `game` phase, all players are playing the game. During this time, the server will prompt each user as necessary for an action, as defined by the rules of the game. Clients should not send anything unless specifically requested by the server.

All messages from the server will look like this: `{"type":"TYPE", "message":"MESSAGE", "content":{CONTENT}}`

The `message` key may not be defined. If it is, the client should show the message to the player. If it is not defined, the client is free to display anything, as long as the displayed content is different than the last `message` received.

The value of the `type` key dictates how the `content` should be interpreted.

The `type` value will be one of these:

- `info` - This message is used for significant information events. Typically, this is when a player wins the game, or when a player dies. The `content` value will be a string to display.
- `log` - This denotes a game event, and should be displayed in the game event log. The `content` value will be a string to display.
- `update` - This signifies a game update. The `content` value will be a JSON array of players. Each player will be defined like this: `{"name":PLAYER-NAME, "coins":2, "cards":["Duke", null]}`. As of now, all players' information is transmitted to everyone, so the client should not display information that the player should not know. The `cards` value will be a JSON array of strings, showing the influence cards a player has. The length of this array will be 2. If a player has less than 2 influence, then the array will be padded with `null` values to maintain a length of 2. Dead players will not be included in game updates.
- `choice` - This tells clients to prompt the player to make a choice. Typically, the `message` value will be the prompt to display to the player. The value of `content` will be a JSON array of strings to display to the player, who must choose one. This is the only message that the client should respond to. The response should be a JSON-formatted string, whose content matches EXACTLY one of the choices given to the client. That is, the response must be one of the elements of `content`. If a client responds with an invalid response, the game will terminate.
- `stopChoice` - This denotes that the player is no longer able to make a choice. All `choice` messages will be followed with a `stopChoice` message, even if the player already made a choice. If the player made a choice and the client received a `stopChoice` message, the client should handle it gracefully. This is the only server message for which the `content` key can be ignored. It may not even be defined.