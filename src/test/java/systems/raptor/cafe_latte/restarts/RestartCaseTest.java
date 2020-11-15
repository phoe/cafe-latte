package systems.raptor.cafe_latte.restarts;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.restarts.Restart.*;
import static systems.raptor.cafe_latte.restarts.RestartCase.withSimpleRestart;

class RestartCaseTest {

  @Test
  public void restartCaseNoRestartTest() {
    var ref = new Object() {
      @SuppressWarnings("rawtypes")
      List<Restart> restarts;
    };
    RestartCase<String, String> restartCase = new RestartCase<>(List.of(), () -> {
      ref.restarts = computeRestarts();
      return "bar";
    });
    String returnValue = restartCase.get();
    assertNotNull(ref.restarts);
    assertEquals(0, ref.restarts.size());
    assertEquals("bar", returnValue);
  }

  @Test
  public void restartCaseMultipleRestartsTest() {
    var ref = new Object() {
      @SuppressWarnings("rawtypes")
      List<Restart> restarts;
    };
    Restart<String, String> restart1 = new Restart<>("ABORT", (x) -> "foo");
    Restart<String, String> restart2 = new Restart<>("RETRY", (x) -> "bar");
    Restart<String, String> restart3 = new Restart<>("FAIL", (x) -> "baz");
    RestartCase<String, String> restartCase =
            new RestartCase<>(List.of(restart1, restart2, restart3), () -> {
              ref.restarts = computeRestarts();
              return "quux";
            });
    String returnValue = restartCase.get();
    assertNotNull(ref.restarts);
    assertEquals(3, ref.restarts.size());
    assertEquals(restart1, ref.restarts.get(0));
    assertEquals(restart2, ref.restarts.get(1));
    assertEquals(restart3, ref.restarts.get(2));
    assertEquals("ABORT", ref.restarts.get(0).getName());
    assertEquals("RETRY", ref.restarts.get(1).getName());
    assertEquals("FAIL", ref.restarts.get(2).getName());
    assertEquals("quux", returnValue);
  }

  @Test
  public void restartCaseNestedTest() {
    var ref = new Object() {
      @SuppressWarnings("rawtypes")
      List<Restart> restarts;
    };
    Restart<String, String> restart1 = new Restart<>("ABORT", (x) -> "foo");
    Restart<String, String> restart2 = new Restart<>("RETRY", (x) -> "bar");
    Restart<String, String> restart3 = new Restart<>("FAIL", (x) -> "baz");
    new RestartCase<>(List.of(restart3), () -> {
      new RestartCase<>(List.of(restart2), () -> {
        new RestartCase<>(List.of(restart1), () -> {
          ref.restarts = computeRestarts();
          return null;
        }).get();
        return null;
      }).get();
      return null;
    }).get();
    assertNotNull(ref.restarts);
    assertEquals(3, ref.restarts.size());
    assertEquals(restart1, ref.restarts.get(0));
    assertEquals(restart2, ref.restarts.get(1));
    assertEquals(restart3, ref.restarts.get(2));
    assertEquals("ABORT", ref.restarts.get(0).getName());
    assertEquals("RETRY", ref.restarts.get(1).getName());
    assertEquals("FAIL", ref.restarts.get(2).getName());
  }

  @Test
  public void restartCaseTransferInvokeByNameTest() {
    Restart<String, String> restart = new Restart<>("ABORT", (x) -> "foo");
    String returnValue =
            new RestartCase<>(List.of(restart), () -> {
              invokeRestart(findRestart("ABORT"));
              return "bar";
            }).get();
    assertEquals("foo", returnValue);
  }

  @Test
  public void restartCaseTransferInvokeByReferenceTest() {
    Restart<String, String> restart = new Restart<>("ABORT", (x) -> "foo");
    String returnValue =
            new RestartCase<>(List.of(restart), () -> {
              invokeRestart(findRestart(restart));
              return "bar";
            }).get();
    assertEquals("foo", returnValue);
  }

  @Test
  public void restartCaseTransferInvokeByDirectReferenceTest() {
    Restart<String, String> restart = new Restart<>("ABORT", (x) -> "foo");
    String returnValue =
            new RestartCase<>(List.of(restart), () -> {
              invokeRestart(restart);
              return "bar";
            }).get();
    assertEquals("foo", returnValue);
  }

  @Test
  public void restartCaseUnwindOrderTest() {
    var ref = new Object() {
      int counter = 0;
    };
    Restart<Void, Void> restart = new Restart<>("ABORT", (x) -> {
      ref.counter *= 2;
      return null;
    });
    new RestartCase<>(List.of(restart), () -> {
      try {
        invokeRestart(restart);
      } finally {
        ref.counter += 10;
      }
      return null;
    }).get();
    assertEquals(20, ref.counter);
  }

  @Test
  public void withSimpleRestartNoTransferTest() {
    Boolean returnValue = withSimpleRestart("ABORT", "foo", () -> {});
    assertEquals(false, returnValue);
  }

  @Test
  public void withSimpleRestartTransferTest() {
    Boolean returnValue = withSimpleRestart("ABORT", "foo", () -> {
      invokeRestart(findRestart("ABORT"));
    });
    assertEquals(true, returnValue);
  }

}