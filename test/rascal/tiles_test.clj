(ns rascal.tiles-test
  (:use [clojure.test])
  (:require [rascal.tiles :refer :all]))

(deftest placing-creatures
  (is (= [(make-creature \b "Beetle" 4 6)
          (make-creature \j "Jackal" 3 5)]
         (place-creatures :board-dimensions [10 12]
                          :dice-rolls       [4 5 3 4]
                          :creatures        [[\b "Beetle"]
                                             [\j "Jackal"]]))))
