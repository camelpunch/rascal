(ns rascal.game
  (:require [rascal.tiles :as t]))

(defn make-game
  [& {player-coords :player
      board-coords  :board
      monsters      :monsters}]
  {:turn      1
   :player    (apply t/make-player player-coords)
   :board     (apply t/make-board board-coords)
   :obstacles (concat (apply t/make-walls-for-board board-coords)
                      monsters)
   :log       ["You entered the dungeon"]})

(defn- log
  [xs & ys]
  (conj xs (apply str ys)))

(defn- conj-obstacles
  "Add to obstacles iff new-obstacle isn't dead."
  [obstacles new-obstacle]
  (if (t/dead? new-obstacle)
    obstacles
    (conj obstacles new-obstacle)))

(defn- do-battle
  "Runs through new obstacles with new player position, updating game
  state accordingly."
  [old-state player new-obstacles]
  (reduce (fn [{acc-log       :log
                acc-obstacles :obstacles
                :as acc}
               old-obstacle]
            (let [in-battle?          (= (:coords player) (:coords old-obstacle))
                  new-obstacle  (if in-battle? (t/damage old-obstacle) old-obstacle)]
              (assoc acc
                     :obstacles (conj-obstacles acc-obstacles new-obstacle)
                     :log       (cond
                                  (t/dead? new-obstacle)
                                  (log acc-log "You defeated the " (:name new-obstacle))

                                  in-battle?
                                  (log acc-log "You hit the "      (:name new-obstacle))

                                  :else
                                  acc-log))))
          (assoc old-state :obstacles [])
          new-obstacles))

(defn- hit-anything?
  [x ys]
  (some #{(:coords x)} (map :coords ys)))

(defn move
  [old-state axis movement]
  (let [{player      :player
         c-obstacles :obstacles
         :as candidate-state} (update-in old-state axis movement)]
    (-> (if (hit-anything? player c-obstacles)
          (do-battle old-state player c-obstacles)
          candidate-state)
        (update-in [:turn] inc))))
