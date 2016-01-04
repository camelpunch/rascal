(ns rascal.tiles)

(defprotocol Obstacle
  (alive? [x])
  (damage [x]))

(defrecord Creature
    [tile name health coords]
  Obstacle
  (alive? [x] (-> x :health pos?))
  (damage [x] (update-in x [:health] - 50)))

(defn make-creature
  [tile creature-name x y]
  (map->Creature
   {:tile   tile
    :name   creature-name
    :health 100
    :coords {:x x :y y}}))

(defrecord Wall
    [tile name coords]
  Obstacle
  (alive? [_] true)
  (damage [x] x))

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
