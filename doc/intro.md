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

## Resources

![SAWMILL](https://github.com/jackHay22/upstream/blob/master/resources/tiles/poc_sawmill.png)

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
 :label :l0
 :entity-handler? false
 :prevent-view-block? false
 :chunk-dim 16
 :grid-dim 32
 :tiles (list (TileResource. "tiles/test_superblock.png" 170 60 292 270)
              (TileResource. "tiles/test_sheet.png" 32 0 64 32))
              ;TileResource record takes path, y-draw offset, tile width, tile height
 :map-attributes (list :image-index :sound)} ;attributes corresponding to map values: i.e. map: -1,1 -> {:image -1 :sound 1}
 ```
### Loaded Resource Formats:
- Tile resource (same for all individual entity chunk views)
```clojure
{:l1 {:images '({:image :height-offset :width :height} ...)
      :widest
      :tallest}
 :l2 ...}
```
- Map resource (each entity has one)
```clojure
{:current-maps '({:label :l1 :map (({:draw? & :fields} ...) ...) :central-chunk Chunk :entity-handler? false :prevent-view-block? false}
                 {:label :l2 :map (({:draw? & :fields} ...) ...) :central-chunk Chunk :entity-handler? true :prevent-view-block? true})
 :grid-dim
 :chunk-dim
 :draw-offset-x
 :draw-offset-y}
```
- Chunk store (full map in memory)
```clojure
{:l1 {:map ((Chunk.) ...) ;where Chunk record is [map offset-x offset-y]
      :tiles-across
      :tiles-down}
 :l2 {}...}
```

## Entities
- On calls to update, all entities provide an update map.  This is either created through keyboard input or through "decisions" introduced by the entitydecisionmanager.

### Entity Resource Specification
```clojure
{:images {
       :display "entities/logger_1.png" ;TODO: load as sheets rather than image lists
       :at-rest (StateImageCollection. 0 '("entities/idle_rough_n.png") '()
                                         '() '("entities/idle_rough_se.png")
                                         '("entities/idle_rough_s.png") '("entities/idle_rough_sw.png")
                                         '() '())
       :walking (StateImageCollection. 0 '() '() '() '() '() '() '() '())
       :running (StateImageCollection. 0 '() '() '() '() '() '() '() '())
       :punching (StateImageCollection. 0 '() '() '() '() '() '() '() '())
  }
  :all-states (list :at-rest :walking :running :punching)
  :all-directions (list :north :north-east
                        :east :south-east
                        :south :south-west
                        :west :north-west)
  :logical-entity-id 0
  :decisions false
  :map-resource nil
  :render-as-central false
  :position-x starting-x
  :position-y starting-y
  :draw-height-offset 20
  :draw-width-offset 20
  :facing :south-west
  :current-action :at-rest
  :run-stamina 300
}
```
### Entity Decision-making
The following specifies the entity decision code implemented in upstream.  Decisions take the form of a <predicate>-<action-category>-<action>:
```
:enemy-visible? :attack :shoot-at-closest
```
At load, predicate-actions are loaded as symbols and stored in an entities decision-list.  When it is time to make a decision the decision manager will execute the action corresponding to the first true predicate.

## Game Saves
- When the game is saved it writes a truncated entity state map to the file: ```user.home/.upstream/<config/SAVE-FILE>```. Ex: ```/Users/jackhay/.upstream/game_saves.txt```.  If the game is loaded from a file, this truncated map will automatically be merged with the corresponding config entity preset. The included resources should then be loaded in the current gamestate by the entity manager.
- By default, level one loads into a saved state and creates a new save file if none exists.  If the save is empty, it will use the configuration preset.
- Here is the code for loading from save with config/LEVEL-ONE-ENTITIES as default.  This also starts the autosaver with a reference to the entity state.
```clojure
(reset! entity-state (entity-manager/load-entities
                          (save/load-from-save config/LEVEL-ONE-ENTITIES)))
(save/start-autosaver entity-state)
```
- The current save file can be overwritten with the following call (save file is replaced with standard preset):
```clojure
(save/overwrite-save! config/LEVEL-ONE-ENTITIES)
```

## Debug Options
- Write a message to std out: ```(log/write-log msg arg1 ... argn)```
- Save a stack trace or debug message to a file in .upstream directory: ```(log/save-critical-log filename message)```
