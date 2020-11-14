package systems.raptor.cafe_latte.control_flow.grasp;

import systems.raptor.cafe_latte.control_flow.TransferOfControl;

public class Fling extends TransferOfControl {
  private final Object value;

  Fling(Object tag, Object value) {
    super(tag);
    this.value = value;
  }

  public Object getValue() {
    return value;
  }
}
