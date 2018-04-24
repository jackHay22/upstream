(ns upstream.core
  (:require [upstream.engine.gamewindowtwo :as engine]
            [upstream.utilities.log :as logger]
            [upstream.utilities.gpsys :as gpsys]
            [upstream.config :as config]
            [upstream.gamestate.gsmanager :as gsm]
            [upstream.server.gameserver :as server])
  (:gen-class))

(import java.awt.Toolkit)

(defn -main
  "entrypoint"
  [& args]
  (if (> (count args) 0)
    (cond
      (= (first args) "-server")
         (do
           (reset! config/HEADLESS-SERVER? true)
           (logger/write-log "Starting in server mode...")
           (gsm/init-gsm 2)
           (server/start-server config/SERVER-LISTEN-PORT gsm/authenticate-user "Game Server")
           (engine/start-headless @config/FRAMERATE))
      (= (first args) "-gp")
          (do
            (reset! config/HEADLESS-SERVER? true)
            (logger/write-log "Starting UpstreamGP...")
            (server/start-server config/SERVER-LISTEN-PORT gpsys/start-gp-simulation "UpstreamGP"))
      :else
          (do
            (reset! config/HEADLESS-SERVER? true)
            (logger/write-log "ERROR: bad initialization argument:" (first args))))
    (do
      (System/setProperty "sun.java2d.opengl" "true")
      (let [screenSize (.getScreenSize (Toolkit/getDefaultToolkit))
            scale (/ (/ (.width screenSize) config/TILES-ACROSS) config/ORIGINAL-TILE-WIDTH)]
        (reset! config/WINDOW-WIDTH (.width screenSize))
        (reset! config/WINDOW-HEIGHT (- (.height screenSize) config/HEIGHT-BUFFER))
        (reset! config/COMPUTED-SCALE 1.5) ;TODO: test this on different screens vs. scale attribute
        (gsm/init-gsm 0)
        ;(engine/start-window config/WINDOW-TITLE @config/FRAMERATE)
        (engine/start-window config/WINDOW-TITLE @config/WINDOW-WIDTH (- (.height screenSize) config/HEIGHT-BUFFER) @config/FRAMERATE)
        (gsm/start-subsequent-loads)))))
