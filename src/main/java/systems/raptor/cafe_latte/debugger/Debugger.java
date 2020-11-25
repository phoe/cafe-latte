package systems.raptor.cafe_latte.debugger;

import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.dynamic_variables.DynamicVariable;

public interface Debugger {

  DynamicVariable<Debugger> currentDebugger = new DynamicVariable<>(new NoDebugger());

  void invoke(Condition condition);

  static void invokeDebugger(Condition condition) {
    currentDebugger.get().invoke(condition);
    // We can only land here if the debugger function returned normally.
    // This is only possible when the debugger declines to handle the condition.
    // Hence, the only remaining choice is to unwind the stack by throwing the condition object.
    condition.superFillInStackTrace();
    throw condition;
  }

}
