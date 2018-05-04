(ns upstream.tilemap.tileinterface
  (:require
    [upstream.config :as config])
  (:gen-class))

(defn get-player-tiles
   "take map-resource, x y and return map of tile info and layer
   for each layer"
   [map-resource px py]
   (let [grid-dim (:grid-dim map-resource)
         corner-chunk (:corner-chunk (first (:current-maps map-resource)))
         offset-x (* grid-dim (:offset-x corner-chunk))
         offset-y (* grid-dim (:offset-y corner-chunk))
         chunk-relative-x (- px offset-x)
         chunk-relative-y (- py offset-y)
         chunk-tile-index-x (int (/ chunk-relative-x grid-dim))
         chunk-tile-index-y (int (/ chunk-relative-y grid-dim))]
         (map #(hash-map :tile (nth (nth (:map %) chunk-tile-index-y) chunk-tile-index-x)
                         :layer (:label %))
                (:current-maps map-resource))))

(defn get-tile-attribute
  "get tile, return attribute"
  [map-resource attrib layer x y]
  (attrib (reduce #(if (= layer (:layer %2)) (reduced (:tile %2)) %1)
                  nil (get-player-tiles map-resource x y))))

(defn blocked?
  "check if loaded tile is blocked"
  [map-resource x y]
  (= 1 (get-tile-attribute map-resource :blocked? :l1 x y)))

(defn try-move
  "check if player can make a move to the next tile"
  [move-fn px py speed map-resource]
  (let [proposed-move (move-fn px py speed)]
      (if (blocked? map-resource
            (first proposed-move) (second proposed-move))
            (list px py) proposed-move)))
