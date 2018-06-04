(ns upstream.tilemap.chunkutility
  (:require
    [upstream.config :as config]
    [clojure.java.io :as io]
    [upstream.utilities.spacial :as spacial-utility])
  (:gen-class))

(import java.io.RandomAccessFile)

(def chunk-store (atom {})) ;only for object layer
(def chunk-store-loaded? (atom false))
(defrecord Chunk [map offset-x offset-y])
(defn make-empty-chunk [size offset-x offset-y] (Chunk. (repeat size (repeat size {:draw? false})) offset-x offset-y))

(def dynamic-store-sublayer1 (atom nil)) ;foliage
(def dynamic-store-sublayer0 (atom nil)) ;base image
(def dynamic-layers
  (list dynamic-store-sublayer1
        dynamic-store-sublayer0))

(def byte-to-int
  (fn [b] (- b 48)))

(defn parse-map-file
  "resource path, list of keywords for storing the game map as a list of maps (i.e. '(:image :sound)
  or '(:image :sound :height :blocked?))
  returns: map of fields and :draw.  result is wrapped with :map, :tiles-across, :tiles-down"
  [path fields encoding]
  (with-open [reader (clojure.java.io/reader (io/resource path))]
  (let [loaded-map
        (pmap (fn [line]
          (pmap (fn [sub-line]
                  (let [location-set
                          (zipmap fields (map #(Integer. %)
                            (clojure.string/split sub-line #",")))]
                  (assoc location-set :draw? (not (= -1 ((first fields) location-set))))))
        (clojure.string/split line (:sub-delim-re encoding))))
      (clojure.string/split (clojure.string/join (:line-delim encoding) (line-seq reader)) (:line-delim-re encoding)))]
      {:map loaded-map
       :tiles-across (count (first loaded-map))
       :tiles-down (count loaded-map)})))

(defn read-dynamic-chunk
  "read length of bytes from offset in file"
  [path row-offset-bytes col-offset-bytes chunk-dim file-bytes-dim field]
  (let [bytes-per-row (+ file-bytes-dim 1) ;newline chars
        offsets (take chunk-dim (iterate #(+ % bytes-per-row)
                  (+ (* row-offset-bytes bytes-per-row) col-offset-bytes)))]
  (with-open [byte-reader (RandomAccessFile. (io/file (io/resource path)) "r")]
      (reduce (fn [result offset]
                (concat result (let [byte-loader (byte-array chunk-dim)]
                                    (doto byte-reader
                                        (.seek offset) (.read byte-loader))
                  (list (map #(hash-map field (byte-to-int %) :draw? true) byte-loader)))))
              '() offsets))))

(defn reload-component-chunk
  "take file, load parts to memory"
  [sublayer-ref cx cy]
  (let [sublayer @sublayer-ref
        window-radius (/ (:frame-dim sublayer) 2)
        grid-dim (:grid-dim sublayer)
        path (:path sublayer)
        updated-offset-x (max 0 (int (- (/ cx grid-dim) window-radius)))
        updated-offset-y (max 0 (int (- (/ cy grid-dim) window-radius)))
        ;updated-map (read-dynamic-chunk path )
        ]
        ;(read-row-bytes path starting-offset chunk-dim) ;repeatedly
        ;TODO: use spacial utility to get player indices based on grid dimensions
        (reset! sublayer-ref
          (merge sublayer (hash-map
            :offset-x updated-offset-x
            :offset-y updated-offset-y
            ;:map updated-map
            )))
  ;TODO: precompute bytes across and bytes down for file (better offset calculation)
  ;(read-row-bytes offset-x)
  ;starting offset: :resource-byte-width * row offset + row offset (for newline chars)
  ))

(defn dynamic-loader-check
  "check if more of the file needs to be loaded into memory"
  [px py]
  (doseq [l dynamic-layers]
           (let [dynamic-sublayer @l
                 loaded-offset-x (:offset-x dynamic-sublayer)
                 loaded-offset-y (:offset-y dynamic-sublayer)
                 loaded-grid-dim (:grid-dim dynamic-sublayer)
                 frame-dimension (:frame-dim dynamic-sublayer)]
                 ;TODO: check if the layer needs to be refreshed
                 ;if needs to be refreshed

                  ;(.start (Thread. (reload-component-chunk l px py))
               )
    ))

(defn tile-to-chunk
  "take tile coordinates, return chunk coordinates"
  [tx ty dim]
  (list (int (/ tx dim)) (int (/ ty dim))))

(defn get-chunk-indices-from-corner
  "get the x,y chunk indices of Chunk"
  [chunk dim]
  (map inc ;empty buffer
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
  (do
    (dynamic-loader-check px py)
  (update-in entity-map-set [:current-maps]
      #(doall (map
                (fn [layer]
                    (if (and (not (empty? (:map layer)))
                             (spacial-utility/coords-equal?
                                (get-chunk-indices-from-corner (:corner-chunk layer) chunk-dim)
                                (tile-to-chunk tile-x tile-y chunk-dim))) layer
                        ;else: perform reload cycle
                        (let [new-map ;(if (= (:loading-scheme layer) :static) ;TODO
                                          (build-map-from-center
                                            (:label layer)
                                            (tile-to-chunk tile-x tile-y chunk-dim))
                                            ;)
                                            ]
                              (merge layer {:map (first new-map) :corner-chunk (second new-map)})))) %))))))

(defn get-chunk-from-offset
  "returns chunk of master given offset and dim"
  [master-array chunk-dim]
  (fn [offset-x offset-y]
    (Chunk. (map #(take chunk-dim (drop offset-x %))
                (take chunk-dim (drop offset-y master-array))) offset-x offset-y)))

(defn map-to-chunks
  "take map layer, return as :label chunk-store"
  [layer]
  (if (= (:loading-scheme layer) :static)
  (let [load-map-resource (parse-map-file (:map layer) (:map-attributes layer) (:encoding layer))
        chunk-loader (get-chunk-from-offset (:map load-map-resource) (:chunk-dim layer))
        chunk-dim (:chunk-dim layer)
        tiles-across (:tiles-across load-map-resource)
        tiles-down (:tiles-down load-map-resource)]
    (hash-map :map (map (fn [y] (map
                      (fn [x] (chunk-loader x y))
                  (range 0 tiles-across chunk-dim)))
                  (range 0 tiles-down chunk-dim))
              :tiles-across tiles-across
              :tiles-down tiles-down))
    ;else requires partial loading
    ;(dynamic-file-loader (:map layer) (:map-attributes layer) (:encoding layer))
              ))

(defn load-chunk-store
  "load backing store with configured map layers"
  [layers]
  (do
    (if (not @chunk-store-loaded?)
      (reset! chunk-store (reduce (fn [resources next-layer]
                                        (assoc resources (:label next-layer)
                                            (map-to-chunks next-layer))) {} layers)))
  (reset! chunk-store-loaded? true)))

(defn prepare-map-chunks
  "take 2D array of map chunk files to be loaded dynamically to prevent system overhead
   -- loads based on starting location"
  [layers start-x start-y]
      ;perform initial chunk load cycle

      ;TODO: if a layer is dynamically loadeded, precalculate bytes across and bytes down for offset lookup in realtime
      ;TODO add resource byte width to final map

      (update-entity-chunk {:current-maps (map #(hash-map :label (:label %)
                                                          :map '()
                                                          :interpolated? (:interpolated? %)
                                                          :prevent-view-block? (:prevent-view-block? %)
                                                          :corner-chunk nil) layers)
                            :chunk-dim (:chunk-dim (first layers))
                            :grid-dim (:grid-dim (first layers))
                            :draw-offset-x 0
                            :draw-offset-y 0}
                            start-x start-y))
