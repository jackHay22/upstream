(ns upstream.gamestate.gsmanager
  (:require [upstream.gamestate.states.menustate :as menu]
            [upstream.gamestate.states.levelone :as level]
            [upstream.utilities.log :as logger]
            [upstream.gamestate.states.loadstate :as loadstate])
  (:gen-class))

(def current-game-state (atom 0))
(defrecord GameState [draw-handler update-handler
                      key-press-handler key-release-handler init-handler])

(def STATES
  [(GameState. #(loadstate/draw-load %)
                #(loadstate/update-load)
                #(loadstate/keypressed-load %)
                #(loadstate/keyreleased-load %)
                #(loadstate/init-load))
    (GameState. #(menu/draw-menu %)
                #(menu/update-menu)
                #(menu/keypressed-menu %)
                #(menu/keyreleased-menu %)
                #(menu/init-menu))
    (GameState. #(level/draw-level-one %)
                #(level/update-level-one)
                #(level/keypressed-level-one %)
                #(level/keyreleased-level-one %)
                #(level/init-level-one))])

(defn start-subsequent-loads
  "take other init functions and load in new thread"
  []
  (.start (Thread. #(doseq [s (rest STATES)] (doall ((:init-handler s)))))))

(defn init-gsm
  "perform resource loads"
  [starting-state]
  (do
    (reset! current-game-state starting-state)
    (logger/write-log "Starting gamestate manager in state:" starting-state)
    (doall ((:init-handler (nth STATES starting-state))))))

(defn update-and-draw
  "Update and Draw the current game state"
  [gr]
  (let [current-state-number @current-game-state]
       (if ((:update-handler (nth STATES current-state-number)))
           ((:draw-handler (nth STATES current-state-number)) gr)
           (do
              ((:draw-handler (nth STATES (+ current-state-number 1))) gr)
              (swap! current-game-state inc)))))

(defn update-no-draw
  "update without drawing"
  []
  (if (not ((:update-handler (nth STATES @current-game-state))))
      (swap! current-game-state inc)))

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
