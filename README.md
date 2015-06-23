# Scala Bay 2015

Code for [@dpp](https://twitter.com/dpp)'s Devoxx Poland 2015
presentation. It's a blend of Scala, Lift, Clojure,
ClojureScript, and Reagent.


To run, you'll need 3 different terminals...

Terminal 1:

```
lein zinc cc
```

This will recompile all the Scala code

Terminal 2:

```
lein figwheel
```

This gives you Figwheel browser REPL stuff.

Terminal 3:

```
lein repl

(require 'sb.server.jetty-runner)

(sb.server.jetty-runner/-main) ;; start the web server
```

Point your browser to [http://localhost:8080](http://localhost:8080)
