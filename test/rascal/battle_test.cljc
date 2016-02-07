(ns rascal.battle-test
  #?(:clj (:use [clojure.test]))
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing]])
            #?(:clj  [clojure.test.check.clojure-test :refer [defspec]])
            #?(:clj  [clojure.test.check.properties :as prop]
               :cljs [clojure.test.check.properties :as prop :include-macros true])
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.string :refer [join]]
            [rascal.game :refer [make-game left right up down]]
            [rascal.tiles :as t :refer [make-creature]]
            [rascal.battle :refer [do-battle]]))

(defn go
  [state f]
  (do-battle state (f state)))

(def dir-fns
  {"left"       #(go % left)
   "right"      #(go % right)
   "up"         #(go % up)
   "down"       #(go % down)
   "up-left"    #(go % (comp up left))
   "up-right"   #(go % (comp up right))
   "down-left"  #(go % (comp down left))
   "down-right" #(go % (comp down right))})

(def directions (gen/elements (keys dir-fns)))

(defn follow
  [game path]
  ((apply comp (reverse (map #(get dir-fns %) path)))
   game))

(defn on-paths
  "f of game must be pred at the end of each path made of dirs"
  [game dirs pred f]
  (prop/for-all [path (gen/vector dirs)]
                (apply pred (map f [game (follow game path)]))))

(deftest walls-dont-fight-back
  (let [game  (make-game :board      [ 8  8]
                         :player     (t/make-player 3 3 10)
                         :monsters   []
                         :dice-rolls (repeat 10))
        check (tc/quick-check 100 (on-paths game directions = t/player-health))]
    (is (true? (:result check))
        (str "Can lose health by going "
             (join ", " (first (get-in check [:shrunk :smallest])))))))

(defn remove-walls
  [obstacles]
  (remove #(= \# (:tile %)) obstacles))

(deftest collision-with-static-monster
  (let [game-start (make-game :board      [ 8 10]
                              :player     (t/make-player 4 2 10)
                              :monsters   [[\j "Jackal"] [\b "Beetle"]]
                              :dice-rolls [ 5  2 ; Jackal at [3 2]
                                           10  3 ; Beetle at [6 3]

                                               4 ; Player misses (< 5)
                                               4 ; damage irrelevant
                                               3 ; Jackal misses
                                               9 ; damage irrelevant

                                               7 ; Player lands a hit
                                               4 ; 40 damage
                                               6 ; Jackal lands hit
                                               4 ; 40 damage

                                               5 ; Player lands hit
                                               4 ; 40 damage
                                               4 ; Jackal misses
                                               9 ; damage irrelevant

                                               5 ; Player lands killer blow
                                               4 ; of 40 damage
                                               6 ; Jackal would've hit, but dead
                                               9 ; damage irrelevant
                                           ])]

    (testing "logs the fight"
      (is (= ["You entered the dungeon"
              "You miss the Jackal"
              "The Jackal misses you"
              "You hit the Jackal"
              "The Jackal hits you"
              "You hit the Jackal"
              "The Jackal misses you"
              "You defeated the Jackal"]
             (-> game-start
                 (go left)
                 (go left)
                 (go left)
                 (go left)
                 :log))))

    (testing "player doesn't move"
      (is (= {:x 4 :y 2}
             (-> game-start
                 (go left)
                 :player
                 :coords))))

    (testing "player loses health"
      (is (= 60
             (-> game-start
                 (go left)
                 (go left)
                 :player
                 :health))))

    (testing "other monsters unaffected"
      (is (= [(assoc (make-creature \j "Jackal" 3 2) :health 60)
              (make-creature \b "Beetle" 6 3)]
             (-> game-start
                 (go left)
                 (go left)
                 :obstacles
                 remove-walls))))))
