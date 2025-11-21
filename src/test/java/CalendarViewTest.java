import static org.junit.Assert.assertEquals;

import calendar.view.CalendarViewImpl;
import org.junit.Test;

/**
 * Represents tests for a Calendar View.
 * It tests constructor validation, rendering, and error message formatting.
 */
public class CalendarViewTest {


  @Test
  public void testViewConstructor() {
    try {
      new CalendarViewImpl(null);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testRender() {
    StringBuilder sb = new StringBuilder();
    CalendarViewImpl view = new CalendarViewImpl(sb);
    CalendarViewImpl defaultView = new CalendarViewImpl();
    view.render("Hello");
    assertEquals(sb.toString(), "Hello" + System.lineSeparator());
  }

  @Test
  public void testRenderErrorWritesFormattedError() {
    StringBuilder sb = new StringBuilder();
    CalendarViewImpl view = new CalendarViewImpl(sb);
    view.renderError("Error Message");
    String expectedPrefix = "!!***Error Message***!!" + System.lineSeparator();
    assertEquals(sb.toString(), expectedPrefix);
  }
}
