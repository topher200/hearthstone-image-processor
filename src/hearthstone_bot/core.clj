(ns hearthstone-bot.core
  (:require
   [hearthstone-bot.file-system :as fs]
   [hearthstone-bot.opencv :as cv]
   [hearthstone-bot.robot :as robot]
   [taoensso.timbre :as timbre]
   [clj-time.core :as time]
   )
  (:use
   ;; for REPL use
   [clojure.pprint :only [pprint]]
   [clojure.reflect :only [reflect]]
   ))
(timbre/refer-timbre)
(timbre/set-config! [:level] :debug)
(timbre/set-config! [:appenders :spit :enabled?] true)
(timbre/set-config! [:shared-appender-config :spit-filename]
                    "d:/log/hearthstone-image-processor.log")
(error "starting core.clj")

(defn draw-board
  []
  (cv/save-image
   (cv/crop-board-image (cv/load-image (fs/path-to-resource "croc_board.png")))
   (fs/path-to-resource "cropped_board.png")))

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

(defn get-match-score
  [card-path]
  (let [start-time (time/now)
        game-image (cv/crop-board-image
                    (cv/load-image (fs/path-to-resource "croc_board.png")))
        card-image (cv/crop-card-image (cv/load-image (.toString card-path)))
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
  (key (apply max-key val (reduce (fn [hash-map card-path]
            (assoc hash-map card-path (get-match-score card-path)))
          {} all-cards))))
(warn "matching card:" (get-best-match-score))

(defn save-screenshot
  [filename]
  (cv/save-image (cv/gray-image (robot/buffered-image-to-mat
                                 (robot/get-screenshot-buffered-image)))
                 filename))
(save-screenshot (fs/path-to-resource "temp.png"))

(defn -main
  [& args]
  (error "---starting---")
  (let [start-time (time/now)]
    (get-best-match-score)
    (info "exiting after"
          (time/in-seconds (time/interval start-time (time/now))) "."
          (time/in-millis (time/interval start-time (time/now))) "secs")))
