package systems.raptor.cafe_latte.debugger;

import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.dynamic_variables.DynamicVariable;

import java.util.function.Consumer;

import static systems.raptor.cafe_latte.dynamic_variables.DynamicVariable.bind;
import static systems.raptor.cafe_latte.restarts.RestartCase.withSimpleRestart;

public interface Debugger {

  DynamicVariable<Consumer<Condition>> debuggerHook = new DynamicVariable<>();

  DynamicVariable<Debugger> currentDebugger = new DynamicVariable<>(new NoDebugger());

  void invoke(Condition condition);

  static void invokeDebugger(Condition condition) {
    Consumer<Condition> hook = debuggerHook.get();
    if (hook != null) {
      hook.accept(condition);
    }
    currentDebugger.get().invoke(condition);
    // The debugger function declined to handle the condition - we must throw it and let Java try to catch it.
    condition.superFillInStackTrace();
    throw condition;
  }

  static void breakIntoDebugger() {
    bind(debuggerHook, (Consumer<Object>) (o) -> {}, () -> {
      Condition condition = new Condition();
      withSimpleRestart("CONTINUE", "Continue from the break.", () -> invokeDebugger(condition));
    });
  }

}
