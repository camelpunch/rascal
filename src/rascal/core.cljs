(ns rascal.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(def . ".")
(def board-width 20)
(def board
  [. . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .])

(defn main
  [board width]
  [:div.textC
   [:h1 "Rascal"]
   [:table.marginC
    [:tbody
     (map-indexed
      (fn [row-idx row]
        [:tr {:key (str "tr" row-idx)}
         (map-indexed
          (fn [cell-idx cell]
            [:td.cell {:key (str "td" cell-idx)}
             cell])
          row)])
      (partition width board))]]])

(defn ^:export run
  []
  (r/render [main board board-width] (js/document.getElementById "app")))
