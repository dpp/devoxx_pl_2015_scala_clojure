(ns sb.client.core
  (:require [dragonmark.util.core]
            [sb.client.client-util :refer [by-id t-read t-write]]
            [reagent.core :refer [atom adapt-react-class render-component]]
            [dragonmark.web.core :refer [xf xform]])
  (:require-macros [dragonmark.web.mac :as mac])
  )

(enable-console-print!)

(defonce chats (atom []))

(defn receive [x]
  (let
    [msg (t-read x)]
    (cond
      (seq? msg)
      (reset! chats (vec msg))

      (string? msg)
      (swap! chats conj msg)

      :else nil))
  )

(def page-text (mac/insert-template "chat.html"))

(defn page []
  (xform page-text
         ["li" :* @chats]
         ["button"
          {:onclick
           (fn [] (let [in (by-id "in")]
                    (-> in .-value t-write js/sendToServer)
                    (set! (.-value in) "")
                    ))}]
         ))

(defn main []
  (render-component [page] (by-id "app")))

(main)
