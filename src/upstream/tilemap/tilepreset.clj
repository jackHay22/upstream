(ns upstream.tilemap.tilepreset
  (:gen-class))

(defrecord TileResource [path origin-offset-x origin-offset-y width height])

(def level-one-layer-0
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
      {:map "maps/level_1-layer_0.txt"
       :label :l0
       :entity-handler? false
       :prevent-view-block? false
       :chunk-dim 16
       :grid-dim 32
       :tiles (list (TileResource. "tiles/test_sheet.png" 32 0 64 32))
       :map-attributes (list :image-index :sound)})

(def level-one-layer-1
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
      {:map "maps/level_1-layer_1.txt"
       :label :l1
       :entity-handler? true
       :prevent-view-block? true
       :chunk-dim 16
       :grid-dim 32
       :tiles (list (TileResource. "tiles/poc_sawmill.png" 170 60 1797 1122)
                    (TileResource. "tiles/tree_set.png" 60 300 130 325))
       :map-attributes (list :image-index :blocked? :height :sound)})
