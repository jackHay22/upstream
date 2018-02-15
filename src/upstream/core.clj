(ns upstream.core
  (:require [upstream.engine.gamewindow :as engine]
            [upstream.engine.config :as config]
            [upstream.server.gameserver :as server])
  (:gen-class))

(defn -main
  "main"
  [& args]
  ;(if (> (count args) 0)
  (engine/start-window config/WINDOW-WIDTH config/WINDOW-HEIGHT)
  ;(server/start-welcome-server config/SERVER-LISTEN-PORT)
  )
