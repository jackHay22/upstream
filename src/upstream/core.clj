(ns upstream.core
  (:require [upstream.engine.gamewindow :as engine]
            [upstream.engine.config :as config]
            [upstream.gamestate.gsmanager :as gsm]
            [upstream.server.gameserver :as server])
  (:gen-class))

(import java.awt.Toolkit)

(defn -main
  "main"
  [& args]
  ;(if (> (count args) 0)
  (let [screenSize (.getScreenSize (Toolkit/getDefaultToolkit))]
    (reset! config/WINDOW-WIDTH (.width screenSize))
    (reset! config/WINDOW-HEIGHT (- (.height screenSize) 100))
    (gsm/init-gsm)
    (engine/start-window config/WINDOW-TITLE)
  ;(server/start-welcome-server config/SERVER-LISTEN-PORT)
  ))
