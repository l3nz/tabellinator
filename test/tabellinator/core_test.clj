(ns tabellinator.core-test
  (:require [clojure.test :refer [deftest is]]
            [tabellinator.core :refer [style]]))

(deftest style-test
  (is (= {:style "font-color:red;font-size:12px"}
         (style {:font-color "red"
                 :font-size "12px"}))))