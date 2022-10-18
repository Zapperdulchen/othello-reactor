# Todo

- one should be able to add pieces also by clicking on pieces. at the moment it only works when clicking on empty spaces on a field 
- the rythm of reactor is not player - computer - player, but player - fissions - computer - fissions otherwise it becomes hard to understand why one has lost
- undo
- replay
- animate fissions
- smarter computer player
- multiple player reactor (1st more computer opponents, 2nd more humans)
- README.md 

# Othello TicTacToe Reactor

This is a fork of http://timothypratley.github.io/othello/

I used the base to implement the front-end to a reactor game. As a Clojure newbie I learned a lot on:

- Moving a ClojureScript project including devcards from Leiningen to clj
- Understanding a miniscule code base from someone else without knowing the involved frameworks so that I can enhance it
- Doing front-end development 

_old readme:_

## Overview

This project demonstrates extending a basic Tic Tac Toe game.
We created the rules for Othello.
Why not create your own board game?

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## License

Copyright © 2015 Timothy Pratley

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
