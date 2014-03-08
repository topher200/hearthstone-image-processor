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

(defn draw-board
  []
  (cv/save-image
   (cv/crop-board-image (cv/load-image (fs/path-to-resource "croc_board.png")))
   (fs/path-to-resource "cropped_board.png")))
(draw-board)

  
(defn find-and-draw-match
  [card-path]
  (let [
        start-time (time/now)
        game-image (cv/crop-board-image
                    (cv/load-image (fs/path-to-resource "croc_board.png")))
        card-image (cv/crop-card-image (cv/load-image (.toString card-path)))
        save-path (fs/path-to-resource "res" (fs/get-card-name card-path))
        ]
    (info "running" (fs/get-card-name card-path))
    (cv/draw-rectangle
     game-image 
     (cv/find-match-location (cv/template-match game-image card-image))
     (.size card-image))
    (cv/save-image game-image save-path)
    (info "completed card in"
          (time/in-millis (time/interval start-time (time/now))) "ms")))

(defn board-image-memo
  [board-image-path]
  (cv/crop-board-image (cv/load-image board-image-path)))

(defn card-image-memo
  [card-image-path]
  (cv/crop-card-image (cv/load-image card-image-path)))

(defn load-card-images
  [cards]
  (doall (map card-image-memo cards)))

(defn get-match-score
  [card-path]
  (let [start-time (time/now)
        game-image (board-image-memo (fs/path-to-resource "croc_board.png"))
        card-image (card-image-memo card-path)
        score (cv/find-match-score (cv/template-match game-image card-image))]
      (info (fs/get-card-name card-path) "score" score)
      (info "completed card in"
            (time/in-millis (time/interval start-time (time/now))) "ms")
      score))

(def all-cards (fs/get-all-files fs/cards-directory))

(defn find-and-draw-all-matches
  []
  (map find-and-draw-match all-cards))

(defn get-best-match-score
  []
  (info "winner!" (fs/get-card-name
                   (first (sort-by (memoize get-match-score) > all-cards)))))

(defn -main
  [& args]
  (error "---starting---")
  (let [start-time (time/now)]
    (info "loading images")
    (load-card-images all-cards)
    (info "running matching")
    (get-best-match-score)
    (info "exiting after"
          (time/in-seconds (time/interval start-time (time/now))) "."
          (time/in-millis (time/interval start-time (time/now))) "secs")))

(-main)
