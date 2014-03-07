(ns hearthstone-bot.core
  (:require
   [hearthstone-bot.file-system :as fs]
   [hearthstone-bot.opencv :as cv]
   )
  (:use [clojure.tools.logging :only (debug info warn error spy)]))

(defn find-and-draw-croc-match
  []
  (let [game-image (load-image (fs/path-to-resource "croc_board.png"))
        card-image (crop-image (load-image (fs/path-to-resource "croc_card.png")))
        save-path (fs/path-to-resource "res" "match.png")]
    (draw-rectangle
     game-image 
     (cv/find-match-location (cv/template-match game-image card-image))
     (.size card-image))
    (cv/save-image game-image save-path)))

(defn -main
  []
  (info "---start---")
  (let [
        board-image-name (path-to-resource "croc_board.png")
        ;; board-image-name (path-to-resource "boar_hand.png")
        ;; card-image-name (path-to-resource "boar_card.png")
        card-image-name (path-to-resource "croc_card.png")
        board-image-color (cv/load-image board-image-name)
        board-image (cv/gray-image board-image-color)
        card-image (cv/gray-image (load-image card-image-name))
        card-image-cropped (cv/crop-image card-image)
        match-image (cv/template-match board-image card-image-cropped)
        normalized-image (cv/normalize match-image)
        match-location (cv/find-match-location match-image)
        template-size (.size card-image-cropped)
        ;; board-to-draw board-image-color
        board-to-draw normalized-image
        save-path (fs/path-to-resource "match.png")
        ]
    (info "checking for" card-image-name "on" board-image-name)
    ;; (cv/draw-rectangle board-to-draw match-location template-size)
    (cv/save-image board-to-draw save-path)))

(find-and-draw-croc-match)
;; (-main)
