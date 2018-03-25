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
        (map (fn [sub-line]
                  (let [location-set
                          (zipmap fields (map #(Integer. %)
                            (clojure.string/split sub-line #",")))]
                  (assoc location-set :draw? (not (= -1 ((first fields) location-set))))))
        (clojure.string/split line #" ")))
      (clojure.string/split-lines (clojure.string/join "\n" (line-seq reader))))))

(defn get-tile
  "given (loaded map), get x,y tile: {:image _ ...}"
  [loaded-map x y])

(defn set-position
    "set tile-map position: based off player loc"
    [x y tile-map]
    ;TODO: rework
    (let [relative-x (- (/ @config/WINDOW-WIDTH 2) x)
          relative-y (- (/ @config/WINDOW-HEIGHT 2) y)
          scale @config/COMPUTED-SCALE
          fix-bounds (fn [dim min]
                         (cond (> dim 0)   0
                               (< dim min) min
                               :else       dim))
          tile-width (:tile-width tile-map)
          bounds-x (fix-bounds relative-x
                      (- (/ @config/WINDOW-WIDTH scale)
                         (* (:tiles-down tile-map) tile-width)))
          bounds-y (fix-bounds relative-y
                    (- (/ @config/WINDOW-HEIGHT scale)
                       (* (:tiles-across tile-map) (/ tile-width 2))))]
          ;TODO: figure out this mess
    (merge tile-map
      {:position-x bounds-x
       :position-y bounds-y
       :start-display-x (int (/ (- bounds-x) tile-width))
       :start-display-y (int (/ (- bounds-y) (/ tile-width 2)))})))

(defn split-master
  "split master image into list of images (1 dimensional)"
  [block-loader tile-width tile-height]
  (let [x-coords (range 0 (:resource-width block-loader) tile-width)
        y-coords (range 0 (:resource-height block-loader) tile-height)]
  (flatten
      (map (fn [r]
           (map (fn [c]
              ((:load-fn block-loader) c r tile-width tile-height)) x-coords)) y-coords))))

(defn init-tile-map
  "take tilemap resource and master tilemap document"
  [tilemap-set]
    ;TODO: correct loading with mapcat
  (let [map-load (parse-map-file
                    (:map-path tilemap-set)
                    (:loaded-map-fields tilemap-set))]
  ;return transformed resource
  {:loaded-images (mapcat (fn [block]
                                (let [block-loader (images/sub-image-loader (:img block))]
                                      (split-master block-loader
                                        (:tile-width block) (:tile-height block))))
                            (:tiles-data tilemap-set))
   :tile-width (* @config/COMPUTED-SCALE (:spacing-paradigm tilemap-set))
   :position-x 0
   :position-y 0
   :start-display-x 0
   :start-display-y 0
   :tiles-down (count map-load)
   :tiles-across (count (first map-load))
   ;TODO: broken
   :display-across (+ config/TILES-ACROSS 2) ;TODO: different with a different spacing type
   ;TODO: improve
   :display-down (+ 2 (/ @config/WINDOW-HEIGHT (/ (* @config/COMPUTED-SCALE (:spacing-paradigm tilemap-set)) 4)))
   :map map-load}))

(defn check-handler
  "check fo handler criteria, draw if applicable"
  [gr handler-set y1 y2]
  (if (and (> (:y handler-set) y1) (< (:y handler-set) y2))
    ((:handler gr))))

(defn render-map
  "render a tilemap/set in loaded form (as tilemap is rendered, system
    will render game entities by providing an x value to any subscribing
    systems)"
    ;TODO:           :display-across (+ window-tiles-across 2)
              ;:display-down (+ 2 (/ @config/WINDOW-HEIGHT (/ new-tile-width 4)))
  [gr tilemap overlap-handler-set] ;handler is only necessary for l1, l2, etc... not l0
  ;overlap handler: {:y :fn}
  (let [images (:loaded-images tilemap)
        map-contents (:map tilemap)
        tile-width (:tile-width tilemap)
        start-draw-x (:start-display-x tilemap)
        start-draw-y (:start-display-y tilemap)
        offset-fn (fn [x y] ;handle alternating offset
                      (+ (* x tile-width)
                         (if (even? y)
                             (/ tile-width 2) 0)))
        ;TODO: incorporate handler, movement based on player loc
        ]
        (doseq [x (range start-draw-x (+ start-draw-x (:display-across tilemap)))
                y (range start-draw-y (+ start-draw-y (:display-down tilemap)))]

          (let [map-entry (nth (nth map-contents y) x)
                r-loc (int (+ (* y (/ tile-width 4)) (:position-y tilemap)))
                c-loc (int (+ (offset-fn x y) (:position-x tilemap)))]
            (if (:draw? map-entry)
              (images/draw-image
                (nth images (:image map-entry))
                gr c-loc r-loc))))))
