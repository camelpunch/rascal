(ns rascal.tiles-test
  (:use [clojure.test])
  (:require [rascal.tiles :refer :all]))

(deftest placing-creatures
  (is (= [(make-creature \j "Jackal" 3 2)
          (make-creature \b "Beetle" 6 3)]
         (place-creatures :board-dimensions [ 8 10]
                          :dice-rolls       [ 5  2
                                             10  3]
                          :creatures        [[\j "Jackal"]
                                             [\b "Beetle"]]))))
