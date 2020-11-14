package systems.raptor.cafe_latte.handlers;

import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.dynamic_variables.DynamicVariable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static systems.raptor.cafe_latte.dynamic_variables.DynamicVariable.bind;

public class Handler<T> implements Function<Condition, T> {

  @SuppressWarnings("rawtypes")
  final static DynamicVariable<List<List<Handler>>> handlerClusters
          = new DynamicVariable<>(new LinkedList<>(new LinkedList<>()));

  private final Class<? extends Condition> conditionClass;
  private final Function<? super Condition, T> function;

  Class<? extends Condition> getConditionClass() {
    return conditionClass;
  }

  public Handler(Class<? extends Condition> conditionClass, Function<? super Condition, T> function) {
    this.conditionClass = conditionClass;
    this.function = function;
  }

  @Override
  public T apply(Condition condition) {
    if (conditionClass.isInstance(condition)) {
      return function.apply(condition);
    } else {
      return null;
    }
  }

  @SuppressWarnings("rawtypes")
  public static void signal(Condition condition) {
    List<List<Handler>> clusters = handlerClusters.get();
    for (int i = 0; i < clusters.size(); ++i) {
      List<Handler> cluster = clusters.get(i);
      List<List<Handler>> remainingClusters = clusters.subList(i + 1, clusters.size());
      bind(handlerClusters, remainingClusters, () -> {
        for (Handler handler : cluster) {
          handler.apply(condition);
        }
      });
    }
  }
}
