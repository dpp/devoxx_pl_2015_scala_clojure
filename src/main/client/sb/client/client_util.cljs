(ns sb.client.client-util
  (:require [cognitect.transit :as t]))

(enable-console-print!)


(def t-reader (t/reader :json))

(def t-writer (t/writer :json))

(defn t-read
  [msg]
  (t/read t-reader msg))

(defn t-write
  [msg]
  (t/write t-writer msg))


(defn by-id [id] (. js/document (getElementById id)))
