(ns upstream.utilities.gpsys
  (:require [upstream.config :as config]
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.gamestate.states.levelone :as env]
            [upstream.utilities.log :as logger])
  (:gen-class))

(defn start-gp-simulation
  "start the gp simulation from server input"
  [starting-simulation-state]
  (let [performance-metrics (:performance-metrics starting-simulation-state)
        run-ttl (:run-ttl starting-simulation-state)
        entity-load (entity-manager/load-entities
                      (map merge config/LEVEL-ONE-ENTITIES (:entity-state starting-simulation-state)))
        with-metrics (map #(assoc % :performance (zipmap performance-metrics (repeat 0))) entity-load)]
        (logger/write-log "Running UpstreamGP simulation with time to live:" run-ttl)
        (map :performance (reduce #(if (> %2 0) (env/continuous-state-update %1) (reduced %1))
                with-metrics run-ttl)))) ;TODO: run-ttl not a list @REDESIGN
