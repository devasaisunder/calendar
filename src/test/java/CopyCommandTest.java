import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import calendar.controller.AdvanceCalendarController;
import calendar.controller.CalendarController;
import calendar.model.CalendarContainerImpl;
import calendar.model.interfaces.AdvancedCalendar;
import calendar.model.interfaces.CalendarContainer;
import calendar.model.interfaces.EventReadOnly;
import calendar.view.CalendarView;
import calendar.view.CalendarViewImpl;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * Tests for verifying the copy event functionality across calendars,
 * including valid, invalid, and time zone–related cases.
 */
public class CopyCommandTest {

  @Test
  public void testCopyEvent() {
    String input = "create calendar --name cal1 --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name cal1"
        + System.lineSeparator()
        + "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59"
        + System.lineSeparator()
        + "create calendar --name cal2 --timezone America/New_York"
        + System.lineSeparator()
        + "copy event Meeting on 2025-06-26T23:45 --target cal2 to 2025-10-26T23:45";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    Map<String, AdvancedCalendar> calendars = calendarContainer.getCalendars();
    assertTrue(calendars.containsKey("cal1"));
    assertTrue(calendars.containsKey("cal2"));
    assertTrue(output.toString().contains("Calendar with name cal1 has been successfully created"));
    assertTrue(output.toString().contains("Created Event"));
    assertTrue(output.toString().contains("Subject: Meeting, Start: 2025-06-26T23:45"));
    assertTrue(output.toString().contains("Calendar with name cal2 has been successfully created"));
    assertTrue(output.toString().contains("Copied Events"));
    assertTrue(output.toString().contains("Subject: Meeting, Start: 2025-10-26T23:45"));
  }

  @Test
  public void testCopyEventToDifferentTimeZone() {
    String input = "create calendar --name cal1 --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name cal1"
        + System.lineSeparator()
        + "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59"
        + System.lineSeparator()
        + "create calendar --name cal2 --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "copy event Meeting on 2025-06-26T23:45 --target cal2 to 2025-10-26T23:45";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    Map<String, AdvancedCalendar> calendars = calendarContainer.getCalendars();
    assertTrue(calendars.containsKey("cal1"));
    assertTrue(calendars.containsKey("cal2"));
    assertTrue(output.toString().contains("Calendar with name cal1 has been successfully created"));
    assertTrue(output.toString().contains("Created Event"));
    assertTrue(output.toString().contains("Subject: Meeting, Start: 2025-06-26T23:45"));
    assertTrue(output.toString().contains("Calendar with name cal2 has been successfully created"));
    assertTrue(output.toString().contains("Copied Events"));
    assertTrue(output.toString().contains("Subject: Meeting, Start: 2025-10-26T23:45"));
  }

  @Test
  public void testCopyEventsToDifferentTimeZoneInvalid() {
    String input = "create calendar --name cal1 --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name cal1"
        + System.lineSeparator()
        + "create event Meet from 2025-10-25T08:00 to 2025-10-25T17:00 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create calendar --name cal2 --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "copy event Meeting on 2025-06-26T23:45 --target cal2 to 2025-10-26T23:45";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    Map<String, AdvancedCalendar> calendars = calendarContainer.getCalendars();
    System.out.println(output.toString());
    assertTrue(calendars.containsKey("cal1"));
    assertTrue(calendars.containsKey("cal2"));
    assertTrue(output.toString().contains("Calendar with name cal1 has been successfully created"));
    assertTrue(output.toString().contains("No matching event found to copy in: cal1"));
  }

  @Test
  public void testCopyEventsToDifferentTimeZoneValid() {
    String input = "create calendar --name cal1 --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name cal1"
        + System.lineSeparator()
        + "create event Meet from 2025-10-25T08:00 to 2025-10-25T17:00 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create calendar --name cal2 --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "copy events on 2025-10-26 --target cal2 to 2030-10-26";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    Map<String, AdvancedCalendar> calendars = calendarContainer.getCalendars();
    assertTrue(calendars.containsKey("cal1"));
    assertTrue(calendars.containsKey("cal2"));
    assertTrue(output.toString().contains("Calendar with name cal1 has been successfully created"));
    assertTrue(output.toString()
        .contains("WARNING: Series event in target calendar is spreading across multiple days"));
    assertTrue(output.toString().contains("Copied Events:"));
    assertTrue(output.toString()
        .contains("Subject: Meet, Start: 2030-10-27T17:30, End: 2030-10-28T02:30"));
    List<EventReadOnly> copiedEvents = calendars
        .get("cal2")
        .getAllEvents()
        .get(LocalDate.of(2030, 10, 27));
    assertEquals(1, copiedEvents.size());
    String copiedSubject = copiedEvents.get(0).getSubject();
    assertEquals("Meet", copiedSubject);
    assertEquals(LocalDateTime.of(2030, 10, 27, 17, 30),
        copiedEvents.get(0).getStartDateTime());
  }

  @Test
  public void testCopyEventsTypeTwoValid() {
    String input = "create calendar --name cal1 --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name cal1"
        + System.lineSeparator()
        + "create event Meet from 2025-10-25T08:00 to 2025-10-25T17:00 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create calendar --name cal2 --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "copy events between 2025-10-30 and 2025-11-31 --target cal2 to 2036-01-01";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    Map<String, AdvancedCalendar> calendars = calendarContainer.getCalendars();
    assertTrue(calendars.containsKey("cal1"));
    assertTrue(calendars.containsKey("cal2"));
    assertTrue(output.toString().contains("Calendar with name cal1 has been successfully created"));
    assertTrue(output.toString()
        .contains("WARNING: Series event in target calendar is spreading across multiple days"));
    assertTrue(output.toString().contains("Copied Events:"));
    Map<LocalDate, List<EventReadOnly>> copiedEvents = calendars
        .get("cal2")
        .getAllEvents();
    assertEquals(8, copiedEvents.size());
  }

  @Test
  public void testCopyInvalidCommands() {
    String input = "copy events between 2025-10-30 and 2025-11-31 --target cal2 to 2036-01-01";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    assertTrue(output.toString().contains("!!***No calendar with name cal2***!!"));
  }

  @Test
  public void testCopyInvalidCommandsTwo() {
    String input = "copy events random text";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    assertTrue(output.toString().contains("Invalid copy events command."));
  }

  @Test
  public void testCopyInvalidCommandsThree() {
    String input = "copy events btwn 2025-10-30 and 2025-11-31 --target cal2 to 2036-01-01";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    assertTrue(output.toString().contains("Invalid copy events command."));
  }

  @Test
  public void testCopyInvalidCommandsFour() {
    String input = "copy events btwn 2025-10-30 2025-11-31 --target cal2 to 2036-01-01";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    assertTrue(output.toString().contains("Invalid copy events command."));
  }

  @Test
  public void testCopyInvalidCommandsMultiple() {
    String input = "copy events btwn 2025-10-30 2025-11-31 --target cal2 to 2036-01-01"
        + System.lineSeparator()
        + "copy events between 2025-10-30 an 2025-11-31 --target cal2 to 2036-01-01"
        + System.lineSeparator()
        + "copy events between 2025-10-30 an 2025-11-31 -target cal2 to 2036-01-01"
        + System.lineSeparator()
        + "copy events between 2025-10-30 and 2025-11-31 --target cal2 too 2036-01-01"
        + System.lineSeparator()
        + "copy events oon 2025-10-26 --target cal2 to 2030-10-26"
        + System.lineSeparator()
        + "copy events on 2025-10-26 -target cal2 to 2030-10-26"
        + System.lineSeparator()
        + "copy events on 2025-10-26 --target cal2 ot 2030-10-26";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    assertTrue(output.toString().contains("Invalid copy events command."));
  }

  @Test
  public void testCopyFromSameCalendarToSameDate() {
    String input = "create calendar --name cal1 --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name cal1"
        + System.lineSeparator()
        + "create event Meet from 2025-10-25T08:00 to 2025-10-25T17:00 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "copy events between 2025-10-30 and 2025-11-31 --target cal1 to 2036-01-01";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    Map<String, AdvancedCalendar> calendars = calendarContainer.getCalendars();
    assertTrue(calendars.containsKey("cal1"));
    assertTrue(output.toString().contains("Calendar with name cal1 has been successfully created"));
    assertTrue(output.toString().contains("Copied Events:"));
    Map<LocalDate, List<EventReadOnly>> copiedEvents = calendars
        .get("cal1")
        .getAllEvents();
    assertEquals(18, copiedEvents.size());
    assertEquals(1,
        copiedEvents.get(LocalDate.of(2036, 01, 03)).size());
    assertEquals(1,
        copiedEvents.get(LocalDate.of(2036, 01, 06)).size());
    assertEquals(1,
        copiedEvents.get(LocalDate.of(2036, 01, 07)).size());
    assertEquals(1,
        copiedEvents.get(LocalDate.of(2036, 01, 10)).size());
    assertEquals(1,
        copiedEvents.get(LocalDate.of(2036, 01, 13)).size());
    assertEquals(1,
        copiedEvents.get(LocalDate.of(2036, 01, 14)).size());
    assertEquals(1,
        copiedEvents.get(LocalDate.of(2036, 01, 17)).size());
    assertEquals(1,
        copiedEvents.get(LocalDate.of(2036, 01, 20)).size());
  }

  @Test
  public void testCopyFromSameCalendarToDifferentDate() {
    String input = "create calendar --name cal1 --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name cal1"
        + System.lineSeparator()
        + "create event Meet from 2025-10-25T08:00 to 2025-10-25T17:00 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "copy events between 2025-10-30 and 2025-11-31 --target cal1 to 2025-10-30";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Event already exists***!!"));
  }

  @Test
  public void testPartialSeriesCopy() {
    String input = "create calendar --name cal1 --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name cal1"
        + System.lineSeparator()
        + "create event Meet from 2025-10-25T08:00 to 2025-10-25T17:00 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create calendar --name cal2 --timezone Asia/Kolkata"
        + System.lineSeparator()
        + "copy events between 2025-10-30 and 2025-11-03 --target cal2 to 2025-10-30";
    StringReader inputStream = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView calendarView = new CalendarViewImpl(output);
    CalendarContainer calendarContainer = new CalendarContainerImpl();
    CalendarController controller =
        new AdvanceCalendarController(calendarContainer, inputStream, calendarView);
    controller.run();
    Map<String, AdvancedCalendar> calendars = calendarContainer.getCalendars();
    assertTrue(calendars.containsKey("cal1"));
    assertTrue(calendars.containsKey("cal2"));
    assertTrue(output.toString().contains("Calendar with name cal1 has been successfully created"));
    assertTrue(output.toString()
        .contains("WARNING: Series event in target calendar is spreading across multiple days"));
    assertTrue(output.toString().contains("Copied Events:"));
    Map<LocalDate, List<EventReadOnly>> copiedEvents = calendars
        .get("cal2")
        .getAllEvents();
    Map<LocalDate, List<EventReadOnly>> originalCal = calendars.get("cal1").getAllEvents();
    assertEquals(10, originalCal.size());
    assertEquals(3, copiedEvents.size());
  }
}