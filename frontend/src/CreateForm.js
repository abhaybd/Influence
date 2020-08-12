 import React from "react";
import {ReactComponent as BackIcon} from './back.svg';
import {doPost} from "./App";
//create Game
export default class CreateForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {name: ''};

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

            // Use an action request to create a new lobby
            doPost("create", null, function (data) {
                console.log(data);
                props.store.code = data.content;
                props.lobby(); // Change the app state to render the lobby screen
            });
        }
    }

    render() {
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
                            <input type="text" value={this.state.name} size='10' placeholder="Aguilar" maxLength="12"
                                   onChange={this.handleChange}/>
                        </td>
                    </tr>
                    <tr>
                        <td colSpan='2'>
                            <input type="submit"  value="Create Lobby"/>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        );
    }
}
