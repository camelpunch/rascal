(ns rascal.tiles)

(declare horz-wall make-empty-space make-wall-tile)

(defn make-board
  [width height]
  (concat
   (horz-wall width 0)
   (for [y (range 1 (dec height))]
     (concat
      [(make-wall-tile 0 y)]
      (for [x (range 1 (dec width))]
        (make-empty-space x y))
      [(make-wall-tile (dec width) y)]))
   (horz-wall width (dec height))))

(defn make-creature
  [tile creature-name x y]
  {:tile   tile
   :name   creature-name
   :health 100
   :coords {:x x :y y}})

(defn make-player
  [x y]
  (make-creature \@ "Player" x y))

(defn wall? [x] (= \# (:tile x)))
(def alive? (comp pos? :health))
(def x-axis [:player :coords :x])
(def y-axis [:player :coords :y])

(defn- make-empty-space
  [x y]
  {:tile \.
   :name "Empty space"
   :coords {:x x :y y}})

(defn- make-wall-tile
  [x y]
  {:tile \#
   :name "Wall"
   :coords {:x x :y y}})

(defn- horz-wall
  [width y]
  [(map #(make-wall-tile % y) (range width))])
