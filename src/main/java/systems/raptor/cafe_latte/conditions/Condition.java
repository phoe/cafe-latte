package systems.raptor.cafe_latte.conditions;

public class Condition extends RuntimeException {

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }

  public void makeReadyToThrow() {
    super.fillInStackTrace();
  }
}
