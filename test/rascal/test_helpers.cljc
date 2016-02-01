(ns rascal.test-helpers
  (:require [rascal.tiles :as tiles]
            [rascal.game :as game]
            [rascal.render :refer [render]]))

(def g
  (game/make-game :player   [ 1  1]
                  :board    [ 6  6]
                  :monsters [(tiles/make-creature \j "Jackal" 3 3)]))

(defn rendered
  [b]
  (map vec (clojure.string/split b #"\n +")))

(defn game-s
  [g]
  (clojure.string/join "\n" (map (partial apply str) (render g))))

(def pb (comp println game-s))
