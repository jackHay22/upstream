# Upstream
![HAY](https://github.com/jackHay22/upstream/blob/master/resources/menus/menu_title.png)

## Info
- Game made by Jack Hay using Clojure. Started in Dublin, Ireland in 2018.
- For more information, read the docs

## App build info
- To build upstream.app: ``` ./build.sh ``` (requires ``` lein ```, ``` javapackager ```)
- For container server mode: ``` ./build.sh -server ``` (broken) (requires ``` lein ```, ``` docker ```)
- If build script fails to install lein, install [lein](https://leiningen.org/#install).

## Docker
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
