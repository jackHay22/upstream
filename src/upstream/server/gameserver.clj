(ns upstream.server.gameserver
  (:require [upstream.utilities.log :as logger]
            [clojure.core.async :as async])
  (:gen-class))

(require '[clojure.java.io :as io])
(import '[java.net ServerSocket])

;swap to false to kill server
(def running (atom true))

;utilities
(def close-socket (fn [socket] (.close socket)))
(def socket-closed? (fn [socket] (.isClosed socket)))

; (defn persistent-connection
;   "start persistent, long-running data connection with user"
;   [port user-auth]
;   (let [socket (ServerSocket. port)]
;   (future
;     (while @running
;       ;get data, process in testbench, send back
;       (with-open [server (.accept socket)]
;       ;TODO: keep this open
;         (let [new-data (.readLine (io/reader server))
;               updated-state (manager/network-update (read-string new-data))
;               processed (pr-str updated-state)
;               writer (io/writer server)]
;               (.write writer processed)
;               (.flush writer))))) running))

(defn persistent-server
    "persistent async TCP server to communicate data connection to remote users"
    [socket handler]
      (future
        (while @running
          ;get data, process in testbench, send back
          (with-open [server (.accept socket)]
            (let [user-code (.readLine (io/reader server))
                  processed-input (pr-str (handler (read-string user-code)))
                  writer (io/writer server)]
                  (.write writer processed-input)
                  (.flush writer))))) running)

(defn start-server
    "start and accept a connection to a tcp socket server for establishing data conn"
    [port handler log-id]
    (let [socket (ServerSocket. port)]
        (logger/write-log "Serving" log-id " on port: " port)
        ;use socket to create async persistent server
        (do (persistent-server socket handler) socket)))
