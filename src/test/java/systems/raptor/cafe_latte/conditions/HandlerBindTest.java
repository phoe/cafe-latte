package systems.raptor.cafe_latte.conditions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.conditions.Handler.signal;

class HandlerBindTest {

  @Test
  public void HandlerBindMultipleHandlerTest() {
    var ref = new Object() {
      int counter = 0;
    };
    Handler<Void> handler1 = new Handler<>(Condition.class,
            (condition) -> {
              ref.counter = ref.counter + 1;
              return null;
            });
    Handler<Void> handler2 = new Handler<>(Condition.class,
            (condition) -> {
              ref.counter = ref.counter * 20;
              return null;
            });
    Handler<Void> handler3 = new Handler<>(Condition.class,
            (condition) -> {
              ref.counter = ref.counter - 15;
              return null;
            });
    int returnValue = new HandlerBind<>(List.of(handler1, handler2, handler3), () -> {
      signal(new Condition());
      return 42;
    }).get();
    assertEquals(42, returnValue);
    assertEquals(5, ref.counter);
  }

  static class TestCondition extends Condition {}

  @Test
  public void HandlerBindInheritanceTest() {
    var ref = new Object() {
      int counter = 0;
    };
    Handler<Void> handler1 = new Handler<>(Condition.class,
            (condition) -> {
              ref.counter = ref.counter + 1;
              return null;
            });
    Handler<Void> handler2 = new Handler<>(TestCondition.class,
            (condition) -> {
              ref.counter = ref.counter * 20;
              return null;
            });
    Handler<Void> handler3 = new Handler<>(Condition.class,
            (condition) -> {
              ref.counter = ref.counter - 15;
              return null;
            });
    int returnValue = new HandlerBind<>(List.of(handler1, handler2, handler3), () -> {
      signal(new Condition());
      return 42;
    }).get();
    assertEquals(42, returnValue);
    assertEquals(-14, ref.counter);
  }

}