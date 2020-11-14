package systems.raptor.cafe_latte.handlers;

import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.conditions.Condition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.handlers.Handler.signal;

class HandlerCaseTest {

  static class TestCondition extends Condition {}

  @Test
  public void HandlerCaseTransferTest() {
    Handler<String> handler = new Handler<>(TestCondition.class, (condition) -> "foo");
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(handler), () -> {
      signal(new TestCondition());
      return "bar";
    });
    String returnValue = handlerCase.get();
    assertEquals("foo", returnValue);
  }

  @Test
  public void HandlerCaseNoTransferTest() {
    Handler<String> handler = new Handler<>(TestCondition.class, (condition) -> "foo");
    HandlerCase<String> handlerCase = new HandlerCase<>(List.of(handler), () -> {
      signal(new Condition());
      return "bar";
    });
    String returnValue = handlerCase.get();
    assertEquals("bar", returnValue);
  }

}