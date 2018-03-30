(ns upstream.entities.entitymanager
  (:require
    [upstream.config :as config]
    [upstream.utilities.images :as images])
  (:gen-class))
  
(defn load-entities
  "perform resource loads on list of entities"
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
