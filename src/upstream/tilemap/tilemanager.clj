(ns upstream.tilemap.tilemanager
  (:require
    [upstream.config :as config]
    [clojure.java.io :as io]
    [upstream.utilities.images :as images]
    [upstream.utilities.spacial :as spacialutility]
    [upstream.tilemap.chunkutility :as chunkutility])
  (:gen-class))

(defn set-position
  "testing set-position fn"
  [px py tilemap]
  (let [updated-chunk-map (chunkutility/update-chunk-view (:map tilemap) px py)
        map-offset-x (- (:offset-x (:central-chunk updated-chunk-map))
                        (:chunk-dim updated-chunk-map))
        map-offset-y (- (:offset-y (:central-chunk updated-chunk-map))
                        (:chunk-dim updated-chunk-map))
        player-position-x-in-map (- px (* map-offset-x (:grid-dimension tilemap)))
        player-position-y-in-map (- py (* map-offset-y (:grid-dimension tilemap)))
        window-width (/ @config/WINDOW-WIDTH @config/COMPUTED-SCALE)
        window-height (/ @config/WINDOW-HEIGHT @config/COMPUTED-SCALE)
        grid-screen-center (spacialutility/isometric-to-cartesian-transform (list (/ window-width 2) (/ window-height 2)))]

        (merge tilemap
            {:draw-offset-x (- (first grid-screen-center) player-position-x-in-map)
             :draw-offset-y (- (second grid-screen-center) player-position-y-in-map)
             :map updated-chunk-map})))

(defn split-master-image
  "split master image into list of image maps: {:image :width :height} (1 dimensional)"
  [block-loader tileset]
  (let [x-coords (range 0 (:resource-width block-loader) (:width tileset))
        y-coords (range 0 (:resource-height block-loader) (:height tileset))]
  (flatten
      (map (fn [r] (map (fn [c]
              (merge tileset
                {:image ((:load-fn block-loader) c r (:width tileset) (:height tileset))}))
            x-coords)) y-coords))))

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
                                (doall (map (fn [loaded-resource]
                                                (update-in loaded-resource [:image]
                                                  #(images/scale-loaded-image-by-factor % @config/COMPUTED-SCALE)))
                                            (split-master-image
                                                (images/sub-image-loader (:path tileset)) tileset))))
                              tile-list)]
                    {:images all-tiles
                     :widest (reduce-map-by-factor :width all-tiles)
                     :tallest (reduce-map-by-factor :height all-tiles)}))))
                     ;misc fields
                     {:draw-offset-x 0
                      :draw-offset-y 0})) map-layers))

(defn entity-handler
  "execute handlers at correct y value"
  [handlers]
  (fn [y]
    (if (not (empty? handlers))
      (doall
        ;TODO: convert to isometric coordinates and draw in relative chunk position
        (map #(if (= y (:y %)) ((:fn %))) handlers)))))

(defn object-blocks-visible?
  "take object bounds,
  -- return fn that takes an image and checks if blocks"
  [entity-set]
  (let [entity-x (:x entity-set)
        entity-y (:y entity-set)]
  (fn [img-map x y]
    (if (= entity-set false) false
        (and (> (:origin-offset-y img-map) entity-y)
             (> entity-x x)
             (> (+ x (:width img-map)) entity-x)
             (> entity-y y)
             (> (+ y (:height img-map)) entity-y))))))

(defn render-map
  "take graphics object and current tilemap layer to render"
  [gr tilemap]
  (let [handler-set (if (:entity-handler? tilemap) (:entity-handlers tilemap) '())
        handle-at-y (entity-handler handler-set)
        tilemap-resource (:map tilemap)
        current-chunked-map (:current-map tilemap-resource)
        check-visible (object-blocks-visible? (reduce #(if (:prevent-block? %2) (reduced %2) false) false handler-set))
        grid-range (spacialutility/lateral-range (count current-chunked-map))]
            ;TODO: (handle-at-y y)
        (doall
            (map
                (fn [tile-coords]
                  (let [tile (nth (nth current-chunked-map (second tile-coords)) (first tile-coords))]
                  (if (:draw? tile)
                    (let [image-resource (nth (:images (:tiles tilemap)) (:image-index tile))
                          iso-coords (spacialutility/cartesian-to-isometric-transform
                                      (list
                                        (+ (* (first tile-coords) (:grid-dimension tilemap)) (:draw-offset-x tilemap))
                                        (+ (* (second tile-coords) (:grid-dimension tilemap)) (:draw-offset-y tilemap))))
                          width-guard (:widest (:tiles tilemap))
                          height-guard (:tallest (:tiles tilemap))
                          ;TODO: confirm that offset computed after transform is correct (might need to be separated from scale up)
                          iso-x (* (- (first iso-coords) (:origin-offset-x image-resource)) @config/COMPUTED-SCALE)
                          iso-y (* (- (second iso-coords) (:origin-offset-y image-resource)) @config/COMPUTED-SCALE)]
                            (if (and (> iso-x (* (- 0 width-guard) 3))
                                     (> iso-y (* (- 0 height-guard) 3)) ;TODO: this won't be efficient for super blocks
                                     (< iso-x (+ @config/WINDOW-WIDTH (* 3 width-guard)))
                                     (< iso-y (+ @config/WINDOW-HEIGHT (* 3 height-guard))))
                            (if (check-visible image-resource iso-x iso-y)
                                (images/draw-image-alpha
                                  (:image image-resource) gr iso-x iso-y 0.5)
                                (images/draw-image
                                  (:image image-resource) gr iso-x iso-y)))))))
                grid-range))))
