(ns upstream.tilemap.tilepreset
  (:gen-class))

(import java.awt.Color)

(defrecord TileResource [path origin-offset-x origin-offset-y width height])

(def level-one-layer-0
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
      {:map "maps/level_1-layer_0.txt"
       :label :l0
       :entity-handler? false
       :prevent-view-block? false
       :chunk-dim 20
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
       :chunk-dim 20
       :grid-dim 32
       :tiles (list (TileResource. "tiles/tree_set.png" 60 300 130 325)
                    (TileResource. "tiles/poc_sawmill.png" 170 60 1797 1122))
       :map-attributes (list :image-index :blocked? :height :sound)})

(def layer-1-lighting-opacity 100)
(def layer-2-lighting-opacity 120)

(def layer-1-lighting-radius (float 800.0))
(def layer-2-lighting-radius (float 950.0))

(defn layer-1-rgb
  [x y] {:color (Color. 0 0 0 layer-1-lighting-opacity) :radius layer-1-lighting-radius})

(defn layer-2-rgb
  [x y] {:color (Color. 164 154 135 layer-2-lighting-opacity) :radius layer-1-lighting-radius})

(def lighting-preset
  {:l0 #(layer-1-rgb %1 %2)
   :l1 #(layer-2-rgb %1 %2)})
