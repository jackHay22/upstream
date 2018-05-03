(ns upstream.gamestate.states.multiplayer
  (:require [upstream.config :as config]
            [upstream.server.serverstatemanager :as state-manager]
            [upstream.gamestate.states.levelone :as level-one-actual])
  (:gen-class))

(defn server-update
  "gsm state manager function for server"
  [state-pipeline]
  (state-manager/distribute-state
    (level-one-actual/update-level-one state-pipeline false) ;TODO: merge client-input buffer with pipeline
    ))
      ;(state-manager/get-client-input-buffer))))

(defn client-update
  "gsm state manager function for multiplayer client"
  [state-pipeline]
  (do
    (state-manager/distribute-input-map
      (:control-input state-pipeline))
    ;return state from server display
    (state-manager/merge-server-state state-pipeline)))
