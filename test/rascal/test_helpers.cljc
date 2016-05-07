(ns rascal.test-helpers
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing]])
            #?(:clj  [clojure.test.check.clojure-test :refer [defspec]])
            #?(:clj  [clojure.test.check.properties :as prop]
               :cljs [clojure.test.check.properties :as prop :include-macros true])
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [rascal.tiles :as tiles]
            [rascal.game :as game :refer [make-game left right up down]]
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

(defn dir-fns
  [move]
  {"left"       #(move % left)
   "right"      #(move % right)
   "up"         #(move % up)
   "down"       #(move % down)
   "up-left"    #(move % (comp up left))
   "up-right"   #(move % (comp up right))
   "down-left"  #(move % (comp down left))
   "down-right" #(move % (comp down right))})

(defn directions [m] (gen/elements (keys m)))

(defn follow
  [game path move]
  ((apply comp (reverse (map #(get (dir-fns move) %) path)))
   game))

(defn on-paths
  "must be pred at the end of each path made of dirs"
  [start move dirs pred]
  (prop/for-all [path (gen/vector dirs)]
                (let [end (follow start path move)]
                  (pred end))))

(defn make-path-follower
  [game move pred]
  (on-paths game move (directions (dir-fns move)) pred))
