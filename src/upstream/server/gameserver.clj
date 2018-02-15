(ns upstream.server.gameserver
  (:require [clojure.core.async :as async])
  (:require [upstream.gamestate.gsmanager :as manager])
  (:gen-class))

(require '[clojure.java.io :as io])
(import '[java.net ServerSocket])

;swap to false to kill server
(def running (atom true))

;utilities
(def close-socket (fn [socket] (.close socket)))
(def socket-closed? (fn [socket] (.isClosed socket)))

(defn persistent-connection
  "start persistent, long-running data connection with user"
  [port user-auth]
  (let [socket (ServerSocket. port)]
  (future
    (while @running
      ;get data, process in testbench, send back
      (with-open [server (.accept socket)]
      ;TODO: keep this open
        (let [new-data (.readLine (io/reader server))
              updated-state (manager/network-update (read-string new-data))
              processed (pr-str updated-state)
              writer (io/writer server)]
              (.write writer processed)
              (.flush writer))))) running))

(defn persistent-server-establish
    "persistent async TCP server to communicate data connection to remote users"
    [socket]
      (future
        (while @running
          ;get data, process in testbench, send back
          (with-open [server (.accept socket)]
            (let [user-code (.readLine (io/reader server))
                  user-auth (manager/authenticate-user (read-string user-code))
                  new-port 4001 ;allocate available port
                  processed (pr-str new-port)
                  writer (io/writer server)]
                  ;start data connection and return new port to user
                  (persistent-connection new-port user-auth)
                  (.write writer processed)
                  (.flush writer))))) running)

(defn start-welcome-server
    "start and accept a connection to a tcp socket server for establishing data conn"
    [port] (let [socket (ServerSocket. port)]
        ;use socket to create async persistent server
        (do (persistent-server-establish socket) socket)))
