# Cafe Latte

This is an attempt to recreate some nifty Common Lisp features in plain Java. These are, most importantly, the foundations for a condition system, and a simple condition system itself.

## Roadmap

* [X] Dynamic variables
  * Implemented in the `DynamicVariable` class.
  * Unit tests done.
* [X] `tagbody`/`go`
  * Implemented in the `Tagbody` class.
  * Unit tests done.
* [X] `block`/`return-from`
  * Implemented in the `Block` class.
  * Unit tests done.
* [X] `throw`/`catch`
  * Because of Java naming conflicts, `catch`/`throw` are renamed to `grasp`/`fling`.
  * Implemented in the `Grasp` class.
  * Unit tests done.
* [ ] Conditions
* [ ] Handlers
* [ ] `handler-bind`
* [ ] `handler-case`
* [ ] `#'signal`
* [ ] Restarts
* [ ] `restart-bind`
* [ ] `restart-case`
* [ ] Debugger
* [ ] `#'error`
