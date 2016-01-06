(ns rascal.game-test
  (:use [clojure.test])
  (:require [rascal.test-helpers :refer [rendered]]
            [rascal.game :as game]
            [rascal.tiles :refer [x-axis
                                  y-axis
                                  make-board
                                  make-player
                                  make-creature
                                  make-wall-tile
                                  make-walls-for-board]]
            [rascal.render :refer [render]]))

(deftest move
  (testing "normal movement moves player"
    (is (= 4
           (get-in (-> (game/make-game :player [5 1]
                                       :board  [6 6])
                       (game/move x-axis dec))
                   [:player :coords :x]))))
  (testing "into wall"
    (is (= 1
           (get-in (-> (game/make-game :player [1 1]
                                       :board  [3 3])
                       (game/move x-axis dec))
                   [:player :coords :x])))))

(deftest collision
  (let [board  (make-board                   6  6)
        player (make-player                  4  3)
        beetle (make-creature \b "Beetle"    4  4)
        jackal (make-creature \j "Jackal"    3  3)]

    (testing "into a monster"
      (is (= {:turn      2
              :board     board
              :player    player
              :obstacles (concat (make-walls-for-board 6 6)
                                 [(assoc jackal :health 50) beetle])
              :log       ["You entered the dungeon"
                          "You hit the Jackal"]}
             (-> (game/make-game :board    [6 6]
                                 :player   [4 3]
                                 :monsters [jackal beetle])
                 (game/move x-axis dec)))
          "Player doesn't move, monster loses health."))

    (testing "defeating a monster"
      (is (= {:turn      2
              :board     board
              :player    player
              :obstacles (concat (make-walls-for-board 6 6)
                                 [jackal])
              :log       ["You entered the dungeon"
                          "You defeated the Beetle"]}
             (-> (game/make-game :board    [6 6]
                                 :player   [4 3]
                                 :monsters [jackal (assoc beetle :health 50)])
                 (game/move y-axis inc)))))))

