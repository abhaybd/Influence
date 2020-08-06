import React from "react";

export default function Lobby(props) {
    const Row = ({player}) => (
        <tr>
            <td>{player}</td>
        </tr>
    );

    console.log(props);

    return (
        <table>
            <tbody>
            {props.players.map((player, i) => (<Row player={player} key={i}/>))}
            <tr>
                <td>
                    <button type="button" className="form-button" onClick={props.start}>Start</button>
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