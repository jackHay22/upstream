(ns upstream.tilemap.tileinterface
  (:require
    [upstream.config :as config])
  (:gen-class))

;Namespace for interacting with a loaded tilemap

(defn get-tile
  "given (loaded map), get x,y tile: {:image _ ...}"
  [loaded-map x y]
    ;TODO: involves calculating x offset if even, y based on increment
    )

(defn get-tile-attribute
  "get tile, return attribute"
  [loaded-map attrib x y]
  (attrib (get-tile loaded-map x y)))

(defn blocked?
  "check if loaded tile is blocked"
  [loaded-map x y]
  (get-tile-attribute :blocked? loaded-map x y))
