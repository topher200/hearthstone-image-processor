(ns hearthstone-bot.core
  (:use [clojure.tools.logging :only (debug info warn error spy)])
  (:import
   org.opencv.core.Core
   org.opencv.core.Mat
   org.opencv.core.Point
   org.opencv.core.Scalar
   org.opencv.imgproc.Imgproc
   org.opencv.highgui.Highgui
   ))
   
(defn create-empty-clone
  "Creates an empty Mat the same size and type as the inputted one"
  [image]
 (Mat. (.rows image) (.cols image) (.type image)))

(defn path-to-resource
  [resource-filename]
  ; HACK: .getPath returns a leading slash, which fails on Windows. Specifying
  ; the path manually.
   (clojure.string/join "" ["d:/dev/clojure/hearthstone-bot/resources/"
                            resource-filename]))
(defn load-image
  [filename]
  (let [image (Highgui/imread (path-to-resource filename))]
    (if (.empty image)
      (error "failed to load" filename image)
      (debug filename "loaded:" image))
    image))

(defn gray-image
  [image]
  (let [dest (create-empty-clone image)]
    (Imgproc/cvtColor image dest Imgproc/COLOR_BGR2GRAY)
    dest))

(defn crop-image
  [image]
  (let [border-width 30]
    (.submat image
             border-width (- (.rows image) border-width)
             border-width (- (.cols image) border-width))))

(defn save-image
  [image path]
  (Highgui/imwrite path image))

(defn create-template-result-mat
  [image template]
  (Mat. (+ (- (.width image) (.width template)) 1)
        (+ (- (.height image) (.height template)) 1)
        (.type image)))

(defn template-match
  [image template]
  (let [dest (create-template-result-mat image template)
        ;; requires minLoc
        ;; method Imgproc/TM_SQDIFF]
        ;; method Imgproc/TM_SQDIFF_NORMED]

        ;; requires maxLoc
        ;; method Imgproc/TM_CCORR]
        method Imgproc/TM_CCORR_NORMED]
        ;; method Imgproc/TM_CCOEFF]
        ;; method Imgproc/TM_CCOEFF_NORMED]
    (Imgproc/matchTemplate image template dest method)
    (debug "match-image:" dest)
    dest))

(defn normalize
  [image]
  (let [dest (create-empty-clone image)]
    (Core/normalize image dest 0 1 Core/NORM_MINMAX)
    (debug "normalized:" dest)
    dest))

(defn find-match-location
  "Our algorithm requires us to find the max location for our match"
  [image]
  (let [location (.maxLoc (Core/minMaxLoc image))]
  ;; (let [location (.minLoc (Core/minMaxLoc image))]
    (info "match maxVal:" (.maxVal (Core/minMaxLoc image)))
    (info "match minVal:" (.minVal (Core/minMaxLoc image)))
    (debug "match location:" location)
    location))

(defn bounding-rectangle-opposite-vertex
  [match-location template-size]
  (Point. (+ (.x match-location) (.width template-size))
          (+ (.y match-location) (.height template-size))))

(defn draw-rectangle
  [image match-location template-size]
  (let [rectangle-bound
        (bounding-rectangle-opposite-vertex match-location template-size)
        color (Scalar. 255 255 255)
        ]
    (debug "drawing from" match-location "to" rectangle-bound)
    (Core/rectangle image match-location rectangle-bound color)))

(defn -main
  []
  (info "---start---")
  (clojure.lang.RT/loadLibrary Core/NATIVE_LIBRARY_NAME)
  (let [
        ;; board-image-name "croc_board.png"
        board-image-name "boar_hand.png"
        card-image-name "boar_card.png"
        ;; card-image-name "croc_card.png"
        board-image-color (load-image board-image-name)
        board-image (gray-image board-image-color)
        card-image (gray-image (load-image card-image-name))
        card-image-cropped (crop-image card-image)
        match-image (template-match board-image card-image-cropped)
        normalized-image (normalize match-image)
        match-location (find-match-location match-image)
        template-size (.size card-image-cropped)
        ;; board-to-draw board-image-color
        board-to-draw board-image
        save-path (path-to-resource "match.png")
        ]
    (info "checking for" card-image-name "on" board-image-name)
    (draw-rectangle board-to-draw match-location template-size)
    (save-image board-to-draw save-path)))
(-main)
