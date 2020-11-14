package systems.raptor.cafe_latte;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.DynamicVariable.bind;

class DynamicVariableTest {

  @Test
  public void testDynamicVariableNullInitialBinding() {
    testBindingHelper(new DynamicVariable<>(), null);
  }

  @Test
  public void testDynamicVariableNonNullInitialBinding() {
    testBindingHelper(new DynamicVariable<>("quux"), "quux");
  }

  private void testBindingHelper(DynamicVariable<String> dynaVar, String initialValue) {
    assertEquals(initialValue, dynaVar.get());
    bind(dynaVar, "foo", () -> {
      assertEquals("foo", dynaVar.get());
      bind(dynaVar, "bar", () -> {
        assertEquals("bar", dynaVar.get());
      });
      assertEquals("foo", dynaVar.get());
    });
    assertEquals(initialValue, dynaVar.get());
  }

}