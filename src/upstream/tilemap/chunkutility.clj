(ns upstream.tilemap.chunkutility
  (:require
    [upstream.config :as config])
  (:gen-class))

(def chunk-store (atom {}))
(defrecord (Chunk [map offset-x offset-y]))

(defn parse-map-file
  "resource path, list of keywords for storing the game map as a list of maps (i.e. '(:image :sound)
  or '(:image :sound :height :blocked?))
  returns: map of fields and :draw.  result is wrapped with :map, :tiles-across, :tiles-down"
  [path fields]
  (with-open [reader (clojure.java.io/reader (io/resource path))]
  (let [loaded-map
        (map (fn [line]
          (map (fn [sub-line]
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
  (list
    (int (/ tx dim)) (int (/ ty dim)))) ;TODO: is int cast correct?

(defn get-chunk-indices
  "get the x,y chunk indices of Chunk"
  [chunk dim]
  (list (/ (:offset-x chunk) dim)
        (/ (:offset-y chunk) dim)))

(defn coords-equal?
  "compare to x,y list pairs"
  [p1 p2]
  (and (= (first p1) (first p2))
       (= (second p1) (second p2))))

(defn get-chunk
  "take label, indices return chunk"
  [label indices]
  (nth (nth (label @chunk-store) (second indices)) (first indices)))

(defn build-map-from-center
  "take indices of center chunk, build 3 chunk x 3 chunk map
   --return tuple of simplified map and central chunk resource"
  [label center-indices]
  (let [load-from-store (label @chunk-store)
        chunk-array (map #(take 3 (drop (min (- (second center-indices) 1) 0) %))
                          (take 3 (drop (min (- (first center-indices) 1) 0)
                            load-from-store)))]
        (list
          (reduce into []
            (map (fn [chunk-row]
              (map #(reduce into [] %) (apply map vector (map #(:map %) chunk-row)))) chunk-array))
          (second (second chunk-array)))))

(defn update-chunk-view
  "take player location, update loaded chunks
   --only swap chunks if player moves out of middle chunk
   --adds 9 chunks to map"
  [current-map px py]
  (let [tile-x (/ px config/ORIGINAL-TILE-WIDTH)
        tile-y (/ py (/ config/ORIGINAL-TILE-WIDTH 2))]
  (cond ;on start
        (empty? (:map current-map)) ()
        ;current view sufficient
        (coords-equal? (get-chunk-indices (:central-chunk current-map) (:chunk-dim current-map))
                       (tile-to-chunk tile-x tile-y (:chunk-dim current-map))) current-map
        ;need to reload map with new chunk set
        :else (let [new-map (build-map-from-center
                                (:label current-map)
                                (tile-to-chunk tile-x tile-y (:chunk-dim current-map)))]
              (assoc current-map :map (first new-map)
                                 :central-chunk (second new-map))))))

(defn get-chunk-from-offset
  "returns chunk of master given offset an dim"
  [master-array chunk-dim]
  (fn [offset-x offset-y]
    (map
      #(Chunk. (take chunk-dim (drop offset-x master-array)) offset-x offset-y)
       (take chunk-dim (drop offset-y master-array)))))

(defn prepare-map-chunks
  "take 2D array of map chunk files to be loaded dynamically to prevent system overhead
   -- loads based on starting location"
  [path fields label chunk-dim start-x start-y]
  (let [master-resource (parse-map-file path fields)
        tiles-across (:tiles-across master-resource)
        tiles-down (:tiles-down master-resource)
        chunk-loader (get-chunk-from-offset master chunk-dim)
        all-chunks (map
                    (fn [x] (map
                      (fn [y] (chunk-loader x y))
                      (range 0 tiles-down (/ tiles-down chunk-dim))))
                      (range 0 tiles-across (/ tiles-across chunk-dim)))]
        (do
          ;save to chunk store with given label
          (swap! chunk-store assoc label all-chunks)
          (update-chunk-view {:map '()
                              :label [label]
                              :chunk-dim chunk-dim
                              :central-chunk nil}
                             start-x start-y))))
