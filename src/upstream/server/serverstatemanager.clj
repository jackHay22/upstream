(ns upstream.server.serverstatemanager
  (:require [clojure.data.json :as json]
            [upstream.server.gameserver :as server])
  (:gen-class))

(defn configure
  "take server task definition and start listening interfaces"
  [config-file]
  (let [task-records (json/read-str (slurp "path/to/file.json")
                        :key-fn keyword)]

    )
  )

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
  )

(defn merge-server-state
  "Client side: merge server state
  output with current"
  [server-state]
  ;error correct by maintaining previous if error
  )

(defn distribute-input-map
  "Client side: send out new input map"
  [input-map])
