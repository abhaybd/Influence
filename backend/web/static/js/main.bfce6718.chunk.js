(this.webpackJsonpfrontend=this.webpackJsonpfrontend||[]).push([[0],{26:function(e,t,a){e.exports=a(52)},31:function(e,t,a){},32:function(e,t,a){},52:function(e,t,a){"use strict";a.r(t);var n=a(0),l=a.n(n),r=a(23),o=a.n(r),c=a(14),s=(a(31),a(7)),i=a(8),u=a(2),m=a(10),h=a(9),p=a(1);a(32);function d(){return(d=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var a=arguments[t];for(var n in a)Object.prototype.hasOwnProperty.call(a,n)&&(e[n]=a[n])}return e}).apply(this,arguments)}function b(e,t){if(null==e)return{};var a,n,l=function(e,t){if(null==e)return{};var a,n,l={},r=Object.keys(e);for(n=0;n<r.length;n++)a=r[n],t.indexOf(a)>=0||(l[a]=e[a]);return l}(e,t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(n=0;n<r.length;n++)a=r[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(l[a]=e[a])}return l}var E=l.a.createElement("path",{d:"M0 0h24v24H0z",fill:"none"}),f=l.a.createElement("path",{d:"M11.67 3.87L9.9 2.1 0 12l9.9 9.9 1.77-1.77L3.54 12z"}),y=function(e){var t=e.svgRef,a=e.title,n=b(e,["svgRef","title"]);return l.a.createElement("svg",d({height:24,viewBox:"0 0 24 24",width:24,ref:t},n),a?l.a.createElement("title",null,a):null,E,f)},v=l.a.forwardRef((function(e,t){return l.a.createElement(y,d({svgRef:t},e))}));a.p;function g(){return(g=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var a=arguments[t];for(var n in a)Object.prototype.hasOwnProperty.call(a,n)&&(e[n]=a[n])}return e}).apply(this,arguments)}function k(e,t){if(null==e)return{};var a,n,l=function(e,t){if(null==e)return{};var a,n,l={},r=Object.keys(e);for(n=0;n<r.length;n++)a=r[n],t.indexOf(a)>=0||(l[a]=e[a]);return l}(e,t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(n=0;n<r.length;n++)a=r[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(l[a]=e[a])}return l}var S=l.a.createElement("g",null,l.a.createElement("path",{d:"M5.32,14.64h20.51V5.32v0h0.01c0-1.47,0.6-2.8,1.56-3.76c0.95-0.95,2.28-1.55,3.75-1.55V0h0h39.61h1.22l0.88,0.88 l31.29,31.41l0.87,2.09v69.2v0h-0.01c0,1.47-0.59,2.8-1.55,3.76h-0.01c-0.95,0.96-2.28,1.55-3.75,1.55v0.01h0H79.19v8.65v0h-0.01 c0,1.47-0.59,2.8-1.55,3.76h-0.01c-0.96,0.95-2.28,1.55-3.75,1.55v0.01h0H5.32h0v-0.01c-1.47,0-2.8-0.6-3.76-1.56 c-0.95-0.96-1.55-2.28-1.55-3.75H0v0V19.97v0h0.01c0-1.47,0.6-2.8,1.56-3.76c0.95-0.95,2.28-1.55,3.75-1.55L5.32,14.64L5.32,14.64 L5.32,14.64z M31.76,14.64h13.17h1.22l0.88,0.88l31.29,31.41l0.87,2.09v53.95h19.89V36.24H74.73h0v0c-1.78,0-3.39-0.74-4.56-1.94 c-1.17-1.19-1.9-2.84-1.9-4.65h0v0V5.94H31.76V14.64L31.76,14.64z M68.39,2.97h2.37l31.29,31.41v1.74H74.73 c-3.49,0-6.35-2.92-6.35-6.48V2.97L68.39,2.97z M73.26,50.88H48.91h0v0c-1.78,0-3.39-0.74-4.56-1.94c-1.17-1.19-1.9-2.84-1.9-4.65 h0v0V20.58H25.83H5.94v96.36h67.32v-8.04v-2.97V50.88L73.26,50.88z"})),O=function(e){var t=e.svgRef,a=e.title,n=k(e,["svgRef","title"]);return l.a.createElement("svg",g({id:"Layer_1",x:"0px",y:"0px",viewBox:"0 0 105.02 122.88",style:{enableBackground:"new 0 0 105.02 122.88"},xmlSpace:"preserve",ref:t},n),a?l.a.createElement("title",null,a):null,S)},C=l.a.forwardRef((function(e,t){return l.a.createElement(O,g({svgRef:t},e))})),w=(a.p,a(24)),j=function(e){Object(m.a)(a,e);var t=Object(h.a)(a);function a(e){var n;return Object(s.a)(this,a),(n=t.call(this,e)).state={players:[]},e.socket&&(n.socket=e.socket,n.onmessage=n.onmessage.bind(Object(u.a)(n)),n.socket.onmessage=n.onmessage),n}return Object(i.a)(a,[{key:"componentDidMount",value:function(){window.onbeforeunload=function(){return!0}}},{key:"componentWillUnmount",value:function(){window.onbeforeunload=null}},{key:"onmessage",value:function(e){console.log(e.data);var t=JSON.parse(e.data);"Start"===t?(this.socket.onmessage=void 0,this.props.onStart()):this.setState({players:t})}},{key:"render",value:function(){var e=function(e){var t=e.player;return l.a.createElement("tr",null,l.a.createElement("td",{colSpan:"2"},t))},t=l.a.createElement("div",{id:"centered"},l.a.createElement("p",null,"You should never see this screen! If you do, please file an issue ",l.a.createElement("a",{href:"https://github.com/abhaybd/Influence/issues"},"here!")),l.a.createElement("button",{type:"button",className:"form-button",onClick:this.props.main},"Go Back"));return this.socket&&(t=l.a.createElement("div",{id:"centered"},l.a.createElement("table",null,l.a.createElement("tbody",null,this.state.players.map((function(t,a){return l.a.createElement(e,{player:t,key:a})})),l.a.createElement("tr",null,l.a.createElement("td",{colSpan:"2"},l.a.createElement("button",{type:"button",className:"form-button",onClick:this.props.start},"Start"))),l.a.createElement("tr",null,l.a.createElement("td",null,"Code: ",l.a.createElement("span",{id:"lobby-code"},this.props.code)),l.a.createElement("td",null,l.a.createElement(w.CopyToClipboard,{text:this.props.code},l.a.createElement("button",{id:"copy-button"},l.a.createElement(C,null))))))))),t}}]),a}(l.a.Component),N=function(e){Object(m.a)(a,e);var t=Object(h.a)(a);function a(e){var n;return Object(s.a)(this,a),(n=t.call(this,e)).state={name:"",code:"",showLobby:!1},n.nameChange=n.nameChange.bind(Object(u.a)(n)),n.codeChange=n.codeChange.bind(Object(u.a)(n)),n.handleSubmit=n.handleSubmit.bind(Object(u.a)(n)),n}return Object(i.a)(a,[{key:"nameChange",value:function(e){this.setState({name:e.target.value.replace(/[^A-Za-z]/g,"")})}},{key:"codeChange",value:function(e){this.setState({code:e.target.value})}},{key:"handleSubmit",value:function(e){e.preventDefault();var t=this.state.name,a=this.state.code,n=this.props;if(a.length>0&&t.length>0){var l=this;F({type:"exists",code:a},(function(e){!0===e.content?F({type:"playerInLobby",code:a,content:t},(function(e){!1===e.content?(n.store.code=a,n.store.name=t,n.store.socket=T(t,a,(function(e){l.setState({showLobby:!0})}),(function(e){l.setState({showLobby:!1,name:""}),alert("Error: "+e.reason)}))):alert("That name is already taken! Please choose another!")})):alert("Invalid room code!")}))}}},{key:"render",value:function(){return this.state.showLobby?l.a.createElement(j,{socket:this.props.store.socket,start:this.props.start,onStart:this.props.onStart,code:this.props.store.code,main:this.props.main}):l.a.createElement("form",{onSubmit:this.handleSubmit},l.a.createElement("table",{className:"form-table"},l.a.createElement("tbody",null,l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("button",{id:"back",type:"button",onClick:this.props.main},l.a.createElement(v,null)))),l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("div",{id:"create-name"},"What's your name, traveler?"))),l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("input",{type:"text",value:this.state.name,size:"10",placeholder:"Alta\xefr",maxLength:"8",onChange:this.nameChange}))),l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("div",{id:"create-code"},"Enter Access Code"))),l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("input",{type:"text",value:this.state.code,size:"10",placeholder:"end-line-game",onChange:this.codeChange}))),l.a.createElement("tr",null,l.a.createElement("td",{colSpan:"2"},l.a.createElement("input",{type:"submit",value:"Join Lobby"}))))))}}]),a}(l.a.Component),A=function(e){Object(m.a)(a,e);var t=Object(h.a)(a);function a(e){var n;return Object(s.a)(this,a),(n=t.call(this,e)).state={name:"",showLobby:!1},n.handleChange=n.handleChange.bind(Object(u.a)(n)),n.handleSubmit=n.handleSubmit.bind(Object(u.a)(n)),n}return Object(i.a)(a,[{key:"handleChange",value:function(e){this.setState({name:e.target.value.replace(/[^A-Za-z]/g,"")})}},{key:"handleSubmit",value:function(e){if(e.preventDefault(),this.state.name.length>0){this.props.store.name=this.state.name;var t=this.props,a=this;F({type:"create"},(function(e){console.log(e),t.store.code=e.content,t.store.socket=T(t.store.name,t.store.code,(function(e){a.setState({showLobby:!0})}),(function(e){alert("Error: "+e.reason),a.setState({showLobby:!1})}))}))}}},{key:"render",value:function(){return this.state.showLobby?l.a.createElement(j,{socket:this.props.store.socket,start:this.props.start,onStart:this.props.onStart,code:this.props.store.code,main:this.props.main}):l.a.createElement("form",{onSubmit:this.handleSubmit},l.a.createElement("table",{className:"form-table"},l.a.createElement("tbody",null,l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("button",{id:"back",type:"button",onClick:this.props.main},l.a.createElement(v,null)))),l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("div",{id:"create-name"},"What's your name, traveler?"))),l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("input",{type:"text",value:this.state.name,size:"10",placeholder:"Aguilar",maxLength:"8",onChange:this.handleChange}))),l.a.createElement("tr",null,l.a.createElement("td",{colSpan:"2"},l.a.createElement("input",{type:"submit",value:"Create Lobby"}))))))}}]),a}(l.a.Component),L=a(19),x=a(13),I=a.n(x),P=function(e){Object(m.a)(a,e);var t=Object(h.a)(a);function a(e){var n,l;return Object(s.a)(this,a),(l=t.call(this,e)).onMessage=l.onMessage.bind(Object(u.a)(l)),l.onDisconnect=l.onDisconnect.bind(Object(u.a)(l)),l.getLocalPlayer=l.getLocalPlayer.bind(Object(u.a)(l)),l.state={players:null!==(n=e.players)&&void 0!==n?n:[],choices:[],message:"",log:["Started game!"],playerColorMap:null},l}return Object(i.a)(a,[{key:"componentDidMount",value:function(){if(console.log("Game mounted!"),window.onbeforeunload=function(){return!0},this.props.socket)console.log("Using an existing socket!"),this.socket=this.props.socket,this.localPlayerName=this.props.localPlayer,this.socket.onclose=this.onDisconnect;else{console.log("Making new socket and trying to join existing game...");var e=I.a.parse(this.props.location.search);this.socket=T(e.name,e.code,null,this.onDisconnect),this.localPlayerName=e.name}this.socket.onmessage=this.onMessage}},{key:"componentWillUnmount",value:function(){window.onbeforeunload=null}},{key:"onDisconnect",value:function(e){e.reason&&(alert("Unexpected disconnection from server! Error: "+e.reason),this.props.history.push("/"))}},{key:"onMessage",value:function(e){var t=e.data,a=JSON.parse(t);switch(a.type){case"update":if(null===this.state.playerColorMap){var n=this.createPlayerColorMap(a.content);this.setState({players:a.content,playerColorMap:n})}else this.setState({players:a.content});break;case"info":alert(a.content);break;case"choice":this.setState({choices:a.content,message:a.message});break;case"stopChoice":this.setState({choices:[],message:"Waiting for others..."});break;case"log":var l=this.state.log;for(l.push(a.content);l.length>5;)l.shift();this.setState({log:l});break;default:console.warn("Unrecognized data "+a.toString())}}},{key:"createPlayerColorMap",value:function(e){for(var t=["#19D2E8","#44DFB6","#77EA83","#E6D517","#E8AA14","#FF5714"],a={},n=0;n<e.length;n++)a[e[n].name]=t[n];return a}},{key:"numInfluence",value:function(e){return e.filter((function(e){return null!==e})).length}},{key:"getLocalPlayer",value:function(){var e,t=Object(L.a)(this.state.players);try{for(t.s();!(e=t.n()).done;){var a=e.value;if(a.name===this.localPlayerName)return a}}catch(n){t.e(n)}finally{t.f()}return{name:"",cards:[],coins:0}}},{key:"onChoice",value:function(e){this.setState({choices:[],message:"Waiting for others..."}),this.socket.send(JSON.stringify(e))}},{key:"render",value:function(){var e,t=this,a=function(e){var a=e.player,n=e.influence,r=e.coins,o=e.color;return l.a.createElement("div",{className:a===t.localPlayerName?"local-player-icon":"player-icon",style:{backgroundColor:o}},l.a.createElement("b",{id:"playerText"},a),l.a.createElement("br",null),l.a.createElement("span",{id:"coinText"},"Coins: ",r)," ",l.a.createElement("br",null),l.a.createElement("span",{id:"influenceText"},"Influences: ",n))},n=function(e){var a=e.choice;return l.a.createElement("button",{className:"game-button",onClick:function(){return t.onChoice(a)}},l.a.createElement("div",{className:"choice-icon"},a))},r=[],o=Object(L.a)(this.state.log);try{for(o.s();!(e=o.n()).done;){var c=e.value;r.push(c)}}catch(s){o.e(s)}finally{o.f()}for(;r.length<5;)r.push("");return l.a.createElement("div",{id:"game-div"},l.a.createElement("div",{id:"event-log"},r.map((function(e,t){return l.a.createElement("div",{key:t},e,l.a.createElement("br",null))}))),l.a.createElement("div",{className:"game-container"},this.state.players.map((function(e){return l.a.createElement(a,{key:e.name,player:e.name,coins:e.coins,influence:t.numInfluence(e.cards),color:t.state.playerColorMap[e.name]})}))),l.a.createElement("div",{className:"game-container"},this.getLocalPlayer().cards.map((function(e,t){return null===e?null:l.a.createElement("div",{className:"card-names",key:t},e)}))),l.a.createElement("div",{className:"game-container"},l.a.createElement("strong",null,this.state.message)),l.a.createElement("div",{className:"game-container"},this.state.choices.map((function(e,t){return l.a.createElement(n,{key:t,choice:e})}))))}}]),a}(l.a.Component),M=Object(p.f)(P);function R(e){return l.a.createElement("div",{id:"rules-box"},l.a.createElement("button",{id:"back",onClick:e.back},l.a.createElement(v,null)),l.a.createElement("h2",null,"Rules"),"2-6 players ",l.a.createElement("br",null),l.a.createElement("br",null),"On your turn, you may choose an action to play. The action you choose may or may not correspond to the influences that you possess. For the action that you choose, other players may potentially block or challenge it.",l.a.createElement("br",null),l.a.createElement("br",null),l.a.createElement("b",null,"Challenge:")," When a player declares an action they are declaring to the rest of the players that they have a certain influence, and any other player can challenge it. When a player is challenged, the challenged player must reveal the correct influence associated with their action. If they reveal the correct influence, the challenger player will lose an influence. However, if they fail to reveal the correct influence the challenged player will lose their incorrectly revealed influence.",l.a.createElement("br",null),l.a.createElement("br",null),l.a.createElement("b",null,"Block:"),' When the any of the actions "Foreign Aid", "Steal", and "Assassinate" are used, they can be blocked. Once again, any player can claim to have the correct influence to block. However, blocks can also be challenged by any player. If a block fails, the original action will take place.',l.a.createElement("br",null),l.a.createElement("br",null),"If a player loses all their influences, they are out of the game. The last player standing wins!",l.a.createElement("br",null),l.a.createElement("br",null),"At this time, if a player disconnects, the game must be recreated.",l.a.createElement("br",null),l.a.createElement("br",null),l.a.createElement("h3",null,"Influences"),l.a.createElement("u",{id:"captain-color"},"Captain"),l.a.createElement("br",null),l.a.createElement("span",{id:"captain-color"},"STEAL"),": Steal 2 coins from a target. Blockable by ",l.a.createElement("span",{id:"captain-color"},"Captain")," or ",l.a.createElement("span",{id:"ambassador-color"},"Ambassador"),". Can block ",l.a.createElement("span",{id:"captain-color"},"STEAL"),l.a.createElement("br",null),l.a.createElement("br",null),l.a.createElement("u",{id:"assassin-color"},"Assassin"),l.a.createElement("br",null),l.a.createElement("span",{id:"assassin-color"},"ASSASSINATE"),": Pay 3 coins to choose a target to assassinate (target loses an influence). ",l.a.createElement("span",{id:"contessa-color"},"Blockable by Contessa."),l.a.createElement("br",null),l.a.createElement("br",null),l.a.createElement("u",{id:"duke-color"},"Duke"),l.a.createElement("br",null),l.a.createElement("span",{id:"duke-color"},"TAX"),": Collect 3 coins from the treasury. Not blockable. Can block Foreign Aid.",l.a.createElement("br",null),l.a.createElement("br",null),l.a.createElement("u",{id:"ambassador-color"},"Ambassador"),l.a.createElement("br",null),l.a.createElement("span",{id:"ambassador-color"},"EXCHANGE"),": Draw 2 influences into your hand and pick any 2 influences to put back. Not blockable. Can block ",l.a.createElement("span",{id:"captain-color"},"STEAL"),l.a.createElement("br",null),l.a.createElement("br",null),l.a.createElement("u",{id:"contessa-color"},"Contessa"),l.a.createElement("br",null),l.a.createElement("span",{id:"contessa-color"},"BLOCK ASSASSINATION"),": Can block ",l.a.createElement("span",{id:"assassin-color"},"assassinations"),". Not blockable.",l.a.createElement("br",null),l.a.createElement("br",null),l.a.createElement("h3",null,"Other Actions"),"INCOME: ",l.a.createElement("br",null),"Collect 1 coins from the treasury.",l.a.createElement("br",null),l.a.createElement("br",null),"FOREIGN AID: ",l.a.createElement("br",null),"Collect 2 coins from the treasury. Blockable by ",l.a.createElement("span",{id:"duke-color"},"Duke"),".",l.a.createElement("br",null),l.a.createElement("br",null),"COUP: ",l.a.createElement("br",null),"Pay 7 coins and choose a target to lose an influence. If a player starts their turn with 10 or more coins, they must Coup. Unblockable.")}function F(e,t){var a=new XMLHttpRequest;a.open("POST","/lobby"),a.setRequestHeader("Content-Type","application/json"),a.onreadystatechange=function(){if(4===a.readyState&&200===a.status){var e=a.getResponseHeader("Content-Type");console.log(e),null!==e&&e.includes("application/json")&&(console.log(a.responseText),t(JSON.parse(a.responseText)))}};var n=JSON.stringify(e);a.send(n)}function T(e,t){var a=arguments.length>2&&void 0!==arguments[2]?arguments[2]:null,n=arguments.length>3&&void 0!==arguments[3]?arguments[3]:null,l=window.location,r="https:"===l.protocol?"wss:":"ws:",o="".concat(r,"//").concat(l.hostname,":8080/ws/join/").concat(t,"/").concat(e);console.log(o);var c=new WebSocket(o);return c.onopen=null!==a&&void 0!==a?a:function(e){console.log("Opened!")},c.onclose=null!==n&&void 0!==n?n:function(e){console.log(e),alert("The server disconnected unexpectedly! Error: "+e.reason)},c}function D(e){return l.a.createElement("table",{className:"buttons"},l.a.createElement("tbody",null,l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("button",{type:"button",className:"form-button",onClick:e.createForm},"Create Game"))),l.a.createElement("tr",null,l.a.createElement("td",null,l.a.createElement("button",{type:"button",className:"form-button",onClick:e.joinForm},"Join Game")))))}function H(e){return l.a.createElement("div",{id:"centered"},l.a.createElement("p",null,"The page you're looking for couldn't be found!"),l.a.createElement("button",{type:"button",className:"form-button",onClick:e.main},"Go Home"))}var W=function(e){Object(m.a)(a,e);var t=Object(h.a)(a);function a(e){var n;return Object(s.a)(this,a),(n=t.call(this,e)).store={},n.createForm=n.createForm.bind(Object(u.a)(n)),n.joinForm=n.joinForm.bind(Object(u.a)(n)),n.mainScreen=n.mainScreen.bind(Object(u.a)(n)),n.start=n.start.bind(Object(u.a)(n)),n.onStart=n.onStart.bind(Object(u.a)(n)),n.toggleRules=n.toggleRules.bind(Object(u.a)(n)),n.pushState=n.pushState.bind(Object(u.a)(n)),n}return Object(i.a)(a,[{key:"pushState",value:function(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{},a=arguments.length>2&&void 0!==arguments[2]?arguments[2]:{},n="?"+I.a.stringify(a);this.props.history.push({pathname:e,state:t,search:n})}},{key:"createForm",value:function(){this.pushState("/create")}},{key:"joinForm",value:function(){this.pushState("/join")}},{key:"mainScreen",value:function(){this.pushState("/")}},{key:"start",value:function(){var e=this.store.code;F({type:"numPlayers",code:e},(function(t){t.content>=2&&(console.log("Starting!"),F({type:"start",code:e},(function(){return!0})))}))}},{key:"onStart",value:function(){var e={name:this.store.name,code:this.store.code};this.pushState("/game",{},e)}},{key:"toggleRules",value:function(){var e,t=(null===(e=this.props.location.state)||void 0===e?void 0:e.showRules)||!1;this.pushState(this.props.location.pathname,{showRules:!t})}},{key:"render",value:function(){var e,t=this,a=function(){return l.a.createElement("div",{id:"header"},l.a.createElement(c.b,{to:"/",style:{textDecoration:"none"}},l.a.createElement("h1",null,"INFLUENCE"),l.a.createElement("br",null),"A Game of Deception"))},n=(null===(e=this.props.location.state)||void 0===e?void 0:e.showRules)||!1;return l.a.createElement("div",{className:"App"},l.a.createElement("header",{className:"App-header"},n?l.a.createElement(R,{back:this.toggleRules}):null,l.a.createElement("div",{id:"rules-button",onClick:this.toggleRules},l.a.createElement("u",null,n?"Hide":"Show"," Rules")),l.a.createElement(p.c,null,l.a.createElement(p.a,{exact:!0,path:"/",component:a}),l.a.createElement(p.a,{path:"/create",component:a}),l.a.createElement(p.a,{path:"/join",component:a})),l.a.createElement(p.c,null,l.a.createElement(p.a,{exact:!0,path:"/"},l.a.createElement(D,{createForm:this.createForm,joinForm:this.joinForm})),l.a.createElement(p.a,{path:"/create"},l.a.createElement(A,{store:this.store,main:this.mainScreen,start:this.start,onStart:this.onStart})),l.a.createElement(p.a,{path:"/join"},l.a.createElement(N,{store:this.store,main:this.mainScreen,start:this.start,onStart:this.onStart})),l.a.createElement(p.a,{path:"/game"},l.a.createElement(M,{players:[],socket:this.store.socket,localPlayer:this.store.name})),l.a.createElement(p.a,{path:"*",component:function(){return l.a.createElement(H,{main:t.mainScreen})}}))),l.a.createElement("div",{id:"footer"},"Made by ",l.a.createElement("a",{href:"https://www.github.com/abhaybd"},"Abhay Deshpande"),l.a.createElement("br",null),"UI design by ",l.a.createElement("a",{href:"https://www.github.com/iwangy"},"Ian Wang")))}}]),a}(l.a.Component),z=Object(p.f)(W);Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));o.a.render(l.a.createElement(l.a.StrictMode,null,l.a.createElement(c.a,null,l.a.createElement(z,null))),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then((function(e){e.unregister()})).catch((function(e){console.error(e.message)}))}},[[26,1,2]]]);
//# sourceMappingURL=main.bfce6718.chunk.js.map