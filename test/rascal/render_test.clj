(ns rascal.render-test
  (:use [clojure.test])
  (:require [rascal.render :refer [render]]
            [rascal.board :as b]))

(defn- rendered
  [b]
  (map vec (clojure.string/split b #"\n +")))

(deftest rendering
  (is (= (rendered
          ".r.
           ..@
           j..")
         (render {:board    (b/make-board  3 3)
                  :player   (b/make-player 2 1)
                  :monsters [(b/make-creature \j "Jackal" 0 2)
                             (b/make-creature \r "Rat"    1 0)]}))))

