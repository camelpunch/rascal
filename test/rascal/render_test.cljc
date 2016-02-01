(ns rascal.render-test
  #?(:clj (:use [clojure.test]))
  (:require #?(:cljs [cljs.test :refer-macros [deftest is]])
            [rascal.test-helpers :refer [rendered]]
            [rascal.render :refer [render]]
            [rascal.tiles :as t]))

(deftest rendering
  (is (= (rendered
          "#####
           #.r.#
           #..@#
           #j..#
           #####")
         (render {:board     (t/make-board  5 5)
                  :player    (t/make-player 3 2)
                  :obstacles (conj (t/make-walls-for-board 5 5)
                                   (t/make-creature \j "Jackal" 1 3)
                                   (t/make-creature \r "Rat"    2 1))}))))
