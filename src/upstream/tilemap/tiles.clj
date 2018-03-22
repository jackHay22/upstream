(ns upstream.tilemap.tiles
  (:require
    [upstream.engine.config :as config]
    [upstream.utilities.images :as images])
  (:gen-class))

(defn init-tile-map
  "take tilemap resource and master tilemap document"
  [tilemap-path tiles-master-path original-tile-width window-tiles-across]
  (let [sub-image-loader-fn (images/load-sub-image tiles-master-path)
        tiles-master-dim (images/get-image-dim tiles-master-path)
        master-tiles-across (/ (first tiles-master-dim) original-tile-width)
        master-tiles-down (/ (second tiles-master-dim) (/ original-tile-width 2))]
        ;using images/scale-loaded-image-by-width img new-width-scale after split
        ;(map
          ;#(images/scale-loaded-image-by-width % new-tile-width)
          ;(split-master
                ;sub-image-loader-fn
                ;master-tiles-across
                ;master-tiles-down
                ;config/ORIGINAL-TILE-WIDTH (remove) --> generalize to different layers
                ;config/ORIGINAL-TILE-HEIGHT (remove)
                ;))
    )
  )

(defn split-master
  "split master image into list of images (1 dimensional)"
  [load-fn tiles-across tiles-down tile-width tile-height]
  (let [x-coords (range 0 (* tiles-across tile-width) tile-width)
        y-coords (range 0 (* tiles-down tile-height) tile-height)]
  (flatten
      (map (fn [r]
           (map (fn [c]
              (load-fn c r tile-width tile-height)) x-coords)) y-coords))))
