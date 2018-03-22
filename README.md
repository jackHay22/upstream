![HAY](https://github.com/jackHay22/upstream/blob/master/resources/app/readme_title.png)

## Info
- Game made by Jack Hay using Clojure. Started in Dublin, Ireland in 2018.
- For more information, read the docs

## Getting started
- The core draw-loop for the game is found [here](https://github.com/jackHay22/upstream/blob/master/src/upstream/engine/gamewindow.clj).
- The function that is called when the canvas repaints is [here](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/engine/gamewindow.clj#L24) where state is the gamestate manager namespace.  The [update-and-draw](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/gamestate/gsmanager.clj#L39) function receives a java Graphics object.
- The Gamestate manager calls the draw/update functions for the current [state](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/gamestate/gsmanager.clj#L8), ``` @current-game-state ```.
- Each gamestate is defined in its own namespace and uses the graphics object to draw to the screen.
- Here is a method for [loading an image](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/utilities/images.clj#L10) which takes a resource path (i.e. ``` "menus/menu_title.png" ```).
- Here is a method for [drawing an image](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/utilities/images.clj#L36) to a graphics object using the clojure library seesaw (Note: namespace includes [seesaw](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/utilities/images.clj#L3) and so does the [project file](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/project.clj#L4) as a dependency).

## App build info
### OSX build
- To build upstream.app: ``` ./build.sh ``` (requires ``` lein ```, ``` javapackager ```)
- If build script fails to install lein, install [here](https://leiningen.org/#install).
### Linux build
- Remove ``` -Xdock:name=Upstream ``` from ``` :jvm-opts ``` in project file and make sure [lein](https://leiningen.org/#install) is installed separately from build script.
- Run ``` ./build.sh -linuxserver ``` (broken) (requires ``` lein ```, ``` docker ```)
- Note: there are potentially other problems .

### Windows build
- Not tested

## Vagrant Ubuntu VM
- Requires ``` vagrant, x11 ```.
- Comment out ``` -Xdock:name=Upstream ``` in ``` project.clj ```.
- Run: ``` vagrant up -provision```
- Once in VM, check for the following installations (potentially made through vagrant): ``` xauth ```, ``` x11-apps ```.
- Recompile with change made to ``` project.clj ```.
- Jar should now run in x11 window.
- NOTE: app stalls in load state

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
