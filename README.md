# Cafe Latte

> "We were not out to win over the Lisp programmers; we were after the C++ programmers. We managed to drag a lot of them about halfway to Lisp."
> 
> --- Guy Steele, Java spec co-author

> "Notice that no one mentions that this way they also managed to drag a lot of Lisp programmers about halfway back to C++."
>
> --- Michał "phoe" Herda, angry Internet rando

## About

This is an implementation of Common Lisp dynamic variables, control flow operators, and condition system in plain Java.

If [Portable Conditon System](https://github.com/phoe/portable-condition-system) is a proof that a Common Lisp condition system can be implemented on a Common Lisp implementation without a condition system of its own, then Cafe Latte is a proof that a Common Lisp-inspired condition system can be implemented even on top of a non-Lisp language that has only automatic memory management and a primitive unwinding operator (in this case, `throw`), but does not support dynamic variables or non-local returns by default.

It should be possible to use it, or parts of it, in other projects, and its source code should be readable enough to understand the underlying mechanics of each Lisp control flow operator.

## Using Cafe Latte

This library is not yet documented, but the respective implementations should behave analogously to their Common Lisp counterparts; see the [Common Lisp HyperSpec](http://clhs.lisp.se/) for their descriptions.

Example uses of the various constructs [implemented](src/main/java/systems/raptor/cafe_latte) here are present in the [test directory](src/test/java/systems/raptor/cafe_latte), containing unit tests for all the present mechanisms.

A stable release of this library will be made when the authors are satisfied enough with its functioning.

## License

AGPLv3. (Unless you are [**@easye**](https://github.com/easye), at which point it's whatever license suits you the best.)

## What's DONE

* [X] **Dynamic variables**
  * [X] Dynamic variable class
    * Implemented in the `DynamicVariable` class.
    * Unit tests done.
* [X] **Control flow**
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
* [X] **Conditions**
  * [X] Condition class
    * Implemented in the `Condtion` class.
    * Subtype of `RuntimeException`.
    * Do not fill in the stack trace when instantiated; see the `superFillInStackTrace()` method.
  * [X] Warning class
    * Implemented in the `Warning` class.
    * Subtype of `Condition`.
  * [X] Error class
    * Implemented in the `Error` class.
    * Subtype of `Condition`.
* [X] **Handlers**
  * [X] Handler class
    * Implemented in the `Handler` class.
    * Tested elsewhere.
  * [X] `signal`
    * Implemented in the `Handler` class as a static method.
    * Tested elsewhere.
  * [X] `warn`
    * Implemented in the `Handler` class as a static method.
    * Unit tests done.
  * [X] `error`
    * Implemented in the `Handler` class as a static method.
    * Unit tests done.
  * [X] `*break-on-signals*`
    * Implemented in the `Handler` class as a dynamic variable.
    * Unit tests done.
  * [X] `handler-bind`
    * Implemented in the `HandlerBind` class.
    * Unit tests done.
  * [X] `handler-case`
    * Implemented in the `HandlerCase` class.
    * Unit tests done.
  * [X] `ignore-errors`
    * Implemented in the `HandlerCase` class as a static method.
    * Unit tests done.
* [X] **Restarts**
  * [X] Restart class
    * Implemented in the `Restart` class.
    * Unit tests done.
  * [X] `restart-bind`
    * Implemented in the `RestartBind` class.
    * Unit tests done.
  * [X] `restart-case`
    * Implemented in the `RestartCase` class.
    * Unit tests done.
  * [X] `with-simple-restart`
    * Implemented in the `RestartCase` class as a static method.
    * Unit tests done.
* [X] **Debugger**
  * [X] Debugger interface
    * Defined in the `Debugger` class.
  * [X] `*debugger-hook*`
    * Implemented in the `Debugger` class as a dynamic variable.
    * Unit tests done.
  * [X] `break`
    * Implemented in the `Debugger` class as a static method.
    * Unit tests done.
    * Because of Java naming conflicts, `break` is renamed to `breakIntoDebugger`.
  * [X] No debugger
    * Implemented in the `NoDebugger` class.
    * Tested along with the `Error` class by means of calling the static `error` method.

## What's TODO

* [ ] Condition reports
  * Should map nicely to the standard Java exception `toString` mechanism.
* [ ] Restart reports
  * Should map nicely to the standard Java exception `toString` mechanism.
* [ ] Interactive debugger
* [ ] JShell as debugger REPL

## What's WONTDO

* [X] `unwind-protect` - Java has `try`/`finally` that is fully equivalent.
