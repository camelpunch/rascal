(ns rascal.game-test
  (:use [clojure.test])
  (:require [rascal.test-helpers :refer [rendered]]
            [rascal.game :refer [make-game move left]]
            [rascal.tiles :refer [x-axis
                                  y-axis
                                  make-board
                                  make-player
                                  make-creature
                                  make-wall-tile
                                  make-walls-for-board]]
            [rascal.render :refer [render]]))

(deftest creating-a-game
  (let [game-start (make-game :board      [ 8 10]
                              :player     [ 5  2]
                              :monsters   [[\j "Jackal"] [\b "Beetle"]]
                              :dice-rolls [ 5  2
                                           10  3
                                           0 10])]
    (testing "starts on turn 1"
      (is (= 1 (:turn game-start))))

    (testing "shifts the dice rolls"
      (is (= [0 10]
             (:dice-rolls game-start))))

    (testing "places monsters inside walls"
      (is (= {:x 6 :y 3}
             (:coords (first (filter #(= \b (:tile %))
                                     (:obstacles game-start)))))))))

(deftest movement
  (testing "normal movement moves player"
    (is (= 4
           (get-in (-> (make-game :player     [5 1]
                                  :board      [6 6]
                                  :dice-rolls [10 10]) ; positioning
                       (move left))
                   [:player :coords :x]))))
  (testing "into wall"
    (is (= 1
           (get-in (-> (make-game :player [1 1]
                                  :board  [3 3]
                                  :dice-rolls [0 0   ; positioning
                                               0 0]) ; 'fighting' the wall
                       (move left))
                   [:player :coords :x])))))

(deftest collision
  (let [game-start (make-game :board      [ 8 10]
                              :player     [ 4  2]
                              :monsters   [[\j "Jackal"] [\b "Beetle"]]
                              :dice-rolls [ 5  2 ; Jackal at [3 2]
                                           10  3 ; Beetle at [6 3]
                                               4 ; Player misses (< 5)
                                               3 ; Jackal misses
                                               7 ; Player lands another hit
                                               6 ; Jackal lands hit
                                               5 ; Player lands hit
                                               4 ; Jackal misses
                                               5 ; Player lands killer blow
                                               6 ; Jackal would've hit, but dead
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
                 (move left)
                 (move left)
                 (move left)
                 (move left)
                 :log))))

    (testing "player doesn't move"
      (is (= {:x 4 :y 2}
             (-> game-start
                 (move left)
                 :player
                 :coords))))

    (testing "player loses health"
      (is (= 60
             (-> game-start
                 (move left)
                 (move left)
                 :player
                 :health))))

    (testing "other monsters unaffected"
      (is (= (make-creature \b "Beetle" 6 3)
             (second (filter #(contains? % :health) (-> game-start
                                                        (move left)
                                                        :obstacles))))))))

