import React from "react";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        this.state = {players: []}
        this.socket = props.socket;

        // Register the onmessage event handler for the websocket
        this.onmessage = this.onmessage.bind(this);

        this.socket.onmessage = this.onmessage;
    }

    onmessage(event) {
        // When we receive a message from the server, it's either a lobby update or a start message
        console.log(event.data);
        let data = JSON.parse(event.data);
        if (data === "Start") {
            // If it's a start message, unregister the event handler
            this.socket.onmessage = undefined;
            this.props.onStart(); // Render the game view
        } else {
            // This is a lobby update, so update the list of players in the lobby
            this.setState({players: data});
        }
    }

    render() {
        // Map a player to a row in a table to display
        const Row = ({player}) => (
            <tr>
                <td>{player}</td>
            </tr>
        );

        return (
            <div id="centered">
                <table>
                    <tbody>
                    {this.state.players.map((player, i) => (<Row player={player} key={i}/>))}
                    <tr>
                        <td>
                            <button type="button" className="form-button" onClick={this.props.start}>Start
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Code: {this.props.code}
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        );
    }

}