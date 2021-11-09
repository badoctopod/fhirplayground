(ns user
  (:require
   [fhirplayground.websocket :as ws]
   [fhirplayground.http :as http]
   [fhirplayground.db :as db]))

(def config
  {:port 8080
   :db-spec {:dbtype   "postgres"
             :dbname   "fhirbase"
             :username "postgres"
             :password "postgres"
             :host     "localhost"
             :port     "5432"}})

(defn start
  []
  (try
    (db/start-connection-pool! (:db-spec config))
    (catch Exception _ nil))
  (ws/start-router!)
  (http/start-web-server! (:port config)))
