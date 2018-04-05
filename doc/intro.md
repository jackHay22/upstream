# Upstream Documentation

## Info
- See _README_ for general app build information.  Standard app build: ```./build.sh```.

## Getting Started
- Engine Core:
  - The core draw-loop for the game is found [here](https://github.com/jackHay22/upstream/blob/master/src/upstream/engine/gamewindow.clj).
  - The function that is called when the canvas repaints is [here](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/engine/gamewindow.clj#L24) where state is the gamestate manager namespace.  The [update-and-draw](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/gamestate/gsmanager.clj#L39) function receives a java Graphics object.
  - The Gamestate manager calls the draw/update functions for the current [state](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/gamestate/gsmanager.clj#L8), ``` @current-game-state ```.
  - Each gamestate is defined in its own namespace and uses the graphics object to draw to the screen.
  - Here is a method for [loading an image](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/utilities/images.clj#L10) which takes a resource path (i.e. ``` "menus/menu_title.png" ```).
  - Here is a method for [drawing an image](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/utilities/images.clj#L36) to a graphics object using the clojure library seesaw (Note: namespace includes [seesaw](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/src/upstream/utilities/images.clj#L3) and so does the [project file](https://github.com/jackHay22/upstream/blob/344ae8c62e00350f3b923db6651b24b75fbe9570/project.clj#L4) as a dependency).

## Technical Documentation

### Resource loading
- At initialization of the gamestate manager, the load state is first started and then subsequent states are loaded in a new thread.
```clojure
(.start (Thread. #(doseq [s states] ((:init-fn s)))))
```

### Resource Specifications
- Fullscreen resource resolution (at menu) is 350x200 (all resources are scaled based on java max window calculation)
- Pixel resolution elsewhere corresponds to ```config/TILES-ACROSS``` X ```config/ORIGINAL-TILE-width```
- Here is an example tilemap resource load:
```clojure
{:map "maps/basic_template.txt"
 :entity-handler? true                              ;optional: used by tilemap to layer in entities (only one layer should set this to true)
 :tiles (list (TileResource. "tiles/test_superblock.png" 200 292 270)
              (TileResource. "tiles/list_load_test.png" 0 64 32))
              ;TileResource record takes path, y-draw offset, tile width, tile height
 :map-attributes (list :image-index :sound)} ;attributes corresponding to map values: i.e. map: -1,1 -> {:image -1 :sound 1}
 ```
## Entities
- On calls to update, all entities provide an update map.  This is either created through keyboard input or through "decisions" introduced by the entitydecisionmanager.

## Game Saves
- When the game is saved it writes a truncated entity state map to the file: ```user.home/.upstream/<config/SAVE-FILE>```. Ex: ```/Users/jackhay/.upstream/game_saves.txt```.  If the game is loaded from a file, this truncated map will automatically be merged with the corresponding config entity preset and loaded by the entity manager.
