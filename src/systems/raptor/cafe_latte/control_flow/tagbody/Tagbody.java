package systems.raptor.cafe_latte.control_flow.tagbody;

import systems.raptor.cafe_latte.control_flow.ControlFlowException;

import java.util.*;
import java.util.function.Consumer;

public class Tagbody implements Consumer<Tagbody> {

  private final List<TagbodyElement> elements;

  private boolean valid;

  public Tagbody(TagbodyElement... elements) {
    this.elements = Arrays.asList(elements);
  }

  @Override
  public void accept(Tagbody tagbody) {
    valid = true;
    Map.Entry<Boolean, Integer> result = new AbstractMap.SimpleEntry<>(true, 0);
    while (result.getKey()) {
      result = runHelper(result.getValue());
    }
    valid = false;
  }

  private Map.Entry<Boolean, Integer> runHelper(int startPosition) {
    try {
      for (int i = startPosition; i < elements.size(); ++i) {
        TagbodyElement element = elements.get(i);
        element.accept(this);
      }
      return new AbstractMap.SimpleEntry<>(false, elements.size());
    } catch (Go go) {
      TagbodyTag tag = (TagbodyTag) go.getTag();
      if (elements.contains(tag)) {
        return new AbstractMap.SimpleEntry<>(true, elements.indexOf(tag));
      } else {
        throw go;
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
    if (!tagbody.valid) {
      throw new ControlFlowException("Attempted to go() to a tagbody that is no longer in scope");
    } else {
      throw new Go(tag);
    }
  }

}
