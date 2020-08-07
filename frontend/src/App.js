import React from "react";
import "./App.css";
import JoinForm from "./JoinForm";
import CreateForm from "./CreateForm";
import Lobby from "./Lobby";
import Game from "./Game";
import Rules from "./Rules";

function doPost(type, code, callback) {
    const http = new XMLHttpRequest();
    http.open("POST", "/lobby");
    http.setRequestHeader("Content-type", "application/json");
    http.onreadystatechange = function () {
        if (http.readyState === 4 && http.status === 200) {
            callback(JSON.parse(http.responseText));
        }
    }

    let body = JSON.stringify({type: type, code: code});
    http.send(body);
}

function Buttons(props) {
    return (
        <table className="buttons">
            <tbody>
            <tr>
                <td>
                    <button type="button" className="form-button" onClick={props.createForm}>Create</button>
                </td>
            </tr>
            <tr>
                <td>
                    <button type="button" className="form-button" onClick={props.joinForm}>Join</button>
                </td>
            </tr>
            </tbody>
        </table>
    );
}

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {view: "buttons", showHeader: true, showRules: false}
        this.store = {};

        this.createForm = this.createForm.bind(this);
        this.joinForm = this.joinForm.bind(this);
        this.mainScreen = this.mainScreen.bind(this);
        this.showLobby = this.showLobby.bind(this);
        this.start = this.start.bind(this);
        this.onStart = this.onStart.bind(this);
        this.toggleRules = this.toggleRules.bind(this);
    }

    createForm() {
        this.setState({view: "create"});
    }

    joinForm() {
        this.setState({view: "join"});
    }

    mainScreen() {
        this.setState({view: "buttons", showHeader: true});
    }

    showLobby() {
        let loc = window.location;
        let new_uri = loc.protocol === "https:" ? "wss:" : "ws:";
        new_uri += "//" + loc.host.replace("3000", "8080");
        if (!new_uri.endsWith(":8080")) new_uri += ":8080"
        new_uri += "/join/" + this.store.code + "/" + this.store.name;
        new_uri = new_uri.replace("3000", "8080");
        console.log(new_uri);
        let socket = new WebSocket(new_uri);
        socket.onopen = function (event) {
            console.log("Opened!");
        }
        socket.onclose = function (event) {
            console.log(event);
            alert("The server disconnected unexpectedly!");
        }
        this.socket = socket;
        this.setState({showHeader: false, view: "lobby"});
    }

    start() {
        let code = this.store.code;
        doPost("numPlayers", code, function (data) {
            if (data.content >= 2) {
                console.log("Starting!");
                doPost("start", code, () => true);
            }
        });
    }

    onStart() {
        this.setState({view:"game"})
    }

    toggleRules() {
        let rules = this.state.showRules;
        this.setState({showRules: !rules});
    }

    render() {
        let content;
        switch (this.state.view) {
            case "buttons":
                content = <Buttons createForm={this.createForm} joinForm={this.joinForm}/>
                break;
            case "create":
                content = <CreateForm store={this.store} main={this.mainScreen} lobby={this.showLobby}/>
                break;
            case "join":
                content = <JoinForm store={this.store} main={this.mainScreen} lobby={this.showLobby}/>
                break;
            case "lobby":
                content =
                    <Lobby socket={this.socket} start={this.start} onStart={this.onStart} code={this.store.code}/>
                break;
            case "game":
                content = <Game players={[]} socket={this.socket} localPlayer={this.store.name}/>
                break;
            default:
                content = null;
                console.warn("Invalid state!");
                break;
        }
        return (
            <div className="App">
                <header className="App-header">
                    {this.state.showRules ? <Rules back={this.toggleRules} /> : null}
                    <div id="rules-button" onClick={this.toggleRules}><u>{this.state.showRules ? "Hide" : "Show"} Rules</u></div>
                    {this.state.showHeader ? <h1>Influence</h1> : null}
                    {content}
                </header>
                <div id="footer"><a href="https://www.github.com/abhaybd">Made by Abhay Deshpande</a></div>
            </div>
        );
    }
}

export default App;
export {doPost};
