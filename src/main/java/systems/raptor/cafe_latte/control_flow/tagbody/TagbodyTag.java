package systems.raptor.cafe_latte.control_flow.tagbody;

import systems.raptor.cafe_latte.control_flow.ControlFlowException;

public class TagbodyTag implements TagbodyElement {

  Tagbody associatedTagbody = null;

  @Override
  public void accept(Tagbody tagbody) {}

  void associate(Tagbody tagbody) {
    if (this.associatedTagbody == null) {
      this.associatedTagbody = tagbody;
    } else {
      throw new ControlFlowException("Attempted to reuse a tag between multiple tagbodies");
    }
  }
}