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
      (logger/write-log-all "Starting in -server mode.")
      (logger/write-log "Logs sending to Sumologic endpt: " config/SUMOLOGIC-ENDPOINT)
      (reset! config/HEADLESS-SERVER? true)
      (gsm/init-gsm 2) ;skip straight to l1 mode
      (server/start-welcome-server config/SERVER-LISTEN-PORT)
      (engine/start-headless))
    (do
      (System/setProperty "sun.java2d.opengl" "true")
      (let [screenSize (.getScreenSize (Toolkit/getDefaultToolkit))]
        (reset! config/WINDOW-WIDTH (.width screenSize))
        (reset! config/WINDOW-HEIGHT (- (.height screenSize) config/HEIGHT-BUFFER))
        (reset! config/COMPUTED-SCALE (/ (/ (.width screenSize) config/TILES-ACROSS)
                                     config/ORIGINAL-TILE-WIDTH))
        (gsm/init-gsm 2)
        (engine/start-window config/WINDOW-TITLE)))))
