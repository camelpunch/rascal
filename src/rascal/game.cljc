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
  (assoc s :obstacles (filter t/alive? (map f obstacles))))

(defn- damager
  [coords]
  (fn [monster]
    (if (and (contains? monster :health) (= coords (:coords monster)))
      (update-in monster [:health] - 50)
      monster)))
