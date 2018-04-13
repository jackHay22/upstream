(ns upstream.tilemap.tilepreset
  (:gen-class))

(defrecord TileResource [path origin-offset-x origin-offset-y width height])

(def level-one-layer-0-preset-chunked
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
      {:map "maps/level_1-layer_0.txt"
       :label :l0
       :chunk-dimension 8
       :grid-dimension 32
       :tiles (list (TileResource. "tiles/test_sheet.png" 32 16 64 32))
       :map-attributes (list :image-index :sound)})
