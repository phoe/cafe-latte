# Cafe Latte

This is an attempt to recreate some nifty Common Lisp features in plain Java. These are, most importantly, the foundations for a condition system, and a simple condition system itself.

## Roadmap

* [X] Dynamic variables
* [X] `tagbody`/`go`
* [X] `block`/`return-from`
* [X] `throw`/`catch`
  * Because of Java naming conflicts, `catch`/`throw` are renamed to `grasp`/`fling`.
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
