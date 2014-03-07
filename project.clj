(defproject hearthstone-bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [criterium "0.4.3"]
                 [clj-time "0.6.0"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.logging "0.2.6"]
                 [org.slf4j/slf4j-log4j12 "1.7.6"]
                 [opencv/opencv "2.4.8"]
                 [opencv/opencv-native "2.4.8"]
                 ]
  :injections
  [(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)]
  :resource-paths ["resources"]
  :main hearthstone-bot.core
  )
