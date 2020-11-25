package systems.raptor.cafe_latte.handlers;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.conditions.Error;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.handlers.Handler.error;

public class ErrorTest {

  @Test
  public void errorTestUnhandled() {
    assertThrows(Error.class, () -> error(new Error()));
  }

  @Test
  public void errorTestHandled() {
    assertDoesNotThrow(() -> new HandlerCase<>(List.of(Pair.of(Error.class, (condition) -> null)), () -> {
      error(new Error());
      return null;
    }).get());
  }

}
