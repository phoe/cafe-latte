package systems.raptor.cafe_latte;

import java.util.LinkedList;
import java.util.List;

public class DynamicVariable<T> {
  private final ThreadLocal<List<T>> bindings = ThreadLocal.withInitial(LinkedList::new);

  public DynamicVariable() {
    bind(null);
  }

  public DynamicVariable(T initialValue) {
    bind(initialValue);
  }

  public void set(T newValue) {
    bindings.get().set(0, newValue);
  }

  public T get() {
    return bindings.get().get(0);
  }

  private void bind(T newValue) {
    bindings.get().add(0, newValue);
  }

  private void unbind() {
    bindings.get().remove(0);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void bind(DynamicVariable dynaVar, Object newValue, Runnable runnable) {
    dynaVar.bind(newValue);
    try {
      runnable.run();
    } finally {
      dynaVar.unbind();
    }
  }

}
