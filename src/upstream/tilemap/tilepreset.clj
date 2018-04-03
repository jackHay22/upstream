(ns upstream.tilemap.tilepreset
  (:gen-class))

(defrecord TileResource [path height-offset tile-width tile-height])

(def level-one-layer-0-preset
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
      {:map "maps/basic_template.txt"
       :tiles (list (TileResource. "tiles/test_sheet.png" 0 64 32))
       :map-attributes (list :image-index :sound)})

(def level-one-layer-1-preset
      {:map "maps/super_block_demo.txt"
       :entity-handler? true
       :tiles (list (TileResource. "tiles/test_superblock.png" 200 292 270)
                    (TileResource. "tiles/list_load_test.png" 0 64 32))
       :map-attributes (list :image-index :height :blocked?)})
