package systems.raptor.cafe_latte.restarts;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static systems.raptor.cafe_latte.dynamic_variables.DynamicVariable.bind;
import static systems.raptor.cafe_latte.restarts.Restart.restartClusters;

public class RestartBind<R> implements Supplier<R> {

  private final Supplier<R> body;
  private final List<Restart<Object, Object>> restarts;

  public RestartBind(List<Restart<Object, Object>> restarts, Supplier<R> body) {
    this.restarts = restarts;
    this.body = body;
  }

  @Override
  public R get() {
    var ref = new Object() {
      R returnValue;
    };
    List<List<Restart<Object, Object>>> newClusters =
            Stream.concat(Stream.of(restarts), restartClusters.get().stream())
                    .collect(Collectors.toList());
    bind(restartClusters, newClusters, () -> ref.returnValue = body.get());
    return ref.returnValue;
  }
}
