(ns upstream.gamestate.gsmanager
  (:require [upstream.gamestate.states.menustate :as menu]
            [upstream.gamestate.states.levelone :as level]
            [upstream.utilities.log :as logger]
            [upstream.gamestate.states.loadstate :as loadstate])
  (:gen-class))

(def current-game-state (atom 0))
(defrecord GameState [draw-handler update-handler
                      key-press-handler key-release-handler init-handler pipeline-ref])

(defn new-state-pipeline [] (atom nil))

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
                (new-state-pipeline))])

(defn start-subsequent-loads
  "take other init functions and load in new thread"
  []
  ;TODO: broken
  (.start (Thread. #(doseq [s (rest STATES)] (doall (reset! (:pipeline-ref s) ((:init-handler s))))))))

(defn init-gsm
  "perform resource loads"
  [starting-state]
  (let [state-record (nth STATES starting-state)]
  (do
    (reset! current-game-state starting-state)
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
