import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.Test;

/**
 * Used to test the Functionality only of the main method.
 */
public class CalendarRunnerTest {

  @Test
  public void testMainRunsSuccessfully() {
    String input = "create event \"Test Event\" on 2025-10-30\nquit\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    System.setOut(new PrintStream(out));

    String[] args = {"--mode", "interactive"};
    CalendarRunner.main(args);

    String output = out.toString();

    assertTrue("Expected 'Created' or 'Series' in output",
        output.contains("Created") || output.contains("Series"));
  }
}
