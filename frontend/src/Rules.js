//Rules Dialogue 'Show Rules'
import React from "react";
import "./Rules.css";
import {ReactComponent as BackIcon} from "./back.svg";

export default function Rules(props) {
    return (
        <div id="rules-box">
            <button id="back" onClick={props.back}><BackIcon/></button>
            <h2>Rules</h2>
            2-6 players <br/><br/>

            On your turn, you may choose an action to play. The action you choose may or may not correspond to the
            influences that you possess. For the action that you choose, other players may potentially block or
            challenge it.
            <br/><br/>
            <b>Challenge:</b> When a player declares an action they are declaring to the rest of the players that they
            have
            a certain influence, and any other player can challenge it. When a player is challenged, the challenged
            player must reveal the correct influence associated with their action. If they reveal the correct
            influence, the challenger player will lose an influence. However, if they fail to reveal the correct
            influence the challenged player will lose their incorrectly revealed influence.
            <br/><br/>
            <b>Block:</b> When the any of the actions "Foreign Aid", "Steal", and "Assassinate" are used, they can be
            blocked. Once again, any player can claim to have the correct influence to block. However, blocks can
            also be challenged by any player. If a block fails, the original action will take place.
            <br/><br/>
            If a player loses all their influences, they are out of the game. The last player standing wins!
            <br/><br/>
            At this time, if a player disconnects, the game must be recreated.
            <br/><br/>
            <h3>Influences</h3>
            <u id="captain-color">Captain</u><br/>
            <span id="captain-color">STEAL</span>
            : Steal 2 coins from a target. Blockable by <span id="captain-color">Captain</span> or <span
            id="ambassador-color">Ambassador</span>. Can block <span id="captain-color">STEAL</span><br/><br/>

            <u id="assassin-color">Assassin</u><br/>
            <span id="assassin-color">ASSASSINATE</span>
            : Pay 3 coins to choose a target to assassinate (target loses an influence). <span
            id="contessa-color">Blockable by
            Contessa.</span><br/><br/>

            <u id="duke-color">Duke</u><br/>
            <span id="duke-color">TAX</span>
            : Collect 3 coins from the treasury. Not blockable. Can block Foreign Aid.<br/><br/>

            <u id="ambassador-color">Ambassador</u><br/>
            <span id="ambassador-color">EXCHANGE</span>
            : Draw 2 influences into your hand and pick any 2 influences to put back. Not blockable. Can
            block <span id="captain-color">STEAL</span><br/><br/>

            <u id="contessa-color">Contessa</u><br/>
            <span id="contessa-color">BLOCK ASSASSINATION</span>
            : Can block <span id="assassin-color">assassinations</span>. Not blockable.<br/><br/>

            <h3>Other Actions</h3>
            INCOME: <br/>Collect 1 coins from the treasury.<br/><br/>

            FOREIGN AID: <br/>Collect 2 coins from the treasury. Blockable by <span id="duke-color">Duke</span>.<br/><br/>

            COUP: <br/>Pay 7 coins and choose a target to lose an influence. If a player starts their turn with 10
            or more coins, they must Coup. Unblockable.
        </div>
    );
}
