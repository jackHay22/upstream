(ns upstream.engine.config
  (:gen-class))

(def WINDOW-WIDTH 1400)
(def WINDOW-HEIGHT 800)
(def WINDOW-TITLE "Upstream")

;4x multiplier on base resolution
;full screen pixel resolution is 350x200
(def TILE-WIDTH 128)
(def TILE-HEIGHT 64)

(def STARTING-STATE {:test 0})

(def SERVER-LISTEN-PORT 4000)
(def SERVER-DATA-PORTS '(4001 4002 4003 4004))
