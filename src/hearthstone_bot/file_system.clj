(ns hearthstone-bot.file-system)

(def root-dir "c:/dev/hearthstone-image-processor")
(def resources-directory (clojure.string/join "/" [root-dir "resources"]))
(def cards-directory
  (clojure.string/join "/" [resources-directory, "some-card-images"]))

(defn get-all-files
  [directory]
  (filter (fn [f] (.isFile f)) (file-seq (clojure.java.io/file directory))))

(defn path-to-resource
  "Recursively combines the args to create a path. Assumes the last arg is a
filename, and the rest are dirs leading up to it."
  ;; HACK: .getPath returns a leading slash, which fails on Windows. Specifying
  ;; the path manually.
  ;; TODO: I know this can be more refactored to cleanly take more args.
  ([filename]
     (clojure.string/join "/" [resources-directory filename]))
  ([dirname & more]
     (path-to-resource (clojure.string/join "/" (concat [dirname] more)))))

(defn make-dirs
  [file-path]
  (clojure.java.io/make-parents file-path))
  
(defn get-card-name
  [card-image-path]
  (.getName card-image-path))
