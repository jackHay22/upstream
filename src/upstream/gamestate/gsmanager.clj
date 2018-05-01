(ns upstream.gamestate.gsmanager
  (:require [upstream.gamestate.states.menustate :as menu]
            [upstream.gamestate.states.levelone :as level]
            [upstream.config :as config]
            [upstream.utilities.gpsys :as gpsys]
            [upstream.utilities.log :as logger]
            [upstream.utilities.save :as save]
            [upstream.gamestate.states.loadstate :as loadstate])
  (:gen-class))

(defrecord GameState [draw-handler update-handler
                      key-press-handler key-release-handler init-handler pipeline-ref])

(defn new-state-pipeline [] (atom nil))

(def LOAD-STATE 0)
(def MENU-STATE 1)
(def LEVEL-STATE 2)
(def CLIENT-STATE 3)
(def SERVER-STATE 4)
(def GP-STATE 5)

(def current-game-state (atom LOAD-STATE))

(def STATES
  [(GameState. #(loadstate/draw-load %1 %2)
                #(loadstate/update-load %)
                #(loadstate/keypressed-load %)
                #(loadstate/keyreleased-load %)
                #(loadstate/init-load)
                (new-state-pipeline))
    (GameState. #(menu/draw-menu %1 %2)
                #(menu/update-menu %)
                #(menu/keypressed-menu %)
                #(menu/keyreleased-menu %)
                #(menu/init-menu)
                (new-state-pipeline))
    (GameState. #(level/draw-level-one %1 %2)
                #(level/update-level-one %)
                #(level/keypressed-level-one %)
                #(level/keyreleased-level-one %)
                #(level/init-level-one)
                (new-state-pipeline))
    (GameState. #(level/draw-level-one %1 %2) ;TODO
                #(level/update-level-one %)
                #(level/keypressed-level-one %)
                #(level/keyreleased-level-one %)
                #(level/init-level-one)
                (new-state-pipeline))
    (GameState. nil ;SERVER
                #(level/update-level-one %) ;TODO: update to accept server based inputs
                nil nil
                #(level/init-actual-state)
                (new-state-pipeline))
    (GameState. nil ;GP
                #(level/continuous-state-update %)
                nil nil
                nil ;TODO redesign start
                (new-state-pipeline))])

(defn start-subsequent-loads
  "take other init functions and load in new thread"
  []
  (.start (Thread. #(doseq [s (rest STATES)] (doall (reset! (:pipeline-ref s) ((:init-handler s))))))))

(defn init-gsm
  "perform resource loads"
  [starting-state]
  (let [state-record (nth STATES starting-state)]
  (do
    (reset! current-game-state starting-state)
     ; (if (not @config/HEADLESS-SERVER?)
     ;   (save/start-autosaver (:pipeline-ref (nth STATES 2)))) ;TODO
    (logger/write-log "Starting gamestate manager in state:" starting-state)
    (doall (reset! (:pipeline-ref state-record) ((:init-handler state-record)))))))

(defn state-draw
  "draw current state"
  [gr]
  (let [state-record (nth STATES @current-game-state)]
    ((:draw-handler state-record) gr @(:pipeline-ref state-record))))

(defn state-update
  "Update and Draw the current game state"
  []
  (let [state-record (nth STATES @current-game-state)
        state-transform ((:update-handler state-record) @(:pipeline-ref state-record))]
      (if (= state-transform nil)
          (swap! current-game-state inc)
          (reset! (:pipeline-ref state-record) state-transform))))

(defn update-no-draw
  "update without drawing"
  []
  (let [state-record (nth STATES @current-game-state)]
  (reset! (:pipeline-ref state-record)
      ((:update-handler state-record) @(:pipeline-ref state-record)))))

(defn network-update
  "receive playerstate update from remote and return gamestate"
  [updated-player-state]
  ;needs to check for valid code from user and authenticate information
  ;return game state
  "{:state :test}")

(defn authenticate-user
  "get user code, return new port for game connection"
  [user-id]
  "test")

(defn keypressed
    "respond to keypress event"
    [key]
    (let [result ((:key-press-handler (nth STATES @current-game-state)) key)]
      (if result (reset! current-game-state result))))

(defn keyreleased
    "respond to keyrelease event"
    [key]
    ((:key-release-handler (nth STATES @current-game-state)) key))
