(ns rascal.game
  (:require [rascal.tiles :as t]))

(defn- roll
  [rolls n]
  (let [[rolled future] (split-at n rolls)]
    {:rolled rolled
     :future future}))

(defn make-game
  [& {player-coords    :player
      board-dimensions :board
      monsters         :monsters
      dice-rolls       :dice-rolls}]
  (let [dice (roll dice-rolls (* 2 (count monsters)))]
    {:turn       1
     :player     (apply t/make-player player-coords)
     :board      (apply t/make-board board-dimensions)
     :obstacles  (concat (apply t/make-walls-for-board board-dimensions)
                         (t/place-creatures :board-dimensions board-dimensions
                                            :dice-rolls       (:rolled dice)
                                            :creatures        monsters))
     :dice-rolls (:future dice)
     :log        ["You entered the dungeon"]}))

(defn- log
  [xs & ys]
  (conj xs (apply str ys)))

(defn- conj-obstacles
  "Add to obstacles iff new-obstacle isn't dead."
  [obstacles new-obstacle]
  (if (t/dead? new-obstacle)
    obstacles
    (conj obstacles new-obstacle)))

(defn- aggressor-verb
  [aggressor]
  (if (= "You" aggressor)
    {true " hit "  false " miss "}
    {true " hits " false " misses "}))

(defn- battle-log-entry
  [acc [hit? aggressor victim]]
  (log acc aggressor ((aggressor-verb aggressor) hit?) victim))

(defn- hits-on-target
  [dice]
  (map #(>= % 5) dice))

(defn- do-battle
  "Runs through new obstacles with new player position, updating game
  state accordingly."
  [old-state candidate-player new-obstacles]
  (reduce (fn [{acc-log       :log
                acc-obstacles :obstacles
                dice-rolls    :dice-rolls
                :as           acc}
               old-obstacle]
            (if (= (:coords candidate-player) (:coords old-obstacle))
              (let [dice                        (roll dice-rolls 2)
                    [obstacle-hit? player-hit?] (hits-on-target (:rolled dice))
                    new-obstacle                (if obstacle-hit?
                                                  (t/damage old-obstacle)
                                                  old-obstacle)
                    new-player                  (if player-hit?
                                                  (t/damage (:player old-state))
                                                  (:player old-state))]
                (assoc acc
                       :player     new-player
                       :dice-rolls (:future dice)
                       :obstacles  (conj-obstacles acc-obstacles new-obstacle)
                       :log        (if (t/dead? new-obstacle)
                                     (log acc-log "You defeated the " (:name new-obstacle))
                                     (reduce battle-log-entry
                                             acc-log
                                             [[obstacle-hit? "You" (str "the " (:name new-obstacle))]
                                              [player-hit? (str "The " (:name new-obstacle)) "you"]]))))
              (update-in acc [:obstacles] conj old-obstacle)))
          (assoc old-state :obstacles [])
          new-obstacles))

(defn- hit-anything?
  [x ys]
  (some #{(:coords x)} (map :coords ys)))

(defn- extra-log-messages
  [{player :player
    :as    s}]
  (if (t/dead? player)
    (update-in s [:log] log "You die")
    s))

(defn move
  [old-state f]
  (if (t/dead? (:player old-state))
    old-state
    (let [{player      :player
           c-obstacles :obstacles
           :as         candidate-state} (f old-state)]
      (-> (if (hit-anything? player c-obstacles)
            (do-battle old-state player c-obstacles)
            candidate-state)
          extra-log-messages
          (update-in [:turn] inc)))))

(def left  #(update-in % t/x-axis dec))
(def right #(update-in % t/x-axis inc))
(def up    #(update-in % t/y-axis dec))
(def down  #(update-in % t/y-axis inc))
