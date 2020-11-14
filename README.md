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
* [X] Conditions
  * Implemented in the `Condition` class.
  * Subtypes of `RuntimeException`.
  * Do not fill in the stack trace when instantiated; see the `makeReadyToThrow()` method.
* [X] Handlers
  * Implemented in the `Handler` class.
* [X] `#'signal`
  * Implemented in the `Handler` class as a static method.
* [X] `handler-bind`
  * Implemented in the `HandlerBind` class.
  * Unit tests done.
* [ ] `handler-case`
* [ ] Restarts
* [ ] `restart-bind`
* [ ] `restart-case`
* [ ] Debugger
* [ ] `#'error`
