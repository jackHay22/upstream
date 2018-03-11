(ns upstream.gamestate.utils.textoptions
  (:require
    [seesaw.graphics :as sawgr]
    [seesaw.icon :as sawicon])
  (:gen-class))

(def textoptions-state (atom '()))
(def selected-option 0)

(defn register-options
  "take list of maps that include text, y location and handler fn"
  [text-option-maps]
  (reset! textoptions-state text-option-maps)) 

(defn option-select-event
  []
  (:fn (deref textoptions-state)))

(defn option-change-event
  "change selected option based on input"
  [dir]
  ;(reset! selected-option (if (= dir :up) )) ;TODO: use mod/rem correctly to change selection
  )

(defn render-options
  "display all text options"
  [gr]
    ; (let [state (deref game-state)
    ;       score (str (:score-1 state) " --- " (:score-2 state))]
    ;     (.setColor gr text-color)
    ;     (.setFont gr score-font)
    ;     (.drawString gr score 465 22))
        )
