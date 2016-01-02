(ns rascal.game-test
  (:use [clojure.test])
  (:require [rascal.test-helpers :refer [rendered]]
            [rascal.game :as game]
            [rascal.tiles :refer [make-board
                                  make-player
                                  make-creature]]
            [rascal.render :refer [render]]))

(deftest move-left
  (testing "normally"
    (is (= 4
           (get-in (game/move-left {:player (make-player 5 0)})
                   [:player :coords :x]))))
  (testing "from leftmost position"
    (is (= 1
           (get-in (game/move-left {:player (make-player 1 1)
                                    :board  (make-board  3 3)})
                   [:player :coords :x])))))

(deftest move-right
  (testing "normally"
    (is (= 2
           (get-in (game/move-right {:board (make-board 4 4)
                                     :player (make-player 1 1)})
                   [:player :coords :x]))))
  (testing "from rightmost position"
    (is (= 2
           (get-in (game/move-right {:board (make-board 4 4)
                                     :player (make-player 2 1)})
                   [:player :coords :x])))))

(deftest move-up
  (testing "normally"
    (is (= 2
           (get-in (game/move-up {:board (make-board 10 10)
                                  :player (make-player 1 3)})
                   [:player :coords :y]))))
  (testing "from top"
    (is (= 1
           (get-in (game/move-up {:board (make-board 5 10)
                                  :player (make-player 1 1)})
                   [:player :coords :y])))))

(deftest move-down
  (testing "normally"
    (is (= 2
           (get-in (game/move-down {:board (make-board 10 10)
                                    :player (make-player 1 1)})
                   [:player :coords :y]))))
  (testing "from bottom"
    (is (= 8
           (get-in (game/move-down {:board (make-board 5 10)
                                    :player (make-player 1 8)})
                   [:player :coords :y])))))

(deftest collision
  (let [board  (make-board                  10 10)
        ant    (make-creature \a "Ant"       4  3)
        player (make-player                  4  4)
        beetle (make-creature \b "Beetle"    4  5)
        rat    (make-creature \r "Rat"       3  4)
        jackal (make-creature \j "Jackal"    5  4)]

    (testing "going left"
      (is (= {:board    board
              :player   player
              :monsters [(assoc rat :health 50) jackal]}
             (game/move-left {:board    board
                              :player   player
                              :monsters [rat jackal]}))
          "Player doesn't move, monster loses health."))

    (testing "going right"
      (is (= {:board    board
              :player   player
              :monsters [rat (assoc jackal :health 50)]}
             (game/move-right {:board    board
                               :player   player
                               :monsters [rat jackal]}))))

    (testing "going up"
      (is (= {:board    board
              :player   player
              :monsters [(assoc ant :health 50) jackal]}
             (game/move-up {:board    board
                            :player   player
                            :monsters [ant jackal]}))))

    (testing "going down"
      (is (= {:board    board
              :player   player
              :monsters [(assoc beetle :health 50) jackal]}
             (game/move-down {:board    board
                              :player   player
                              :monsters [beetle jackal]}))))

    (testing "defeating a monster"
      (is (= {:board    board
              :player   player
              :monsters [jackal]}
             (game/move-down {:board    board
                              :player   player
                              :monsters [jackal (assoc beetle :health 50)]}))))))
