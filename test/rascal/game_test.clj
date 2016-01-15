(ns rascal.game-test
  (:use [clojure.test])
  (:require [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [rascal.test-helpers :refer [rendered]]
            [clojure.string :refer [join]]
            [rascal.game :refer [make-game move left right up down]]
            [rascal.tiles :refer [x-axis
                                  y-axis
                                  make-board
                                  make-player
                                  make-creature
                                  make-wall-tile
                                  make-walls-for-board]]
            [rascal.render :refer [render]]))

(def dir-fns
  {"left"       #(move % left)
   "right"      #(move % right)
   "up"         #(move % up)
   "down"       #(move % down)
   "up-left"    #(move % (comp up left))
   "up-right"   #(move % (comp up right))
   "down-left"  #(move % (comp down left))
   "down-right" #(move % (comp down right))})

(def directions (gen/elements (keys dir-fns)))

(defn follow-path
  [game path]
  ((apply comp (map #(get dir-fns %) path))
   game))

(defn no-damage-on-paths
  [game directions]
  (prop/for-all [path (gen/vector directions)]
                (let [end-game (follow-path game path)]
                  (= (get-in game     [:player :health])
                     (get-in end-game [:player :health])))))

(deftest walls-dont-fight-back
  (let [check (tc/quick-check 100
                              (no-damage-on-paths (make-game :board [8 8]
                                                             :player [3 3]
                                                             :monsters []
                                                             :dice-rolls (repeat 10))
                                                  directions))]
    (is (true? (:result check))
        (str "Can lose health by going "
             (join ", " (first (get-in check [:shrunk :smallest])))))))

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

(deftest game-over
  (let [game-start (make-game :board      [12 12]
                              :player     [ 4  5]
                              :monsters   [[\j "Jackal"]]
                              :dice-rolls [ 5  5 ; Jackal at [5 5]

                                               0 ; Player misses
                                              10 ; Jackal hits

                                               0 ; Player misses
                                              10 ; Jackal hits

                                               0 ; Player misses
                                              10 ; Jackal hits and kills player
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
