(ns fhirplayground.test-runner
  (:require
   [fhirplayground.dummy-test]
   [figwheel.main.testing :refer-macros [run-tests-async]]))

(defn -main
  [& args]
  (run-tests-async 5000))
