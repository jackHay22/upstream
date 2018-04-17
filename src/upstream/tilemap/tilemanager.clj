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
  [px py map-resource]
  (let [updated-chunk-map (chunkutility/update-entity-chunk map-resource px py)
        central-chunk (:central-chunk (first (:current-maps updated-chunk-map)))
        map-offset-x (- (:offset-x central-chunk)
                        (:chunk-dim updated-chunk-map))
        map-offset-y (- (:offset-y central-chunk)
                        (:chunk-dim updated-chunk-map))
        player-position-x-in-map (- px (* map-offset-x (:grid-dim map-resource)))
        player-position-y-in-map (- py (* map-offset-y (:grid-dim map-resource)))
        window-width (/ @config/WINDOW-WIDTH @config/COMPUTED-SCALE)
        window-height (/ @config/WINDOW-HEIGHT @config/COMPUTED-SCALE)
        grid-screen-center (spacialutility/isometric-to-cartesian-transform
                                  (list (/ window-width 2) (/ window-height 2)))]
        (merge updated-chunk-map
            {:draw-offset-x (int (- (first grid-screen-center) player-position-x-in-map))
             :draw-offset-y (int (- (second grid-screen-center) player-position-y-in-map))})))

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

(defn load-tile-resource
  "take list of tilemap layers, load all and return as list"
  [map-layers]
  (reduce
    (fn [loaded-tile-resource layer]
        (if (not @config/HEADLESS-SERVER?)
            (let [all-tiles (mapcat
                              (fn [tileset]
                                (doall (map (fn [loaded-resource]
                                                (update-in loaded-resource [:image]
                                                  #(images/scale-loaded-image-by-factor % @config/COMPUTED-SCALE)))
                                            (split-master-image
                                                (images/sub-image-loader (:path tileset)) tileset))))
                              (:tiles layer))]
                  (assoc loaded-tile-resource (:label layer)
                    {:images all-tiles
                     :widest (reduce-map-by-factor :width all-tiles)
                     :tallest (reduce-map-by-factor :height all-tiles)})))) {} map-layers))

(defn load-map-resource
  "load map resources for an entity"
  [map-layers x-loc-suggestion y-loc-suggestion]
  (chunkutility/prepare-map-chunks map-layers x-loc-suggestion y-loc-suggestion))

(defn entity-handler
  "execute handlers at correct y value"
  [gr handlers x y offset-x offset-y]
    (if (not (empty? handlers))
        (let [tile (reduce #(if (and (= x (:x %2)) (= y (:y %2))) (reduced %2) %1) false handlers)]
              (if tile ((:fn tile) gr offset-x offset-y)))))

(defn object-blocks-visible?
  "take object bounds,
  -- return fn that takes an image and checks if blocks"
  [entity-set]
  (if (= entity-set false)
      (fn [& args] false)
      (let [entity-x (:x entity-set)
            entity-y (:y entity-set)]
            (fn [img-map x y]
                (and (> (:origin-offset-y img-map) entity-y)
                     (> entity-x x)
                     (> (+ x (:width img-map)) entity-x)
                     (> entity-y y)
                     (> (+ y (:height img-map)) entity-y))))))

(defn image-visible?
  "take image resource and determine if it needs to be drawn"
  [x y image-resource scale]
  (let [image-height (* (:height image-resource) scale)
        image-width (* (:width image-resource) scale)
        center-visible? (fn [x y w h]
                          (or
                            (and (< x 0) (> (+ x w) @config/WINDOW-WIDTH))
                            (and (< y 0) (> (+ y h) @config/WINDOW-HEIGHT))))
        corner-visible? (fn [x y] (and (> x 0) (> y 0)
                                       (< x @config/WINDOW-WIDTH)
                                       (< y @config/WINDOW-HEIGHT)))]
        (or (center-visible? x y image-width image-height)
            (corner-visible? x y)
            (corner-visible? (+ x image-width) y)
            (corner-visible? x (+ y image-height))
            (corner-visible? (+ x image-width) (+ y image-height)))))

(defn render-layer
  "take map layer and render"
  [gr map-resource tile-resource lateral-coordinate-set blocks-visible? handlers]
  (fn [current-layer]
    (doall (map
          (fn [tile-coords]
            (let [tile (nth (nth (:map current-layer) (second tile-coords)) (first tile-coords))]
            (if (:draw? tile)
              (let [image-set ((:label current-layer) tile-resource)
                    scale @config/COMPUTED-SCALE
                    image-resource (nth (:images image-set) (:image-index tile))
                    iso-coords (spacialutility/cartesian-to-isometric-transform
                                  (list
                                    (+ (* (first tile-coords) (:grid-dim map-resource)) (:draw-offset-x map-resource))
                                    (+ (* (second tile-coords) (:grid-dim map-resource)) (:draw-offset-y map-resource))))
                    iso-x (int (Math/ceil (* (- (first iso-coords) (:origin-offset-x image-resource)) scale)))
                    iso-y (int (Math/ceil (* (- (second iso-coords) (:origin-offset-y image-resource)) scale)))] ;(int (Math/ceil
                      (if (> 32 (:origin-offset-x image-resource)) (println (:origin-offset-x image-resource)))
                      (if (:entity-handler? current-layer)
                          (entity-handler gr handlers
                                  (first tile-coords) (second tile-coords) ;TODO: make this map relative with chunk offset
                                  (:draw-offset-x map-resource) (:draw-offset-y map-resource)))
                      (if (image-visible? iso-x iso-y image-resource scale)
                          (if (and (:prevent-view-block? current-layer) (blocks-visible? image-resource iso-x iso-y))
                              (images/draw-image-alpha
                                (:image image-resource) gr iso-x iso-y 0.5)
                              (images/draw-image
                                (:image image-resource) gr iso-x iso-y)))))))
          lateral-coordinate-set))))

(defn render-map
  "take graphics object and render all map layers"
  [gr map-resource tile-resource entity-handlers]
  (let [render-map-layer (render-layer gr map-resource tile-resource
                              (spacialutility/lateral-range (* 3 (:chunk-dim map-resource))) ;TODO: doesn't need to be recomputed
                              (object-blocks-visible? (reduce #(if (:prevent-block? %2) (reduced %2) false) false entity-handlers))
                              entity-handlers)]
        (doall (map #(render-map-layer %) (:current-maps map-resource)))))
