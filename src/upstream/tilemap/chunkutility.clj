(ns upstream.tilemap.chunkutility
  (:require
    [upstream.config :as config]
    [clojure.java.io :as io]
    [upstream.utilities.spacial :as spacial-utility])
  (:gen-class))

(def chunk-store (atom {}))
(def chunk-store-loaded? (atom false))
(defrecord Chunk [map offset-x offset-y])
(defn make-empty-chunk [size offset-x offset-y] (Chunk. (repeat size (repeat size {:draw? false})) offset-x offset-y))

(defn parse-map-file
  "resource path, list of keywords for storing the game map as a list of maps (i.e. '(:image :sound)
  or '(:image :sound :height :blocked?))
  returns: map of fields and :draw.  result is wrapped with :map, :tiles-across, :tiles-down"
  [path fields]
  (with-open [reader (clojure.java.io/reader (io/resource path))]
  (let [loaded-map
        (pmap (fn [line]
          (pmap (fn [sub-line]
                  (let [location-set
                          (zipmap fields (map #(Integer. %)
                            (clojure.string/split sub-line #",")))]
                  (assoc location-set :draw? (not (= -1 ((first fields) location-set))))))
        (clojure.string/split line #" ")))
      (clojure.string/split-lines (clojure.string/join "\n" (line-seq reader))))]
      {:map loaded-map
       :tiles-across (count (first loaded-map))
       :tiles-down (count loaded-map)})))

(defn tile-to-chunk
  "take tile coordinates, return chunk coordinates"
  [tx ty dim]
  (list (int (/ tx dim)) (int (/ ty dim))))

(defn get-chunk-indices-from-corner
  "get the x,y chunk indices of Chunk"
  [chunk dim]
  (map inc
  (list (/ (:offset-x chunk) dim)
        (/ (:offset-y chunk) dim))))

(defn build-map-from-center
  "take indices of center chunk, build 3 chunk x 3 chunk map
   --return tuple of simplified map and central chunk resource"
  [label center-indices]
  (let [chunk-array (map #(take 3 (drop (- (first center-indices) 1) %))
                          ;accounts for empty chunk buffer
                          (take 3 (drop (- (second center-indices) 1)
                            (:map (label @chunk-store)))))]
        (list
          (reduce into []
            (map (fn [chunk-row]
              (map #(reduce into [] %)
                    (apply map vector (map #(:map %) chunk-row))))
                  chunk-array))
          (first (first chunk-array)))))

(defn update-entity-chunk
  "take player location, update loaded chunks
   --only swap chunks if player moves out of middle chunk
   --adds 9 chunks to map"
  [entity-map-set px py]
  (let [tile-x (int (/ px (:grid-dim entity-map-set)))
        tile-y (int (/ py (:grid-dim entity-map-set)))
        chunk-dim (:chunk-dim entity-map-set)]
  (update-in entity-map-set [:current-maps]
      #(doall (map
                (fn [layer]
                    (if (and (not (empty? (:map layer)))
                             (spacial-utility/coords-equal?
                                (get-chunk-indices-from-corner (:corner-chunk layer) chunk-dim)
                                (tile-to-chunk tile-x tile-y chunk-dim))) layer
                        ;else: perform reload cycle
                        (let [new-map (build-map-from-center
                                        (:label layer)
                                        (tile-to-chunk tile-x tile-y chunk-dim))]
                              (merge layer {:map (first new-map) :corner-chunk (second new-map)})))) %)))))

(defn get-chunk-from-offset
  "returns chunk of master given offset and dim"
  [master-array chunk-dim]
  (fn [offset-x offset-y]
    (Chunk. (map #(take chunk-dim (drop offset-x %))
                (take chunk-dim (drop offset-y master-array))) offset-x offset-y)))

(defn map-to-chunks
  "take map layer, return as :label chunk-store"
  [layer]
  (let [load-map-resource (parse-map-file (:map layer) (:map-attributes layer))
        chunk-loader (get-chunk-from-offset (:map load-map-resource) (:chunk-dim layer))
        chunk-dim (:chunk-dim layer)
        tiles-across (:tiles-across load-map-resource)
        tiles-down (:tiles-down load-map-resource)]
    (hash-map :map (map (fn [y] (map
                      (fn [x] (chunk-loader x y))
                  (range 0 tiles-across chunk-dim)))
                  (range 0 tiles-down chunk-dim))
              :tiles-across tiles-across
              :tiles-down tiles-down)))

(defn prepare-map-chunks
  "take 2D array of map chunk files to be loaded dynamically to prevent system overhead
   -- loads based on starting location"
  [layers start-x start-y]
  (do
      (if (not @chunk-store-loaded?) ;only needs to be loaded once
          (do
            (reset! chunk-store (reduce (fn [resources next-layer]
                                                  (assoc resources (:label next-layer)
                                                      (map-to-chunks next-layer))) {} layers))
            (reset! chunk-store-loaded? true)))
      ;perform initial chunk load cycle
      (update-entity-chunk {:current-maps (map #(hash-map :label (:label %)
                                                          :map '()
                                                          :entity-handler? (:entity-handler? %)
                                                          :prevent-view-block? (:prevent-view-block? %)
                                                          :corner-chunk nil) layers)
                            :chunk-dim (:chunk-dim (first layers))
                            :grid-dim (:grid-dim (first layers))
                            :draw-offset-x 0
                            :draw-offset-y 0}
                            start-x start-y)))
