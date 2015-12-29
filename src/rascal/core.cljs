(ns rascal.core
  (:require [reagent.core :as r]
            [rascal.board :as b :refer [->Board]]))

(enable-console-print!)

(def state (r/atom {:board (->Board 15 15)
                    :player {:coords {:x 7 :y 7}}}))

(def keymap
  {72 b/move-left
   76 b/move-right
   75 b/move-up
   74 b/move-down})

(defn keydown-handler
  [e]
  (when-let [f (keymap (-> e .-keyCode))]
    (swap! state f)))

(defn main
  []
  [:div#game.invisibleFocus.textC
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
      (b/render @state))]]])

(def main-focused
  (-> main (with-meta {:component-did-mount
                       (fn [this]
                         (.focus (js/document.getElementById "game")))})))

(defn ^:export run
  []
  (r/render [main-focused] (js/document.getElementById "app")))
