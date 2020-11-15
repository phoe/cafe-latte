# Cafe Latte

> "We were not out to win over the Lisp programmers; we were after the C++ programmers. We managed to drag a lot of them about halfway to Lisp."
> 
> --- Guy Steele, Java spec co-author

> "Notice that no one mentions that this way they also managed to drag a lot of Lisp programmers about halfway back to C++."
>
> --- angery Internet rando

## About

This is an attempt to recreate some nifty Common Lisp features in plain Java. These are, most importantly, the foundations for a condition system, and a simple condition system itself.

Each part of Common Lisp implemented here should have basic unit test coverage.

## Using

**don't**

Seriously, though, this library is not yet documented or complete; take a look at the unit tests if you really need to see how to use the various constructs provided here.

## License

AGPLv3. (Unless you are [**@easye**](https://github.com/easye), at which point it's whatever license suits you the best.)

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
* [X] `handler-case`
  * Implemented in the `HandlerCase` class.
  * Unit tests done.
* [X] Restarts
  * Implemented in the `Restart` class.
  * Unit tests done.
* [ ] `restart-bind`
  * Implemented in the `RestartBind` class.
  * Unit tests **NOT** yet done.
* [ ] `restart-case`
  * Implemented in the `RestartCase` class.
  * Unit tests **NOT** yet done.
* [ ] Debugger
* [ ] `#'error`
