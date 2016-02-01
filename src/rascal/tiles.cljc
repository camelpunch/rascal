(ns rascal.tiles
  (:require [clojure.string :refer [join]]))

(defprotocol Obstacle
  (attack?        [x roll])
  (defense?       [x roll])
  (dead?          [x])
  (damage         [x])
  (hit-verbiage   [x victim])
  (miss-verbiage  [x victim]))

(defn- battle-verbiage
  [perp victim k]
  (join " " [(:battle-name perp) (k perp) (:battle-name victim)]))

(defrecord Creature
    [tile health coords name battle-name hit-verb miss-verb]
  Obstacle
  (attack?        [x roll] (>= roll 5))
  (defense?       [x roll] (>= roll 5))
  (dead?          [x]      ((complement pos?) (:health x)))
  (damage         [x]      (update-in x [:health] - 40))
  (hit-verbiage   [x victim] (battle-verbiage x victim :hit-verb))
  (miss-verbiage  [x victim] (battle-verbiage x victim :miss-verb)))

(defn make-creature
  [tile creature-name x y]
  (map->Creature
   {:tile        tile
    :name        creature-name
    :health      100
    :coords      {:x x :y y}
    :battle-name (str "the " creature-name)
    :hit-verb    "hits"
    :miss-verb   "misses"}))

(defrecord Player
    [tile health coords battle-name hit-verb miss-verb]
  Obstacle
  (attack?        [x roll]   (>= roll 5))
  (defense?       [x roll]   (>= roll 5))
  (dead?          [x]        ((complement pos?) (:health x)))
  (damage         [x]        (update-in x [:health] - 40))
  (hit-verbiage   [x victim] (battle-verbiage x victim :hit-verb))
  (miss-verbiage  [x victim] (battle-verbiage x victim :miss-verb)))

(defn attack
  [candidate-player old-player obstacle attack-roll defense-roll]
  (let [obstacle-hit? (attack? candidate-player attack-roll)
        player-hit?   (defense? obstacle defense-roll)]
    {:obstacle-hit? obstacle-hit?
     :player-hit?   player-hit?
     :new-obstacle  (if obstacle-hit? (damage obstacle)   obstacle)
     :new-player    (if player-hit?   (damage old-player) old-player)}))

(defn make-player
  [x y]
  (map->Player
   {:tile        \@
    :health      100
    :coords      {:x x :y y}
    :battle-name "you"
    :hit-verb    "hit"
    :miss-verb   "miss"}))

(defn player-health [game] (get-in game [:player :health]))

(defn- axis-pos
  [length roll]
  (let [dice-sides    10
        wall-count     2
        zero-offset    1
        roll-multiply #(* % (/ roll dice-sides))]
    (-> length
        (- wall-count zero-offset)
        roll-multiply
        inc
        int)))

(defn place-creatures
  [& {[width height] :board-dimensions
      dice-rolls     :dice-rolls
      creature-pairs :creatures}]
  (map (fn [[tile name] [x-mult y-mult]]
         (let [x (axis-pos width x-mult)
               y (axis-pos height y-mult)]
           (make-creature tile name x y)))
       creature-pairs
       (take (count creature-pairs)
             (partition 2 dice-rolls))))

(defrecord Wall
    [tile name coords battle-name]
  Obstacle
  (attack?        [_ _]    false)
  (defense?       [_ _]    false)
  (dead?          [_]      false)
  (damage         [x]      x)
  (hit-verbiage   [_ _]    "Something")
  (miss-verbiage  [_ _]    "The wall doesn't want to fight"))

(defn make-wall-tile
  [x y]
  (map->Wall
   {:tile \#
    :name "Wall"
    :coords {:x x :y y}
    :battle-name "the wall"}))

(defn- vert-walls
  [width height]
  (reduce (fn [acc y]
            (conj acc
                  (make-wall-tile 0 y)
                  (make-wall-tile (dec width) y)))
          []
          (range 1 (dec height))))

(defn- horz-wall
  [width y]
  (map #(make-wall-tile % y) (range width)))

(defn make-walls-for-board
  [width height]
  (concat
   (horz-wall width 0)
   (vert-walls width height)
   (horz-wall width (dec height))))

(defn- make-empty-space
  [x y]
  {:tile \.
   :name "Empty space"
   :coords {:x x :y y}})

(defn make-board
  [width height]
  (for [y (range height)]
    (for [x (range width)]
      (make-empty-space x y))))

(def x-axis [:player :coords :x])
(def y-axis [:player :coords :y])
