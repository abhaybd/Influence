import React from "react";
import {doPost, lobby, main} from "./App";
import {ReactComponent as BackIcon} from "./back.svg";

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
        let name = this.state.name;
        let code = this.state.code;
        let props = this.props;
        if (code.length > 0 && name.length > 0) {
            doPost("exists", code, function(data) {
                if (data.content) {
                    props.store.code = code;
                    props.store.name = name;
                    lobby();
                } else {
                    alert("Invalid room code!");
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
                            <button id="back"><BackIcon onClick={main}/></button>
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

export default JoinForm;