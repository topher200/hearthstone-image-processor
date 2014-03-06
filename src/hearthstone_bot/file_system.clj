(ns hearthstone-bot.file-system)

(def resources-directory "d:/dev/clojure/hearthstone-bot/resources/")
(def cards-directory
  (clojure.string/join "" [resources-directory, "card-images"]))

(defn get-all-files
  [directory]
  (filter (fn [f] (.isFile f)) (file-seq (clojure.java.io/file directory))))

(defn path-to-resource
  [resource-filename]
  ; HACK: .getPath returns a leading slash, which fails on Windows. Specifying
  ; the path manually.
   (clojure.string/join "" [resources-directory resource-filename]))
