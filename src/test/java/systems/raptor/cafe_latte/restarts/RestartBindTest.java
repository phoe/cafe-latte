package systems.raptor.cafe_latte.restarts;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.restarts.Restart.invokeRestart;

class RestartBindTest {

  private static class Counter {
    int counter;
  }

  @Test
  public void restartBindNoRestartTest() {
    var counter = new Counter();
    int returnValue = new RestartBind<>(List.of(), () -> 42).get();
    assertEquals(42, returnValue);
    assertEquals(0, counter.counter);
  }

  @Test
  public void restartBindSingleRestartTest() {
    var counter = new Counter();
    Restart<Object, Object> restart = new Restart<>("ABORT", (x) -> {
      counter.counter += 10;
      return null;
    });
    int returnValue = new RestartBind<>(List.of(restart), () -> {
      invokeRestart(restart);
      return 42;
    }).get();
    assertEquals(42, returnValue);
    assertEquals(10, counter.counter);
  }

  @Test
  public void restartBindMultipleRestartTest() {
    var counter = new Counter();
    Restart<Object, Object> restart1 = new Restart<>("ABORT", (x) -> {
      counter.counter += 10;
      return null;
    });
    Restart<Object, Object> restart2 = new Restart<>("RETRY", (x) -> {
      counter.counter *= 10;
      return null;
    });
    int returnValue = new RestartBind<>(List.of(restart1, restart2), () -> {
      invokeRestart(restart1);
      invokeRestart(restart2);
      invokeRestart(restart1);
      return 42;
    }).get();
    assertEquals(42, returnValue);
    assertEquals(110, counter.counter);
  }

}