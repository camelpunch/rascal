(ns rascal.game-test
  (:use [clojure.test])
  (:require [rascal.test-helpers :refer [rendered]]
            [rascal.game :as game]
            [rascal.tiles :refer [x-axis
                                  y-axis
                                  make-board
                                  make-player
                                  make-creature
                                  make-wall-tile]]
            [rascal.render :refer [render]]))

(deftest move
  (testing "normally"
    (is (= 4
           (get-in (-> {:player (make-player 5 0)}
                       (game/move x-axis dec))
                   [:player :coords :x]))))
  (testing "into wall"
    (is (= 1
           (get-in (-> {:player (make-player 1 1)
                        :board  (make-board  3 3)
                        :obstacles [(make-wall-tile 0 1)]}
                       (game/move x-axis dec))
                   [:player :coords :x])))))

(deftest collision
  (let [board  (make-board                  10 10)
        ant    (make-creature \a "Ant"       4  3)
        player (make-player                  4  4)
        beetle (make-creature \b "Beetle"    4  5)
        rat    (make-creature \r "Rat"       3  4)
        jackal (make-creature \j "Jackal"    5  4)]

    (testing "into a monster"
      (is (= {:board     board
              :player    player
              :obstacles [(assoc rat :health 50) jackal]}
             (-> {:board     board
                  :player    player
                  :obstacles [rat jackal]}
                 (game/move x-axis dec)))
          "Player doesn't move, monster loses health."))

    (testing "defeating a monster"
      (is (= {:board     board
              :player    player
              :obstacles [jackal]}
             (-> {:board     board
                  :player    player
                  :obstacles [jackal (assoc beetle :health 50)]}
                 (game/move y-axis inc)))))))

