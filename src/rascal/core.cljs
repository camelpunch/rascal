(ns rascal.core
  (:require [reagent.core :as r]
            [rascal.game :as g :refer [make-game move left right up down]]
            [rascal.tiles :as t]
            [rascal.render :refer [render]]))

(enable-console-print!)

(declare main-focused)
(defn ^:export run
  []
  (r/render [main-focused] (js/document.getElementById "app")))

(def state
  (r/atom
   (make-game
    :board      [30 25]
    :player     (t/make-player 15 23 1)
    :monsters   [[\j "Jackal"]
                 [\r "Rat"]
                 [\p "Pheasant"]]
    :dice-rolls (repeatedly #(rand-int 10)))))

(def debug-fixture
  {:game (make-game :board      [12 12]
                    :player     (t/make-player 4 5 10)
                    :monsters   [[\j "Jackal"]]
                    :dice-rolls [ 5  5      ; Jackal at [5 5]

                                 0          ; Player misses
                                 9          ; damage irrelevant
                                 10         ; Jackal hits
                                 4          ; 40 damage

                                 0          ; Player misses
                                 9          ; damage irrelevant
                                 10         ; Jackal hits
                                 4          ; 40 damage

                                 0          ; Player misses
                                 9          ; damage irrelevant
                                 10         ; Jackal hits and kills player
                                 4          ; with 40
                                 ])
   :path   [right right right]})

(def keymap
  {72 #(move % left)
   76 #(move % right)
   75 #(move % up)
   74 #(move % down)
   89 #(move % (comp left up))
   85 #(move % (comp right up))
   78 #(move % (comp left down))
   77 #(move % (comp right down))})

(defn keydown-handler
  [e]
  (if (= 80 (.-keyCode e))
    (do
      (reset! state (:game debug-fixture))
      (doseq [[idx step] (map-indexed vector (:path debug-fixture))]
        (js/setTimeout #(swap! state move step) (* idx 1000))))
    (when-let [f (keymap (.-keyCode e))]
      (println "Keydown")
      (swap! state f))))

(defn game-cell
  [idx contents]
  [:td.cell {:key (str "td" idx)} contents])

(defn game-row
  [idx contents]
  [:tr {:key (str "tr" idx)}
   (map-indexed game-cell contents)])

(defn health-line
  [idx obstacle]
  [:li {:key (str "health" idx)}
   [:h2.bld "(" (:tile obstacle) ") " (:name obstacle)]
   [:p.break "Health: " (:health obstacle)]])

(defn log-line
  [idx msg]
  [:li {:key (str "log" idx)} msg])

(defn main
  []
  [:div#game.page.invisibleFocus
   {:tab-index 0
    :on-key-down keydown-handler}
   [:div.break.yui3-g
    [:div.yui3-u-1-3
     [:h2.bld "Player"]
     [:p "Health: " (t/player-health @state)]
     [:p "Turn: " (:turn @state)]]
    [:div.yui3-u-1-3.textC
     [:h1.mainHeading "Rascal"]
     [:p [:a {:href "https://github.com/camelpunch/rascal"} "Source"]]]
    [:div.yui3-u-1-3]]
   [:div.yu3-g
    [:div.yui3-u-1-4
     [:ul (map-indexed health-line (filter #(contains? % :health) (:obstacles @state)))]]
    [:div.board.yui3-u-3-4
     [:table.textC.break
      [:tbody (map-indexed game-row (render @state))]]
     [:h2.bld "Keys:"]
     [:p "h,j,k,l,y,u,n,m - movement"]
     [:h2.bld "Activity:"]
     [:ol (map-indexed log-line (reverse (:log @state)))]]]])

(def main-focused
  (-> main (with-meta {:component-did-mount
                       (fn [this]
                         (.focus (js/document.getElementById "game")))})))
