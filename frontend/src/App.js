import React from "react";
import ReactDOM from "react-dom";
import "./App.css";
import JoinForm from "./JoinForm";
import CreateForm from "./CreateForm";
import Lobby from "./Lobby";
import Game from "./Game";

class Store {
    constructor(data = {}) {
        Object.assign(this, data);
    }
}

let lobbyInfo = new Store({name: "", code: ""});
let socket;
let started = false;

function doPost(type, code, callback) {
    const http = new XMLHttpRequest();
    http.open("POST", "/lobby");
    http.setRequestHeader("Content-type", "application/json");
    http.onreadystatechange = function() {
        if (http.readyState === 4 && http.status === 200) {
            callback(JSON.parse(http.responseText));
        }
    }

    let body = JSON.stringify({type:type, code:code});
    console.log(body)
    http.send(body);
}

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
        if (event.data === "Start") {
            started = true;
            onStart();
        } else {
            let players = JSON.parse(event.data);
            const elem = <div className="App">
                <header className="App-header">
                    <div id="centered"><Lobby players={players} code={lobbyInfo.code} start={start}/></div>

                </header>
            </div>;
            ReactDOM.render(elem, document.getElementById("root"));
        }
    }
}

function lobby() {
    let loc = window.location;
    let new_uri = loc.protocol === "https:" ? "wss:" : "ws:";
    new_uri += "//" + loc.host;
    new_uri += "/join/" + lobbyInfo.code + "/" + lobbyInfo.name;
    new_uri = new_uri.replace("3000", "8080");
    console.log(new_uri);
    socket = new WebSocket(new_uri);
    socket.onmessage = onmessage;
    socket.onopen = function (event) {
        console.debug("Opened!");
    }

    socket.onclose = function (event) {
        console.debug(event);
        alert("The server disconnected unexpectedly!");
    }

    const elem = <div className="App">
        <header className="App-header">
            <div id="centered"><Lobby players={[]} code={lobbyInfo.code} start={start}/></div>

        </header>
    </div>;
    ReactDOM.render(elem, document.getElementById("root"));
}

function onStart() {
    const elem = <header className="App-header">
        <Game socket={socket} players={[]} localPlayer={lobbyInfo.name} />
    </header>
    ReactDOM.render(elem, document.getElementById("root"));
}

function start() {
    doPost("numPlayers", lobbyInfo.code, function(data) {
        if (data.content >= 2) {
            console.log("Starting!");
            doPost("start", lobbyInfo.code, () => {});
        }
    });
}

function Buttons() {
    return (
        <table className="buttons">
            <tbody>
            <tr>
                <td>
                    <button type="button" className="form-button" onClick={create}>Create</button>
                </td>
            </tr>
            <tr>
                <td>
                    <button type="button" className="form-button" onClick={join}>Join</button>
                </td>
            </tr>
            </tbody>
        </table>
    );
}

function main() {
    const elem = <div className="App">
        <header className="App-header">
            <h1>
                Influence
            </h1>
            <Buttons/>
        </header>
    </div>;
    ReactDOM.render(elem, document.getElementById("root"));
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
export {lobby, main, doPost};
