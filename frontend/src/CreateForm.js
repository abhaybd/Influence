import React from "react";
import {lobby, main} from "./App";
import {ReactComponent as BackIcon} from './back.svg';

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
        if (this.state.name.length > 0) {
            this.props.store.name = this.state.name;
            const url = '/lobby';

            const http = new XMLHttpRequest();
            http.open("POST", url);
            http.setRequestHeader('Content-type', 'application/json');
            http.send(JSON.stringify({type:"create"}));

            let props = this.props;
            http.onreadystatechange = function() {
                if (this.readyState === 4 && this.status === 200) {
                    console.log(http.responseText);
                    props.store.code = JSON.parse(http.responseText).content;
                    lobby();
                }
            }
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