package systems.raptor.cafe_latte.handlers;

import systems.raptor.cafe_latte.conditions.Condition;
import systems.raptor.cafe_latte.conditions.Error;
import systems.raptor.cafe_latte.control_flow.block.Block;
import systems.raptor.cafe_latte.control_flow.tagbody.Tagbody;
import systems.raptor.cafe_latte.control_flow.tagbody.TagbodyElement;
import systems.raptor.cafe_latte.control_flow.tagbody.TagbodyTag;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static systems.raptor.cafe_latte.control_flow.block.Block.returnFrom;
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.go;
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.tag;

public class HandlerCase<T> implements Supplier<T> {

  private final Supplier<T> body;
  private final List<Handler<T>> handlers;
  private final Block<T> block;

  public HandlerCase(List<Handler<T>> handlers, Supplier<T> body) {
    this.handlers = handlers;
    this.body = body;
    block = generateBlock();
  }

  static class ConditionStorage {
    Condition transferredCondition;
  }

  private Block<T> generateBlock (){
    Block<T> block = new Block<>();
    Tagbody tagbody = generateTagbody(block);
    block.setFunction((block1) -> {
      tagbody.accept(tagbody);
      return null;
    });
    return block;
  }

  private Tagbody generateTagbody(Block<T> block) {
    ConditionStorage conditionStorage = new ConditionStorage();
    List<TagbodyElement> tagbodyElements = new LinkedList<>();
    List<Handler<Object>> trampolineHandlers = new LinkedList<>();
    Tagbody tagbody = new Tagbody();
    tagbodyElements.add((tagbody1) -> new HandlerBind<T>(trampolineHandlers, () -> {
      returnFrom(block, body.get());
      return null;
    }).get());
    for (Handler<T> handler : handlers) {
      TagbodyTag tag = tag();
      Handler<Object> newHandler = new Handler<>(handler.getConditionClass(), (condition) -> {
        conditionStorage.transferredCondition = condition;
        go(tagbody, tag);
        return null;
      });
      trampolineHandlers.add(newHandler);
      tagbodyElements.add(tag);
      tagbodyElements.add((tagbody1) ->
              returnFrom(block, handler.apply(conditionStorage.transferredCondition)));
    }
    tagbody.setElements(tagbodyElements.toArray(new TagbodyElement[]{}));
    return tagbody;
  }

  @Override
  public T get() {
    return block.get();
  }

  public static Condition ignoreErrors(Runnable body) {
    Handler<Condition> handler = new Handler<>(Error.class, (x) -> x);
    HandlerCase<Condition> handlerCase = new HandlerCase<>(List.of(handler), () -> {
      body.run();
      return null;
    });
    return handlerCase.get();
  }
}
