package systems.raptor.cafe_latte.control_flow.tagbody;

import systems.raptor.cafe_latte.control_flow.ControlFlowException;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Tagbody implements Consumer<Tagbody> {

  private final List<TagbodyElement> elements;

  private boolean valid;

  public Tagbody(TagbodyElement... elements) {
    this.elements = Arrays.asList(elements);
    for (TagbodyElement element : elements) {
      if (element instanceof TagbodyTag) {
        ((TagbodyTag) element).associate(this);
      }
    }
  }

  @Override
  public void accept(Tagbody tagbody) {
    int startFrom = 0;
    boolean keepGoing = true;
    while (keepGoing) {
      valid = true;
      try {
        for (int i = startFrom; i < elements.size(); ++i) {
          TagbodyElement element = elements.get(i);
          element.accept(this);
        }
        keepGoing = false;
      } catch (Go go) {
        TagbodyTag tag = (TagbodyTag) go.getTag();
        if (elements.contains(tag)) {
          startFrom = elements.indexOf(tag);
        } else {
          throw go;
        }
      } finally {
        valid = false;
      }
    }
  }

  public static void tagbody(TagbodyElement... elements) {
    Tagbody tagbody = new Tagbody(elements);
    tagbody.accept(tagbody);
  }

  public static TagbodyTag tag() {
    return new TagbodyTag();
  }

  public static void go(Tagbody tagbody, TagbodyTag tag) {
    if (!(tagbody == tag.associatedTagbody)) {
      throw new ControlFlowException("Attempted to go() to a tag not associated with a given tagbody");
    } else if (!tagbody.valid) {
      throw new ControlFlowException("Attempted to go() to a tagbody that is no longer in scope");
    } else  {
      throw new Go(tag);
    }
  }

}
