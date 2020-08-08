import React from "react";
import {doPost} from "./App";
import {ReactComponent as BackIcon} from "./back.svg";

export default class JoinForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {name: '', code: ''};

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
            // Use an info request to check if the supplied lobby exists
            doPost("exists", code, function (data) {
                if (data.content) {
                    // If the lobby exists, save the code and name for later
                    props.store.code = code;
                    props.store.name = name;
                    props.lobby(); // Render the lobby screen
                } else {
                    alert("Invalid room code!"); // Tell the player that the lobby doesn't exist
                }
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
                            Name:
                        </td>
                        <td>
                            <input type="text" value={this.state.name} size='10' onChange={this.nameChange}/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Code:
                        </td>
                        <td>
                            <input type="text" value={this.state.code} size='10' onChange={this.codeChange}/>
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
