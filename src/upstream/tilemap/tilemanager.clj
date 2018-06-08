(ns upstream.tilemap.tilemanager
  (:require
    [upstream.config :as config]
    [clojure.java.io :as io]
    [upstream.utilities.images :as images]
    [upstream.utilities.spacial :as spacialutility]
    [upstream.utilities.lighting :as lighting]
    [upstream.tilemap.chunkutility :as chunkutility])
  (:gen-class))

(defn set-position
  "testing set-position fn"
  [px py map-resource]
  (let [updated-chunk-map (chunkutility/update-entity-chunk map-resource px py)
        corner-chunk (:corner-chunk (first (:current-maps updated-chunk-map)))
        player-position-x-in-map (- px (* (:offset-x corner-chunk) (:grid-dim map-resource)))
        player-position-y-in-map (- py (* (:offset-y corner-chunk) (:grid-dim map-resource)))
        window-width @config/WINDOW-RESOURCE-WIDTH
        window-height @config/WINDOW-RESOURCE-HEIGHT
        ;TODO: always the same, optimize
        grid-screen-center (spacialutility/isometric-to-cartesian-transform
                                  (list (/ window-width 2) (+ (/ window-height 2) 50)))]
        (merge updated-chunk-map
            {:draw-offset-x (Math/floor (- (first grid-screen-center) player-position-x-in-map))
             :draw-offset-y (Math/floor (- (second grid-screen-center) player-position-y-in-map))})))

(defn update-chunk-view
  "update an entities chunk without computing draw offsets"
  [px py map-resource]
  (chunkutility/update-entity-chunk map-resource px py))

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
                              (fn [tileset] (split-master-image
                                                (images/sub-image-loader (:path tileset)) tileset))
                              (:tiles layer))]
                  (assoc loaded-tile-resource (:label layer)
                    {:images all-tiles
                     :widest (reduce-map-by-factor :width all-tiles)
                     :tallest (reduce-map-by-factor :height all-tiles)})))) {} map-layers))

(defn preload-map-resources!
  "load map resources into memory"
  [map-layers]
  (chunkutility/load-chunk-store map-layers))

(defn load-map-resource
  "load map resources for an entity"
  [map-layers x-loc-suggestion y-loc-suggestion all-layers?]
  (let [to-load (if all-layers? map-layers (filter #(:context-dependent? %) map-layers))]
    (chunkutility/prepare-map-chunks to-load x-loc-suggestion y-loc-suggestion)))

(defn entity-handler
  "execute handlers at correct y value"
  [gr handlers x y offset-x offset-y]
    (if (not (empty? handlers))
        (let [tile (reduce #(if (and (= x (:x %2)) (= y (:y %2))) (reduced %2) %1) false handlers)]
              (if tile ((:fn tile) gr offset-x offset-y)))))

(defn object-blocks-visible?
  "take object bounds,
  -- return fn that takes an image and checks if blocks"
  [entity-set offset-x offset-y grid-dim]
  (if (= entity-set false)
      (constantly false)
      (let [iso-indices (spacialutility/cartesian-to-isometric-transform
                            (list (+ (* (:x entity-set) grid-dim) offset-x)
                                  (+ (* (:y entity-set) grid-dim) offset-y)))
            entity-x (first iso-indices)
            entity-y (second iso-indices)]
            (fn [img-map x y]
                (and
                  (> entity-x x)
                  (> entity-y y)
                  (> (+ x (:width img-map)) entity-x)
                  (> (+ y (:height img-map)) entity-y))))))

(defn image-visible?
  "take image resource and determine if it needs to be drawn"
  [x y image-resource]
  (let [image-height (:height image-resource)
        image-width (:width image-resource)
        window-width @config/WINDOW-RESOURCE-WIDTH
        window-height @config/WINDOW-RESOURCE-HEIGHT
        center-visible? (fn [x y w h]
                          (or
                            (and (< x 0) (> (+ x w) window-width))
                            (and (< y 0) (> (+ y h) window-height))))
        corner-visible? (fn [pt] (and (> (first pt) 0) (> (second pt) 0)
                                      (< (first pt) window-width)
                                      (< (second pt) window-height)))]
                                       ;TODO: optimize
        (or (center-visible? x y image-width image-height)
            (reduce #(or %1 %2) (map corner-visible?
                      (spacialutility/get-bounds
                        (list x y) image-width image-height))))))

(defn render-interpolated-layers
  "take multiple maps and interpolate with entity-handling"
  [gr]
  ;TODO
  )

;TODO: each row should be a list
(defn render-layer
  "take map layer and render"
  [gr map-resource tile-resource lateral-coordinate-set blocks-visible? handlers]
  (fn [current-layer]
    (doall (map
          (fn [tile-coords]
            (if (= tile-coords :render-lighting)
                (lighting/render-lighting gr
                  (/ @config/WINDOW-RESOURCE-WIDTH 2)
                  (/ @config/WINDOW-RESOURCE-HEIGHT 2) (:label current-layer))
            (let [tile (nth (nth (:map current-layer) (second tile-coords)) (first tile-coords))]
                  ;layer-brightness (nth tile-coords 2)]
            (if (:draw? tile)
              (let [image-set ((:label current-layer) tile-resource)
                    image-resource (nth (:images image-set) (:image-index tile))
                    iso-coords (spacialutility/cartesian-to-isometric-transform
                                  (list
                                    (+ (* (first tile-coords) (:grid-dim map-resource)) (:draw-offset-x map-resource))
                                    (+ (* (second tile-coords) (:grid-dim map-resource)) (:draw-offset-y map-resource))))
                    iso-x (- (int (first iso-coords)) (:occlusion-offset-x image-resource)) ;TODO occlusion offset
                    iso-y (- (int (second iso-coords)) (:occlusion-offset-y image-resource))]
                    (if (image-visible? iso-x iso-y image-resource)
                        (if (and (:prevent-view-block? current-layer) (blocks-visible? image-resource iso-x iso-y))
                            (images/draw-image-alpha
                                (:image image-resource) gr iso-x iso-y 0.5)
                            (images/draw-image
                                (:image image-resource) gr iso-x iso-y)
                                ))))
              (if (:interpolated? current-layer)
                  (entity-handler gr handlers
                      (first tile-coords) (second tile-coords)
                      (:draw-offset-x map-resource) (:draw-offset-y map-resource))))))
          lateral-coordinate-set))))

(defn render-map
  "take graphics object and render all map layers"
  [gr map-resource tile-resource entity-handlers]
  (let [render-map-layer (render-layer gr map-resource tile-resource
                              (concat (spacialutility/lateral-range-cached (* 3 (:chunk-dim map-resource))) '(:render-lighting))
                              (object-blocks-visible? (reduce #(if (:prevent-block? %2) (reduced %2) false) false entity-handlers)
                                                      (:draw-offset-x map-resource) (:draw-offset-y map-resource)
                                                      (:grid-dim map-resource))
                              entity-handlers)]
        (doall (map #(render-map-layer %) (:current-maps map-resource)))))
