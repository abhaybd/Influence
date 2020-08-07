import React from "react";
import {ReactComponent as BackIcon} from "./back.svg";

export default function Rules(props) {
    return (
        <div id="rules-box">
            <button id="back" onClick={props.back}><BackIcon/></button>
            <h2>Rules</h2>
            <b>2-6 players</b> <br/><br/>

            On your turn, you may choose an action to play. The action you choose may or may not correspond to the
            influences that you possess. For the action that you choose, other players may potentially block or
            challenge it.
            <br/><br/>
            Challenge: When a player declares an action they are declaring to the rest of the players that they have
            a certain influence, and any other player can challenge it. When a player is challenged, the challenged
            player must reveal the correct influence associated with their action. If they reveal the correct
            influence, the challenger player will lose an influence. However, if they fail to reveal the correct
            influence the challenged player will lose their incorrectly revealed influence.
            <br/><br/>
            Block: When the any of the actions "Foreign Aid", "Steal", and "Assasinate" are used, they can be
            blocked. Once again, any player can claim to have the correct influence to block. However, blocks can
            also be challenged by any player. If a block fails, the original action will take place.
            <br/><br/>
            If a player loses all their influences, they are out of the game. The last player standing wins!
            <br/><br/>
            At this time, if a player disconnects, the game must be recreated.
            <br/><br/>
            <h3>Influences</h3>
            Captain<br/>
            STEAL: Steal 2 coins from a target. Blockable by Captain or Ambassador. Can block STEAL<br/><br/>

            Assassin<br/>
            ASSASSINATE: Pay 3 coins to choose a target to assassinate (target loses an influence). Blockable by
            Contessa.<br/><br/>

            Duke<br/>
            TAX: Collect 3 coins from the treasury. Not blockable. Can block Foreign Aid.<br/><br/>

            Ambassador<br/>
            EXCHANGE: Draw 2 influences into your hand and pick any 2 influences to put back. Not blockable. Can
            block STEAL<br/><br/>

            Contessa<br/>
            BLOCK ASSASSINATION: Can block assassinations. Not blockable.<br/><br/>

            <h3>Other Actions</h3>
            INCOME: <br/>Collect 1 coins from the treasury.<br/><br/>

            FOREIGN AID: <br/>Collect 2 coins from the treasury. Blockable by Duke.<br/><br/>

            COUP: <br/>Pay 7 coins and choose a target to lose an influence. If a player starts their turn with 10
            or more coins, they must Coup. Unblockable.
        </div>
    );
}