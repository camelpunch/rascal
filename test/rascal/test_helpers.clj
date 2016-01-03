(ns rascal.test-helpers
  (:require [rascal.tiles :as tiles]
            [rascal.render :refer [render]]))

(def g
  {:board (tiles/make-board 10 10)
   :player (tiles/make-player 1 1)
   :monsters [(tiles/make-creature \j "Jackal" 1 2)]})

(defn rendered
  [b]
  (map vec (clojure.string/split b #"\n +")))

(defn game-s
  [g]
  (clojure.string/join "\n" (map (partial apply str) (render g))))

(def pb (comp println game-s))
