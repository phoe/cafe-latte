package systems.raptor.cafe_latte.handlers;

import org.junit.jupiter.api.*;
import systems.raptor.cafe_latte.conditions.Warning;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static systems.raptor.cafe_latte.handlers.Handler.warn;
import static systems.raptor.cafe_latte.restarts.Restart.findRestart;
import static systems.raptor.cafe_latte.restarts.Restart.invokeRestart;

public class WarnTest {
  private static final PrintStream originalErr = System.err;
  private static ByteArrayOutputStream errContent;

  @BeforeEach
  void setUpStreams() {
    errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));
  }

  @Test
  public void warnTestUnhandled() {
    warn(new Warning());
    assertEquals(String.format("Warning: systems.raptor.cafe_latte.conditions.Warning%n"), errContent.toString());
  }

  @Test
  public void warnTestHandled() {
    Handler<Object> handler = new Handler<>(Warning.class, (condition) -> {
      invokeRestart(findRestart("MUFFLE-WARNING"));
      return null;
    });
    new HandlerBind<>(List.of(handler), () -> {
      warn(new Warning());
      return null;
    }).get();
    assertEquals("", errContent.toString());
  }

  @AfterAll
  static void restoreStreams() {
    System.setErr(originalErr);
  }
}
