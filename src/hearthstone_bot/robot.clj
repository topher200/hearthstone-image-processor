(ns hearthstone-bot.robot
  (:import
   java.awt.Rectangle
   java.awt.Robot
   java.nio.ByteBuffer
   org.opencv.core.CvType
   ))

(defn get-screenshot-buffered-image
  ;; Returns a BufferedImage, which is cool in Java-land but isn't with opencv
  []
  (.createScreenCapture (Robot.) (Rectangle. 100 100)))

(defn buffered-image-to-mat
  ;; from http://stackoverflow.com/a/21175472
  [buffered-image]
  (let [byte-buffer Bytebuffer.
        image-data (.getData (.getDataBuffer (.getRaster buffered-image)))]
    (Bytebuffer/allocate byte-buffer (* (.length image-data) 4))
    (let [int-buffer (.asIntBuffer byte-buffer)]
      (.put int-buffer image-data)
      (.put (Mat. (.getHeight buffered-image) (.getWidth buffered-image)
                  CvType/CV_8UC3)
            0 0 (.array byte-buffer)))))

