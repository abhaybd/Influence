import React from "react";
import { ReactComponent as CopyIcon } from "./copy.svg";
import { CopyToClipboard } from "react-copy-to-clipboard/lib/Component";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        this.state = { players: [] }

        if (props.socket) {
            this.socket = props.socket;

            // Register the onmessage event handler for the websocket
            this.onmessage = this.onmessage.bind(this);

            this.socket.onmessage = this.onmessage;
            window.onbeforeunload = () => true; // block refreshes
        }
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
            this.setState({ players: data });
        }
    }

    render() {
        // Map a player to a row in a table to display
        const Row = ({ player }) => (
            <tr>
                <td colSpan="2">{player}</td>
            </tr>
        );

        let component = (
            <div>
                <p>An error occurred! Please create a new lobby or join an existing one!</p>
                <button type="button" className="form-button" onClick={this.props.main}>Go Back</button>
            </div>
        );
        // If the socket exists, we're connected to the lobby. Otherwise, show an error message.
        if (this.socket) {
            component = (
                <div id="centered">
                    <table>
                        <tbody>
                            {this.state.players.map((player, i) => (<Row player={player} key={i} />))}
                            <tr>
                                <td colSpan="2">
                                    <button type="button" className="form-button" onClick={this.props.start}>Start
                                </button>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Code: <span id="lobby-code">{this.props.code}</span>
                                </td>
                                <td>
                                    <CopyToClipboard text={this.props.code}><button id="copy-button"><CopyIcon /></button></CopyToClipboard>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            );
        }

        return component;
    }
}