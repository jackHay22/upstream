(ns upstream.gamestate.states.multiplayer
  (:require [upstream.config :as config]
            [upstream.server.serverstatemanager :as state-manager]
            [upstream.gamestate.states.levelone :as level-one-actual])
  (:gen-class))

(defn server-update
  "gsm state manager function for server"
  [state-pipeline]
  (state-manager/distribute-state
    (level-one-actual/update-entities state-pipeline false) ;TODO: merge client-input buffer with pipeline
    ))
      ;(state-manager/get-client-input-buffer))))

(defn client-update
  "gsm state manager function for multiplayer client"
  [state-pipeline]
  (do
    (state-manager/distribute-input-map
      (:control-input state-pipeline))
    ;return state from server
    (state-manager/merge-server-state state-pipeline)))

(defn continuous-state-update
  "take entity-state and return update
  state as continuous update for gp"
  [entities]
  (level-one-actual/update-entities entities false))
