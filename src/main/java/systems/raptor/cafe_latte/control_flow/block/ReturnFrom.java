package systems.raptor.cafe_latte.control_flow.block;

import systems.raptor.cafe_latte.control_flow.TransferOfControl;

public class ReturnFrom extends TransferOfControl {

  private final Object value;

  public ReturnFrom(Object tag, Object value) {
    super(tag);
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

}
