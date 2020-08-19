import React from "react";
import {ReactComponent as BackIcon} from './back.svg';
import {createSocket, doPost} from "./App";
import Lobby from "./Lobby";

//create Game
class CreateForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {name: "", showLobby: false};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        // Only letters allowed, so replace invalid characters with an empty string
        this.setState({name: event.target.value.replace(/[^A-Za-z]/g, "")});
    }

    handleSubmit(event) {
        event.preventDefault(); // override default behavior
        // Only submit the create request if there is a valid name
        if (this.state.name.length > 0) {
            this.props.store.name = this.state.name; // Store the player name for use in a later component
            let props = this.props;
            let comp = this;

            // Use an action request to create a new lobby
            doPost({type: "create"}, function (data) {
                console.log(data);
                props.store.code = data.content;
                props.store.socket = createSocket(props.store.name, props.store.code,
                    function (event) {
                        comp.setState({showLobby: true}); // render the lobby within this component
                    },
                    function (event) {
                        // Show the error and go back to the create screen
                        alert("Error: " + event.reason);
                        comp.setState({showLobby: false});
                    }
                );
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
                                <input type="text" value={this.state.name} size='10' placeholder="Aguilar" maxLength="8"
                                       onChange={this.handleChange}/>
                            </td>
                        </tr>
                        <tr>
                            <td colSpan='2'>
                                <input type="submit" value="Create Lobby"/>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </form>
        );
    }
}

export default CreateForm;
