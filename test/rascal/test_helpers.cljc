(ns rascal.test-helpers
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing]])
            #?(:clj  [clojure.test.check.clojure-test :refer [defspec]])
            #?(:clj  [clojure.test.check.properties :as prop]
               :cljs [clojure.test.check.properties :as prop :include-macros true])
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [rascal.tiles :as tiles :as t]
            [rascal.game :as game :refer [make-game left right up down]]
            [rascal.render :refer [render]]))

(def g
  (game/make-game :player   [ 1  1]
                  :board    [ 6  6]
                  :monsters [(t/make-creature \j "Jackal" 3 3)]))

(defn rendered
  [b]
  (map vec (clojure.string/split b #"\n +")))

(defn game-s
  [g]
  (clojure.string/join "\n" (map (partial apply str) (render g))))

(def pb (comp println game-s))

(defn dir-fns
  [go]
  {"left"       #(go % left)
   "right"      #(go % right)
   "up"         #(go % up)
   "down"       #(go % down)
   "up-left"    #(go % (comp up left))
   "up-right"   #(go % (comp up right))
   "down-left"  #(go % (comp down left))
   "down-right" #(go % (comp down right))})

(defn directions [m] (gen/elements (keys m)))

(defn follow
  [game path go]
  ((apply comp (reverse (map #(get (dir-fns go) %) path)))
   game))

(defn on-paths
  "must be pred at the end of each path made of dirs"
  [start go dirs pred]
  (prop/for-all [path (gen/vector dirs)]
                (let [end (follow start path go)]
                  (pred end))))

(defn make-path-follower
  [game go pred]
  (on-paths game go (directions (dir-fns go)) pred))
