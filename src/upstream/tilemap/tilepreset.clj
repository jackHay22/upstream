(ns upstream.tilemap.tilepreset
  (:gen-class))

(import java.awt.Color)

(defrecord TileResource [path width height occlusion-offset-x occlusion-offset-y])

(def level-one-layer-0
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
      {:map "maps/level_1-layer_0.txt"
       :label :l0
       :entity-handler? false
       :prevent-view-block? false
       :chunk-dim 20
       :grid-dim 32
       :tiles (list (TileResource. "tiles/test_sheet.png" 64 32 0 16))
       :map-attributes (list :image-index :sound)})

(def level-one-layer-1
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
      {:map "maps/level_1-layer_1.txt"
       :label :l1
       :entity-handler? true
       :prevent-view-block? true
       :chunk-dim 20
       :grid-dim 32
       :tiles (list (TileResource. "tiles/foliage/tree_set.png" 130 325 45 325) ;indices: 0-4
                    (TileResource. "tiles/foliage/bushes.png" 32 40 0 40) ;indices 5-13
                    (TileResource. "tiles/foliage/stumps.png" 64 44 0 39) ;indices 14-17
                    (TileResource. "tiles/structures/bunkhouse_front.png" 757 741 57 554)
                    (TileResource. "tiles/structures/bunkhouse_middle.png" 636 721 0 554)
                    (TileResource. "tiles/structures/bunkhouse_rear.png" 669 727 0 554)
                    (TileResource. "tiles/poc_sawmill.png" 1797 1122 170 60)
                    )
       :map-attributes (list :image-index :blocked? :height :sound)})

(def layer-1-lighting-opacity 200)
(def layer-2-lighting-opacity 100)

(def layer-1-lighting-radius (float 1000.0))
(def layer-2-lighting-radius (float 800.0))

(defn layer-1-rgb
  [x y] {:color (Color. 0 0 0 layer-1-lighting-opacity) :radius layer-1-lighting-radius})

(defn layer-2-rgb
  [x y] {:color (Color. 164 154 135 layer-2-lighting-opacity) :radius layer-1-lighting-radius})

(def lighting-preset
  {:l0 #(layer-1-rgb %1 %2)
   :l1 #(layer-2-rgb %1 %2)})
