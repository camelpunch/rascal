(ns rascal.game-test
  (:use [clojure.test])
  (:require [rascal.test-helpers :refer [rendered]]
            [rascal.game :as game :refer [move left]]
            [rascal.tiles :refer [x-axis
                                  y-axis
                                  make-board
                                  make-player
                                  make-creature
                                  make-wall-tile
                                  make-walls-for-board]]
            [rascal.render :refer [render]]))

(deftest creating-a-game
  (let [game-start (game/make-game :board      [ 8 10]
                                   :player     [ 5  2]
                                   :monsters   [[\j "Jackal"] [\b "Beetle"]]
                                   :dice-rolls [ 5  2
                                                10  3
                                                 0 10])]
    (testing "starts on turn 1"
      (is (= 1 (:turn game-start))))

    (testing "shifts the dice rolls"
      (is (= [0 10]
             (:dice-rolls game-start))))))

(deftest movement
  (testing "normal movement moves player"
    (is (= 4
           (get-in (-> (game/make-game :player [5 1]
                                       :board  [6 6])
                       (move left))
                   [:player :coords :x]))))
  (testing "into wall"
    (is (= 1
           (get-in (-> (game/make-game :player [1 1]
                                       :board  [3 3])
                       (move left))
                   [:player :coords :x])))))

(deftest collision
  (let [game-start (game/make-game :board      [ 8 10]
                                   :player     [ 5  2]
                                   :monsters   [[\j "Jackal"] [\b "Beetle"]]
                                   :dice-rolls [ 5  2 ; jackal at [4 2]
                                                10  3])] ; beetle at [8 3]

    (testing "logs the fight"
      (is (= ["You entered the dungeon"
              "You hit the Jackal"
              "You defeated the Jackal"]
             (-> game-start
                 (move left)
                 (move left)
                 :log))))

    (testing "player doesn't move"
      (is (= {:x 5 :y 2}
             (-> game-start
                 (move left)
                 :player
                 :coords))))))

