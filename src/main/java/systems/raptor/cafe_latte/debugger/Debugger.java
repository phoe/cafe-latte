package systems.raptor.cafe_latte.debugger;

import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.dynamic_variables.DynamicVariable;

public interface Debugger {

  DynamicVariable<Debugger> currentDebugger = new DynamicVariable<>(new ThrowingDebugger());

  void invoke(Condition condition);

  static void invokeDebugger(Condition condition) {
    currentDebugger.get().invoke(condition);
  }

}
