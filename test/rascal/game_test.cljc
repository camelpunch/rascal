(ns rascal.game-test
  #?(:clj (:use [clojure.test]))
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing]])
            #?(:clj  [clojure.test.check.clojure-test :refer [defspec]])
            #?(:clj  [clojure.test.check.properties :as prop]
               :cljs [clojure.test.check.properties :as prop :include-macros true])
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [rascal.test-helpers :refer [rendered]]
            [clojure.string :refer [join]]
            [rascal.game :refer [make-game move left right up down]]
            [rascal.tiles :as t :refer [x-axis
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

(defn follow
  [game path]
  ((apply comp (map #(get dir-fns %) path))
   game))

(defn on-paths
  "f must be pred at the end of each path made of dirs"
  [game dirs pred f]
  (prop/for-all [path (gen/vector dirs)]
                (apply pred (map f [game (follow game path)]))))

(deftest walls-dont-fight-back
  (let [game  (make-game :board      [ 8  8]
                         :player     [ 3  3]
                         :monsters   []
                         :dice-rolls (repeat 10))
        check (tc/quick-check 100 (on-paths game directions = t/player-health))]
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
                                               4 ; damage irrelevant
                                               3 ; Jackal misses
                                               9 ; damage irrelevant

                                               7 ; Player lands a hit
                                               4 ; 40 damage
                                               6 ; Jackal lands hit
                                               4 ; 40 damage

                                               5 ; Player lands hit
                                               4 ; 40 damage
                                               4 ; Jackal misses
                                               9 ; damage irrelevant

                                               5 ; Player lands killer blow
                                               4 ; of 40 damage
                                               6 ; Jackal would've hit, but dead
                                               9 ; damage irrelevant
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
      (is (= [(assoc (make-creature \j "Jackal" 3 2) :health 60)
              (make-creature \b "Beetle" 6 3)]
             (filter #(not= \# (:tile %)) (-> game-start
                                              (move left)
                                              (move left)
                                              :obstacles)))))))

(deftest game-over
  (let [game-start (make-game :board      [12 12]
                              :player     [ 4  5]
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
