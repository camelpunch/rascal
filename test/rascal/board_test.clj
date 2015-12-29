(ns rascal.board-test
  (:use [clojure.test])
  (:require [rascal.board :as board :refer [. c ->Board]]))

(deftest move-left
  (testing "normally"
    (is (= 4
           (get-in (board/move-left {:player {:coords {:x 5}}})
                   [:player :coords :x]))))
  (testing "from leftmost position"
    (is (= 0
           (get-in (board/move-left {:player {:coords {:x 0}}})
                   [:player :coords :x])))))

(deftest move-right
  (testing "normally"
    (is (= 2
           (get-in (board/move-right {:board (->Board 3 3)
                                      :player {:coords {:x 1}}})
                   [:player :coords :x]))))
  (testing "from rightmost position"
    (is (= 3
           (get-in (board/move-right {:board (->Board 4 4)
                                      :player {:coords {:x 3}}})
                   [:player :coords :x])))))

(deftest move-up
  (testing "normally"
    (is (= 2
           (get-in (board/move-up {:board (->Board 10 10)
                                   :player {:coords {:y 3}}})
                   [:player :coords :y]))))
  (testing "from top"
    (is (= 0
           (get-in (board/move-up {:board (->Board 5 10)
                                   :player {:coords {:y 0}}})
                   [:player :coords :y])))))

(deftest move-down
  (testing "normally"
    (is (= 2
           (get-in (board/move-down {:board (->Board 10 10)
                                     :player {:coords {:y 1}}})
                   [:player :coords :y]))))
  (testing "from bottom"
    (is (= 9
           (get-in (board/move-down {:board (->Board 5 10)
                                     :player {:coords {:y 9}}})
                   [:player :coords :y])))))

(deftest render
  (is (= [[. . .]
          [. . c]
          [. . .]]
         (board/render {:board (->Board 3 3)
                        :player {:coords {:x 2 :y 1}}}))))
