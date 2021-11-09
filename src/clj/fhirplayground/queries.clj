(ns fhirplayground.queries
  (:require
   [clojure.string :as str]
   [hugsql.adapter.next-jdbc :as next-adapter]
   [hugsql.core :as hugsql]
   [next.jdbc.result-set :as result-set]))

(defn as-kebab-maps
  [rs opts]
  (let [kebab #(str/lower-case (str/replace % #"_" "-"))]
    (result-set/as-unqualified-modified-maps rs (assoc opts :label-fn kebab))))

(hugsql/set-adapter!
 (next-adapter/hugsql-adapter-next-jdbc
  {:builder-fn as-kebab-maps}))

(hugsql/def-db-fns "sql/queries.sql")

(hugsql/def-sqlvec-fns "sql/queries.sql")
