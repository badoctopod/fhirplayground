(ns build
  (:require
   [clojure.tools.build.api :as b]))

(def main 'fhirplayground.core)
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file "target/fhirplayground.jar")

(defn uberjar
  [_]
  (println "Cleaning target path...")
  (b/delete {:path "target"})
  (println "Copying source directories...")
  (b/copy-dir {:src-dirs   ["src/clj" "resources"]
               :target-dir class-dir})
  (println "Compiling...")
  (b/compile-clj {:basis     basis
                  :src-dirs  ["src/clj"]
                  :class-dir class-dir})
  (println "Creating uberjar...")
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis     basis
           :main      main}))
