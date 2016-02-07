(ns rascal.game-test
  #?(:clj (:use [clojure.test]))
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing]])
            [rascal.game :refer [make-game move left right up down]]
            [rascal.tiles :as t]))

(deftest creating-a-game
  (let [game-start (make-game :board      [ 8 10]
                              :player     (t/make-player 5 2 10)
                              :monsters   [[\j "Jackal"] [\b "Beetle"]]
                              :dice-rolls [ 5  2
                                           10  3
                                           0 10])]
    (testing "starts on turn 1"
      (is (= 1 (:turn game-start))))

    (testing "places monsters inside walls"
      (is (= {:x 6 :y 3}
             (:coords (first (filter #(= \b (:tile %))
                                     (:obstacles game-start)))))))))

(deftest movement
  (testing "normal movement moves player"
    (is (= 4
           (get-in (-> (make-game :player     (t/make-player 5 1 10)
                                  :board      [6 6]
                                  :dice-rolls [10 10]) ; positioning
                       (move left))
                   [:player :coords :x]))))
  (testing "into wall"
    (is (= 1
           (get-in (-> (make-game :player (t/make-player 1 1 10)
                                  :board  [3 3]
                                  :dice-rolls [0 0   ; positioning
                                               0 0]) ; 'fighting' the wall
                       (move left))
                   [:player :coords :x])))))

(deftest game-over
  (let [game-start (make-game :board      [12 12]
                              :player     (t/make-player 4 5 10)
                              :monsters   [[\j "Jackal"]]
                              :dice-rolls [ 5  5 ; Jackal at [5 5]

                                               0 ; Player misses
                                               9 ; damage irrelevant
                                              10 ; Jackal hits
                                               4 ; 40 damage

                                               0 ; Player misses
                                               9 ; damage irrelevant
                                              10 ; Jackal hits
                                               4 ; 40 damage

                                               0 ; Player misses
                                               9 ; damage irrelevant
                                              10 ; Jackal hits and kills player
                                               4 ; with 40
                                           ])
        game-end   (-> game-start
                       (move right)
                       (move right)
                       (move right))]

    (testing "movement no longer works"
      (is (= {:x 4 :y 5}
             (get-in (move game-end down) [:player :coords]))))

    (testing "death is logged"
      (is (= ["The Jackal hits you"
              "You die"]
             (take-last 2 (:log game-end)))))))
