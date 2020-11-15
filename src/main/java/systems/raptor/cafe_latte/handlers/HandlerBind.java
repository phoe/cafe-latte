package systems.raptor.cafe_latte.handlers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static systems.raptor.cafe_latte.handlers.Handler.handlerClusters;
import static systems.raptor.cafe_latte.dynamic_variables.DynamicVariable.bind;

public class HandlerBind<T> implements Supplier<T> {

  private final Supplier<T> body;
  private final List<Handler<Object>> handlers;

  public HandlerBind(List<Handler<Object>> handlers, Supplier<T> body) {
    this.handlers = handlers;
    this.body = body;
  }

  @Override
  public T get() {
    var ref = new Object() {
      T returnValue;
    };
    List<List<Handler<Object>>> newClusters =
            Stream.concat(Stream.of(handlers), handlerClusters.get().stream())
                    .collect(Collectors.toList());
    bind(handlerClusters, newClusters, () -> ref.returnValue = body.get());
    return ref.returnValue;
  }
}
