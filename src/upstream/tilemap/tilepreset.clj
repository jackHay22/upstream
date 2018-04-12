(ns upstream.tilemap.tilepreset
  (:gen-class))

(defrecord TileResource [path origin-offset-x origin-offset-y width height])

(def level-one-layer-0-preset-chunked
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
      {:map "maps/level_1-layer_0.txt"
       :label :l0
       :chunk-dimension 16
       :grid-dimension 32
       :tiles (list (TileResource. "tiles/test_sheet.png" 32 16 64 32))
       :map-attributes (list :image-index :sound)})

; (def level-one-layer-1-preset
;       {:map "maps/upstream_level_1.txt"
;        :entity-handler? true
;        :tiles (list (TileResource. "tiles/test_superblock.png" 200 292 270)
;                     (TileResource. "tiles/list_load_test.png" 0 64 32))
;        :map-attributes (list :image-index :sound :height :blocked?)})
