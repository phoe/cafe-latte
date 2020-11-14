package systems.raptor.cafe_latte.control_flow;

public class TransferOfControl extends RuntimeException {
  private final Object tag;

  public TransferOfControl(Object tag) {
    super("Transfer of control", null, false, false);
    this.tag = tag;
  }

  public Object getTag() {
    return tag;
  }
}
