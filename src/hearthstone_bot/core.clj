(ns hearthstone-bot.core
  (:require
   [hearthstone-bot.file-system :as fs]
   [hearthstone-bot.opencv :as cv]
   [clj-time.core :as time]
   )
  (:use
   [clojure.tools.logging :only (debug info warn error spy)]

   ;; for REPL use
   [clojure.pprint :only [pprint]]
   [clojure.reflect :only [reflect]]
   ))

(defn find-and-draw-match
  [card-path]
  (let [
        start-time (time/now)
        game-image (cv/load-image (fs/path-to-resource "croc_board.png"))
        card-image (cv/crop-image (cv/load-image (.toString card-path)))
        save-path (fs/path-to-resource "res" (.getName card-path))
        ]
    (info "running" (.getName card-path))
    (cv/draw-rectangle
     game-image 
     (cv/find-match-location (cv/template-match game-image card-image))
     (.size card-image))
    (cv/save-image game-image save-path)
    (info "completed card in"
          (time/in-millis (time/interval start-time (time/now))) "ms")))

(def all-cards (fs/get-all-files fs/cards-directory))

(defn find-and-draw-all-matches
  []
  (map find-and-draw-match all-cards))

(defn -main
  [& args]
  (error "---starting---")
  (let [start-time (time/now)]
    (doall (find-and-draw-all-matches))
    (info "exiting after"
          (time/in-seconds (time/interval start-time (time/now))) "."
          (time/in-millis (time/interval start-time (time/now))) "secs")))

(-main)
