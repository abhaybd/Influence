import React from 'react';
import ReactDOM from 'react-dom';
import './App.css';
import JoinForm from "./JoinForm";
import CreateForm from "./CreateForm";

class Store {
    constructor(data={}) {
        Object.assign(this, data);
    }
}

let lobbyInfo = new Store({name:"", code:""});
let socket;
let started = false;

function create() {
    const elem = <div className="App">
        <header className="App-header">
            <h1>
                Influence
            </h1>
            <CreateForm store={lobbyInfo}/>
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
            <JoinForm store={lobbyInfo}/>
        </header>
    </div>;
    ReactDOM.render(elem, document.getElementById("root"));
}

function onmessage(event) {
    console.log(event.data);
    if (!started) {
        let players = JSON.parse(event.data);
        const elem = <div className="App">
            <header className="App-header">
                <div id="centered"><Lobby players={players} code={lobbyInfo.code}/></div>

            </header>
        </div>;
        ReactDOM.render(elem, document.getElementById("root"));
    }
}

function lobby() {
    // let loc = window.location, new_uri;
    // if (loc.protocol === "https:") {
    //     new_uri = "wss:";
    // } else {
    //     new_uri = "ws:";
    // }
    // new_uri += "//" + loc.host;
    let new_uri = "ws://localhost:8080/Influence_war_exploded/join";
    new_uri += "/" + lobbyInfo.code + "/" + lobbyInfo.name;
    console.log(new_uri);
    socket = new WebSocket(new_uri);
    socket.onmessage = onmessage;
    socket.onopen = function (event) {
        console.debug("Opened!");
    }

    const elem = <div className="App">
        <header className="App-header">
            <div id="centered"><Lobby players={[]} code={lobbyInfo.code}/></div>

        </header>
    </div>;
    ReactDOM.render(elem, document.getElementById("root"));
}

function Lobby(props) {
    const Row = ({player}) => (
        <tr>
            <td>{player}</td>
        </tr>
    );

    console.log(props);

    return (
        <table>
            <tbody>
            {props.players.map(player => (<Row player={player} />))}
            <tr>
                <td>
                    <button type="button">Start</button>
                </td>
            </tr>
            <tr>
                <td>
                    Code: {props.code}
                </td>
            </tr>
            </tbody>
        </table>
    );
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
export {lobby};
