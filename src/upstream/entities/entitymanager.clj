(ns upstream.entities.entitymanager
  (:require
    [upstream.config :as config]
    [upstream.utilities.images :as images])
  (:gen-class))

(defn image-load-transform
  "take a single image map and map load all directions"
  [action-map])

(defn load-entities
  "perform resource loads on list of entities, create draw-handler for each"
  [])

(defn create-draw-handlers
  "take entity state, build a draw handler for each entity"
  [])

(defn update-entity
  "update given entity"
  [])

(defn draw-entity
  "draw given entity (should be used as draw handler in tilemap ns)"
  [gr e])
