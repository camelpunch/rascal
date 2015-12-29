(ns rascal.board-test
  (:use [clojure.test])
  (:require [rascal.board :as board :refer [. c]]))

(deftest create-a-board
  (is (= 20 (count (board/create-board)))))

(deftest move-right
  (testing "from centre"
    (is (= [[. . c]
            [. . .]
            [. . .]]
           (board/move-right [[. c .]
                              [. . .]
                              [. . .]]
                             c))))
  (testing "from rightmost position"
    (is (= [[. . c]] (board/move-right [[. . c]] c)))))

;; (deftest move-left
;;   (testing "from centre"
;;     (is (= [[. . .]
;;             [c . .]
;;             [. . .]]
;;            (board/move-left [[. . .]
;;                              [. c .]
;;                              [. . .]]
;;                             c))))
;;   (testing "from leftmost position"
;;     (is (= [[c . .]] (board/move-left [[c . .]] c)))))
