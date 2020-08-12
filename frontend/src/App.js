//Main Screen - Overall Layout
import React from "react";
import {Switch, Route, withRouter} from "react-router-dom";
import "./App.css";
import JoinForm from "./JoinForm";
import CreateForm from "./CreateForm";
import Lobby from "./Lobby";
import Game from "./Game";
import Rules from "./Rules";

/**
 * Perform a POST request to the lobby API endpoint. This can either be an info or action request.
 *
 * @param type The type of request.
 * @param code The lobby code, if applicable. May be null if no lobby code is required.
 * @param callback A callback to call once the request succeeds.
 */
function doPost(type, code, callback) {
    // Do a POST request
    const http = new XMLHttpRequest();
    http.open("POST", "/lobby");
    http.setRequestHeader("Content-Type", "application/json");
    http.onreadystatechange = function () {
        // This signifies that the request was successful
        if (http.readyState === 4 && http.status === 200) {
            let contentType = http.getResponseHeader("Content-Type");
            console.log(contentType);
            if (contentType !== null && contentType.includes("application/json")) {
                console.log(http.responseText);
                callback(JSON.parse(http.responseText));
            }
        }
    }

    // Serialize the content and send
    let body = JSON.stringify({type: type, code: code});
    http.send(body);
}

function MainScreen(props) {
    // This defines the main screen, with the buttons to either join or create a lobby
    return (
        <table className="buttons">
            <tbody>
            <tr>
                <td>
                    <button type="button" className="form-button" onClick={props.createForm}>Create Game</button>
                </td>
            </tr>
            <tr>
                <td>
                    <button type="button" className="form-button" onClick={props.joinForm}>Join Game</button>
                </td>
            </tr>
            </tbody>
        </table>
    );
}

class App extends React.Component {
    constructor(props) {
        super(props);
        // Create the initial state
        this.state = {showRules: false};
        // This is where child components can store the info they get (the player name, lobby code, etc)
        this.store = {};

        // Bind methods to this instance
        this.createForm = this.createForm.bind(this);
        this.joinForm = this.joinForm.bind(this);
        this.mainScreen = this.mainScreen.bind(this);
        this.showLobby = this.showLobby.bind(this);
        this.start = this.start.bind(this);
        this.onStart = this.onStart.bind(this);
        this.toggleRules = this.toggleRules.bind(this);
    }

    createForm() {
        this.props.history.push("/create");
    }

    joinForm() {
        this.props.history.push("/join");
    }

    mainScreen() {
        this.props.history.push("/");
    }

    showLobby() {
        // This assembles the websocket uri
        // Essentially, change the protocol from http to ws, and direct the websocket to port 8080
        let loc = window.location;
        let new_uri = loc.protocol === "https:" ? "wss:" : "ws:";
        // React proxy doesn't redirect websockets, so we'll have to manually replace the port 3000 with 8080
        new_uri += "//" + loc.host.replace("3000", "8080");
        // On prod servers, the port isn't in the url but on dev servers it is. So make sure to not duplicate the port
        if (!new_uri.endsWith(":8080")) new_uri += ":8080";
        new_uri += "/ws/join/" + this.store.code + "/" + this.store.name;
        new_uri = new_uri.replace("3000", "8080");
        console.log(new_uri);
        let socket = new WebSocket(new_uri); // Open the websocket connection
        socket.onopen = function (event) {
            console.log("Opened!");
        }
        socket.onclose = function (event) {
            console.log(event);
            alert("The server disconnected unexpectedly!");
        }
        this.socket = socket;
        this.props.history.push("/lobby");
    }

    start() {
        let code = this.store.code;
        // Use an info request to determine the amount players. If there's enough, start the game
        doPost("numPlayers", code, function (data) {
            if (data.content >= 2) {
                console.log("Starting!");
                // Use an action request to start the game
                // Once the game starts, it'll send an event via websocket to trigger a state change, so we don't do that here
                doPost("start", code, () => true);
            }
        });
    }

    onStart() {
        this.props.history.push("/game");
    }

    toggleRules() {
        let rules = this.state.showRules;
        this.setState({showRules: !rules});
    }

    render() {
        const Header = () => <div id="header"><h1>INFLUENCE</h1><br/>A Game of Deception</div>

        // Render the app and the content
        return (
            <div className="App">
                <header className="App-header">
                    {this.state.showRules ? <Rules back={this.toggleRules}/> : null}
                    <div id="rules-button" onClick={this.toggleRules}>
                        <u>{this.state.showRules ? "Hide" : "Show"} Rules</u></div>
                    <Switch>
                        <Route exact path="/" component={Header}/>
                        <Route path="/create" component={Header}/>
                        <Route path="/join" component={Header}/>
                        <Route path="/lobby" component={Header}/>
                    </Switch>
                    <Switch>
                        <Route exact path="/">
                            <MainScreen createForm={this.createForm} joinForm={this.joinForm}/>
                        </Route>
                        <Route path="/create">
                            <CreateForm store={this.store} main={this.mainScreen} lobby={this.showLobby}/>
                        </Route>
                        <Route path="/join">
                            <JoinForm store={this.store} main={this.mainScreen} lobby={this.showLobby}/>
                        </Route>
                        <Route path="/lobby">
                            <Lobby socket={this.socket} start={this.start} onStart={this.onStart} code={this.store.code}/>
                        </Route>
                        <Route path="/game">
                            <Game players={[]} socket={this.socket} localPlayer={this.store.name}/>
                        </Route>
                    </Switch>
                </header>
                <div id="footer">Made by <a href="https://www.github.com/abhaybd">Abhay Deshpande</a></div>
            </div>
        );
    }
}

export default withRouter(App);
export {doPost};
