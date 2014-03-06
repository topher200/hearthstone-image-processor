(ns hearthstone-bot.core
  (:require
   [hearthstone-bot.file-system :as fs]
   [hearthstone-bot.opencv :as cv]
   )
  (:use [clojure.tools.logging :only (debug info warn error spy)]))

(defn -main
  []
  (info "---start---")
  (clojure.lang.RT/loadLibrary Core/NATIVE_LIBRARY_NAME)
  (let [
        ;; ;; board-image-name "croc_board.png"
        ;; board-image-name "boar_hand.png"
        ;; card-image-name "boar_card.png"
        ;; ;; card-image-name "croc_card.png"
        ;; board-image-color (load-image board-image-name)
        ;; board-image (gray-image board-image-color)
        ;; card-image (gray-image (load-image card-image-name))
        ;; card-image-cropped (crop-image card-image)
        ;; match-image (template-match board-image card-image-cropped)
        ;; normalized-image (normalize match-image)
        ;; match-location (find-match-location match-image)
        ;; template-size (.size card-image-cropped)
        ;; ;; board-to-draw board-image-color
        ;; board-to-draw board-image
        ;; save-path (path-to-resource "match.png")
        ]
    (error (get-all-files fs/cards-directory))))
    ;; (info "checking for" card-image-name "on" board-image-name)
    ;; (draw-rectangle board-to-draw match-location template-size)
    ;; (save-image board-to-draw save-path)))
(-main)
