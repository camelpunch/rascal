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
         monsters         :monsters
         board            :board
         :as candidate-state} (update-in s axis movement)
        walls             (filter t/wall? (flatten board))
        obstacles         (concat monsters walls)]
    (if-let [battle-coords (some #{coords} (map :coords obstacles))]
      (do-battle s (damager battle-coords))
      candidate-state)))

(defn- do-battle
  [{monsters :monsters :as s} f]
  (assoc s :monsters (filter t/alive? (map f monsters))))

(defn- affect
  [x ks f & args]
  (update-in x ks #(apply f % args)))

(defn- damager
  [coords]
  (fn [monster]
    (if (= coords (:coords monster))
      (affect monster [:health] - 50)
      monster)))
