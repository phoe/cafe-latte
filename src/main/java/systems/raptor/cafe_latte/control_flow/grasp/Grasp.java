package systems.raptor.cafe_latte.control_flow.grasp;

import systems.raptor.cafe_latte.dynamic_variables.DynamicVariable;
import systems.raptor.cafe_latte.control_flow.ControlFlowException;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static systems.raptor.cafe_latte.dynamic_variables.DynamicVariable.bind;

public class Grasp<T> implements Supplier<T> {

  private final static DynamicVariable<List<Object>> activeTags = new DynamicVariable<>(new LinkedList<>());

  private final Supplier<T> supplier;
  private final Object tag;

  public Grasp(Object tag, Supplier<T> supplier) {
    this.tag = tag;
    this.supplier = supplier;
  }

  @SuppressWarnings({"unchecked"})
  public T get() {
    List<Object> newActiveTags = Stream.concat(Stream.of(tag), activeTags.get().stream())
            .collect(Collectors.toList());
    var ref = new Object() {
      T returnValue;
    };
    bind(activeTags, newActiveTags, () -> {
      try {
        ref.returnValue = supplier.get();
      } catch (Fling rf) {
        if (rf.getTag() == tag) {
          ref.returnValue = (T) rf.getValue();
        } else {
          throw rf;
        }
      }
    });
    return ref.returnValue;
  }

  public static void fling(Object tag, Object value) {
    if (!activeTags.get().contains(tag)) {
      throw new ControlFlowException("Attempted to fling() to a grasp tag that is not in scope");
    } else {
      throw new Fling(tag, value);
    }
  }

  public static void fling(Object tag) {
    fling(tag, null);
  }

}
