(ns upstream.tilemap.tiles
  (:require
    [upstream.engine.config :as config]
    [clojure.java.io :as io]
    [upstream.utilities.images :as images])
  (:gen-class))

(defn parse-map-file
  "resource path, list of keywords for storing the game map as a list of maps (i.e. '(:image :sound)
  or '(:image :sound :height :blocked?))"
  [path fields]
  (with-open [reader (clojure.java.io/reader (io/resource path))]
    (map (fn [line]
        (map
          (fn [sub-line] (zipmap fields (map #(Integer. %)
                 (clojure.string/split sub-line #","))))
        (clojure.string/split line #" ")))
      (clojure.string/split-lines (clojure.string/join "\n" (line-seq reader))))))

(defn get-tile
  "given (loaded map), get x,y tile"
  [loaded-map x y])

(defn split-master
  "split master image into list of images (1 dimensional)"
  [load-fn tiles-across tiles-down tile-width tile-height]
  (let [x-coords (range 0 (* tiles-across tile-width) tile-width)
        y-coords (range 0 (* tiles-down tile-height) tile-height)]
  (flatten
      (map (fn [r]
           (map (fn [c]
              (load-fn c r tile-width tile-height)) x-coords)) y-coords))))

(defn init-tile-map
  "take tilemap resource and master tilemap document"
  [tilemap-path tiles-master-path original-tile-width window-tiles-across & fields]
  (let [sub-image-loader-fn (images/load-sub-image tiles-master-path)
        tiles-master-dim (images/get-image-dim tiles-master-path)
        master-tiles-across (/ (first tiles-master-dim) original-tile-width)
        master-tiles-down (/ (second tiles-master-dim) (/ original-tile-width 2))
        new-tile-width (/ @config/WINDOW-WIDTH window-tiles-across)]
        {:images (map #(images/scale-loaded-image-by-width % new-tile-width)
                       (split-master
                         sub-image-loader-fn
                         master-tiles-across
                         master-tiles-down
                         original-tile-width
                         (/ original-tile-width 2)))
          :display-across (+ window-tiles-across 2)
          :display-down (+ 2 (/ @config/WINDOW-HEIGHT (/ 2 new-tile-width)))
          :tile-width new-tile-width
          :map (parse-map-file tilemap-path fields)}))

(defn render-map
  "render a tilemap/set in loaded form (as tilemap is rendered, system
    will render game entities by providing an x value to any subscribing
    systems)"
  [gr tilemap overlap-handler] ;handler is only necessary for l1, l2, etc... not l0
  (let [images (:images tilemap)
        map-contents (:map tilemap)
        start-draw-x 0 ;hardcoded for testing
        start-draw-y 0 ;hardcoded for testing
        ;TODO: incorporate handler, movement based on player loc
        ]
        (doseq [x (range start-draw-x (:display-across tilemap))]
          (let [map-entry (nth (first map-contents) x)]
            (images/draw-image
              (nth images (:image map-entry))
               gr (* x (:tile-width tilemap)) 0)
          )
        ))
  )

  ;(if (= x 10) (fn) )
