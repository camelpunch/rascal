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

(defn- attack
  "Return a value representing an attack's consequences"
  [candidate-player old-player obstacle
   attack-roll attack-damage-roll
   defense-roll defense-damage-roll]
  (let [obstacle-hit? (t/attack? candidate-player attack-roll)
        player-hit?   (t/defense? obstacle defense-roll)
        new-obstacle  (if obstacle-hit?
                        (t/damage obstacle attack-damage-roll)
                        obstacle)
        new-player    (if (and player-hit? (not (t/dead? new-obstacle)))
                        (t/damage old-player defense-damage-roll)
                        old-player)]
    {:obstacle-hit? obstacle-hit?
     :player-hit?   player-hit?
     :new-obstacle  new-obstacle
     :new-player    new-player}))

(defn- battle-obstacle
  [{acc-log              :log
    acc-obstacles        :obstacles
    [attack-roll
     attack-damage-roll
     defense-roll
     defense-damage-roll
     & future-dice]      :dice-rolls
    old-player           :player
    :as                  acc}
   candidate-player
   old-obstacle]
  (let [{obstacle-hit? :obstacle-hit?
         player-hit?   :player-hit?
         new-obstacle  :new-obstacle
         new-player    :new-player}   (attack candidate-player
                                              old-player
                                              old-obstacle
                                              attack-roll
                                              attack-damage-roll
                                              defense-roll
                                              defense-damage-roll)]
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
