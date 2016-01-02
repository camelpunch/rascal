(ns rascal.render-test
  (:use [clojure.test])
  (:require [rascal.test-helpers :refer [rendered]]
            [rascal.render :refer [render]]
            [rascal.board :as b]))

(deftest rendering
  (is (= (rendered
          "#####
           #.r.#
           #..@#
           #j..#
           #####")
         (render {:board    (b/make-board  5 5)
                  :player   (b/make-player 3 2)
                  :monsters [(b/make-creature \j "Jackal" 1 3)
                             (b/make-creature \r "Rat"    2 1)]}))))
