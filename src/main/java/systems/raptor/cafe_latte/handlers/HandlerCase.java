package systems.raptor.cafe_latte.handlers;

import org.apache.commons.lang3.tuple.Pair;
import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.conditions.Error;
import systems.raptor.cafe_latte.control_flow.block.Block;
import systems.raptor.cafe_latte.control_flow.tagbody.Tagbody;
import systems.raptor.cafe_latte.control_flow.tagbody.TagbodyElement;
import systems.raptor.cafe_latte.control_flow.tagbody.TagbodyTag;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static systems.raptor.cafe_latte.control_flow.block.Block.returnFrom;
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.go;
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.tag;

public class HandlerCase<T> implements Supplier<T> {

  private final List<Pair<Class<? extends Condition>, Function<Condition, T>>> pairs;
  private final Block<T> block;

  public HandlerCase(List<Pair<Class<? extends Condition>, Function<Condition, T>>> pairs,
                     Supplier<T> body) {
    this.pairs = pairs;
    block = generateBlock(body);
  }

  private Block<T> generateBlock (Supplier<T> body){
    Block<T> block = new Block<>();
    Tagbody tagbody = generateTagbody(block, body);
    block.setFunction((block1) -> {
      tagbody.accept(tagbody);
      return null;
    });
    return block;
  }

  private Tagbody generateTagbody(Block<T> block, Supplier<T> body) {
    var ref = new Object() {
      Condition transferredCondition;
    };
    List<TagbodyElement> tagbodyElements = new LinkedList<>();
    List<Handler> handlers = new LinkedList<>();
    Tagbody tagbody = new Tagbody();
    tagbodyElements.add((tagbody1) -> new HandlerBind<T>(handlers, () -> {
      returnFrom(block, body.get());
      return null;
    }).get());
    for (Pair<Class<? extends Condition>, Function<Condition, T>> pair : pairs) {
      TagbodyTag tag = tag();
      handlers.add(new Handler(pair.getLeft(), (c) -> {
        ref.transferredCondition = c;
        go(tagbody, tag);
      }));
      tagbodyElements.add(tag);
      tagbodyElements.add((tagbody1) ->
              returnFrom(block, pair.getRight().apply(ref.transferredCondition)));
    }
    tagbody.setElements(tagbodyElements.toArray(new TagbodyElement[]{}));
    return tagbody;
  }

  @Override
  public T get() {
    return block.get();
  }

  public static Condition ignoreErrors(Runnable body) {
    Function<Condition, Condition> function = (x) -> x;
    HandlerCase<Condition> handlerCase = new HandlerCase<>(List.of(Pair.of(Error.class, function)), () -> {
      body.run();
      return null;
    });
    return handlerCase.get();
  }
}
