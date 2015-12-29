(ns rascal.board)

(def . ".")
(def c "@")
(defn create-board
  []
  [[. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . c . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]
   [. . . . . . . . . . . . . . . . . . . .]])

(defn move-right-in-row
  [acc [x & xs] character]
  (cond
    (empty? xs)     (concat acc [x])
    (= character x) (concat acc [. character] (drop 1 xs))
    :else           (recur (concat acc [x])
                           xs
                           character)))

(comment
  (move-right-in-row [] [c . .] c)
  (move-right-in-row [] [. . .] c)
  (move-right-in-row [] [. . c] c)
  )

(defn move-right
  [game character]
  (map #(move-right-in-row [] % character) game))

(comment

  )
