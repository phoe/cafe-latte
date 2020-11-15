package systems.raptor.cafe_latte.restarts;

import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.control_flow.tagbody.Tagbody;
import systems.raptor.cafe_latte.control_flow.tagbody.TagbodyTag;
import systems.raptor.cafe_latte.dynamic_variables.DynamicVariable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.go;

public class Restart<R, T> implements Function<R, T> {

  final static DynamicVariable<List<List<Restart<Object, Object>>>> restartClusters
          = new DynamicVariable<>(new LinkedList<>(new LinkedList<>()));

  private final String name;
  private final Function<R, T> function;
  private final Supplier<String> reportFunction;
  private final Supplier<R> interactiveFunction;
  private final Function<Condition, Boolean> testFunction;
  private final List<Condition> associatedConditions = new LinkedList<>();

  @Override
  public String toString() {
    return String.format("Restart{[%s] %s}", name, getReport());
  }

  public String getName() {
    return name;
  }

  public String getReport() {
    return reportFunction.get();
  }

  Function<R, T> getFunction() {
    return function;
  }

  Supplier<String> getReportFunction() {
    return reportFunction;
  }

  Supplier<R> getInteractiveFunction() {
    return interactiveFunction;
  }

  Function<Condition, Boolean> getTestFunction() {
    return testFunction;
  }

  public Restart(String name, Function<R, T> function) {
    this(name, function, name);
  }

  public Restart(String name, Function<R, T> function, String report) {
    this(name, function, () -> report, () -> null, (condition) -> true);
  }

  public Restart(String name, Function<R, T> function, Supplier<String> reportFunction,
                 Supplier<R> interactiveFunction, Function<Condition, Boolean> testFunction) {
    this.name = name;
    this.function = function;
    this.reportFunction = reportFunction;
    this.interactiveFunction = interactiveFunction;
    this.testFunction = testFunction;
  }

  public boolean isVisible(Condition condition) {
    return testFunction.apply(condition) &&
            (condition == null || associatedConditions.isEmpty() || associatedConditions.contains(condition));
  }

  static class ArgumentStorage {
    Object transferredArgument;
  }

  // Trampoline mechanism (for RestartCase)
  // Required to preserve identity of restart objects - see https://github.com/phoe/cafe-latte/issues/4

  private ArgumentStorage argumentStorage = null;
  private Tagbody tagbody = null;
  private TagbodyTag trampolineTag = null;
  private boolean shouldTrampoline = false;

  void trampolineTo(ArgumentStorage argumentStorage, Tagbody tagbody, TagbodyTag tag) {
    this.argumentStorage = argumentStorage;
    this.tagbody = tagbody;
    this.trampolineTag = tag;
    this.shouldTrampoline = true;
  }

  @Override
  public T apply(R argument) {
    if (shouldTrampoline) {
      shouldTrampoline = false;
      argumentStorage.transferredArgument = argument;
      go(tagbody, trampolineTag);
      return null;
    } else {
      return function.apply(argument);
    }
  }
  // Static methods

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void withConditionRestarts(Condition condition, List<Restart> restarts,
                                           Runnable runnable) {
    for (Restart restart : restarts) {
      restart.associatedConditions.add(condition);
    }
    try {
      runnable.run();
    } finally {
      for (Restart restart : restarts) {
        restart.associatedConditions.remove(condition);
      }
    }
  }

  @SuppressWarnings({"rawtypes"})
  public static List<Restart> computeRestarts(Condition condition) {
    List<Restart> result = new LinkedList<>();
    for (List<Restart<Object, Object>> cluster : restartClusters.get()) {
      for (Restart restart : cluster) {
        if (restart.isVisible(condition)) {
          result.add(restart);
        }
      }
    }
    return result;
  }

  @SuppressWarnings("rawtypes")
  public static List<Restart> computeRestarts() {
    return computeRestarts(null);
  }

  @SuppressWarnings({"rawtypes"})
  public static Restart findRestart(String name, Condition condition) {
    for (List<Restart<Object, Object>> cluster : restartClusters.get()) {
      for (Restart restart : cluster) {
        if (restart.name.equals(name) && restart.isVisible(condition)) {
          return restart;
        }
      }
    }
    return null;
  }

  @SuppressWarnings({"rawtypes"})
  public static Restart findRestart(Restart restart, Condition condition) {
    for (List<Restart<Object, Object>> cluster : restartClusters.get()) {
      for (Restart restart1 : cluster) {
        if (restart1 == restart && restart.isVisible(condition)) {
          return restart;
        }
      }
    }
    return null;
  }

  @SuppressWarnings({"rawtypes"})
  public static Restart findRestart(String name) {
    return findRestart(name, null);
  }

  @SuppressWarnings({"rawtypes"})
  public static Restart findRestart(Restart restart) {
    return findRestart(restart, null);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Object invokeRestart(Restart restart, Object argument) {
    return restart.apply(argument);
  }

  @SuppressWarnings({"rawtypes"})
  public static Object invokeRestart(Restart restart) {
    return invokeRestart(restart, null);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Object invokeRestartInteractively(Restart restart) {
    Object argument = restart.interactiveFunction.get();
    return restart.apply(argument);
  }

  @SuppressWarnings("rawtypes")
  public static Object findRestartHelper(String name, Function<Restart, Object> function) {
    Restart restart = findRestart(name);
    if (restart == null) {
      throw new RestartInvocationException(String.format("Attempted to invoke an inactive restart named \"%s\"", name));
    } else {
      return function.apply(restart);
    }
  }

  public static Object invokeRestart(String name, Object argument) {
    return findRestartHelper(name, (restart) -> invokeRestart(restart, argument));
  }

  public static Object invokeRestartInteractively(String name) {
    return findRestartHelper(name, Restart::invokeRestartInteractively);
  }

}
