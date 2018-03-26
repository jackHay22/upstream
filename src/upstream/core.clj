(ns upstream.core
  (:require [upstream.engine.gamewindow :as engine]
            [upstream.utilities.log :as logger]
            [upstream.config :as config]
            [upstream.gamestate.gsmanager :as gsm]
            [upstream.server.gameserver :as server])
  (:gen-class))

(import java.awt.Toolkit)

(defn -main
  "entrypoint"
  [& args]
  (if (and (> (count args) 0) (= (first args) "-server"))
    (do
      (logger/write-log "Starting in -server mode.")
      (reset! config/HEADLESS-SERVER? true)
      (gsm/init-gsm)
      (server/start-welcome-server config/SERVER-LISTEN-PORT)
      (engine/start-headless))

      ;ELSE
      (do
      ;potential opengl performance acceleration
      (System/setProperty "sun.java2d.opengl" "true")

      (let [screenSize (.getScreenSize (Toolkit/getDefaultToolkit))]
        ;various config setup changes
        (reset! config/WINDOW-WIDTH (.width screenSize))
        (reset! config/WINDOW-HEIGHT (- (.height screenSize) config/HEIGHT-BUFFER))
        (reset! config/COMPUTED-SCALE (/ (/ (.width screenSize) config/TILES-ACROSS)
                                     config/ORIGINAL-TILE-WIDTH))
        (gsm/init-gsm)
        (engine/start-window config/WINDOW-TITLE)))))
