# Cafe Latte

> "We were not out to win over the Lisp programmers; we were after the C++ programmers. We managed to drag a lot of them about halfway to Lisp."
> 
> --- Guy Steele, Java spec co-author

> "Notice that no one mentions that this way they also managed to drag a lot of Lisp programmers about halfway back to C++."
>
> --- Michał "phoe" Herda, angry Internet rando

## About

This is an implementation of Common Lisp dynamic variables, control flow operators, and condition system in plain Java.

## Using Cafe Latte

This library is not yet documented, but the respective implementations should behave analogously to their Common Lisp counterparts; see the [Common Lisp HyperSpec](http://clhs.lisp.se/) for their descriptions.

The best examples that show use of the various constructs [implemented](src/main/java/systems/raptor/cafe_latte) here is the [test directory](src/test/java/systems/raptor/cafe_latte) containing unit tests for all of the implemented mechanisms.

A stable release will be made when the authors are satisfied enough with its functioning.

## License

AGPLv3. (Unless you are [**@easye**](https://github.com/easye), at which point it's whatever license suits you the best.)

## What's DONE

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
  * `throw`/`catch` are not required for implementing a condition system, but their implementations are nonetheless included here for completeness.
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
* [X] `handler-case`
  * Implemented in the `HandlerCase` class.
  * Unit tests done.
* [X] Restarts
  * Implemented in the `Restart` class.
  * Unit tests done.
* [X] `restart-bind`
  * Implemented in the `RestartBind` class.
  * Unit tests done.
* [X] `restart-case`
  * Implemented in the `RestartCase` class.
  * Unit tests done.

## What's TODO

* [ ] `#'warn`
* [ ] `#'error`
* [ ] Debugger
* [ ] JShell as debugger REPL

## What's WONTDO

* [X] `unwind-protect` - Java has `finally` that is fully equivalent.