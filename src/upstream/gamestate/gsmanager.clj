(ns upstream.gamestate.gsmanager
  (:require [upstream.gamestate.states.menustate :as menu]
            [upstream.gamestate.states.levelone :as level]
            [upstream.gamestate.states.loadstate :as loadstate])
  (:gen-class))

;atom for current index and global list of game states

(def current-game-state (atom 0))
(def STATES
  (list {:update-handler #(menu/update-and-draw-menu %)
     :key-press-handler #(menu/keypressed-menu %)
     :key-release-handler #(menu/keyreleased-menu %)}

    {:update-handler #(loadstate/update-and-draw-load %)
     :key-press-handler #(loadstate/keypressed-load %)
     :key-release-handler #(loadstate/keyreleased-load %)}

    {:update-handler #(level/update-and-draw-level-one %)
     :key-press-handler #(level/keypressed-level-one %)
     :key-release-handler #(level/keyreleased-level-one %)}))

(defn update-and-draw
  "Update and Draw the current game state"
  [gr]
  ((:update-handler (nth STATES (deref current-game-state))) gr))

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
    ((:key-press-handler (nth STATES (deref current-game-state))) key))

(defn keyreleased
    "respond to keyrelease event"
    [key]
    ((:key-release-handler (nth STATES (deref current-game-state))) key))
