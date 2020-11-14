package systems.raptor.cafe_latte.control_flow.block;

import systems.raptor.cafe_latte.control_flow.ControlFlowException;

import java.util.function.Function;
import java.util.function.Supplier;

public class Block<T> implements Supplier<T> {

  private Function<Block<T>, T> function;

  private boolean valid;

  public Block() {}

  public Block(Function<Block<T>, T> function) {
    setFunction(function);
  }

  public void setFunction(Function<Block<T>, T> function) {
    this.function = function;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public T get() {
    var ref = new Object() {
      T returnValue;
    };
    valid = true;
    try {
      ref.returnValue = function.apply(this);
    } catch (ReturnFrom returnFrom) {
      if (returnFrom.getTag() == this) {
        ref.returnValue = (T) returnFrom.getValue();
      } else {
        throw returnFrom;
      }
    } finally {
      valid = false;
    }
    return ref.returnValue;
  }

  @SuppressWarnings("rawtypes")
  public static void returnFrom(Block block, Object value) {
    if (!block.valid) {
      throw new ControlFlowException("Attempted to returnFrom() a block that is no longer in scope");
    } else {
      throw new ReturnFrom(block, value);
    }
  }

  @SuppressWarnings("rawtypes")
  public static void returnFrom(Block block) {
    returnFrom(block, null);
  }

}
