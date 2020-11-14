package systems.raptor.cafe_latte.handlers;

import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.conditions.Condition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.handlers.Handler.signal;

class HandlerCaseTest {

  @Test
  public void HandlerCaseNoHandlersTest() {
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(), () -> {
      signal(new Condition());
      return "bar";
    });
    String returnValue = handlerCase.get();
    assertEquals("bar", returnValue);
  }

  @Test
  public void HandlerCaseMultipleHandlersTest() {
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
  public void HandlerCaseMultipleHandlersInheritanceTest() {
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
  public void HandlerCaseTransferTest() {
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(
            new Handler<>(Condition.class, (condition) -> "foo")), () -> {
      signal(new Condition());
      return "bar";
    });
    String returnValue = handlerCase.get();
    assertEquals("foo", returnValue);
  }

  @Test
  public void HandlerCaseNoTransferTest() {
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(
            new Handler<>(TestCondition.class, (condition) -> "foo")), () -> {
      signal(new Condition());
      return "bar";
    });
    String returnValue = handlerCase.get();
    assertEquals("bar", returnValue);
  }

  @Test
  public void HandlerCaseUnwindOrderTest() {
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

}