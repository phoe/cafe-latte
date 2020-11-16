package systems.raptor.cafe_latte.debugger;

import systems.raptor.cafe_latte.conditions.Condition;

public class ThrowingDebugger implements Debugger {

  @Override
  public void invoke(Condition condition) {
    condition.makeReadyToThrow();
    throw condition;
  }

}
