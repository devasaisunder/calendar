package calendar.model.testing;

import static org.junit.Assert.assertTrue;

import calendar.controller.AdvanceCalendarController;
import calendar.controller.CalendarController;
import calendar.model.interfaces.CalendarContainer;
import calendar.view.CalendarView;
import calendar.view.CalendarViewImpl;
import java.io.StringReader;
import org.junit.Test;

/**
 * Tests the flow of different calendar commands in AdvanceCalendarController
 * using mock objects to verify method calls and error handling.
 */

public class AdvancedControllerTest {

  @Test
  public void testCreateCommandFlow() {
    String createCalendar = "create calendar --name newcal --timezone Asia/Kolkata";
    StringReader inputStream = new StringReader(createCalendar);
    StringBuilder output = new StringBuilder();
    CalendarContainer calendarContainer = new MockCalendarContainer(output);
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    assertTrue(output.toString().contains("Call reached addCalendar"));
  }

  @Test
  public void testEditCommandFlow() {
    String createCalendar = "edit calendar --name newcal --property name cal";
    StringReader inputStream = new StringReader(createCalendar);
    StringBuilder output = new StringBuilder();
    StringBuilder log = new StringBuilder();
    CalendarContainer calendarContainer = new MockCalendarContainer(log);
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    assertTrue(log.toString().contains("Call reached updateCalendar"));
  }

  @Test
  public void testUseCalendarCommandFlow() {
    String createCalendar = "use calendar --name newcal";
    StringReader inputStream = new StringReader(createCalendar);
    StringBuilder output = new StringBuilder();
    StringBuilder log = new StringBuilder();
    CalendarContainer calendarContainer = new MockCalendarContainer(log);
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    System.out.println(log.toString());
    assertTrue(log.toString().contains("Call reached setActiveCalendar"));
  }

  @Test
  public void testGetActiveCalenar() {
    StringBuilder output = new StringBuilder();
    CalendarContainer mockContainer = new MockCalendarContainer(output);
    mockContainer.getActiveCalendar();
    assertTrue(output.toString().contains("Call reached getActiveCalendar"));
  }

  @Test
  public void testGetCalendars() {
    StringBuilder output = new StringBuilder();
    CalendarContainer mockContainer = new MockCalendarContainer(output);
    mockContainer.getCalendars();
    assertTrue(output.toString().contains("Call reached getCalendars"));
  }

  @Test
  public void testIoException() {
    String createCalendar = "use calendar --name newcal";
    StringReader inputStream = new StringReader(createCalendar);
    Appendable log = new FakeAppendable();
    CalendarContainer calendarContainer = new MockCalendarContainer(log);
    CalendarView calendarView = new CalendarViewImpl(log);
    try {
      new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
      assert false;
    } catch (Exception e) {
      assert true;
    }

  }
}
