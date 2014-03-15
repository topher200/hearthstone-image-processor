(ns hearthstone-bot.robot
  (:require
   [taoensso.timbre :as timbre]
   )
  (:import
   java.awt.Rectangle
   java.awt.Robot
   java.awt.Toolkit
   java.nio.ByteBuffer
   org.opencv.core.CvType
   org.opencv.core.Mat
   ))
(timbre/refer-timbre)

(defn get-screen-dimension
  []
  (Rectangle. (.getScreenSize (Toolkit/getDefaultToolkit))))

(defn get-screenshot-buffered-image
  ;; Returns a BufferedImage, which is cool in Java-land but isn't with opencv
  []
  (.createScreenCapture (Robot.) (get-screen-dimension)))

(defn buffered-image-to-mat
  ;; from http://stackoverflow.com/a/21175472
  [buffered-image]
  (let [image-data (.getData (.getDataBuffer (.getRaster buffered-image)))
        ;; using image-array.size() instead of image-array.length because I
        ;; can't easily figure out how to access the static field
        byte-buffer (ByteBuffer/allocate (* (alength image-data) 4))
        ;; int-buffer is a view of byte-buffer. we pack data into it
        int-buffer (.asIntBuffer byte-buffer)
        result-mat (Mat. (.getHeight buffered-image) (.getWidth buffered-image)
                         CvType/CV_8UC4)]
    (.put int-buffer image-data)
    (.put result-mat 0 0 (.array byte-buffer))
    result-mat))

(buffered-image-to-mat (get-screenshot-buffered-image))
