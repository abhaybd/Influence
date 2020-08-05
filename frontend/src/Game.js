import React from "react";

export default class Game extends React.Component {
    constructor(props) {
        super(props);
        this.socket = props.socket;
        this.state = {players: props.players}

        this.onmessage = this.onmessage.bind(this);
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
                break;

            case "choice":
                break;

            case "stopChoice":
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

    render() {
        const Player = ({player, influence, coins}) => (
            <div className="player-icon">
                {player} <br/>
                Coins: {coins} <br/>
                Influence: {influence}
            </div>
        );

        return (
            <div id="game-container">
                {this.state.players.map(player => <Player key={player.name} player={player.name} coins={player.coins}
                                                          influence={this.numInfluence(player.cards)}/>)}
            </div>
        );
    }
}