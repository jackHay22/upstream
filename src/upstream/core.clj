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
      (gsm/init-gsm 2) ;skip straight to l1 mode
      (server/start-welcome-server config/SERVER-LISTEN-PORT)
      (engine/start-headless))
    (do
      (System/setProperty "sun.java2d.opengl" "true")
      ;TODO: refactor
      (let [screenSize (.getScreenSize (Toolkit/getDefaultToolkit))
            scale (/ (/ (.width screenSize) @config/TILES-ACROSS)
                                         config/ORIGINAL-TILE-WIDTH)]
        (reset! config/WINDOW-WIDTH (.width screenSize))
        (reset! config/WINDOW-HEIGHT (- (.height screenSize) config/HEIGHT-BUFFER))
        (reset! config/COMPUTED-SCALE scale)
        (reset! config/TILES-DOWN (int (+
                                        (/ (.height screenSize) (/ (* scale config/ORIGINAL-TILE-HEIGHT) 2))
                                         2)))
        (gsm/init-gsm 0)
        (engine/start-window config/WINDOW-TITLE)
        (gsm/start-subsequent-loads)))))
