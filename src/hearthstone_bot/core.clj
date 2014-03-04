(ns hearthstone-bot.core
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
      (println "failed to load" filename image)
      (println filename "loaded:" image))
    image))

(defn gray-image
  [image]
  (let [dest (create-empty-clone image)]
    (Imgproc/cvtColor image dest Imgproc/COLOR_BGR2GRAY)
    dest))

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
    (println "match-image:" dest)
    dest))

(defn normalize
  [image]
  (let [dest (create-empty-clone image)]
    (Core/normalize image dest 0 1 Core/NORM_MINMAX)
    (println "normalized:" dest)
    dest))

(defn find-match-location
  "Our algorithm requires us to find the max location for our match"
  [image]
  (let [location (.maxLoc (Core/minMaxLoc image))]
  ;; (let [location (.minLoc (Core/minMaxLoc image))]
    (println "match maxVal:" (.maxVal (Core/minMaxLoc image)))
    (println "match minVal:" (.minVal (Core/minMaxLoc image)))
    (println "match location:" location)
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
    (println "drawing from" match-location "to" rectangle-bound)
    (Core/rectangle image match-location rectangle-bound color)))

(defn main
  []
  (println "---start---")
  (clojure.lang.RT/loadLibrary Core/NATIVE_LIBRARY_NAME)
  (let [board-image (gray-image (load-image "croc_board.png"))
        croc-image (gray-image (load-image "croc_card.png"))
        match-image (template-match board-image croc-image)
        normalized (normalize match-image)
        match-location (find-match-location normalized)
        template-size (.size croc-image)
        save-path (path-to-resource "match.png")]
    (draw-rectangle board-image match-location template-size)
    (save-image normalized save-path)))

(main)
