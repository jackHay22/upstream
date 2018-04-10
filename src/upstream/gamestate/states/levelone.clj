(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.utilities.images :as images] ;remove
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.utilities.save :as save]
            [upstream.tilemap.tilemanager :as tile-manager])
  (:gen-class))

(def game-state (atom config/STARTING-GAME-STATE))
(def example-player (atom 0)) ;remove
(def tile-map-layers (atom '()))
(def player-input-map (atom {}))
(def this-x (atom 400)) ;remove
(def this-y (atom 250)) ;remove
(def entity-state (atom '()))

(defn init-level-one
  "load resources"
  []
  ;TODO: configure for server mode (TODO: check for server here rather than in each manager)
  (reset! example-player (images/load-image-scale-by-factor "entities/logger_1.png" @config/COMPUTED-SCALE))
  ;(reset! tile-map-layers
  ;        (tile-manager/load-tile-maps config/LEVEL-ONE-TILEMAPS 100 100)) ;TODO: change starting location

  (reset! entity-state (entity-manager/load-entities
                                (save/load-from-save config/LEVEL-ONE-ENTITIES)))
  (save/start-autosaver entity-state))

(defn update-via-server
  "receive state from server rather than internal"
  [server-state]
  (reset! game-state server-state))

(defn update-level-one
  "update"
  []
  ;entities: create overlap handler with subscribers?, send to tilemap at render
  (let [state @game-state
        current-x (+  @this-x 1)]
     (reset! this-x current-x)
     ; (reset! tile-map-layers
     ;   (doall (map #(tile-manager/set-position
     ;                 @this-x
     ;                 @this-y %)
     ;    @tile-map-layers)))
  true))

(defn draw-level-one
  "update and draw handler for level one"
  [gr]
  (let [temp-handler-set (list {:x 0 :y 5 :prevent-block? true :fn #(println "handler 1")} {:x 0 :y 10 :fn #(println "handler 2")})
          ;handlers have grid coords
          
        ;tilemaps (map #(if (:entity-handler? %) (assoc % :entity-handlers temp-handler-set) %) @tile-map-layers) ;get from entity manager layers
]

        ;(println tilemaps)
        ;(println "\n")
        ;(System/exit 1)
  ;(doall (map #(tile-manager/render-map gr %) tilemaps)) ;tilemaps
    ; (images/draw-image @example-player gr
    ;   (+ @this-x (:map-offset-x (first tilemaps)))
    ;   (+ @this-y (:map-offset-y (first tilemaps))))
  ))

(defn keypressed-level-one
  "key press handler for level one"
  [key]
  (cond
    (= key :r) (init-level-one) ;(remove in prod)
    (= key :s) (save/save-state @entity-state))
  (reset! player-input-map (entity-manager/entitykeypressed key @player-input-map))
  false)

(defn keyreleased-level-one
  "key release handler for level one"
  [key]
  (reset! player-input-map (entity-manager/entitykeyreleased key @player-input-map)))
