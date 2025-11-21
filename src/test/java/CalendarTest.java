import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.controller.AdvanceCalendarController;
import calendar.controller.CalendarController;
import calendar.controller.CalendarControllerImpl;
import calendar.model.CalendarContainerImpl;
import calendar.model.CalendarImpl;
import calendar.model.interfaces.CalendarContainer;
import calendar.model.interfaces.CalendarEditable;
import calendar.model.interfaces.EventReadOnly;
import calendar.view.CalendarView;
import calendar.view.CalendarViewImpl;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Represents the tests for a calendar through commands by mimicking terminal via String Reader
 * and String Builder classes.
 */
public class CalendarTest {

  private CalendarEditable calendar;
  private CalendarControllerImpl calendarController;
  private StringBuilder output;
  private CalendarView cliView;

  /**
   * Sets up a fresh calendar instance, a string builder output and view before each test.
   */
  @Before
  public void setUp() {
    calendar = new CalendarImpl();
    output = new StringBuilder();
    cliView = new CalendarViewImpl(output);
  }


  @Test
  public void testCreateSingleEvent() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("Created Event: " + System.lineSeparator()
        + "Subject: Meeting, Start: 2025-06-26T23:45, End: 2025-06-26T23:59, "
        + "Description: No description given, "
        + "Type: SINGLE, AllDayEvent: false" + System.lineSeparator()));
  }

  @Test
  public void testCreateSingleEventDifferentAssertion() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    List<EventReadOnly> addedEvents = calendar.getEvents(
        LocalDateTime.of(2025, 6, 26, 23, 45),
        LocalDateTime.of(2025, 6, 26, 23, 58)
    );
    assertEquals(1, addedEvents.size());
    assertEquals("Meeting", addedEvents.get(0).getSubject());
    assertEquals(LocalDateTime.of(2025, 6, 26, 23, 45),
        addedEvents.get(0).getStartDateTime());
    assertEquals(LocalDateTime.of(2025, 6, 26, 23, 59),
        addedEvents.get(0).getEndDateTime());
  }

  @Test
  public void testCreateSingleEventMultipleWordSubject() {
    String input = "create event \"Online Meeting\" from 2025-06-26T23:45 to 2025-06-26T23:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    List<EventReadOnly> addedEvents = calendar.getEvents(
        LocalDateTime.of(2025, 6, 26, 23, 45),
        LocalDateTime.of(2025, 6, 26, 23, 58)
    );
    assertEquals(1, addedEvents.size());
    assertEquals("Online Meeting", addedEvents.get(0).getSubject());
    assertEquals(LocalDateTime.of(2025, 6, 26, 23, 45),
        addedEvents.get(0).getStartDateTime());
    assertEquals(LocalDateTime.of(2025, 6, 26, 23, 59),
        addedEvents.get(0).getEndDateTime());
  }

  @Test
  public void testCreateSingleEventMultipleWordSubjectDifferentAssertion() {
    String input = "create event \"Online Meeting\" from 2025-06-26T23:45 to 2025-06-26T23:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("Created Event: " + System.lineSeparator()
        + "Subject: Online Meeting, Start: 2025-06-26T23:45, End: 2025-06-26T23:59, "
        + "Description: No description given, "
        + "Type: SINGLE, AllDayEvent: false" + System.lineSeparator()));

  }

  @Test
  public void testCreateMultipleEvents() {
    String input = "create event \"Multiple Events\" from "
        + "2025-06-26T10:00 to 2025-06-26T11:00 repeats MRU for 5 times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    String expected = "Created a Series Event: "
        + System.lineSeparator()
        + "Subject: Multiple Events, Start: 2025-06-26T10:00, "
        + "End: 2025-06-26T11:00, Description: "
        + "No description given, Type: SERIES, AllDayEvent: false"
        + System.lineSeparator()
        + "Subject: Multiple Events, Start: 2025-06-29T10:00, "
        + "End: 2025-06-29T11:00, Description: "
        + "No description given, Type: SERIES, AllDayEvent: false"
        + System.lineSeparator()
        + "Subject: Multiple Events, Start: 2025-06-30T10:00, "
        + "End: 2025-06-30T11:00, Description: "
        + "No description given, Type: SERIES, AllDayEvent: false"
        + System.lineSeparator()
        + "Subject: Multiple Events, Start: 2025-07-03T10:00, "
        + "End: 2025-07-03T11:00, Description: "
        + "No description given, Type: SERIES, AllDayEvent: false"
        + System.lineSeparator()
        + "Subject: Multiple Events, Start: 2025-07-06T10:00, "
        + "End: 2025-07-06T11:00, Description: "
        + "No description given, Type: SERIES, AllDayEvent: false"
        + System.lineSeparator()
        + System.lineSeparator();

    System.out.println(output.toString());
    assertTrue(output.toString().contains(expected));
  }

  @Test
  public void testCreateMultipleEventsDifferentAssertion() {
    String input = "create event \"Multiple Events\" from "
        + "2025-06-26T10:00 to 2025-06-26T11:00 repeats MRU for 5 times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    List<LocalDateTime> expectedDates = ExpectedOutputGenerator.getTimesDates(
        LocalDateTime.of(2025, 6, 26, 10, 0),
        "MRU", 5);
    assertEquals(5, expectedDates.size());

    List<EventReadOnly> addedEvents = calendar.getEvents(
        LocalDateTime.of(2025, 6, 26, 10, 0),
        expectedDates.get(expectedDates.size() - 1));

    assertEquals(5, addedEvents.size());

    assertEquals(expectedDates.size(), addedEvents.size());
    List<DayOfWeek> eventDays = ExpectedOutputGenerator.populateDaysOfWeek("MRU");
    for (int i = 0; i < addedEvents.size(); i++) {
      assertEquals("Multiple Events", addedEvents.get(i).getSubject());
      assertEquals(expectedDates.get(i), addedEvents.get(i).getStartDateTime());
      assertTrue(eventDays.contains(addedEvents.get(i).getStartDateTime().getDayOfWeek()));
    }
  }

  @Test
  public void testCreateMultipleEventsUntilDate() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU until 2025-06-30";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();

    LocalDateTime start = LocalDateTime.of(2025, 6, 26, 23, 45);
    LocalDateTime end = LocalDateTime.of(2025, 6, 30, 23, 59);

    List<LocalDateTime> expectedDates = ExpectedOutputGenerator.getUntilDates(
        start, "MRU", end);

    assertEquals(3, expectedDates.size());

    List<EventReadOnly> addedEvents = calendar.getEvents(
        LocalDateTime.of(2025, 6, 26, 23, 45),
        expectedDates.get(expectedDates.size() - 1));

    assertEquals(3, addedEvents.size());

    assertEquals(expectedDates.size(), addedEvents.size());

    List<DayOfWeek> eventDays = ExpectedOutputGenerator.populateDaysOfWeek("MRU");

    for (int i = 0; i < addedEvents.size(); i++) {
      assertEquals("Meeting", addedEvents.get(i).getSubject());
      assertEquals(expectedDates.get(i), addedEvents.get(i).getStartDateTime());
      assertTrue(eventDays.contains(addedEvents.get(i).getStartDateTime().getDayOfWeek()));
    }
  }

  @Test
  public void testAllDayEventSingle() {
    String input = "create event \"Event 4\" on 2025-10-25";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    List<EventReadOnly> addedEvents = calendar.getEvents(
        LocalDateTime.of(2025, 10, 25, 0, 0),
        LocalDateTime.of(2025, 10, 25, 23, 59));

    assertEquals(1, addedEvents.size());
    assertEquals("Event 4", addedEvents.get(0).getSubject());
    assertEquals(LocalDateTime.of(2025, 10, 25, 8, 0), addedEvents.get(0).getStartDateTime());
    assertEquals(LocalDateTime.of(2025, 10, 25, 17, 0), addedEvents.get(0).getEndDateTime());
    assertTrue(addedEvents.get(0).isAllDay());
  }

  @Test
  public void testAllDayEventTimesMultiple() {
    String input = "create event \"Event 5\" on 2025-10-26 repeats MTW for 3 times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    List<LocalDateTime> expectedDates = ExpectedOutputGenerator.getTimesDates(
        LocalDateTime.of(2025, 10, 26, 8, 0),
        "MTW",
        3);
    System.out.println(expectedDates.toString());

    assertEquals(3, expectedDates.size());

    List<EventReadOnly> addedEvents = calendar.getEvents(
        LocalDateTime.of(2025, 10, 26, 0, 0),
        expectedDates.get(expectedDates.size() - 1));

    assertEquals(3, addedEvents.size());

    assertEquals(expectedDates.size(), addedEvents.size());

    List<DayOfWeek> eventDays = ExpectedOutputGenerator.populateDaysOfWeek("MTW");

    for (int i = 0; i < addedEvents.size(); i++) {
      assertEquals("Event 5", addedEvents.get(i).getSubject());
      assertEquals(expectedDates.get(i), addedEvents.get(i).getStartDateTime());
      assertTrue(eventDays.contains(addedEvents.get(i).getStartDateTime().getDayOfWeek()));
    }
  }

  @Test
  public void testAllDayEventUntilMultiple() {
    String input = "create event \"Event 6\" on 2025-10-12 repeats TUF until 2025-12-12";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    List<LocalDateTime> expectedDates = ExpectedOutputGenerator.getUntilDates(
        LocalDateTime.of(2025, 10, 12, 8, 0),
        "TUF",
        LocalDateTime.of(2025, 12, 12, 17, 0));
    System.out.println(expectedDates.toString());

    assertEquals(27, expectedDates.size());

    List<EventReadOnly> addedEvents = calendar.getEvents(
        LocalDateTime.of(2025, 10, 12, 0, 0),
        expectedDates.get(expectedDates.size() - 1));

    assertEquals(27, addedEvents.size());

    assertEquals(expectedDates.size(), addedEvents.size());

    List<DayOfWeek> eventDays = ExpectedOutputGenerator.populateDaysOfWeek("TUF");

    for (int i = 0; i < addedEvents.size(); i++) {
      assertEquals("Event 6", addedEvents.get(i).getSubject());
      assertEquals(expectedDates.get(i), addedEvents.get(i).getStartDateTime());
      assertTrue(eventDays.contains(addedEvents.get(i).getStartDateTime().getDayOfWeek()));
    }
  }

  @Test
  public void testAddEventUntilBeforeStart() {
    String input = "create event \"Hello Meet\" "
        + "on 2026-10-13 repeats TUF until 2025-12-12";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("!!***Repeat until date cannot be before start date***!!"
        + System.lineSeparator()));
  }

  @Test
  public void testAddEventEndBeforeStart() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2024-06-26T23:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("!!***End is before start***!!"
        + System.lineSeparator()));
  }

  @Test
  public void testAddEventsMultipleZeroTimes() {
    String input = "create event \"Multiple Events\" from 2025-06-26T10:00 to "
        + "2025-06-26T11:00 repeats MRU for 0 times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("!!***Invalid command.***!!"));
  }

  @Test
  public void testCreateEventOnRepetition() {
    String input = "create event \"Event 5\" on 2025-10-26 repeats MTW for 3 times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("Created a Series Event: "));
    assertTrue(output.toString().contains("Start: 2025-10-28T08:00, End: 2025-10-28T17:00"));
    assertTrue(output.toString().contains("Start: 2025-10-29T08:00, End: 2025-10-29T17:00"));
  }

  @Test
  public void testAllDayEventUntilMultipleTwo() {
    String input = "create event \"Event 6\" on 2025-10-12 repeats TUF until 2025-12-12";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    List<LocalDateTime> expectedDates = ExpectedOutputGenerator.getUntilDates(
        LocalDateTime.of(2025, 10, 12, 8, 0),
        "TUF",
        LocalDateTime.of(2025, 12, 12, 17, 0));
    System.out.println(expectedDates.toString());
    assertTrue(output.toString().contains("Created a Series Event:"));
    assertTrue(output.toString().contains("Event 6"));
  }

  @Test
  public void testThroughControllerString() {

    String input =
        "create event \"Meeting at Park\" from 2025-06-26T23:45 to 2025-06-26T23:59 "
            + "repeats MRU until 2025-06-30";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(System.out);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, view);
    controller.run();
  }

  @Test
  public void testException() {
    String input = "create event event1 from 2025-10-25T09:00 to 2025-10-25T10:00\n"
        + "create event event1 from 2025-10-20T09:00 to 2025-10-20T10:00 repeats S for 10 times\n"
        + "exit";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(System.out);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, view);
    controller.run();

  }

  @Test
  public void testAllDayEvent() {
    String input = "create event event1 from 2025-10-25T08:00 to 2025-10-25T17:00\n"
        + "edit event end event1 from 2025-10-25T08:00 to 2025-10-25T10:00 with 2025-10-25T17:00\n"
        + "exit";
    StringReader reader = new StringReader(input);
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(System.out);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, view);
    controller.run();
  }

  @Test
  public void testShowStatusTestAvailable() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59"
        + System.lineSeparator()
        + "show status on 2025-06-26T23:44";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("User is Available"));
  }

  @Test
  public void testShowStatusTestBusy() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59"
        + System.lineSeparator()
        + "show status on 2025-06-26T23:45";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("User is Busy"));
  }

  @Test
  public void testShowStatusTestBusyAgain() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59"
        + System.lineSeparator()
        + "show status on 2025-06-26T23:54";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("User is Busy"));
  }

  @Test
  public void testInvalidShowStatus() {
    String input = "show status from 2025-06-26T23:54 to 2025-06-26T23:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid show status command***!!"));
  }

  @Test
  public void testInvalidShowStatusTwo() {
    String input = "show status from 2025-06-26T23:54";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid show status command***!!"));
  }

  @Test
  public void testInvalidShowStatusAgain() {
    String input = "show status on";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid show status command***!!"));
  }

  @Test
  public void testInvalidExportCommand() {
    String input = "export cal";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid command: export cal***!!"));
  }

  @Test
  public void testExportCommand() throws IOException {
    String input = "create event event1 from 2025-10-25T09:00 to 2025-10-25T10:00"
        + System.lineSeparator()
        + "create event Meet from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create event \"Meeting at Park\" from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU until 2025-06-30" + System.lineSeparator()
        + "create event \"Event 4\" on 2025-10-25" + System.lineSeparator()
        + "create event \"Event 5\" on 2025-10-26 repeats MTW for 3 times"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "export cal exportedevents.csv"
        + System.lineSeparator()
        + "exit";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("Successfully exported"));
    File csvFile = new File("exportedevents.csv");
    assertTrue("CSV file should exist after export command", csvFile.exists());

    List<String> lines = Files.readAllLines(csvFile.toPath());
    assertFalse("CSV file should not be empty", lines.isEmpty());

    assertTrue(lines.stream().anyMatch(line -> line.contains("event1")));
    assertTrue(lines.stream().anyMatch(line -> line.contains("Meet")));
    assertTrue(lines.stream().anyMatch(line -> line.contains("Meeting at Park")));
    assertTrue(lines.stream().anyMatch(line -> line.contains("Event 4")));
    assertTrue(lines.stream().anyMatch(line -> line.contains("Event 5")));
    assertTrue(lines.stream().anyMatch(line -> line.contains("Hello Meet")));
    csvFile.delete();
  }

  @Test
  public void testInvalidExportCommandTwo() {
    String input = "export cal nofile";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString()
        .contains("Invalid file type"));
  }

  @Test
  public void testPrintCommand() {
    String input = "create event event1 from 2025-10-25T09:00 to 2025-10-25T10:00"
        + System.lineSeparator()
        + "create event Meet from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create event \"Meeting at Park\" from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU until 2025-06-30" + System.lineSeparator()
        + "create event \"Event 4\" on 2025-10-25" + System.lineSeparator()
        + "create event \"Event 5\" on 2025-10-26 repeats MTW for 3 times"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "print events on 2025-10-25"
        + System.lineSeparator()
        + "exit";

    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("> Subject: event1"));
  }

  @Test
  public void testPrintCommandFrom() {
    String input = "create event event1 from 2025-10-25T09:00 to 2025-10-25T10:00"
        + System.lineSeparator()
        + "create event Meet from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create event \"Meeting at Park\" from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU until 2025-06-30" + System.lineSeparator()
        + "create event \"Event 4\" on 2025-10-25" + System.lineSeparator()
        + "create event \"Event 5\" on 2025-10-26 repeats MTW for 3 times"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "print events from 2025-06-26T00:00 to 2025-06-30T00:00"
        + System.lineSeparator()
        + "exit";

    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("> Meet starting on 2025-06-26 at 23:45, "
        + "ending on 2025-06-26 at 23:59"));
    assertTrue(output.toString().contains("> Meeting at Park starting on 2025-06-26 at 23:45, "
        + "ending on 2025-06-26 at 23:59"));
    assertTrue(output.toString().contains("> Meet starting on 2025-06-29 at 23:45, "
        + "ending on 2025-06-29 at 23:59"));
    assertTrue(output.toString().contains("> Meeting at Park starting on 2025-06-29 at 23:45, "
        + "ending on 2025-06-29 at 23:59"));
  }

  @Test
  public void testPrintCommandInvalidOne() {
    String input = "create event event1 from 2025-10-25T09:00 to 2025-10-25T10:00"
        + System.lineSeparator()
        + "create event Meet from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create event \"Meeting at Park\" from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU until 2025-06-30" + System.lineSeparator()
        + "create event \"Event 4\" on 2025-10-25" + System.lineSeparator()
        + "create event \"Event 5\" on 2025-10-26 repeats MTW for 3 times"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "print events on"
        + System.lineSeparator()
        + "exit";

    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    System.out.println("done");
    assertTrue(output.toString().contains("!!***Invalid print command***!!"));
  }

  @Test
  public void testPrintCommandInvalidTwo() {
    String input = "create event event1 from 2025-10-25T09:00 to 2025-10-25T10:00"
        + System.lineSeparator()
        + "create event Meet from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create event \"Meeting at Park\" from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU until 2025-06-30" + System.lineSeparator()
        + "create event \"Event 4\" on 2025-10-25" + System.lineSeparator()
        + "create event \"Event 5\" on 2025-10-26 repeats MTW for 3 times"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "print events from 2025-06-26T00:00 too 2025-06-30T00:00"
        + System.lineSeparator()
        + "exit";

    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid print command***!!"));
  }

  @Test
  public void testPrintCommandInvalidThree() {
    String input = "create event event1 from 2025-10-25T09:00 to 2025-10-25T10:00"
        + System.lineSeparator()
        + "create event Meet from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create event \"Meeting at Park\" from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU until 2025-06-30" + System.lineSeparator()
        + "create event \"Event 4\" on 2025-10-25" + System.lineSeparator()
        + "create event \"Event 5\" on 2025-10-26 repeats MTW for 3 times"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "print events from 2025-06-26T00:00 to"
        + System.lineSeparator()
        + "exit";

    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid print command***!!"));
  }

  @Test
  public void testPrintCommandInvalidFour() {
    String input = "create event event1 from 2025-10-25T09:00 to 2025-10-25T10:00"
        + System.lineSeparator()
        + "create event Meet from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "create event \"Meeting at Park\" from 2025-06-26T23:45 to 2025-06-26T23:59 "
        + "repeats MRU until 2025-06-30" + System.lineSeparator()
        + "create event \"Event 4\" on 2025-10-25" + System.lineSeparator()
        + "create event \"Event 5\" on 2025-10-26 repeats MTW for 3 times"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "print events from"
        + System.lineSeparator()
        + "exit";

    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    System.out.println("done");
    assertTrue(output.toString().contains("!!***Invalid print command***!!"));
  }

  @Test
  public void testCreateInvalidCommand() {
    String input = "create event event1";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid creation command***!!"));
  }

  @Test
  public void testCreateInvalidCommandTwo() {
    String input = "create event eventSubject on 2025-10-25 repeats MRU";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid creation command***!!"));
  }

  @Test
  public void testCreateInvalidCommandThree() {
    String input = "create event eventSubject from 2025-06-26T23:45 "
        + "to 2025-06-26T23:59 repeats MRU";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid creation command***!!"));
  }

  @Test
  public void testCreateInvalidCommandFour() {
    String input = "create event eventSubject from 2025-06-26T23:45 "
        + "to 2025-06-26T23:59 repeats MRU fro 30 times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid repetition command: fro***!!"));
  }

  @Test
  public void testCreateInvalidCommandFive() {
    String input = "create event eventSubject from 2025-06-26T23:45 "
        + "to 2025-06-26T23:59 repeats MRU fro 30 times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid repetition command: fro***!!"));
  }

  @Test
  public void testMultiDaysSingleEvent() {
    String input = "create event eventSubject from 2025-06-26T23:45 "
        + "to 2025-06-30T22:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    List<LocalDateTime> dates = new ArrayList<>();
    LocalDateTime start = LocalDateTime.of(2025, 6, 26, 23, 45);
    LocalDateTime end = LocalDateTime.of(2025, 6, 30, 23, 59);
    while (end.isAfter(start)) {
      dates.add(start);
      start = start.plusDays(1);
    }
    assertEquals(5, dates.size());
    List<EventReadOnly> addedEvents = calendar.getEvents(
        LocalDateTime.of(2025, 6, 26, 0, 0),
        dates.get(dates.size() - 1));
    assertEquals(5, addedEvents.size());
  }

  @Test
  public void testMultiDaysSingleEventTwo() {
    String input = "create event eventSubject from 2025-06-26T23:45 "
        + "to 2025-06-30T22:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("Created Event:"));
    assertTrue(output.toString().contains("Start: 2025-06-26T23:45, End: 2025-06-26T23:59"));
    assertTrue(output.toString().contains("Start: 2025-06-27T00:00, End: 2025-06-27T23:59"));
    assertTrue(output.toString().contains("Start: 2025-06-28T00:00, End: 2025-06-28T23:59"));
    assertTrue(output.toString().contains("Start: 2025-06-28T00:00, End: 2025-06-28T23:59"));
    assertTrue(output.toString().contains("Start: 2025-06-30T00:00, End: 2025-06-30T22:59"));
  }

  @Test
  public void testSingleConflictMultiple() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59"
        + System.lineSeparator()
        + "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59 repeats MRU for 5 times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("!!***Cannot create series "
        + "— conflict with "
        + "existing event***!!"));
  }

  @Test
  public void testSingleConflictMultipleUntil() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59"
        + System.lineSeparator()
        + "create event Meeting from 2025-06-26T23:45 to "
        + "2025-06-26T23:59 repeats MRU until 2025-06-30";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("!!***Cannot create series — "
        + "conflict with " + "existing event***!!"));
  }

  @Test
  public void testSingleConflictSingle() {
    String input = "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59"
        + System.lineSeparator()
        + "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("!!***Event already exists***!!"));
  }

  @Test
  public void testInvalidDateFormat() {
    String input = "create event Meeting from 2025-06-26 to "
        + "2025-06-26T23:59";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("!!***Invalid date format***!!"));
  }

  @Test
  public void testInvalidNumberInRepeats() {
    String input = "create event Meeting from 2025-06-26T23:45 to "
        + "2025-06-26T23:59 "
        + "repeats MRU for N times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
    assertTrue(output.toString().contains("!!***Invalid value for N***!!"));
  }

  @Test
  public void testInvalidDayInDayString() {
    String input = "create event Meeting from 2025-06-26T23:45 to "
        + "2025-06-26T23:59 "
        + "repeats ghj for 8 times";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    assertTrue(output.toString().contains("!!***Invalid day of week: g***!!"));
  }

  @Test
  public void testEditSingle() {
    String input = "create event envet1 from 2025-11-12T10:00 to 2025-11-12T11:00"
        + System.lineSeparator()
        + "edit event description event1 from 2025-11-12T10:00 to 2025-11-12T11:00 "
        + "with Description";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
  }

  @Test
  public void testEditSingleTwo() {
    String input = "create event First from 2025-05-05T10:00 to 2025-05-05T11:00 "
        + "repeats MW for 6 times"
        + System.lineSeparator()
        + "edit series start First from 2025-05-05T10:00 with 2025-05-05T11:00";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
  }

  @Test
  public void testEditSingleThree() {
    String input = "create event Meet from 2025-10-25T08:00 to 2025-10-25T17:00 "
        + "repeats MRU for 10 times"
        + System.lineSeparator()
        + "edit event start Meet from 2025-10-26T08:00 to 2025-10-26T17:00 with 2025-10-26T10:00";
    StringReader reader = new StringReader(input);
    CalendarControllerImpl controller = new CalendarControllerImpl(calendar, reader, cliView);
    controller.run();
    System.out.println(output.toString());
  }


  @Test
  public void testValidThroughFile() throws IOException {

    File inputFile = File.createTempFile("commands", ".txt");
    try (Writer fileWriter = new FileWriter(inputFile)) {
      String commands = String.join(System.lineSeparator(),
          "create calendar --name cal --timezone Asia/Kolkata",
          "edit calendar --name cal --property name cal1",
          "edit calendar --name cal1 --property timezone America/New_York",
          "use calendar --name cal1",
          "create event Testing from 2025-11-12T05:58 to 2025-11-12T09:00",
          "create event Submission from 2025-11-12T09:00 to 2025-11-12T21:00 repeats W "
              + "for 30 times",
          "create event Assignment from 2025-09-03T03:00 to 2025-09-03T06:00 repeats WF "
              + "until 2025-12-16",
          "create event Lab on 2025-11-17",
          "create event Labs on 2025-09-03 repeats M for 11 times",
          "create event Work on 2025-11-13 repeats MTWRFSU until 2025-12-31",
          "edit event subject Work from 2025-11-13T08:00 to 2025-11-13T17:00 with WorkDone",
          "print events on 2025-11-13",
          "print events from 2024-01-01T00:00 to 2025-12-31T00:00",
          "export cal exportedEvents.csv",
          "show status on 2025-11-13T08:00",
          "export cal exportToIcal.ical",
          "edit calendar --name cal1 --property timezone Europe/Paris",
          "print events from 2024-01-01T00:00 to 2025-12-31T00:00",
          "create calendar --name cal2 --timezone Asia/Kolkata",
          "edit calendar --name cal1 --property timezone America/New_York",
          "copy event Testing on 2025-11-12T05:58 --target cal2 to 2025-11-12T08:58",
          "copy events on 2025-12-24 --target cal2 to 2026-11-24",
          "copy events between 2024-01-01 and 2026-12-31 --target cal2 to 2024-01-01"
      );
      fileWriter.write(commands);
    }


    File outputFile = File.createTempFile("outputValidCommands", ".txt");


    try (Reader fileReader = new FileReader(inputFile);
         Writer fileWriter = new FileWriter(outputFile)) {

      CalendarView calendarView = new CalendarViewImpl(fileWriter);
      CalendarContainer container = new CalendarContainerImpl();
      CalendarController controller =
          new AdvanceCalendarController(container, fileReader, calendarView);


      controller.run();
    }


    String output = new String(Files.readAllBytes(outputFile.toPath()));


    assertTrue(output.contains("Welcome to the calendar!"));
    assertTrue(output.contains("Calendar with name cal has been successfully created"));
    assertTrue(output.contains("Calendar timezone edited successfully"));
    assertTrue(output.contains("Created Event: "));
    assertTrue(output.contains("Created a Series Event:"));
    assertTrue(output.contains("Event updated:"));
    assertTrue(output.contains("> Subject: WorkDone"));
    assertTrue(output.contains("Successfully exported to"));
    assertTrue(output.contains("User is Busy"));
    assertTrue(output.contains("Calendar timezone edited successfully"));
    assertTrue(output.contains("Calendar with name cal2 has been successfully created"));
    assertTrue(output.contains("Calendar timezone edited successfully"));
    assertTrue(output.contains("Copied Events:"));
    assertTrue(output.contains("WARNING: Series event in target calendar "
        + "is spreading across multiple days"));


    inputFile.delete();
    outputFile.delete();
  }



  @Test
  public void testInvalidThroughFile() throws IOException {

    File inputFile = File.createTempFile("invalid", ".txt");
    try (Writer fileWriter = new FileWriter(inputFile)) {
      String commands = String.join(System.lineSeparator(),
          "create event event1",
          "create event \"Multiple Events\" from 2025-06-26T10:00 to 2025-06-26T11:00 repeats"
             + " MRU for 0 times",
          "create event eventSubject from 2025-06-26T23:45 to 2025-06-26T23:59 repeats MRU for"
              + " N times",
          "create event Meeting from 2025-06-26T23:45 to 2025-06-26T23:59 repeats ghj for 8 times",
          "create event eventSubject from 2025-06-26T23:45 to 2025-06-26T23:59 repeats MRU "
              + "fro 30 times",
          "create",
          "cret",
          "creat evnt",
          "create event eventSubject on 2025-10-25 repeats MRU",
          "export cal nofile",
          "show status from 2025-06-26T23:54",
          "show status on",
          "print events on",
          "print events from 2025-06-26T00:00 too 2025-06-30T00:00",
          "print events from 2025-06-26T00:00 to",
          "print events from",
          "edit event sub firstEvent from 2025-10-25T08:00 to 2025-10-25T17:00 with"
              + " \"first Event New Subject\"",
          "edit event start firstEvent from 2025-10-25T08:00 to 2025-10-25T17:00 with "
              + "\"first Event New Subject\"",
          "edit event end firstEvent from 2025-10-25T08:00 to 2025-10-25T17:00 with \"first"
              + " Event New Subject\"",
          "edit event start firstEvent from 2025-10-25T08:00 to 2025-10-25T17:00 with"
              + " 2025-10-25T18:00",
          "edit event subject first",
          "edit series subject first",
          "edit events subject first",
          "edit series subject Meet from 2025-10-26T08:00 abc \"Updated Meet\"",
          "edit series subject Meet abc 2025-10-26T08:00 with \"Updated Meet\"",
          "edit events subject Meet from 2025-10-26T08:00 abc \"Updated Meet\"",
          "edit events subject Meet abc 2025-10-26T08:00 with \"Updated Meet\"",
          "edit series START Meet from 2025-10-26T08:00 abc \"Updated Meet\"",
          "create calendar --name",
          "create cal",
          "edit cal",
          "random check",
          "edit calendar --name cal1 --property names calendar",
          "use --name <name-of-calendar>",
          "copy event event1",
          "copy events on 20252310 --target calendarName to",
          "copy events between dateString and dateString --target calendarName to dateString"
      );
      fileWriter.write(commands);
    }


    File outputFile = File.createTempFile("outputInvalidCommands", ".txt");


    try (Reader fileReader = new FileReader(inputFile);
         Writer fileWriter = new FileWriter(outputFile)) {

      CalendarView calendarView = new CalendarViewImpl(fileWriter);
      CalendarContainer container = new CalendarContainerImpl();
      CalendarController controller =
          new AdvanceCalendarController(container, fileReader, calendarView);


      controller.run();
    }


    String output = new String(Files.readAllBytes(outputFile.toPath()));


    assertTrue(output.contains("Welcome to the calendar!"));
    assertTrue(output.contains("!!***Invalid creation command***!!"));
    assertTrue(output.contains("!!***Invalid command.***!!"));
    assertTrue(output.contains("!!***Invalid value for N***!!"));
    assertTrue(output.contains("!!***Invalid day of week: g***!!"));
    assertTrue(output.contains("!!***Invalid repetition command: fro***!!"));
    assertTrue(output.contains("!!***Invalid show status command***!!"));
    assertTrue(output.contains("!!***Event with given details doesn't exist***!!"));
    assertTrue(output.contains("!!***Invalid command format***!!"));
    assertTrue(output.contains("!!***Invalid command syntax***!!"));
    assertTrue(output.contains("!!***Invalid create calendar command. Usage: create calendar "
        + "--name <calName> --timezone area/location***!!"));
    assertTrue(output.contains("!!***Invalid command: create cal***!!"));
    assertTrue(output.contains("!!***Invalid command: random check***!!"));
    assertTrue(output.contains("!!***Calendar with name cal1 does not exist***!!"));
    assertTrue(output.contains("!!***Invalid command: use --name <name-of-calendar>***!!"));
    assertTrue(output.contains("!!***Wrong command format. Usage: copy event <eventName> on "
        + "<dateTime> --target <calendarName> to <dateTime>***!!"));
    assertTrue(output.contains("!!***Invalid copy events command."));
    assertTrue(output.contains("Usage: copy events on <dateString> "
        + "--target <calendarName> to <dateString>"));
    assertTrue(output.contains("or copy events between <dateString> and <dateString>"));
    assertTrue(output.contains("--target <calendarName> to <dateString>***!!"));


    inputFile.delete();
    outputFile.delete();
  }
}
