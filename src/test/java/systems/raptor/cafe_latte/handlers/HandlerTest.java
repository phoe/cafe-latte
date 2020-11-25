package systems.raptor.cafe_latte.handlers;

import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.conditions.Warning;
import systems.raptor.cafe_latte.conditions.Error;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.debugger.Debugger.*;
import static systems.raptor.cafe_latte.dynamic_variables.DynamicVariable.bind;
import static systems.raptor.cafe_latte.handlers.Handler.breakOnSignals;
import static systems.raptor.cafe_latte.handlers.Handler.signal;
import static systems.raptor.cafe_latte.restarts.Restart.invokeRestart;

class HandlerTest {

  @Test
  public void testBreakOnSignalsThrow() {
    assertThrows(Condition.class, () ->
            bind(breakOnSignals, List.of(Warning.class), () ->
                    signal(new Warning())));
  }

  @Test
  public void testBreakOnSignalsNoThrow() {
    bind(breakOnSignals, List.of(Warning.class), () ->
            signal(new Error()));
    bind(breakOnSignals, List.of(), () ->
            signal(new Error()));
  }

  @Test
  public void testBreakOnSignalsRestart() {
    Handler handler = new Handler(Condition.class, (c) -> {
      invokeRestart("CONTINUE");
    });
    assertDoesNotThrow(() -> bind(breakOnSignals, List.of(Warning.class), () -> {
      new HandlerBind<Handler>(List.of(handler), () -> {
        breakIntoDebugger();
        return null;
      });
    }));
  }

  @Test
  public void testDebuggerHook() {
    var ref = new Object() {
      int counter = 0;
    };
    Consumer<Condition> hook = (x) -> ref.counter++;
    assertThrows(Condition.class, () ->
            bind(debuggerHook, hook, () ->
                    invokeDebugger(new Condition())));
    assertEquals(ref.counter, 1);
    assertThrows(Condition.class, () ->
            bind(debuggerHook, null, () ->
                    invokeDebugger(new Condition())));
    assertEquals(ref.counter, 1);
  }

}