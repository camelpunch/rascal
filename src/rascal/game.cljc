(ns rascal.game
  (:require [rascal.tiles :as t]
            [rascal.render :refer [render]]))

(declare affect damager do-battle move)

(defn move-left  [s] (move s t/x-axis dec))
(defn move-right [s] (move s t/x-axis inc))
(defn move-up    [s] (move s t/y-axis dec))
(defn move-down  [s] (move s t/y-axis inc))

(defn- move
  [s axis movement]
  (let [{{coords :coords} :player
         obstacles        :obstacles
         board            :board
         :as candidate-state} (update-in s axis movement)
        walls             (filter t/wall? (flatten board))
        all-obstacles     (concat obstacles walls)]
    (if-let [battle-coords (some #{coords} (map :coords all-obstacles))]
      (do-battle s (damager battle-coords))
      candidate-state)))

(defn- do-battle
  [{obstacles :obstacles :as s} f]
  (assoc s :obstacles (filter t/alive? (map f obstacles))))

(defn- damager
  [coords]
  (fn [monster]
    (if (= coords (:coords monster))
      (update-in monster [:health] - 50)
      monster)))
