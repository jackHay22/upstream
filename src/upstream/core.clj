(ns upstream.core
  (:require [upstream.engine.gamewindow :as engine]
            [upstream.engine.config :as config]
            [upstream.gamestate.gsmanager :as gsm]
            [upstream.server.gameserver :as server])
  (:gen-class))

(import java.awt.Toolkit)

(defn -main
  "entrypoint"
  [& args]
  ;(if (> (count args) 0)
  
  ;potential opengl performance acceleration
  (System/setProperty "sun.java2d.opengl" "true")

  (let [screenSize (.getScreenSize (Toolkit/getDefaultToolkit))]
    (reset! config/WINDOW-WIDTH (.width screenSize))
    (reset! config/WINDOW-HEIGHT (- (.height screenSize) config/HEIGHT-BUFFER))
    (gsm/init-gsm)
    (engine/start-window config/WINDOW-TITLE)
  ;(server/start-welcome-server config/SERVER-LISTEN-PORT)
  ))
