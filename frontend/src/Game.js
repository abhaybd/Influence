import React from "react";
import "./Game.css";
import {createSocket} from "./App";
import queryString from "query-string";
import {withRouter} from "react-router";

const WAITING_MSG = "Waiting for others...";

//game
class Game extends React.Component {
    constructor(props) {
        super(props);

        this.onMessage = this.onMessage.bind(this);
        this.onDisconnect = this.onDisconnect.bind(this);

        this.state = {players: [], localPlayerCards: [], choices: [], message: WAITING_MSG, log: ["Started game!"], playerColorMap: null}
    }


    componentDidMount() {
        console.log("Game mounted!");
        window.onbeforeunload = () => true; // block refreshes
        if (this.props.socket) {
            console.log("Using an existing socket!");
            this.socket = this.props.socket;
            this.localPlayerName = this.props.localPlayer;
            this.socket.onclose = this.onDisconnect;
        } else {
            console.log("Making new socket and trying to join existing game...");
            let args = queryString.parse(this.props.location.search);
            this.socket = createSocket(args.name, args.code, null, this.onDisconnect);
            this.localPlayerName = args.name;
        }

        // Register the onmessage event handler for the websocket
        this.socket.onmessage = this.onMessage;
    }

    componentWillUnmount() {
        window.onbeforeunload = null;
    }

    onDisconnect(event) {
        // The server will always define a close reason, so if none is defined, then this close was not initiated by the server
        // In that case, we want to stay on this page so the client can reconnect
        if (event.reason) {
            // If the close was initiated by the server, alert the user and go to the homepage
            alert("Unexpected disconnection from server! Error: " + event.reason);
            this.props.history.push("/");
        }
    }

    onMessage(event) {
        let json = event.data;
        let data = JSON.parse(json);
        // We've received a message from the server, so follow the instruction
        switch (data.type) {
            case "update":
                // This is an update message, so update the player information
                if (this.state.playerColorMap === null) {
                    let colorMap = this.createPlayerColorMap(data.content.players);
                    this.setState({players: data.content.players, localPlayerCards: data.content.localPlayerCards, playerColorMap: colorMap});
                } else {
                    this.setState({players: data.content.players, localPlayerCards: data.content.localPlayerCards});
                }
                break;

            case "info":
                // This is an info message, so alert the player with the info
                alert(data.content);
                break;

            case "choice":
                console.log("Making choice!");
                // We need to prompt the player to make a choice, so display those now
                this.setState({choices: data.content, message: data.message});
                break;

            case "stopChoice":
                console.log("Stopping choice!");
                // The time for making choices has ended, so stop making a choice
                // If the player wasn't already making a choice, this doesn't break anything
                this.setState({choices: [], message: WAITING_MSG});
                break;

            case "log":
                // Push this data to the game event log
                let log = this.state.log;
                log.push(data.content);
                // Make sure the log length never exceeds the max length
                while (log.length > 5) {
                    log.shift();
                }
                this.setState({log: log})
                break;

            default:
                // We received an unrecognized server message
                console.warn("Unrecognized data " + data.toString());
                break;
        }
    }

    //Assigns Colors to each player for their player card
    createPlayerColorMap(players) {
        const colorList = ["#19D2E8", "#44DFB6", "#77EA83", "#E6D517", "#E8AA14", "#FF5714"];
        let map = {};
        for (let i = 0; i < players.length; i++) {
            map[players[i].name] = colorList[i];
        }
        return map;
    }

    onChoice(choice) {
        // The player has made a choice, so stop displaying the choices
        this.setState({choices: [], message: WAITING_MSG});
        this.socket.send(JSON.stringify(choice)); // Send the players choice to the server
    }

    render() {
        // Map a player to a JSX element for displaying
        const Player = ({player, influence, coins, color}) => (
            <div className={player === this.localPlayerName ? "local-player-icon" : "player-icon"}
                 style={{backgroundColor: color}}>
                <b id="playerText">{player}</b><br/>
                <span id="coinText">Coins: {coins}</span> <br/>
                <span id="influenceText">Influences: {influence}</span>
            </div>
        );

        // Map a choice to a JSX element for displaying
        const Choice = ({choice}) => (
            <button className="game-button" onClick={() => this.onChoice(choice)}>
                <div className="choice-icon">
                    {choice}
                </div>
            </button>
        );

        // Copy the contents of the event log into a temp variable
        let log = [];
        for (let event of this.state.log) {
            log.push(event);
        }

        // Pad to 5 entries, so even if the log isn't full it takes up the same amount of space
        while (log.length < 5) {
            log.push("");
        }

        // Render the component contents
        return (
            <div id="game-div">
                <div id="event-log">
                    {log.map((line,i) => <div key={i}>{line}<br/></div>)}
                </div>
                <div className="game-container">
                    {this.state.players.map(player => <Player key={player.name} player={player.name}
                                                              coins={player.coins}
                                                              influence={player.influence}
                                                              color={this.state.playerColorMap[player.name]}/>)}
                </div>
                <div className="game-container">
                    {this.state.localPlayerCards.map((card, i) => card === null ? null :
                        <div className="card-names" key={i}>{card}</div>)}
                </div>
                <div className="game-container">
                    <strong>{this.state.message}</strong>
                </div>
                <div className="game-container">
                    {this.state.choices.map((choice, i) => <Choice key={i} choice={choice}/>)}
                </div>
            </div>
        );
    }
}

export default withRouter(Game);
