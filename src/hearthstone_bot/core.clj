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

(defn get-match-score
  [card-path]
  (let [start-time (time/now)
        game-image (cv/load-image (fs/path-to-resource "boar_board.png"))
        card-image (cv/crop-image (cv/load-image (.toString card-path)))
        score (cv/find-match-score (cv/template-match game-image card-image))]
      (info (.getName card-path) "score" score)
      (info "completed card in"
            (time/in-millis (time/interval start-time (time/now))) "ms")
      score))

(def all-cards (fs/get-all-files fs/cards-directory))

(defn find-and-draw-all-matches
  []
  (map find-and-draw-match all-cards))

(defn -main
  [& args]
  (error "---starting---")
  (let [start-time (time/now)]
    (info "winner!" (first (sort-by get-match-score > all-cards)))
    (info "exiting after"
          (time/in-seconds (time/interval start-time (time/now))) "."
          (time/in-millis (time/interval start-time (time/now))) "secs")))

(-main)
