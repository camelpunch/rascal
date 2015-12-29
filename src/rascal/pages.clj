(ns rascal.pages
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn index
  []
  (html5
   [:head
    (include-css "https://cdnjs.cloudflare.com/ajax/libs/normalize/3.0.3/normalize.css"
                 "style.css")]
   [:body
    [:div#app]
    (include-js "main.js")
    [:script "rascal.core.run();"]]))
