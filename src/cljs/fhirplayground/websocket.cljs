(ns fhirplayground.websocket
  (:require
   [fhirplayground.config :as config]
   [fhirplayground.handler :as handler]
   [taoensso.sente :as sente]))

(defonce router (atom nil))

(defn stop-router!
  []
  (when-let [stop-f @router]
    (stop-f)))

(defn start-router!
  []
  (stop-router!)
  (reset! router
          (sente/start-client-chsk-router!
           config/ch-chsk handler/event-msg-handler)))
