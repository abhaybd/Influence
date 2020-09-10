import React from "react";
import "./Form.css";
import {createSocket, doPost} from "./App";
import {ReactComponent as BackIcon} from "./back.svg";
import Lobby from "./Lobby";

const JOIN_FAIL_NO_LOBBY = 0b00;
const JOIN_FAIL_NAME_TAKEN = 0b01;
const JOIN_FAIL_LOBBY_FULL = 0b10;
const JOIN_SUCCESS = 0b11;

//Join Game
class JoinForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {name: "", code: "", showLobby: false};

        this.nameChange = this.nameChange.bind(this);
        this.codeChange = this.codeChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    nameChange(event) {
        // Only letters allowed, so replace invalid characters with an empty string
        this.setState({name: event.target.value.replace(/[^A-Za-z]/g, "")});
    }

    codeChange(event) {
        this.setState({code: event.target.value});
    }

    handleSubmit(event) {
        event.preventDefault(); // override default behavior
        let name = this.state.name;
        let code = this.state.code;
        let props = this.props;
        // Only submit the create request if there is a valid name and code
        if (code.length > 0 && name.length > 0) {
            let comp = this;
            // Use an info request to check if we can join this lobby
            doPost({type: "canJoin", code: code, content: name}, function (data) {
                // If we can, join now
                if (data.content === JOIN_SUCCESS) {
                    // This isn't fool proof. A player may join with this name between this POST request and the WS connection.
                    // This site should handle it gracefully. (the backend does, but the frontend should too)
                    props.store.code = code;
                    props.store.name = name;
                    props.store.socket = createSocket(name, code,
                        function () {
                            comp.setState({showLobby: true}); // render the lobby within this component
                        },
                        function (event) {
                            // Show the error and go back to the join screen
                            comp.setState({showLobby: false, name: ""});
                            alert("Error: " + event.reason);
                        }
                    );
                } else {
                    switch (data.content) {
                        case JOIN_FAIL_NO_LOBBY:
                            alert("That lobby doesn't exist!");
                            break;

                        case JOIN_FAIL_NAME_TAKEN:
                            alert("That name is already taken! Please choose another!");
                            break;

                        case JOIN_FAIL_LOBBY_FULL:
                            alert("This lobby is full!");
                            break;

                        default:
                            console.error("Unrecognized response code: " + data.content.toString(2));
                            alert("Unable to join the lobby!");
                            break;
                    }
                }
            });
        }
    }

    render() {
        if (this.state.showLobby) {
            return <Lobby socket={this.props.store.socket} start={this.props.start} onStart={this.props.onStart}
                          code={this.props.store.code} main={this.props.main}/>
        }
        return (
            <form onSubmit={this.handleSubmit}>
                <table className="form-table">
                    <tbody>
                    <tr>
                        <td>
                            <button id="back" type="button" onClick={this.props.main}><BackIcon/></button>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div id="create-name">What's your name, traveler?</div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="text" value={this.state.name} size='10' placeholder="Altaïr" maxLength='8'
                                   onChange={this.nameChange}/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div id="create-code">Enter Access Code</div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="text" value={this.state.code} size='10' placeholder="end-line-game"
                                   onChange={this.codeChange}/>
                        </td>
                    </tr>
                    <tr>
                        <td colSpan='2'>
                            <input type="submit" value="Join Lobby"/>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        );
    }
}

export default JoinForm;
