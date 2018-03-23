(ns upstream.gamestate.gsmanager
  (:require [upstream.gamestate.states.menustate :as menu]
            [upstream.gamestate.states.levelone :as level]
            [upstream.gamestate.states.loadstate :as loadstate])
  (:gen-class))

;atom for current index and global list of game states
(def current-game-state (atom 2))
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

;on startup
(defn init-gsm
  "perform resource loads"
  []
  (do
    ((:init-fn (nth STATES (deref current-game-state))))
    (reset! RUNNING true)))

(defn update-and-draw
  "Update and Draw the current game state"
  [gr]
  (if @RUNNING
  (let [current-state-number (deref current-game-state)]
  (if ((:update-handler (nth STATES current-state-number)))
      ((:draw-handler (nth STATES current-state-number)) gr)
      (do
        ((:init-fn (nth STATES (+ current-state-number 1))))
        ((:draw-handler (nth STATES (+ current-state-number 1))) gr)
        (swap! current-game-state inc))))))

(defn network-update
  "receive playerstate update from remote and return gamestate"
  [updated-player-state]
  ;return game state
  "{:state :test}")

(defn authenticate-user
  "get user code, return new port for game connection"
  [user-id]
  "test")

(defn keypressed
    "respond to keypress event"
    [key]
    (let [result ((:key-press-handler (nth STATES (deref current-game-state))) key)]
      (if result (reset! current-game-state result)))) ;TODO: init next state

(defn keyreleased
    "respond to keyrelease event"
    [key]
    ((:key-release-handler (nth STATES (deref current-game-state))) key))
