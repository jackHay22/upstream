![HAY](https://github.com/jackHay22/upstream/blob/master/resources/app/readme_title.png)

## Info
- Game made by Jack Hay using Clojure. Started in Dublin, Ireland in 2018.
- For more information, read the docs

## Getting started
- The core draw-loop for the game is found [here](https://github.com/jackHay22/upstream/blob/master/src/upstream/engine/gamewindow.clj).
- The function that is called when the canvas repaints is [here](https://github.com/jackHay22/upstream/blob/master/src/upstream/engine/gamewindow.clj) where state is the gamestate manager namespace.  The [update-and-draw](https://github.com/jackHay22/upstream/blob/master/src/upstream/gamestate/gsmanager.clj#L39) function received a java Graphics object.
- The Gamestate manager calls the draw/update functions for the current [state](https://github.com/jackHay22/upstream/blob/master/src/upstream/gamestate/gsmanager.clj#L8), ``` @current-game-state ```.
- Each gamestate is defined in its own namespace and uses the graphics object to draw to the screen.

## App build info
- To build upstream.app: ``` ./build.sh ``` (requires ``` lein ```, ``` javapackager ```)
- For container server mode: ``` ./build.sh -server ``` (broken) (requires ``` lein ```, ``` docker ```)
- If build script fails to install lein, install [here](https://leiningen.org/#install).

## Docker
- Definitely broken
- Raspberry pi use: ``` FROM hypriot/rpi-java ```
- Note: there is currently a problem running app in container.  I am currently trying to use an X11 server for graphics.

## Resource Specifications
- Fullscreen resource resolution is 350x200 (all resources are scaled based on java max window calculation)

## TODO:
- [ ] Better resource loading at boot (optimize game loads)
- [ ] Fix resolution of menu options
- [ ] Tile engine
- [x] Better reactive scaling
- [ ] Art, art, art
- [ ] Multicast server config (and setting up game to respond to server driven state updates)
- [ ] Figure out docker X11 server
- [ ] Music from nick
- [ ] Pause menu
- [ ] Layer driven tilemap (terrain, obstacles @ height, layers that render behind player and then in front of player)
- [ ] Dynamic grass, bushes, trees
- [ ] Water (boats)
- [ ] Enemy AI (big time)
- [ ] More art

## Upstream Copyright © 2018 Jack Hay.
