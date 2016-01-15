(ns rascal.tiles)

(defprotocol Obstacle
  (attack?  [x roll])
  (defense? [x roll])
  (dead?    [x])
  (damage   [x]))

(defrecord Creature
    [tile name health coords]
  Obstacle
  (attack?  [x roll] (>= roll 5))
  (defense? [x roll] (>= roll 5))
  (dead?    [x]      ((complement pos?) (:health x)))
  (damage   [x]      (update-in x [:health] - 40)))

(defn make-creature
  [tile creature-name x y]
  (map->Creature
   {:tile   tile
    :name   creature-name
    :health 100
    :coords {:x x :y y}}))

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
    [tile name coords]
  Obstacle
  (attack?  [_ _] false)
  (defense? [_ _] false)
  (dead?    [_]   false)
  (damage   [x]   x))

(defn make-wall-tile
  [x y]
  (map->Wall
   {:tile \#
    :name "Wall"
    :coords {:x x :y y}}))

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

(defn make-player
  [x y]
  (make-creature \@ "Player" x y))

(def x-axis [:player :coords :x])
(def y-axis [:player :coords :y])
