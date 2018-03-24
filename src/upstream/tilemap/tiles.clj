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
          fix-bounds (fn [dim min]
                         (cond (> dim 0)   0
                               (< dim min) min
                               :else       dim))
          tile-width (:tile-width tile-map)
          bounds-x (fix-bounds relative-x
                      (- (/ @config/WINDOW-WIDTH (:scale tile-map))
                         (* (:tiles-down tile-map) tile-width)))
          bounds-y (fix-bounds relative-y
                    (- (/ @config/WINDOW-HEIGHT (:scale tile-map))
                       (* (:tiles-across tile-map) (/ tile-width 2))))]
          ;TODO: figure out this mess
    (merge tile-map
      {:position-x bounds-x
       :position-y bounds-y
       :start-display-x (int (/ (- bounds-x) tile-width))
       :start-display-y (int (/ (- bounds-y) (/ tile-width 2)))})))

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
          :display-down (+ 2 (/ @config/WINDOW-HEIGHT (/ new-tile-width 4)))
          :tile-width new-tile-width
          :scale (/ new-tile-width original-tile-width) ;TODO: @/config/COMPUTED-SCALE
          :position-x 0
          :position-y 0
          :tiles-down 45
          :tiles-across 45 ;TODO: fix
          :start-display-x 0
          :start-display-y 0
          :map (parse-map-file tilemap-path fields)}))

(defn check-handler
  "check fo handler criteria, draw if applicable"
  [gr handler-set y1 y2]
  (if (and (> (:y handler-set) y1) (< (:y handler-set) y2))
    ((:handler gr))))

(defn render-map
  "render a tilemap/set in loaded form (as tilemap is rendered, system
    will render game entities by providing an x value to any subscribing
    systems)"
  [gr tilemap overlap-handler-set] ;handler is only necessary for l1, l2, etc... not l0
  ;overlap handler: {:y :fn}
  (let [images (:images tilemap)
        map-contents (:map tilemap)
        tile-width (:tile-width tilemap)
        start-draw-x (:start-display-x tilemap)
        start-draw-y (:start-display-y tilemap)
        offset-fn (fn [x y]
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
