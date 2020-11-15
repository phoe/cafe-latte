(defsystem cafe-latte
  :components nil)

(defsystem cafe-latte/doc/easye
  :depends-on (caffe-latte)
  :components ((:module doc :pathname "doc/"
                :components ((:static-file "easye-notes.org")))))


