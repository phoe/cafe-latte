package systems.raptor.cafe_latte.handlers;

import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.conditions.Error;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.handlers.Handler.signal;
import static systems.raptor.cafe_latte.handlers.HandlerCase.ignoreErrors;

class HandlerCaseTest {

  @Test
  public void handlerCaseNoHandlersTest() {
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(), () -> {
      signal(new Condition());
      return "bar";
    });
    String returnValue = handlerCase.get();
    assertEquals("bar", returnValue);
  }

  @Test
  public void handlerCaseMultipleHandlersTest() {
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(
            new Handler<>(Condition.class, (condition) -> "foo"),
            new Handler<>(Condition.class, (condition) -> "bar"),
            new Handler<>(Condition.class, (condition) -> "baz")), () -> {
      signal(new Condition());
      return "quux";
    });
    String returnValue = handlerCase.get();
    assertEquals("foo", returnValue);
  }

  private static class TestCondition extends Condition {}

  @Test
  public void handlerCaseMultipleHandlersInheritanceTest() {
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(
            new Handler<>(TestCondition.class, (condition) -> "foo"),
            new Handler<>(Condition.class, (condition) -> "bar"),
            new Handler<>(TestCondition.class, (condition) -> "baz")), () -> {
      signal(new Condition());
      return "quux";
    });
    String returnValue = handlerCase.get();
    assertEquals("bar", returnValue);
  }

  @Test
  public void handlerCaseTransferTest() {
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(
            new Handler<>(Condition.class, (condition) -> "foo")), () -> {
      signal(new Condition());
      return "bar";
    });
    String returnValue = handlerCase.get();
    assertEquals("foo", returnValue);
  }

  @Test
  public void handlerCaseNoTransferTest() {
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(
            new Handler<>(TestCondition.class, (condition) -> "foo")), () -> {
      signal(new Condition());
      return "bar";
    });
    String returnValue = handlerCase.get();
    assertEquals("bar", returnValue);
  }

  @Test
  public void handlerCaseUnwindOrderTest() {
    var ref = new Object() {
      int counter = 0;
    };
    Handler<Void> handler = new Handler<>(Condition.class, (condition) -> {
      ref.counter *= 2;
      return null;
    });
    new HandlerCase<>(List.of(handler), () -> {
      try {
        signal(new Condition());
      } finally {
        ref.counter += 10;
      }
      return null;
    }).get();
    assertEquals(20, ref.counter);
  }

  @Test
  public void ignoreErrorsNoTransferTest() {
    Condition returnValue = ignoreErrors(() -> {});
    assertNull(returnValue);
  }

  @Test
  public void ignoreErrorsTransferTest() {
    Error error = new Error();
    Condition returnValue = ignoreErrors(() -> signal(error));
    assertNotNull(returnValue);
    assertEquals(returnValue, error);
  }

}