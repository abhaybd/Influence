import React from 'react';
import ReactDOM from 'react-dom';
import './App.css';
import JoinForm from "./JoinForm";
import CreateForm from "./CreateForm";

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
