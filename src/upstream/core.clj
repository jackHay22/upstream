(ns upstream.core
  (:require [upstream.engine.gamewindow :as engine]
            [upstream.utilities.log :as logger]
            [upstream.config :as config]
            [upstream.gamestate.gsmanager :as gsm]
            [upstream.server.gameserver :as server]
            [upstream.utilities.windowutil :as windowutility])
  (:gen-class))

(set! *warn-on-reflection* true)

(defn normal-start-procedure
  "normal game starting procedure"
  []
  (do
  (System/setProperty "sun.java2d.opengl" "true")
  (let [window-resource (windowutility/compute-window-resource
                            (* config/ORIGINAL-TILE-WIDTH config/TILES-ACROSS))]
    (reset! config/WINDOW-RESOURCE-WIDTH (:width window-resource))
    (reset! config/WINDOW-RESOURCE-HEIGHT (:height window-resource))
    (reset! config/COMPUTED-SCALE 1.5) ;TODO: remove
    (gsm/init-gsm gsm/LEVEL-STATE)
    (engine/start-window config/WINDOW-TITLE window-resource config/FRAMERATE)
    ; start subsequent loads here
    (gsm/start-pipeline-autosave gsm/LEVEL-STATE)
  )))

(defn -main
  "entrypoint"
  [& args]
  (if (> (count args) 0)
    (cond
      (= (first args) "-server")
         (do
           (reset! config/HEADLESS-SERVER? true) ;TODO: find and remove
           (logger/write-log "Starting in server mode...")
           (gsm/init-gsm gsm/SERVER-STATE)
           ;(server/start-server config/SERVER-LISTEN-PORT gsm/authenticate-user "Game Server")
           (engine/start-headless config/FRAMERATE))
      (= (first args) "-gp")
          (do
            (reset! config/HEADLESS-SERVER? true) ;TODO: find and remove
            (logger/write-log "Starting UpstreamGP...")
            (gsm/init-gsm gsm/GP-STATE)

            ;(server/start-server config/SERVER-LISTEN-PORT gpsys/start-gp-simulation "UpstreamGP")
            )
      :else
          (do
            (reset! config/HEADLESS-SERVER? true)
            (logger/write-log "ERROR: bad initialization argument:" (first args))))
    (do
        (normal-start-procedure)
        )))
