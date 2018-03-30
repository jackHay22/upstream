# Upstream Documentation

## Info
- See _README_ for general app build information

## Getting Started
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
- This introduces a problem with shared utility namespaces.  Both the load and menu states attempt to register static images and thus garble the load state.

### Resource Specifications
- Fullscreen resource resolution (at menu) is 350x200 (all resources are scaled based on java max window calculation)
- Pixel resolution elsewhere corresponds to ```config/TILES-ACROSS``` X ```config/ORIGINAL-TILE-width```
- Here is an example tilemap resource load:
```clojure
{:map-path "maps/basic_template.txt"
 :spacing-paradigm SPACING-STANDARD                 ;use base tile dimension of 64 (applicable for most layers)
 :render-optimization RENDER-OVERSIZED              ;render superblocks that are partially visible (applicable for layers with superblocks)
 :entity-handler? true                              ;optional: used by tilemap to layer in entities
 :tiles-data (list
                {:img "tiles/test_sheet.png"
                 :tile-width ORIGINAL-TILE-WIDTH
                 :tile-height ORIGINAL-TILE-HEIGHT}
                {:img "tiles/list_load_test.png"
                 :tile-width 64
                 :tile-height 32})
 :loaded-map-fields (list :image :sound)} ;attributes corresponding to map values: i.e. map: -1,1 -> {:image -1 :sound 1}
 ```
