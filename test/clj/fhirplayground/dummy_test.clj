(ns fhirplayground.dummy-test
  (:require  [clojure.test :refer [deftest is testing]]))

(deftest dummy-test
  (is (= (+ 1 1) (* 1 2))))
