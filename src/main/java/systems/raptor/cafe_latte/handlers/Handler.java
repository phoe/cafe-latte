package systems.raptor.cafe_latte.handlers;

import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.dynamic_variables.DynamicVariable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static systems.raptor.cafe_latte.debugger.Debugger.*;
import static systems.raptor.cafe_latte.dynamic_variables.DynamicVariable.bind;
import static systems.raptor.cafe_latte.restarts.RestartCase.withSimpleRestart;

public class Handler implements Consumer<Condition> {

  public final static DynamicVariable<List<Class<? extends Condition>>> breakOnSignals
          = new DynamicVariable<>(new LinkedList<>());

  final static DynamicVariable<List<List<Handler>>> handlerClusters
          = new DynamicVariable<>(new LinkedList<>(new LinkedList<>()));

  private final Class<? extends Condition> conditionClass;
  private final Consumer<? super Condition> consumer;

  Class<? extends Condition> getConditionClass() {
    return conditionClass;
  }

  public Handler(Class<? extends Condition> conditionClass, Consumer<? super Condition> consumer) {
    this.conditionClass = conditionClass;
    this.consumer = consumer;
  }

  @Override // TODO handlers are supposed to return void
  public void accept(Condition condition) {
    if (conditionClass.isInstance(condition)) {
      consumer.accept(condition);
    }
  }

  private static void maybeBreakIntoDebugger(Condition condition) {
    List<Class<? extends Condition>> classes = breakOnSignals.get();
    for (Class<? extends Condition> clazz : classes) {
      if (clazz.isInstance(condition)) {
        breakIntoDebugger();
      }
    }
  }

  private static void doSignal(Condition condition) {
    List<List<Handler>> clusters = handlerClusters.get();
    for (int i = 0; i < clusters.size(); ++i) {
      List<Handler> cluster = clusters.get(i);
      List<List<Handler>> remainingClusters = clusters.subList(i + 1, clusters.size());
      bind(handlerClusters, remainingClusters, () -> {
        for (Handler handler : cluster) {
          handler.accept(condition);
        }
      });
    }
  }

  public static void signal(Condition condition) {
    maybeBreakIntoDebugger(condition);
    doSignal(condition);
  }

  public static void warn(Condition condition) {
    withSimpleRestart("MUFFLE-WARNING", "Muffle this warning.", () -> {
      signal(condition);
      System.err.printf("Warning: %s%n", condition);
    });
  }

  public static void error(Condition condition) {
    signal(condition);
    invokeDebugger(condition);
  }

}
