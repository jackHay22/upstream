(ns upstream.tilemap.tilemanager
  (:require
    [upstream.config :as config]
    [clojure.java.io :as io]
    [upstream.utilities.images :as images]
    [upstream.utilities.spacial :as spacialutility]
    [upstream.tilemap.chunkutility :as chunkutility])
  (:gen-class))

(defn set-position
    "set tile-map position: based off player's location in map"
    [px py tilemap]
    (let [increment-width (* config/ORIGINAL-TILE-WIDTH @config/COMPUTED-SCALE) ;TODO: fix
          increment-height (/ increment-width 2)
          window-width @config/WINDOW-WIDTH
          window-height @config/WINDOW-HEIGHT
          updated-chunked-map (chunkutility/update-chunk-view (:map tilemap) px py)
          tiles-across-master (:tiles-across updated-chunked-map)
          tiles-down-master (:tiles-down updated-chunked-map)
          current-map-view-offset-x (- (:offset-x (:central-chunk updated-chunked-map)) (:chunk-dim updated-chunked-map))
          current-map-view-offset-y (- (:offset-y (:central-chunk updated-chunked-map)) (:chunk-dim updated-chunked-map))

          fix-offset-at-edges (fn [computed-negative-offset max-negative-offset buffer]
                                (cond
                                    (> computed-negative-offset buffer) buffer
                                    (> max-negative-offset
                                       computed-negative-offset) max-negative-offset
                                    :else computed-negative-offset))

          tilemap-negative-offset-x (fix-offset-at-edges (- (/ window-width 2) px)
                                      (+ (- window-width
                                            (* increment-width (:tiles-across updated-chunked-map)))
                                         increment-width)
                                      (- 0 increment-width))
          tilemap-negative-offset-y (fix-offset-at-edges (- (/ window-height 2) py increment-width)
                                      (+ (- window-height
                                            (* increment-height (:tiles-down updated-chunked-map)))
                                         increment-height) ;TODO: change to account for layer offset
                                      (- 0 increment-height))]
          (merge tilemap
            {:map-offset-x tilemap-negative-offset-x
             :map-offset-y tilemap-negative-offset-y
             :map updated-chunked-map
             :start-display-x (- (int (/ (- tilemap-negative-offset-x) increment-width)) 1 current-map-view-offset-x) ;TODO: verify
             :start-display-y (- (int (/ (- tilemap-negative-offset-y) increment-height)) 1 current-map-view-offset-y)})))

(defn split-master-image
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
      (merge
      (update-in
        (update-in layer [:map] #(chunkutility/prepare-map-chunks % (:map-attributes layer) (:label layer)
                                    (:chunk-dimension layer)  x-loc-suggestion y-loc-suggestion))
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
                                            (split-master-image
                                              (images/sub-image-loader (:path tileset))
                                              (:tile-width tileset)
                                              (:tile-height tileset)
                                              (:height-offset tileset)))))
                              tile-list)]
                    {:images all-tiles
                     :widest (reduce-map-by-factor :width all-tiles)
                     :tallest (reduce-map-by-factor :height all-tiles)}))))
                     ;misc fields
                     {:map-offset-x 0
                      :map-offset-y 0})) map-layers))

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

                        (let [map-entry (nth (nth (:current-map (:map tilemap)) y) x)
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

(defn render-map-v2
  "take graphics object and current tilemap layer to render"
  [gr tilemap]
  (let [handle-at-y (entity-handler (if (:entity-handler? tilemap) (:entity-handlers tilemap) '()))
        tilemap-resource (:map tilemap)
        current-chunked-map (:current-map tilemap-resource)
        grid-dim (:grid-dimension tilemap)
        draw-offset-x 0 ;TODO
        draw-offset-y 0]
        ;TODO: figure out max viable offset and add guard
            ;TODO: (handle-at-y y)
          (reduce (fn [y-offset row]
              (reduce (fn [x-offset tile]
                  (do
                    (if (:draw? tile)
                        (let [image-resource (nth (:images (:tiles tilemap)) (:image-index tile))
                              iso-coords (spacialutility/cartesian-to-isometric-transform
                                                (list x-offset (- y-offset (:height-offset image-resource))))
                              iso-x (first iso-coords)
                              iso-y (second iso-coords)]
                              ;TODO: draw at 50% alpha if obscuring a player
                              ;TODO: grid dim might need to be greater
                              ;TODO: change draw location based on square image offset
                            (if (and (> iso-x (- 0 grid-dim))
                                     (> iso-y (- 0 grid-dim))
                                     (< iso-x @config/WINDOW-WIDTH)
                                     (< iso-y @config/WINDOW-HEIGHT))
                            (images/draw-image
                              (:image image-resource) gr iso-x iso-y))))
                    (+ x-offset grid-dim))) draw-offset-x row)
            (+ y-offset grid-dim))
          draw-offset-y current-chunked-map)))
