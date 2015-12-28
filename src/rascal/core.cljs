(ns rascal.core
  (:require [reagent.core :as r]))

(enable-console-print!)

(def . ".")
(def c "@")
(def board-width 20)
(def board
  [. . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . . . . . . . . . . . . . . . .
   . . . . . c . . . . . . . . . . . . . .
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

(defn keydown-handler
  [e]
  (println (-> e .-keyCode)))

(defn main
  [board width]
  [:div#game.textC
   {:tab-index 0
    :on-key-down keydown-handler}
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

(def main-focused
  (-> main (with-meta {:component-did-mount
                       (fn [this]
                         (.focus (js/document.getElementById "game")))})))

(defn ^:export run
  []
  (r/render [main-focused board board-width] (js/document.getElementById "app")))
