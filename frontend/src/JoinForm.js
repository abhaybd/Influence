import React from "react";
import {lobby} from "./App";

class JoinForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {name: '', code: ''};

        this.nameChange = this.nameChange.bind(this);
        this.codeChange = this.codeChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    nameChange(event) {
        this.setState({name: event.target.value.replace(/[^A-Za-z]/g, "")});
    }

    codeChange(event) {
        this.setState({code: event.target.value});
    }

    handleSubmit(event) {
        event.preventDefault();
        this.props.store.code = this.state.code;
        this.props.store.name = this.state.name;
        lobby();
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <table>
                    <tbody>
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

export default JoinForm;