(ns fhirplayground.core
  (:require
   [fhirplayground.config :as config]
   [fhirplayground.db :as db]
   [fhirplayground.http :as http]
   [fhirplayground.websocket :as ws])
  (:gen-class))

(defn -main
  []
  (let [port    (Integer. (System/getenv "PORT"))
        db-spec (config/db-spec (System/getenv "DATABASE_URL"))]
    (try
      (db/start-connection-pool! db-spec)
      (catch Exception _ nil))
    (ws/start-router!)
    (http/start-web-server! port)))
