(ns sb.client.core
  (:require [dragonmark.util.core]
            [sb.client.client-util :refer [t-read t-write by-id swap-id!]]
            [reagent.core :refer [atom adapt-react-class render-component]]
            [dragonmark.web.core :refer [xf xform]])
  (:require-macros [dragonmark.web.mac :as mac :refer [insert-template]])
  )

(enable-console-print!)

(defonce chats (atom []))

(defn receive [x]
  (let
    [msg (t-read x)]
    (cond
      (sequential? msg)
      (reset! chats (vec msg))

      (string? msg)
      (swap! chats conj msg)

      :else nil))
  )

(def page-text (insert-template "chat.html"))

(defn page []
  (xform page-text
         ["li" :* @chats]
         ["button"
          {:onclick
           (fn [] (some->
                    (swap-id! "in" "")
                    first
                    t-write
                    js/sendToServer))}]
         ))

(defn main []
  (render-component [page] (by-id "app")))

(main)
