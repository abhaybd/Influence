import React from "react";

export default class Lobby extends React.Component {
    constructor(props) {
        super(props);
        this.state = {players: []}
        this.socket = props.socket;

        this.onmessage = this.onmessage.bind(this);

        this.socket.onmessage = this.onmessage;
    }

    onmessage(event) {
        console.log(event.data);
        if (event.data === "Start") {
            this.socket.onmessage = undefined;
            this.props.onStart();
        } else {
            let players = JSON.parse(event.data);
            this.setState({players: players});
        }
    }

    render() {
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