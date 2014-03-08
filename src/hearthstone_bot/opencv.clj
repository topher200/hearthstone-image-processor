(ns hearthstone-bot.opencv
  (:require
   [hearthstone-bot.file-system :as fs]
   )
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

(defn gray-image
  [image]
  (let [dest (create-empty-clone image)]
    (Imgproc/cvtColor image dest Imgproc/COLOR_BGR2GRAY)
    dest))

(defn load-image
  [filename]
  (let [image (Highgui/imread (.toString filename))]
    (if (.empty image)
      (error "failed to load" filename image)
      (debug filename "loaded:" image))
    (gray-image image)))

(defn crop-card-image
  [image]
  (let [border-width 30]
    (.submat image
             border-width (- (.rows image) border-width)
             border-width (- (.cols image) border-width))))

(defn save-image
  [image file-path]
  (fs/make-dirs file-path)
  (if-not (Highgui/imwrite file-path image)
    (warn "Unable to save image to" file-path)))

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
  "Our algorithm tells us to find the max location to get our match"
  [image]
  (.maxLoc (Core/minMaxLoc image)))

(defn find-match-score
  "Like find-match-location, algorithm specifies max is best"
  [image]
  (.maxVal (Core/minMaxLoc image)))

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
