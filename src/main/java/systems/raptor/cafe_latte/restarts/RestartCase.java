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
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.tag;

public class RestartCase<RestartArgument, ReturnType> implements Supplier<ReturnType> {

  private final Supplier<RestartArgument> body;
  private final List<Restart<RestartArgument, ReturnType>> restarts;
  private final Block<ReturnType> block;

  public RestartCase(List<Restart<RestartArgument, ReturnType>> restarts, Supplier<RestartArgument> body) {
    this.restarts = restarts;
    this.body = body;
    block = generateBlock();
  }

  private Block<ReturnType> generateBlock() {
    Block<ReturnType> block = new Block<>();
    Tagbody tagbody = generateTagbody(block);
    block.setFunction((block1) -> {
      tagbody.accept(tagbody);
      return null;
    });
    return block;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Tagbody generateTagbody(Block<ReturnType> block) {
    ArgumentStorage argumentStorage = new ArgumentStorage();
    List<TagbodyElement> tagbodyElements = new LinkedList<>();
    Tagbody tagbody = new Tagbody();
    tagbodyElements.add((tagbody1) -> new RestartBind<ReturnType>((List) restarts, () -> {
      returnFrom(block, body.get());
      return null;
    }).get());
    for (Restart<RestartArgument, ReturnType> restart : restarts) {
      TagbodyTag tag = tag();
      restart.trampolineTo(argumentStorage, tagbody, tag);
      Supplier<ReturnType> supplier = () -> restart.getFunction().apply((RestartArgument) argumentStorage.transferredArgument);
      tagbodyElements.add(tag);
      tagbodyElements.add((tagbody1) -> returnFrom(block, supplier.get()));
    }
    tagbody.setElements(tagbodyElements.toArray(new TagbodyElement[]{}));
    return tagbody;
  }

  @Override
  public ReturnType get() {
    return block.get();
  }

  public static Boolean withSimpleRestart(String name, String report, Runnable body) {
    Restart<Object, Boolean> restart = new Restart<>(name, (x) -> true, report);
    RestartCase<Object, Boolean> restartCase = new RestartCase<>(List.of(restart), () -> {
      body.run();
      return false;
    });
    return restartCase.get();
  }
}
