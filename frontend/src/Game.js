import React from "react";

export default class Game extends React.Component {
    constructor(props) {
        super(props);
        this.socket = props.socket;
        this.localPlayerName = props.localPlayer;
        this.state = {players: props.players, choices:[], message:"", log:["Started game!"]}

        this.onmessage = this.onmessage.bind(this);
        this.getLocalPlayer = this.getLocalPlayer.bind(this);

        // Register the onmessage event handler for the websocket
        this.socket.onmessage = this.onmessage;

        window.onbeforeunload = () => true; // block refreshes
    }

    onmessage(event) {
        let json = event.data;
        let data = JSON.parse(json);
        // We've received a message from the server, so follow the instruction
        switch (data.type) {
            case "update":
                // This is an update message, so update the player information
                this.setState({players: data.content})
                break;

            case "info":
                // This is an info message, so alert the player with the info
                alert(data.content);
                break;

            case "choice":
                // We need to prompt the player to make a choice, so display those now
                this.setState({choices:data.content, message:data.message});
                break;

            case "stopChoice":
                // The time for making choices has ended, so stop making a choice
                // If the player wasn't already making a choice, this doesn't break anything
                this.setState({choices:[], message:"Waiting for others..."});
                break;

            case "log":
                // Push this data to the game event log
                let log = this.state.log;
                log.push(data.content);
                // Make sure the log length never exceeds the max length
                while (log.length > 5) {
                    log.shift();
                }
                this.setState({log:log})
                break;

            default:
                // We received an unrecognized server message
                console.warn("Unrecognized data " + data.toString());
                break;
        }
    }

    numInfluence(cards) {
        // Count the number of non-null cards
        return cards.filter(x => x !== null).length;
    }

    getLocalPlayer() {
        // Find the player from the list of players that matches the local player's name
        for (let player of this.state.players) {
            if (player.name === this.localPlayerName) {
                return player;
            }
        }
        // If no such player exists, just return an empty player
        return {name:"", cards:[], coins:0};
    }

    onChoice(choice) {
        // The player has made a choice, so stop displaying the choices
        this.setState({choices:[], message:"Waiting for others..."});
        this.socket.send(JSON.stringify(choice)); // Send the players choice to the server
    }

    render() {
        // Map a player to a JSX element for displaying
        const Player = ({player, influence, coins}) => (
            <div className={player===this.localPlayerName ? "local-player-icon" : "player-icon"}>
                {player} <br/>
                Coins: {coins} <br/>
                Influence: {influence}
            </div>
        );

        // Map a choice to a JSX element for displaying
        const Choice = ({choice}) => (
            <button className="game-button" onClick={() => this.onChoice(choice)}>
                <div className="player-icon">
                    {choice}
                </div>
            </button>
        );

        // Render the component contents
        return (
            <div>
                <div id="event-log">
                    {this.state.log.map(line => <div>{line}<br /></div>)}
                </div>
                <div className="game-container">
                    {this.state.players.map(player => <Player key={player.name} player={player.name} coins={player.coins}
                                                              influence={this.numInfluence(player.cards)}/>)}
                </div>
                <div className="game-container">
                    {this.getLocalPlayer().cards.map(card => card === null ? null : <div className="card-names">{card}</div>)}
                </div>
                <div className="game-container">
                    {this.state.message}
                </div>
                <div className="game-container">
                    {this.state.choices.map((choice,i) => <Choice key={i} choice={choice} />)}
                </div>
            </div>
        );
    }
}