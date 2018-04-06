(ns upstream.tilemap.tilemanager
  (:require
    [upstream.config :as config]
    [clojure.java.io :as io]
    [upstream.utilities.images :as images]
    [upstream.tilemap.chunkutility :as chunkutility])
  (:gen-class))

(defn set-position
    "set tile-map position: based off player's location in map"
    [px py tilemap]
    (let [increment-width (* config/ORIGINAL-TILE-WIDTH @config/COMPUTED-SCALE) ;TODO: fix
          increment-height (/ increment-width 2)
          window-width @config/WINDOW-WIDTH
          window-height @config/WINDOW-HEIGHT
          fix-offset-at-edges (fn [computed-negative-offset max-negative-offset buffer]
                                (cond
                                    (> computed-negative-offset buffer) buffer
                                    (> max-negative-offset
                                       computed-negative-offset) max-negative-offset
                                    :else computed-negative-offset))
          initial-window-offset-x (- (/ window-width 2) px)
          initial-window-offset-y (- (/ window-height 2) py increment-width)
          tilemap-negative-offset-x (fix-offset-at-edges initial-window-offset-x
                                      (+ (- window-width
                                            (* increment-width (:tiles-across (:map tilemap))))
                                         increment-width)
                                      (- 0 increment-width))
          tilemap-negative-offset-y (fix-offset-at-edges initial-window-offset-y
                                      (+ (- window-height
                                            (* increment-height (:tiles-down (:map tilemap))))
                                         increment-height) ;TODO: change to account for layer offset
                                      (- 0 increment-height))]
          (merge tilemap
            {:map-offset-x tilemap-negative-offset-x
             :map-offset-y tilemap-negative-offset-y
             :start-display-x (- (int (/ (- tilemap-negative-offset-x) increment-width)) 1)
             :start-display-y (- (int (/ (- tilemap-negative-offset-y) increment-height)) 1)})))

(defn split-master
  "split master image into list of image maps: {:image :width :height} (1 dimensional)"
  [block-loader tile-width tile-height height-offset]
  (let [x-coords (range 0 (:resource-width block-loader) tile-width)
        y-coords (range 0 (:resource-height block-loader) tile-height)]
  (flatten
      (map (fn [r]
           (map (fn [c]
              {:image ((:load-fn block-loader) c r tile-width tile-height)
               :height-offset (* height-offset @config/COMPUTED-SCALE)
               :width tile-width
               :height tile-height}) x-coords)) y-coords))))

(defn reduce-map-by-factor
  "take list of maps and find largest of type"
  [type map-list]
  (reduce
    (fn [est next]
      (if (> (type next) est) (type next) est))
    (type (first map-list)) (rest map-list)))

(defn load-tile-maps
  "take list of tilemap layers, load all and return as list"
  [map-layers x-loc-suggestion y-loc-suggestion]
  (map
    (fn [layer]
      (update-in
        (update-in layer [:map] #(chunkutility/prepare-map-chunks % (:map-attributes layer) (:label layer)
                                    (:chunk-dimension layer)  x-loc-suggestion y-loc-suggestion))
                                      ;returns {:map :label :central-chunk} (x,y)
                                      ;(label for accessing chunk-store), map is current generated view
        [:tiles]
        (fn [tile-list]
            (if (not @config/HEADLESS-SERVER?)
                (let [all-tiles
                        (mapcat
                            (fn [tileset]
                                (doall
                                    (map (fn [loaded-resource]
                                              (update-in loaded-resource [:image]
                                                #(images/scale-loaded-image-by-factor % @config/COMPUTED-SCALE)))
                                            (split-master
                                              (images/sub-image-loader (:path tileset))
                                              (:tile-width tileset)
                                              (:tile-height tileset)
                                              (:height-offset tileset)))))
                              tile-list)]
                    {:images all-tiles
                     :widest (reduce-map-by-factor :width all-tiles)
                     :tallest (reduce-map-by-factor :height all-tiles)}))))) map-layers))

(defn entity-handler
  "execute handlers at correct y value"
  [handlers]
  (fn [y]
    (if (not (empty? handlers))
      (doall
        (map #(if (= y (:y %)) ((:fn %))) handlers)))))

(defn render-map
  "render a tilemap/set in loaded form (as tilemap is rendered, system
    will render game entities by providing an x value to any subscribing
    systems)"
  [gr tilemap]

  (let [handle-at-y (entity-handler (if (:entity-handler? tilemap) (:entity-handlers tilemap) '()))
        start-draw-x (:start-display-x tilemap)
        start-draw-y (:start-display-y tilemap)
        offset-fn (fn [x y] ;handle alternating offset
                      (+ (* x (* @config/COMPUTED-SCALE 64))
                         (if (even? y)
                             (/ (* @config/COMPUTED-SCALE 64) 2) 0)))
        range-across (range
                        (- start-draw-x (:widest (:tiles tilemap)))
                        (+ start-draw-x @config/TILES-ACROSS (:widest (:tiles tilemap))))
        range-down  (range
                          (- start-draw-y (:tallest (:tiles tilemap)))
                          (+ start-draw-y @config/TILES-DOWN (:tallest (:tiles tilemap))))]

        (doall (map (fn [y]
            (do
            (handle-at-y y)
            (doall (map (fn [x]
                (if (and
                        (and (>= x 0) (> (:tiles-across (:map tilemap)) x))
                        (and (>= y 0) (> (:tiles-down (:map tilemap)) y)))

                        (let [map-entry (nth (nth (:map (:map tilemap)) y) x)
                              ;TODO: update hardcoded 16
                              r-loc (int (+ (* y (* 16 @config/COMPUTED-SCALE)) (:map-offset-y tilemap)))
                              c-loc (int (+ (offset-fn x y) (:map-offset-x tilemap)))]
                            (do
                              (if (:draw? map-entry)
                                  (let [image-resource (nth (:images (:tiles tilemap)) (:image-index map-entry))]
                                      (images/draw-image
                                        (:image image-resource)
                                        ;:draw-height-offset: correct for tall resource
                                        gr c-loc (- r-loc (:height-offset image-resource)))))))))
                range-across))))
                range-down))))
