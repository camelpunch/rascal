(ns rascal.tiles)

(defn- make-empty-space
  [x y]
  {:tile \.
   :name "Empty space"
   :coords {:x x :y y}})

(defn make-wall-tile
  [x y]
  {:tile \#
   :name "Wall"
   :coords {:x x :y y}})

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

(defn make-board
  [width height]
  (for [y (range height)]
    (concat
     (for [x (range width)]
       (make-empty-space x y)))))

(defn make-creature
  [tile creature-name x y]
  {:tile   tile
   :name   creature-name
   :health 100
   :coords {:x x :y y}})

(defn make-player
  [x y]
  (make-creature \@ "Player" x y))

(defn alive?
  [x]
  (or (not (contains? x :health)) (pos? (:health x))))
(def x-axis [:player :coords :x])
(def y-axis [:player :coords :y])
