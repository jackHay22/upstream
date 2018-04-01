(ns upstream.tilemap.tilepreset
  (:gen-class))

(def level-one-layer-0-preset
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
      {:map "maps/basic_template.txt"
       :tiles (list {:path "tiles/test_sheet.png"
                     :height-offset 0
                     :tile-width 64
                     :tile-height 32})
       :map-attributes (list :image-index :sound)})

(def level-one-layer-1-preset
      {:map "maps/super_block_demo.txt"
       :entity-handler? true
       :tiles (list
                       {:path "tiles/test_superblock.png"
                        :height-offset 200
                        :tile-width 292
                        :tile-height 270}
                       {:path "tiles/list_load_test.png"
                        :height-offset 0
                        :tile-width 64
                        :tile-height 32})
       :map-attributes (list :image-index :height :blocked?)})
