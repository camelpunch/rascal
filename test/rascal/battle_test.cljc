(ns rascal.battle-test
  #?(:clj (:use [clojure.test]))
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing]])
            [rascal.game :refer [make-game left right up down]]
            [rascal.tiles :as t :refer [make-creature]]
            [rascal.battle :refer [do-battle]]))

(defn go
  [state f]
  (do-battle state (f state)))

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
