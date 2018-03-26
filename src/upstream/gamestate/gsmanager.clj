(ns upstream.gamestate.gsmanager
  (:require [upstream.gamestate.states.menustate :as menu]
            [upstream.gamestate.states.levelone :as level]
            [upstream.utilities.log :as logger]
            [upstream.gamestate.states.loadstate :as loadstate])
  (:gen-class))

(def current-game-state (atom 0))
(def RUNNING (atom false))

(def STATES
  (list
    {:draw-handler #(loadstate/draw-load %)
     :update-handler #(loadstate/update-load)
     :key-press-handler #(loadstate/keypressed-load %)
     :key-release-handler #(loadstate/keyreleased-load %)
     :init-fn #(loadstate/init-load)}

    {:draw-handler #(menu/draw-menu %)
     :update-handler #(menu/update-menu)
     :key-press-handler #(menu/keypressed-menu %)
     :key-release-handler #(menu/keyreleased-menu %)
     :init-fn #(menu/init-menu)}

    {:draw-handler #(level/draw-level-one %)
     :update-handler #(level/update-level-one)
     :key-press-handler #(level/keypressed-level-one %)
     :key-release-handler #(level/keyreleased-level-one %)
     :init-fn #(level/init-level-one)}))

(defn start-subsequent-loads
  "take other init functions and load in new thread"
  [states]
    (.start (Thread. #(doseq [s states] ((:init-fn s))))))

(defn init-gsm
  "perform resource loads"
  [starting-state]
  (do
    (reset! current-game-state starting-state)
    (logger/write-log-all "Starting gamestate manager in state: " @current-game-state)
    ((:init-fn (nth STATES @current-game-state)))
    (reset! RUNNING true)
    ;TODO: this is causing a load state problem: both load state and menu state share static screen
    (start-subsequent-loads (rest STATES))))

(defn update-and-draw
  "Update and Draw the current game state"
  [gr]
  (if @RUNNING
    (let [current-state-number @current-game-state]
          (if ((:update-handler (nth STATES current-state-number)))
              ((:draw-handler (nth STATES current-state-number)) gr)
              (do
                ((:draw-handler (nth STATES (+ current-state-number 1))) gr)
                (swap! current-game-state inc))))))

(defn update-no-draw
  "update without drawing"
  []
  (if @RUNNING
      (if (not ((:update-handler (nth STATES @current-game-state))))
          (swap! current-game-state inc))))

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
