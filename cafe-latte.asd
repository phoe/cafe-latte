(defsystem cafe-latte
  :components nil)

(defsystem cafe-latte/doc/easye
  :depends-on (cafe-latte)
  :components ((:module doc :pathname "doc/"
                :components ((:static-file "easye-notes.org")))))


