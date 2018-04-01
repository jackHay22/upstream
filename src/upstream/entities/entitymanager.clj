(ns upstream.entities.entitymanager
  (:require
    [upstream.config :as config]
    [upstream.entities.entitypreset :as entity-preset]
    [upstream.utilities.images :as images])
  (:gen-class))

(defn image-load-transform
  "take a single image map and map load all directions"
  [action-map])

(defn load-entities
  "perform resource loads on list of entities, TODO: create draw-handler for each"
  [entity-list]
  ;TODO: load decisions if added, create draw handler
  (let [states-to-load entity-preset/entity-preset-1-states
        directions entity-preset/entity-preset-1-directions
        load-image #(images/load-image-scale-by-factor % @config/COMPUTED-SCALE)]
    (map (fn [entity]
         (update-in entity [:images]
            (fn [state-map]
                (reduce (fn [all-states current-state]
                        (update-in all-states [current-state]
                                   (fn [directions-map]
                                   (reduce (fn [all-directions current-direction]
                                              (update-in all-directions [current-direction]
                                                #(doall (if (not (empty? %))
                                                            (map load-image %)))))
                                            directions-map directions))))
                        state-map states-to-load))))
          entity-list)))


(defn create-draw-handlers
  "take entity state, build a draw handler for each entity"
  [])

(defn update-entity
  "update given entity (either from decisions or player input)"
  [entity update-map]
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
  ;return entity update map
  {})

(defn entitykeyreleased
  "respond to key release"
  [key]
  ;return entity update map
  {})
