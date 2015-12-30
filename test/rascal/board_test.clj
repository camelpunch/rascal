(ns rascal.board-test
  (:use [clojure.test])
  (:require [rascal.board :as board :refer [->Board]]))

(declare rendered)

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
  (is (= (rendered
          ".r.
           ..@
           j..")
         (board/render {:board (->Board 3 3)
                        :player {:tile   \@
                                 :coords {:x 2 :y 1}}
                        :monsters [{:tile \j
                                    :name "Jackal"
                                    :coords {:x 0 :y 2}
                                    :health 100}
                                   {:tile \r
                                    :name "Rat"
                                    :coords {:x 1 :y 0}
                                    :health 100}]}))))

(defn- rendered
  [b]
  (map vec (clojure.string/split b #"\n +")))
