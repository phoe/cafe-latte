package systems.raptor.cafe_latte;

import systems.raptor.cafe_latte.control_flow.block.Block;
import systems.raptor.cafe_latte.control_flow.grasp.Grasp;
import systems.raptor.cafe_latte.control_flow.tagbody.TagbodyElement;
import systems.raptor.cafe_latte.control_flow.tagbody.TagbodyTag;

import static systems.raptor.cafe_latte.DynamicVariable.bind;
import static systems.raptor.cafe_latte.control_flow.grasp.Grasp.fling;
import static systems.raptor.cafe_latte.control_flow.tagbody.Tagbody.*;
import static systems.raptor.cafe_latte.control_flow.block.Block.*;

public class Main {

  public static void main(String[] args) {
    testDynaVars();
    testTagbody();
    testBlock();
    testGrasp();
  }

  public static DynamicVariable<String> dynaVar = new DynamicVariable<>("Hello");

  public static void testDynaVars() {
    System.out.println(dynaVar.get());
    bind(dynaVar, "Dear", () -> {
      System.out.println(dynaVar.get());
      bind(dynaVar, "World", () -> {
        System.out.println(dynaVar.get());
        dynaVar.set("Not-World");
        System.out.println(dynaVar.get());
      });
    });

    System.out.println(dynaVar.get());
    bind(dynaVar, "Dear", () -> {
      System.out.println(dynaVar.get());
      testDynaVarsHelperFunction();
    });
  }

  public static void testDynaVarsHelperFunction() {
    bind(dynaVar, "Kinda-Sorta-World", () -> {
      System.out.println(dynaVar.get());
    });
  }

  public static void testTagbody() {
    var ref = new Object() {
      boolean continueRunning = true;
    };
    TagbodyTag tag1 = tag(), tag2 = tag();
    TagbodyElement code1 = (tagbody) -> {
      if (ref.continueRunning) {
        ref.continueRunning = false;
        go(tagbody, tag1);
      } else {
        go(tagbody, tag2);
      }
    };
    tagbody((tagbody) -> System.out.println("Hello world!"),
            tag1,
            (tagbody) -> System.out.println("This should get printed twice"),
            code1,
            (tagbody) -> System.out.println("This will not get printed"),
            tag2,
            (tagbody) -> System.out.println("Goodbye world!"));
  }

  public static void testBlock() {
    System.out.println(new Block<>((block) -> "Hello world!").get());
    System.out.println(new Block<>((block) -> {
      returnFrom(block, "This should be returned");
      return "This should not be returned";
    }).get());
  }

  public static void testGrasp() {
    System.out.println(new Grasp<>(42, () -> "Hello world!").get());
    System.out.println(new Grasp<>(42, () -> {
      fling(42, "This should be returned");
      return "This should not be returned";
    }).get());
  }

}
