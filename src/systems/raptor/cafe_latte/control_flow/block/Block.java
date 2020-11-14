package systems.raptor.cafe_latte.control_flow.block;

import systems.raptor.cafe_latte.DynamicVariable;
import systems.raptor.cafe_latte.control_flow.ControlFlowException;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static systems.raptor.cafe_latte.DynamicVariable.bind;

public class Block<T> {

  @SuppressWarnings("rawtypes")
  private final static DynamicVariable<List<Block>> activeBlocks = new DynamicVariable<>(new LinkedList<>());

  private final Function<Block<T>, T> function;

  public Block(Function<Block<T>, T> function) {
    this.function = function;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public T run() {
    List<Block> newActiveBlocks = Stream.concat(Stream.of(this), activeBlocks.get().stream())
            .collect(Collectors.toList());
    var ref = new Object() {
      T returnValue;
    };
    bind(activeBlocks, newActiveBlocks, () -> {
      try {
        ref.returnValue = function.apply(this);
      } catch (ReturnFrom rf) {
        if (rf.getTag() == this) {
          ref.returnValue = (T) rf.getValue();
        } else {
          throw rf;
        }
      }
    });
    return ref.returnValue;
  }

  @SuppressWarnings("rawtypes")
  public static void returnFrom(Block block, Object value) {
    if (!activeBlocks.get().contains(block)) {
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
