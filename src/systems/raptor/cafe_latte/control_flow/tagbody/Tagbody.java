package systems.raptor.cafe_latte.control_flow.tagbody;

import systems.raptor.cafe_latte.DynamicVariable;
import systems.raptor.cafe_latte.control_flow.ControlFlowException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static systems.raptor.cafe_latte.DynamicVariable.bind;

public class Tagbody {

  private final static DynamicVariable<List<TagbodyTag>> activeTags = new DynamicVariable<>(new LinkedList<>());

  private final List<TagbodyElement> elements;

  private Tagbody(TagbodyElement... elements) {
    this.elements = Arrays.asList(elements);
    List<TagbodyTag> tags = this.elements.stream()
            .filter((x) -> x instanceof TagbodyTag)
            .map((x) -> (TagbodyTag) x)
            .collect(Collectors.toList());
    List<TagbodyTag> newActiveTags = Stream.concat(tags.stream(), activeTags.get().stream())
            .collect(Collectors.toList());
    bind(activeTags, newActiveTags, () -> {
      Map.Entry<Boolean, Integer> result = new AbstractMap.SimpleEntry<>(true, 0);
      while (result.getKey()) {
        result = runHelper(result.getValue());
      }
    });
  }

  private Map.Entry<Boolean, Integer> runHelper(int startPosition) {
    try {
      for (int i = startPosition; i < elements.size(); ++i) {
        TagbodyElement element = elements.get(i);
        element.run();
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
    new Tagbody(elements);
  }

  public static TagbodyTag tag() {
    return new TagbodyTag();
  }

  public static void go(TagbodyTag tag) {
    if (!activeTags.get().contains(tag)) {
      throw new ControlFlowException("Attempted to go() to a tagbody tag that is no longer in scope");
    } else {
      throw new Go(tag);
    }
  }

}
