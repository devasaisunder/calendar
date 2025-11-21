import org.junit.Test;

/**
 * This file is testing the responsibility of the Main method.
 * input handling through InputFactory method.
 * It checks for valid modes.
 * valid modes:
 * --mode interactive.
 * --mode headless file.
 * Test verifies that the main method directs the classes as expected in headless, interactive
 * and wrong input modes.
 */
public class CalendarInputFactoryTest {


  @Test
  public void testInputFactoryInvalid() {
    String[] args = new String[0];

    try {
      InputFactory.getInput(args);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }

  }

  @Test
  public void testInputFactoryInvalid1() {
    String[] args = new String[3];
    args[0] = "--mode";
    args[1] = "--mode";

    try {
      InputFactory.getInput(args);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }

  }

  @Test
  public void testInputFactoryInvalidSize() {
    String[] args = new String[4];

    try {
      InputFactory.getInput(args);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }

  }

  @Test
  public void testInputFactoryInvalidSize1() {
    String[] args = new String[3];
    args[0] = "interactive";

    try {
      InputFactory.getInput(args);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testInputFactoryInvalidSize2() {
    String[] args = new String[3];
    args[0] = "interactive";

    try {
      InputFactory.getInput(args);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testInputFactoryValidInteractive() {
    String[] args = new String[3];
    args[0] = "--mode";
    args[1] = "interactive";

    Readable input;
    try {
      input = InputFactory.getInput(args);
      assert true;
    } catch (IllegalArgumentException e) {
      assert false;
    }
  }

  @Test
  public void testInputFactoryValidHeadless() {
    String[] args = new String[3];
    args[0] = "--mode";
    args[1] = "headless";
    args[2] = "src/test/java/CreateCommands.txt";

    Readable input;
    try {
      input = InputFactory.getInput(args);
      assert true;
    } catch (IllegalArgumentException e) {
      assert false;
    }
  }

  @Test
  public void testInputFactoryInValidHeadless() {
    String[] args = new String[2];
    args[0] = "--mode";
    args[1] = "headless";

    Readable input;
    try {
      input = InputFactory.getInput(args);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testInputFactoryInValidHeadless1() {
    String[] args = new String[3];
    args[0] = "--mode";
    args[1] = "headless";
    args[2] = "CommandsInvalidFile.txt";

    InputFactory inputFactory = new InputFactory();

    Readable input;
    try {
      input = InputFactory.getInput(args);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }


}
