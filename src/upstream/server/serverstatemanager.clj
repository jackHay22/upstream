(ns upstream.server.serverstatemanager
  (:require [clojure.data.json :as json]
            [upstream.utilities.log :as log]
            [upstream.server.verifier :as verifier]
            [upstream.server.gameserver :as server])
  (:gen-class))

(def lifetime-broadcasts (atom 0))

(defn configure
  "take server task definition and start listening interfaces"
  [config-file]
  (do
    (log/write-log "registering task definition: " config-file)
    (let [task-records (json/read-str (slurp config-file)
                        :key-fn keyword)]

    )
  ))

(defn register-client-input
  "Server side: receive client
  control map and merge with system"
  [raw-input])

(defn get-client-input-buffer
  "Server side: return buffer of client
  inputs since last update call"
  []
  )

(defn distribute-state
  "Server side: clean current state
  for distribution"
  [server-state]
  (swap! lifetime-broadcasts inc)
  )

(defn merge-server-state
  "Client side: merge server state
  output with current"
  [server-state]
  ;error correct by maintaining previous if error
  )

(defn distribute-input-map
  "Client side: send out new input map"
  [input-map]
  (log/write-log "distributing input map to verified client ")
  )

(defn get-server-metrics
  [current-game-tick]
  (.start (Thread.
            #(do
                  (log/write-log "lifetime broadcasts: " @lifetime-broadcasts)
                  (log/write-log "lifetime gameticks: " current-game-tick)
                  (log/write-log "relative server performance: "
                          (* 100 (/ @lifetime-broadcasts current-game-tick)) "%")
                  (log/write-log "active users: ")
                  (log/write-log "inactive users: "))
  )))

(defn register-new-client
  [client]
  (log/write-log "registering new client: " client)
  )
