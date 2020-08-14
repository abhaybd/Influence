# Influence

A clone of the board game [Coup](https://boardgamegeek.com/boardgame/131357/coup), available to play [here](http://www.influencegame.xyz).

If you find any bugs, please create an issue!

The frontend is written in JS with React, and the backend is written in Java, running on Tomcat.

The project is being hosted on AWS Elastic Beanstalk.



## Features

- Online multiplayer 
  - Play with your friends from anywhere (2-6 players)
- Graceful disconnection/reconnection handling
  - When an active player disconnects, the game will hold and wait for them to reconnect
  - This covers brief Wi-Fi disconnections, reloading the page, etc.
- Painless lobby creation 
  - Instead of a mess of random characters, join codes are a few words, making it much easier to share
  - Instantly copy the join code to the clipboard for fast sharing
- Easy access to rules
  - A full writeup of the rules is accessible from anywhere in the game
- Implemented true to the rules
  - The rules of the game has been followed to the letter, making this a faithful Coup clone
  - If you find a discrepancy, let me know by filing an issue!
- First-come-first-serve actions
  - When it comes time to challenge or block other players, the first player to click the button will get to do so
- Intuitive user interface