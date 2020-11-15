package systems.raptor.cafe_latte.handlers;

import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.conditions.Error;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.handlers.Handler.error;

public class ErrorTest {

  @Test
  public void errorTestUnhandled() {
    // TODO: this will need to be adjusted when we implement the debugger.
    assertThrows(Error.class, () -> error(new Error()));
  }

  @Test
  public void errorTestHandled() {
    Handler<Object> handler = new Handler<>(Error.class, (condition) -> null);
    assertDoesNotThrow(() -> new HandlerCase<>(List.of(handler), () -> {
      error(new Error());
      return null;
    }).get());
  }

}
