package systems.raptor.cafe_latte.restarts;

import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.conditions.Condition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.dynamic_variables.DynamicVariable.bind;
import static systems.raptor.cafe_latte.restarts.Restart.*;

class RestartTest {

  @Test
  public void restartComponentsTest() {
    var ref = new Object() {
      boolean functionRun = false;
      boolean visible = false;
    };
    Restart<Integer, String> restart = new Restart<>("CONTINUE",
            (i) -> {
              ref.functionRun = true;
              assertEquals(42, i);
              return "foo";
            },
            () -> "bar",
            () -> 42,
            (condition) -> ref.visible);
    assertEquals("CONTINUE", restart.getName());
    assertEquals("foo", restart.apply(42));
    assertEquals("foo", invokeRestart(restart, 42));
    assertEquals("foo", invokeRestartInteractively(restart));
    assertEquals("bar", restart.getReport());
    assertFalse(restart.isVisible(null));
    ref.visible = true;
    assertTrue(restart.isVisible(null));
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void restartSingleDynamicBindingTest() {
    Restart<Integer, String> restart = new Restart<>("CONTINUE", (i) -> String.format("%d", i),
            () -> "bar", () -> 24, (condition) -> true);
    List<List<Restart<Integer, String>>> newClusters = List.of(List.of(restart));
    bind(Restart.restartClusters, newClusters, () -> {
      List<Restart> restarts = computeRestarts();
      assertEquals(1, restarts.size());
      assertEquals(restart, restarts.get(0));
      assertEquals(restart, findRestart("CONTINUE"));
      assertEquals(restart, findRestart(restarts.get(0)));
      assertEquals("42", invokeRestart("CONTINUE", 42));
      assertEquals("24", invokeRestartInteractively("CONTINUE"));
    });
  }

  @Test
  public void withConditionRestartsTest() {
    Restart<Integer, String> restart = new Restart<>("CONTINUE", (i) -> String.format("%d", i));
    List<List<Restart<Integer, String>>> newClusters = List.of(List.of(restart));
    Condition condition1 = new Condition(), condition2 = new Condition();
    bind(Restart.restartClusters, newClusters, () -> {
      assertEquals(restart, findRestart("CONTINUE"));
      assertEquals(restart, findRestart(restart));
      assertEquals(restart, findRestart("CONTINUE", condition1));
      assertEquals(restart, findRestart(restart, condition1));
      assertEquals(restart, findRestart("CONTINUE", condition2));
      assertEquals(restart, findRestart(restart, condition2));
      assertEquals(1, computeRestarts().size());
      assertEquals(1, computeRestarts(condition1).size());
      assertEquals(1, computeRestarts(condition2).size());
      withConditionRestarts(condition1, List.of(restart), () -> {
        assertEquals(restart, findRestart("CONTINUE"));
        assertEquals(restart, findRestart(restart));
        assertEquals(restart, findRestart("CONTINUE", condition1));
        assertEquals(restart, findRestart(restart, condition1));
        assertNull(findRestart("CONTINUE", condition2));
        assertNull(findRestart(restart, condition2));
        assertEquals(1, computeRestarts().size());
        assertEquals(1, computeRestarts(condition1).size());
        assertEquals(0, computeRestarts(condition2).size());
      });
      assertEquals(restart, findRestart("CONTINUE"));
      assertEquals(restart, findRestart(restart));
      assertEquals(restart, findRestart("CONTINUE", condition1));
      assertEquals(restart, findRestart(restart, condition1));
      assertEquals(restart, findRestart("CONTINUE", condition2));
      assertEquals(restart, findRestart(restart, condition2));
      assertEquals(1, computeRestarts().size());
      assertEquals(1, computeRestarts(condition1).size());
      assertEquals(1, computeRestarts(condition2).size());
    });
  }

}