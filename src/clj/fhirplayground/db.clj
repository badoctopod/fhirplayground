(ns fhirplayground.db
  (:require
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection])
  (:import
   [com.zaxxer.hikari HikariDataSource]))

(defonce datasource (atom nil))

(defn start-connection-pool!
  [db-spec]
  (let [^HikariDataSource ds (connection/->pool HikariDataSource db-spec)]
    (reset! datasource ds)
    (.close (jdbc/get-connection @datasource))))

(defn stop-connection-pool!
  []
  (.close @datasource))
