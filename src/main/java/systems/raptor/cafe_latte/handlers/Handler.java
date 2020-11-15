package systems.raptor.cafe_latte.handlers;

import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.dynamic_variables.DynamicVariable;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static systems.raptor.cafe_latte.dynamic_variables.DynamicVariable.bind;
import static systems.raptor.cafe_latte.restarts.RestartCase.withSimpleRestart;

public class Handler<T> implements Function<Condition, T> {

  final static DynamicVariable<List<List<Handler<Object>>>> handlerClusters
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

  public static void signal(Condition condition) {
    List<List<Handler<Object>>> clusters = handlerClusters.get();
    for (int i = 0; i < clusters.size(); ++i) {
      List<Handler<Object>> cluster = clusters.get(i);
      List<List<Handler<Object>>> remainingClusters = clusters.subList(i + 1, clusters.size());
      bind(handlerClusters, remainingClusters, () -> {
        for (Handler<Object> handler : cluster) {
          handler.apply(condition);
        }
      });
    }
  }

  public static void warn(Condition condition) {
    withSimpleRestart("MUFFLE-WARNING", "Muffle this warning.", () -> {
      signal(condition);
      System.err.printf("Warning: %s%n", condition);
    });
  }

  public static void error(Condition condition) {
    signal(condition);
    // Should be:
    // invokeDebugger(condition);
    // But we have no debugger yet, so we must throw the given condition instead.
    {
      condition.makeReadyToThrow();
      throw condition;
    }
  }

}
