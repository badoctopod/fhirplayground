(ns fhirplayground.dummy-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]))

(deftest dummy-test
  (is (= (+ 1 1) (* 1 2))))
