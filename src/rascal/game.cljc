(ns rascal.game
  (:require [rascal.tiles :as t]))

(declare affect damager do-battle move)

(defn move
  [s axis movement]
  (let [{{coords :coords} :player
         obstacles        :obstacles
         board            :board
         :as candidate-state} (update-in s axis movement)]
    (if-let [battle-coords (some #{coords} (map :coords obstacles))]
      (do-battle s (damager battle-coords))
      candidate-state)))

(defn- do-battle
  [{obstacles :obstacles :as s} f]
  (assoc s :obstacles (remove t/dead? (map f obstacles))))

(defn- damager
  [coords]
  (fn [x]
    (if (= coords (:coords x))
      (t/damage x)
      x)))
