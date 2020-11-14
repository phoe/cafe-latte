package systems.raptor.cafe_latte.conditions;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.conditions.Handler.signal;

class HandlerBindTest {

  private static class Counter {
    int counter;
  }

  @Test
  public void handlerBindMultipleHandlerTest() {
    var counter = new Counter();
    int returnValue = new HandlerBind<>(List.of(
            makeAdditionHandler(Condition.class, counter, 1, false),
            makeMultiplicationHandler(Condition.class, counter, 20, false),
            makeAdditionHandler(Condition.class, counter, -15, false)), () -> {
      signal(new Condition());
      return 42;
    }).get();
    assertEquals(42, returnValue);
    assertEquals(5, counter.counter);
  }

  private static class TestCondition extends Condition {}

  @Test
  public void handlerBindInheritanceTest() {
    var counter = new Counter();
    new HandlerBind<>(List.of(
            makeAdditionHandler(Condition.class, counter, 1, false),
            makeMultiplicationHandler(TestCondition.class, counter, 20, false),
            makeAdditionHandler(Condition.class, counter, -15, false)), () -> {
      signal(new Condition());
      return 42;
    }).get();
    assertEquals(-14, counter.counter);
  }

  @Test
  public void handlerBindNestedResignalTest() {
    var counter = new Counter();
    new HandlerBind<Void>(List.of(makeAdditionHandler(Condition.class, counter, 1, true)), () -> {
      new HandlerBind<Void>(List.of(makeAdditionHandler(Condition.class, counter, 10, true)), () -> {
        new HandlerBind<Void>(List.of(makeAdditionHandler(Condition.class, counter, 100, true)), () -> {
          new HandlerBind<Void>(List.of(makeAdditionHandler(Condition.class, counter, 1000, true)), () -> {
          signal(new Condition());
            return null;
          }).get();
          return null;
        }).get();
        return null;
      }).get();
      return null;
    }).get();
    assertEquals(1248, counter.counter);
  }

  @SuppressWarnings("SameParameterValue")
  private Handler<Void> makeAdditionHandler(Class<? extends Condition> conditionClass,
                                            Counter counter, int amount, boolean resignal) {
    return new Handler<>(conditionClass, (condition) -> {
      counter.counter += amount;
      if (resignal) signal(condition);
      return null;
    });
  }

  @SuppressWarnings("SameParameterValue")
  private Handler<Void> makeMultiplicationHandler(Class<? extends Condition> conditionClass,
                                                  Counter counter, int amount, boolean resignal) {
    return new Handler<>(conditionClass, (condition) -> {
      counter.counter *= amount;
      if (resignal) signal(condition);
      return null;
    });
  }

}