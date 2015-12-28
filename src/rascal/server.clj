(ns rascal.server
  (:require [ring.util.response :refer [response content-type]]
            [ring.middleware.resource :refer [wrap-resource]]
            [bidi.bidi :as bidi]
            [bidi.ring :refer [make-handler]]
            [rascal.pages :as pages]))

(defn serve-index
  [request]
  (-> (pages/index)
      (response)
      (content-type "text/html; charset=UTF-8")))

(def routes ["/" {"" {:get serve-index}}])

(def app (-> routes
             (make-handler)
             (wrap-resource "/")))
