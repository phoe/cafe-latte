package systems.raptor.cafe_latte.restarts;

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

public class RestartCase<T, R> implements Supplier<R> {

  private final Supplier<R> body;
  private final List<Restart<T, R>> restarts;
  private final Block<R> block;

  public RestartCase(List<Restart<T, R>> restarts, Supplier<R> body) {
    this.restarts = restarts;
    this.body = body;
    block = generateBlock();
  }

  static class argumentStorage {
    Object transferredArgument;
  }

  private Block<R> generateBlock() {
    Block<R> block = new Block<>();
    Tagbody tagbody = generateTagbody(block);
    block.setFunction((block1) -> {
      tagbody.accept(tagbody);
      return null;
    });
    return block;
  }

  @SuppressWarnings("unchecked")
  private Tagbody generateTagbody(Block<R> block) {
    argumentStorage argumentStorage = new argumentStorage();
    List<TagbodyElement> tagbodyElements = new LinkedList<>();
    List<Restart<Object, Object>> trampolineRestarts = new LinkedList<>();
    Tagbody tagbody = new Tagbody();
    tagbodyElements.add((tagbody1) -> new RestartBind<T, R>(trampolineRestarts, () -> {
      returnFrom(block, body.get());
      return null;
    }).get());
    for (Restart<T, R> restart : restarts) {
      TagbodyTag tag = tag();
      Restart<Object, Object> newRestart = new Restart<>(restart.getName(), (argument) -> {
        argumentStorage.transferredArgument = argument;
        go(tagbody, tag);
        return null;
      }, restart.getReportFunction(), (Supplier<Object>) restart.getInteractiveFunction(),
              restart.getTestFunction());
      trampolineRestarts.add(newRestart);
      tagbodyElements.add(tag);
      tagbodyElements.add((tagbody1) ->
              returnFrom(block, restart.apply((T) argumentStorage.transferredArgument)));
    }
    tagbody.setElements(tagbodyElements.toArray(new TagbodyElement[]{}));
    return tagbody;
  }

  @Override
  public R get() {
    return block.get();
  }
}
