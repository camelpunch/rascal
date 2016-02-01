(ns rascal.battle
  (:require [clojure.string :refer [join upper-case]]
            [rascal.tiles :as t]
            [rascal.logging :refer [log]]))

(defn- upper-case-first
  [s]
  (join (conj (rest s)
              (upper-case (first s)))))

(defn- battle-log-entry
  [acc [hit? aggressor victim]]
  (log acc (upper-case-first
            (if hit?
              (t/hit-verbiage aggressor victim)
              (t/miss-verbiage aggressor victim)))))

(defn- conj-obstacles
  "Add to obstacles iff new-obstacle isn't dead."
  [obstacles new-obstacle]
  (if (t/dead? new-obstacle)
    obstacles
    (conj obstacles new-obstacle)))

(defn- battle-obstacle
  [{acc-log       :log
    acc-obstacles :obstacles
    dice-rolls    :dice-rolls
    old-player    :player
    :as           acc}
   candidate-player old-obstacle]
  (let [[attack-roll defense-roll & future-dice] dice-rolls
        obstacle-hit?                            (t/attack?  candidate-player attack-roll)
        player-hit?                              (t/defense? old-obstacle     defense-roll)
        new-obstacle                             (if obstacle-hit? (t/damage old-obstacle) old-obstacle)
        new-player                               (if player-hit?   (t/damage old-player)   old-player)]
    (assoc acc
           :player     new-player
           :dice-rolls future-dice
           :obstacles  (conj-obstacles acc-obstacles new-obstacle)
           :log        (if (t/dead? new-obstacle)
                         (log acc-log "You defeated the " (:name new-obstacle))
                         (reduce battle-log-entry
                                 acc-log
                                 [[obstacle-hit? candidate-player new-obstacle]
                                  [player-hit? new-obstacle candidate-player]])))))

(defn- hit-anything?
  [x ys]
  (some #{(:coords x)} (map :coords ys)))

(defn do-battle
  "Runs through new obstacles with new player position, updating game
  state accordingly."
  [state
   {candidate-player :player
    new-obstacles    :obstacles
    :as candidate-state}]
  (if (hit-anything? candidate-player new-obstacles)
    (reduce (fn [acc old-obstacle]
              (if (= (:coords candidate-player) (:coords old-obstacle))
                (battle-obstacle acc candidate-player old-obstacle)
                (update-in acc [:obstacles] conj old-obstacle)))
            (assoc state :obstacles [])
            new-obstacles)
    candidate-state))

