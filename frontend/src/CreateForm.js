import React from "react";
import {lobby} from "./App";

class CreateForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {name: ''};

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({name: event.target.value.replace(/[^A-Za-z]/g, "")});
    }

    handleSubmit(event) {
        event.preventDefault();
        this.props.store.name = this.state.name;
        const Http = new XMLHttpRequest();
        const url = '/Influence_war_exploded/create';
        Http.open("POST", url);

        Http.send();
        let props = this.props;
        Http.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
                console.log(Http.responseText);
                props.store.code = Http.responseText;
                lobby();
            }
        }
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
                            <input type="text" value={this.state.name} size='10' onChange={this.handleChange}/>
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