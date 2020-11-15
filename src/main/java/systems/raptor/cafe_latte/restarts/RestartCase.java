package systems.raptor.cafe_latte.restarts;

import systems.raptor.cafe_latte.control_flow.block.Block;
import systems.raptor.cafe_latte.control_flow.tagbody.Tagbody;
import systems.raptor.cafe_latte.control_flow.tagbody.TagbodyElement;
import systems.raptor.cafe_latte.control_flow.tagbody.TagbodyTag;
import systems.raptor.cafe_latte.restarts.Restart.ArgumentStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static systems.raptor.cafe_latte.control_flow.block.Block.returnFrom;
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.go;
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.tag;

public class RestartCase<T, R> implements Supplier<T> {

  private final Supplier<T> body;
  private final List<Restart<R, T>> restarts;
  private final Block<T> block;

  public RestartCase(List<Restart<R, T>> restarts, Supplier<T> body) {
    this.restarts = restarts;
    this.body = body;
    block = generateBlock();
  }

  private Block<T> generateBlock() {
    Block<T> block = new Block<>();
    Tagbody tagbody = generateTagbody(block);
    block.setFunction((block1) -> {
      tagbody.accept(tagbody);
      return null;
    });
    return block;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Tagbody generateTagbody(Block<T> block) {
    ArgumentStorage argumentStorage = new ArgumentStorage();
    List<TagbodyElement> tagbodyElements = new LinkedList<>();
    Tagbody tagbody = new Tagbody();
    tagbodyElements.add((tagbody1) -> new RestartBind<T>((List) restarts, () -> {
      returnFrom(block, body.get());
      return null;
    }).get());
    for (Restart<R, T> restart : restarts) {
      TagbodyTag tag = tag();
      restart.trampolineTo(argumentStorage, tagbody, tag);
      Supplier<T> supplier = () -> restart.getFunction().apply((R) argumentStorage.transferredArgument);
      tagbodyElements.add(tag);
      tagbodyElements.add((tagbody1) -> returnFrom(block, supplier.get()));
    }
    tagbody.setElements(tagbodyElements.toArray(new TagbodyElement[]{}));
    return tagbody;
  }

  @Override
  public T get() {
    return block.get();
  }
}
