(ns sb.server.jetty-runner
  (:import [org.eclipse.jetty.server.handler ContextHandler]
           [org.eclipse.jetty.server.nio SelectChannelConnector]
           [org.eclipse.jetty.server Server Connector]
           [org.eclipse.jetty.webapp WebAppContext]
           (org.slf4j LoggerFactory Logger)
           (ch.qos.logback.classic Level))
  )

;; Run the Jetty server

(def server-atom (atom nil))

(defn stop
  []
  (println "Stopping")
  (some-> @server-atom .stop .join)
  (reset! server-atom nil)
  (println "Stopped"))

(defn -main [& args]
  (when @server-atom (stop))
  (let [root (LoggerFactory/getLogger Logger/ROOT_LOGGER_NAME)]
    (.setLevel root Level/INFO))
  (let [server (Server.)
        scc (SelectChannelConnector.)]
    (.setPort scc 8080)
    (.setConnectors server (into-array Connector [scc]))
    (let [context (WebAppContext.)]
      (.setWar context "src/webapp")
      (.setServer context server)
      (let [context0 (ContextHandler.)]
        (.setHandler context0 context)
        (.setHandler server context0)
        (reset! server-atom server)
        (.start server)))
    ))

