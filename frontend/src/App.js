import React from 'react';
import ReactDOM from 'react-dom';
import './App.css';

function create() {
    const elem = <div className="App">
        <header className="App-header">
            <h1>
                Influence
            </h1>
            <CreateForm/>
        </header>
    </div>;
    ReactDOM.render(elem, document.getElementById("root"));
}

function join() {
    const elem = <div className="App">
        <header className="App-header">
            <h1>
                Influence
            </h1>
            <JoinForm />
        </header>
    </div>;
    ReactDOM.render(elem, document.getElementById("root"));
}

function lobby() {

}

function Buttons() {
    return (
        <table className="buttons">
            <tbody>
            <tr>
                <td>
                    <button type="button" onClick={create}>Create</button>
                </td>
            </tr>
            <tr>
                <td>
                    <button type="button" onClick={join}>Join</button>
                </td>
            </tr>
            </tbody>
        </table>
    );
}

class JoinForm extends React.Component {
    constructor(props) {
        super(props);

        this.state = {name: '', code: ''};

        this.nameChange = this.nameChange.bind(this);
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
        const Http = new XMLHttpRequest();
        const url = '/Influence_war_exploded/create';
        Http.open("POST", url);
        Http.send();

        Http.onreadystatechange = (e) => {
            console.log(Http.responseText);
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

function App() {
    return (
        <div className="App">
            <header className="App-header">
                <h1>
                    Influence
                </h1>
                <Buttons/>
            </header>
        </div>
    );
}

export default App;
