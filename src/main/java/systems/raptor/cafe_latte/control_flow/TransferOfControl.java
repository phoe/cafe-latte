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

  /**
   * Disable creation of stack traces for this kind of flow control exception.
   *
   * <p>See: <a href="https://www.baeldung.com/java-exceptions-performance">Baeldung - Performance
   * Effects of Exceptions in Java</a>
   */
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
