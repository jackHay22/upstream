(ns upstream.tilemap.tileinterface
  (:require
    [upstream.config :as config]
    [upstream.utilities.spacial :as spacialutility])
  (:gen-class))

(defn get-player-tiles
   "take map-resource, x y and return map of tile info and layer
   for each layer"
   [map-resource pt]
   (let [grid-dim (:grid-dim map-resource)
         corner-chunk (:corner-chunk (first (:current-maps map-resource)))
         chunk-relative-pt (spacialutility/map-relative-to-chunk-relative
                               (first pt) (second pt)
                               (:offset-x corner-chunk) (:offset-y corner-chunk)
                               grid-dim)
         tile-location-pt (spacialutility/pt-to-grid chunk-relative-pt grid-dim)]
         (map #(hash-map :tile (nth (nth (:map %) (second tile-location-pt)) (first tile-location-pt))
                         :layer (:label %))
                (:current-maps map-resource))))

(defn get-tile-attribute
  "get tile, return attribute"
  [map-resource attrib layer pt]
  (attrib (reduce #(if (= layer (:layer %2)) (reduced (:tile %2)) %1)
                  nil (get-player-tiles map-resource pt))))

(defn blocked?
  "check if loaded tile is blocked"
  [map-resource pt collider]
    (reduce or
      (map #(= 1 (get-tile-attribute map-resource :blocked? :l1 %))
          (spacialutility/get-isometric-bounds pt collider))))

(defn try-move
  "check if player can make a move to the next tile"
  [move-fn px py collision-diameter speed map-resource]
  (let [proposed-move (move-fn px py speed)]
      (if (blocked? map-resource
            proposed-move collision-diameter)
            (list px py) proposed-move)))
