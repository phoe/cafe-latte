package systems.raptor.cafe_latte.control_flow.grasp;

import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.control_flow.ControlFlowException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static systems.raptor.cafe_latte.control_flow.grasp.Grasp.fling;

class GraspTest {

  @Test
  public void GraspNormalReturnTest() {
    assertEquals("foo", new Grasp<>(42, () -> "foo").get());
  }

  @Test
  public void GraspReturnFromTest() {
    assertEquals("foo", new Grasp<>(42, () -> {
      fling(42, "foo");
      return "bar";
    }).get());
  }

  @Test
  public void NestedGraspReturnFromTest() {
    assertEquals("foo", new Grasp<>(42, () -> {
      new Grasp<>(24, () -> {
        fling(42, "foo");
        return "bar";
      }).get();
      return "bar";
    }).get());
  }

  @Test
  public void GraspControlFlowExceptionTest() {
    Grasp<String> grasp = new Grasp<>(42, () -> "foo");
    assertThrows(ControlFlowException.class, () -> fling(grasp, "bar"));
  }

}