package systems.raptor.cafe_latte.control_flow.tagbody;

import org.junit.jupiter.api.Test;
import systems.raptor.cafe_latte.control_flow.ControlFlowException;

import static org.junit.jupiter.api.Assertions.*;
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.*;
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.tag;

class TagbodyTest {

  @Test
  public void tagbodyNormalReturnTest() {
    var ref = new Object() {
      boolean passed = true;
    };
    tagbody((tagbody) -> {
      ref.passed = true;
    });
    assertTrue(ref.passed);
  }

  @Test
  public void tagbodySingleGoTest() {
    var ref = new Object() {
      int count = 0;
    };
    TagbodyTag tag = tag();
    tagbody(tag,
            (tagbody) -> ref.count++,
            (tagbody) -> {
              if (ref.count < 10) {
                go(tagbody, tag);
              }
            });
    assertEquals(10, ref.count);
  }

  @Test
  public void tagbodyDoubleGoTest() {
    var ref = new Object() {
      int count = 0;
    };
    TagbodyTag tag1 = tag(), tag2 = tag();
    tagbody(tag1,
            (tagbody) -> ref.count++,
            (tagbody) -> {
              if (ref.count < 10) {
                go(tagbody, tag1);
              } else {
                go (tagbody, tag2);
              }
            },
            (tagbody) -> ref.count++,
            tag2);
    assertEquals(10, ref.count);
  }

  @Test
  public void tagbodyControlFlowExceptionTest() {
    TagbodyTag tag = tag();
    Tagbody tagbody = new Tagbody(tag);
    assertThrows(ControlFlowException.class, () -> go(tagbody, tag));
  }

  @Test
  public void tagbodyInvalidAssociationExceptionTest() {
    TagbodyTag tag = tag();
    assertThrows(ControlFlowException.class, () ->
            tagbody((tagbody) -> go(tagbody, tag)));
  }

}