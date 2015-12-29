(ns rascal.core
  (:require [reagent.core :as r]
            [rascal.board :as b]))

(enable-console-print!)

(def state (r/atom {:board (b/create-board)}))

(defn left
  [s]
  (b/move-left (:board s) b/c))

(defn right
  [s]
  (println "right")
  s)

(defn up
  [s]
  (println "up")
  s)

(defn down
  [s]
  (println "down")
  s)

(def keymap
  {72 left
   76 right
   75 up
   74 down})

(defn keydown-handler
  [e]
  (swap! state (keymap (-> e .-keyCode)))
  (println @state))

(defn main
  []
  [:div#game.invisibleFocus.textC
   {:tab-index 0
    :on-key-down keydown-handler}
   [:h1 "Rascal"]
   [:table.marginC
    [:tbody
     (let [board (:board @state)]
       (map-indexed
        (fn [row-idx row]
          [:tr {:key (str "tr" row-idx)}
           (map-indexed
            (fn [cell-idx cell]
              [:td.cell {:key (str "td" cell-idx)}
               cell])
            row)])
        board))]]])

(def main-focused
  (-> main (with-meta {:component-did-mount
                       (fn [this]
                         (.focus (js/document.getElementById "game")))})))

(defn ^:export run
  []
  (r/render [main-focused] (js/document.getElementById "app")))
