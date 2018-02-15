(ns upstream.gamestate.gsmanager (:gen-class))

;EXAMPLE gamestate format
;{:update-handler update-fn (takes gr object)
; :key-press-handler key-pressed-fn (takes key code)
; :key-release-handler key-released-fn (takes key code)
;}
;atom for current index and global list of game states

(defn update-and-draw
  "Update and Draw the current game state"
  [gr])

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
    (cond
      (= key :p) (println "test-press")))

(defn keyreleased
    "respond to keyrelease event"
    [key]
    (cond
      (= key :p) (println "test-release")))
