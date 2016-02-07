(ns rascal.monster-movement-test
  #?(:clj (:use [clojure.test]))
  (:require #?(:cljs [cljs.test :refer-macros [deftest is testing]])
            #?(:clj  [clojure.test.check.clojure-test :refer [defspec]])
            #?(:clj  [clojure.test.check.properties :as prop :refer [for-all]]
               :cljs [clojure.test.check.properties :as prop :include-macros true :refer-macros [for-all]])
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen :refer [such-that nat]]
            [rascal.tiles :as t]
            [rascal.monster-movement :refer [move-monsters]]))

(defn moves-toward?
  [dst x1 x2]
  (if (< x2 dst)
    (= x2 (inc x1))
    (= x2 (dec x1))))

(defn first-named
  [xs name]
  (first (filter #(= name (:name %)) xs)))

(deftest monsters-move-towards-player
  (testing "x axis movement"
    (let [player-x    3
          player-y    3
          moveable?   #(or (< % (dec player-x))
                           (> % (inc player-x))
                           (< % (dec player-y))
                           (> % (inc player-y)))
          moveable-xs (such-that moveable? nat)
          check       (tc/quick-check
                       100
                       (for-all
                        [x1 moveable-xs
                         x2 moveable-xs
                         y1 moveable-xs
                         y2 moveable-xs]
                        (let [state       {:player    (t/make-player player-x player-y 10)
                                           :obstacles [(t/make-creature \f "Foo" x1 y1)
                                                       (t/make-creature \g "Goo" x2 y2)]}
                              after-state (move-monsters state)
                              {new-x1 :x
                               new-y1 :y} (-> after-state :obstacles (first-named "Foo") :coords)
                              {new-x2 :x
                               new-y2 :y} (-> after-state :obstacles (first-named "Goo") :coords)]
                          (and (moves-toward? player-x x1 new-x1)
                               (moves-toward? player-x x2 new-x2)
                               (moves-toward? player-y y1 new-y1)
                               (moves-toward? player-y y2 new-y2)))))]
      (is (true? (:result check))
          (str "When monsters are at "
               (get-in check [:shrunk :smallest])
               ", one didn't move toward player on both axes.\n"
               check)))))

(deftest walls-dont-move
  (is (= {:x 0 :y 0}
         (-> {:player (t/make-player 5 5 10)
              :obstacles [(t/make-wall-tile 0 0)]}
             move-monsters
             :obstacles first :coords))))
