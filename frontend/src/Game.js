import React from "react";

export default class Game extends React.Component {
    constructor(props) {
        super(props);
        this.socket = props.socket;
        this.localPlayerName = props.localPlayer;
        this.state = {players: props.players, choices:[], message:"", log:["Started game!"]}

        this.onmessage = this.onmessage.bind(this);
        this.getLocalPlayer = this.getLocalPlayer.bind(this);

        this.socket.onmessage = this.onmessage;
    }

    onmessage(event) {
        let json = event.data;
        console.log(json);
        let data = JSON.parse(json);
        switch (data.type) {
            case "update":
                this.setState({players: data.content})
                break;

            case "info":
                alert(data.content);
                break;

            case "choice":
                this.setState({choices:data.content, message:data.message});
                break;

            case "stopChoice":
                this.setState({choices:[], message:"Waiting for others..."});
                break;

            case "log":
                let log = this.state.log;
                log.push(data.content);
                while (log.length > 5) {
                    log.shift();
                }
                this.setState({log:log})
                break;

            default:
                console.warn("Unrecognized type " + data.type);
                break;
        }
    }

    numInfluence(cards) {
        let influence = 0;
        for (let card of cards) {
            if (card !== null) influence++;
        }
        return influence;
    }

    getLocalPlayer() {
        console.log(this.state.players);
        console.log(this.localPlayerName);
        for (let player of this.state.players) {
            if (player.name === this.localPlayerName) {
                return player;
            }
        }
        return {name:"", cards:[], coins:0};
    }

    onChoice(choice) {
        this.setState({choices:[], message:"Waiting for others..."});
        this.socket.send(JSON.stringify(choice));
    }

    render() {
        const Player = ({player, influence, coins}) => (
            <div className={player===this.localPlayerName ? "local-player-icon" : "player-icon"}>
                {player} <br/>
                Coins: {coins} <br/>
                Influence: {influence}
            </div>
        );

        const Choice = ({choice}) => (
            <button className="game-button" onClick={() => this.onChoice(choice)}>
                <div className="player-icon">
                    {choice}
                </div>
            </button>
        );

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