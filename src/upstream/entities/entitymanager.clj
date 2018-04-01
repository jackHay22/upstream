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
  "update given entity (either from decisions or player input)"
  [entity & input]
  (let [facing (:facing entity)
        action (:current-action entity)
        px (:position-x entity)
        py (:position-y entity)]
    )
  )

(defn draw-entity
  "draw given entity (should be used as draw handler in tilemap ns)"
  [gr e]
  (let [action-set ((:current-action e) (:images e))
        current-image (nth ((:facing e) action-set)
                           (:current-frame-index action-set))]
    (images/draw-image
      current-image gr
      (:position-x e)
      (- (:position-y e) (:draw-height-offset e)))))

(defn entitykeypressed
  "respond to key press"
  [key]
  {})

(defn entitykeyreleased
  "respond to key release"
  [key]
  {})
