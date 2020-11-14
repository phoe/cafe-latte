package systems.raptor.cafe_latte.control_flow.block;

import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.control_flow.ControlFlowException;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.control_flow.block.Block.returnFrom;

class BlockTest {

  @Test
  public void BlockNormalReturnTest() {
    assertEquals("foo", new Block<>((block) -> "foo").get());
  }

  @Test
  public void BlockReturnFromTest() {
    assertEquals("foo", new Block<>((block) -> {
      returnFrom(block, "foo");
      return "bar";
    }).get());
  }

  @Test
  public void NestedBlockReturnFromTest() {
    assertEquals("foo", new Block<>((block1) -> {
      new Block<>((block2) -> {
        returnFrom(block1, "foo");
        return "bar";
      }).get();
      return "bar";
    }).get());
  }

  @Test
  public void BlockControlFlowExceptionTest() {
    Block<String> block1 = new Block<>((block) -> "foo");
    assertThrows(ControlFlowException.class, () -> returnFrom(block1, "bar"));
  }

}