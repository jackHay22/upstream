(ns upstream.engine.config
  (:gen-class))

(def WINDOW-WIDTH 1400)
(def WINDOW-HEIGHT 800)

;2x multiplier on base resolution
;TODO: should be 4?
(def TILE-WIDTH 128)
(def TILE-HEIGHT 64)

(def SERVER-LISTEN-PORT 4000)
(def SERVER-DATA-PORTS '(4001 4002 4003 4004))
