import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.controller.CalendarControllerImpl;
import calendar.controller.CalendarFilter;
import calendar.model.CalendarImpl;
import calendar.model.datatypes.EventStatus;
import calendar.model.datatypes.Location;
import calendar.model.datatypes.TypeOfEvent;
import calendar.model.interfaces.CalendarEditable;
import calendar.model.interfaces.EventReadOnly;
import calendar.view.CalendarViewImpl;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing the all the types of Valid and Invalid edit commands.
 * This tests the EditEventCommand, EditMultipleCommand and EditMultipleEventsCommand.
 * List of all the tested edit commands are mentioned in res/commands.txt.
 */
public class CalendarEditEventsTest {

  CalendarEditable calendar;
  Appendable output;
  CalendarControllerImpl calendarController;
  CalendarViewImpl view;
  StringReader inputReader;
  String createSingle;
  String createSeries;

  /**
   * Initializes all the required objects before every test to test edit command.
   * It initializes certain common commands needed for most of the tests.
   */
  @Before
  public void setUp() {
    createSingle = "create event firstEvent from 2025-10-25T08:00 to 2025-10-25T17:00"
        + System.lineSeparator();

    createSeries = "create event Meet from 2025-10-25T08:00 to 2025-10-25T17:00"
        + " repeats MRU for 10 times"
        + System.lineSeparator();

    inputReader = new StringReader(createSingle);
    calendar = new CalendarImpl();
    output = new StringBuilder();
    view = new CalendarViewImpl(output);

  }


  @Test
  public void testCalendarEditEvents1Subject() {

    String edit = createSingle
        +
        "edit event subject firstEvent from 2025-10-25T08:00"
        + " to 2025-10-25T17:00 with \"first Event New Subject\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();


    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 10, 25, 8, 0),
        LocalDateTime.of(2025, 10, 25, 8, 0));


    assertEquals(1, list.size());
    assertEquals("first Event New Subject", list.get(0).getSubject());
  }


  @Test
  public void testCalendarEditEventInvalidProperty() {

    String edit = createSingle
        +
        "edit event sub firstEvent from 2025-10-25T08:00 to "
        + "2025-10-25T17:00 with \"first Event New Subject\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();
    //Invalid property value to edit:
    assertTrue(output.toString().contains("Invalid property"));

  }

  @Test
  public void testCalendarEditEventInvalidNewValue() {

    String edit = createSingle
        +
        "edit event start firstEvent from 2025-10-25T08:00 to "
        + "2025-10-25T17:00 with \"first Event New Subject\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();
    assertTrue(output.toString().contains("Invalid date-time format:"));


  }

  @Test
  public void testCalendarEditEventInvalidNewValueEndDate() {

    String edit = createSingle
        +
        "edit event end firstEvent from 2025-10-25T08:00 to "
        + "2025-10-25T17:00 with \"first Event New Subject\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();
    assertTrue(output.toString().contains("Invalid date-time format:"));

  }


  @Test
  public void testCalendarEditEvents1Start() {

    String edit = createSingle
        +
        "edit event start firstEvent from 2025-10-25T08:00 to "
        + "2025-10-25T17:00 with 2025-10-25T08:15"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();


    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 9, 1, 8, 0),
        LocalDateTime.of(2025, 11, 25, 8, 0));


    assertEquals(list.get(0).getStartDateTime(),
        LocalDateTime.of(2025, 10, 25, 8, 15));
  }

  @Test
  public void testCalendarEditEvents1StartAfterEnd() {

    String edit = createSingle
        +
        "edit event start firstEvent from 2025-10-25T08:00 to "
        + "2025-10-25T17:00 with 2025-10-25T18:00"
        + System.lineSeparator();

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();


    assertTrue(output.toString().contains("Created Event: "));
    assertTrue(output.toString().contains("Subject: firstEvent, Start: 2025-10-25T08:00"));
    assertTrue(output.toString().contains("endDateTime cannot be before startDateTime"));
  }

  @Test
  public void testCalendarEditEvents1End() {

    String edit = createSingle
        +
        "edit event end firstEvent from 2025-10-25T08:00 to 2025-10-25T17:00 with 2025-10-25T12:15"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();


    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 9, 1, 8, 0),
        LocalDateTime.of(2025, 11, 25, 8, 0));


    assertFalse(list.get(0).isAllDay());

    assertEquals(list.get(0).getEndDateTime(),
        LocalDateTime.of(2025, 10, 25, 12, 15));
    System.out.println(output.toString());
    assertTrue(output.toString().contains("Event updated:"));
  }

  @Test
  public void testCalendarEditEvents1Location() {

    String edit = createSingle
        + "edit event location firstEvent from 2025-10-25T08:00 to 2025-10-25T17:00 with physical"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();


    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 9, 1, 8, 0),
        LocalDateTime.of(2025, 11, 25, 8, 0));


    assertEquals(Location.PHYSICAL, list.get(0).getLocation());

  }

  @Test
  public void testCalendarEditEvents1LocationUnknown() {

    String edit = createSingle
        + "edit event location firstEvent from 2025-10-25T08:00 to 2025-10-25T17:00 with phy"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();


    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 9, 1, 8, 0),
        LocalDateTime.of(2025, 11, 25, 8, 0));


    assertEquals(Location.UNKNOWN, list.get(0).getLocation());

  }

  @Test
  public void testCalendarEditEvents1Description() {

    String edit = createSingle
        +
        "edit event description firstEvent from 2025-10-25T08:00 "
        + "to 2025-10-25T17:00 with \"Editing event description\" "
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();


    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 9, 1, 8, 0),
        LocalDateTime.of(2025, 11, 25, 8, 0));
    System.out.println(output.toString());


    assertEquals("Editing event description", list.get(0).getDescription());

  }

  @Test
  public void testCalendarEditEvents1EventStatus() {

    String edit = createSingle
        + "edit event status firstEvent from 2025-10-25T08:00 to 2025-10-25T17:00 with Public"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 9, 1, 8, 0),
        LocalDateTime.of(2025, 11, 25, 8, 0));

    assertEquals(EventStatus.PUBLIC, list.get(0).getEventStatus());
  }

  @Test
  public void testCalendarEditEventsEventId() {

    String edit = createSingle
        + "edit event ID firstEvent from 2025-10-25T08:00 to "
        + "2025-10-25T17:00 with f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 9, 1, 8, 0),
        LocalDateTime.of(2025, 11, 25, 8, 0));

    assertEquals("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454", list.get(0).getId().toString());

  }

  @Test
  public void testCalendarEditEvents1EventSubjectInvalid() {

    String edit = createSingle
        +
        "edit event ID firstEven from 2025-11-25T08:00 to "
        + "2025-10-25T17:00 with f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("!!***Event with given details doesn't exist***!!"));

  }

  @Test
  public void testCalendarEditEventsSeriesSubject() {


    String edit = createSeries
        + "edit event start Meet from 2025-10-30T08:00 to 2025-10-30T17:00 with 2025-10-30T13:00"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 10, 24, 8, 0),
        LocalDateTime.of(2026, 2, 1, 8, 0));

    for (EventReadOnly event : list) {
      if (Objects.equals(event.getStartDateTime(),
          LocalDateTime.of(2025, 10, 30, 13, 0))) {
        assertEquals(TypeOfEvent.SINGLE, event.getEventType());
      }
    }

  }

  @Test
  public void testCalendarEditEventsMultipleEventsSeries() {


    String edit = createSeries
        + "edit events subject Meet from 2025-10-26T08:00 with \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 1, 24, 8, 0),
        LocalDateTime.of(2026, 2, 1, 8, 0));

    for (EventReadOnly eventReadOnly : list) {
      assertEquals("Updated Meet", eventReadOnly.getSubject());
    }

  }

  @Test
  public void testCalendarEditEventsMultipleEvents() {


    String edit = createSeries
        + "create event Meet from 2025-10-26T08:00 to 2025-10-26T18:00"
        + System.lineSeparator()
        + "edit events subject Meet from 2025-10-26T08:00 with \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 1, 24, 8, 0),
        LocalDateTime.of(2026, 2, 1, 8, 0));

    for (EventReadOnly eventReadOnly : list) {
      assertEquals("Updated Meet", eventReadOnly.getSubject());
    }

  }


  @Test
  public void testCalendarEditEventsMultipleEventsSeriesBeforeStart() {


    String edit = createSeries
        + "edit events subject Meet from 2025-10-27T08:00 with \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 1, 24, 8, 0),
        LocalDateTime.of(2026, 2, 1, 8, 0));

    for (EventReadOnly event : list) {
      if (event.getStartDateTime()
          .isAfter(LocalDateTime.of(2025, 10, 26, 9, 0))) {
        assertEquals("Updated Meet", event.getSubject());
      }
    }
    assertTrue(output.toString().contains("Event updated:"));

  }


  @Test
  public void testCalendarEditEventsSeriesMultipleEventsSeries() {


    String edit = createSeries
        + "edit series subject Meet from 2025-10-26T08:00 with \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    List<EventReadOnly> list = calendar.getEvents(
        LocalDateTime.of(2025, 1, 24, 8, 0),
        LocalDateTime.of(2026, 2, 1, 8, 0));

    for (EventReadOnly eventReadOnly : list) {
      assertEquals("Updated Meet", eventReadOnly.getSubject());
    }

  }

  @Test
  public void testCalendarEditEventsSeries() {


    String edit = createSeries
        + "edit series subject Meet from 2025-10-26T08:00 with \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("Event updated:"));

  }

  @Test
  public void testCalendarEditEventsPrint() {


    String edit = createSeries
        + "print events on 2020-10-26"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("!!***No Events found with the given details***!!"));

  }


  @Test
  public void testCalendarEditEventInvalidNewValueInvalid1() {

    String edit = createSingle
        +
        "edit event subject first"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();
    assertTrue(output.toString().contains("Invalid command format"));

  }

  @Test
  public void testCalendarEditEventInvalidNewValueInvalidSeries() {

    String edit = createSingle
        +
        "edit series subject first"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();
    assertTrue(output.toString().contains("Invalid command format"));

  }


  @Test
  public void testCalendarEditEventInvalidNewValueInvalidEvents() {

    String edit = createSingle
        +
        "edit events subject first"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();
    assertTrue(output.toString().contains("Invalid command format"));

  }

  @Test
  public void testCalendarEditEventsSeriesInvalid() {


    String edit = createSeries
        + "edit series subject Meet from 2025-10-26T08:00 abc \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("Invalid command syntax"));

  }

  @Test
  public void testCalendarEditEventsSeriesInvalidIndex4() {


    String edit = createSeries
        + "edit series subject Meet abc 2025-10-26T08:00 with \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("Invalid command syntax"));

  }

  @Test
  public void testCalendarEditEventsEventsInvalid6() {


    String edit = createSeries
        + "edit events subject Meet from 2025-10-26T08:00 abc \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("Invalid command syntax"));

  }

  @Test
  public void testCalendarEditEventsInvalidIndex4() {


    String edit = createSeries
        + "edit events subject Meet abc 2025-10-26T08:00 with \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("Invalid command syntax"));

  }


  @Test
  public void testCalendarEditEventsSeriesFilter() {


    String edit = "create event Meet from 2025-10-25T08:00 to 2025-10-25T16:00"
        + " repeats MRU for 10 times"
        + System.lineSeparator()
        + "edit series subject Meet from 2025-10-26T08:00 abc \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    CalendarFilter filter = new CalendarFilter(calendar);

    List<EventReadOnly> list = filter.filter(EventReadOnly::isAllDay);

    for (EventReadOnly event : list) {
      assertFalse(event.isAllDay());
    }

  }

  @Test
  public void testCalendarEditEventsSeriesFilterTest() {


    String edit = "CREATE event Meet from 2025-10-25T08:00 to 2025-10-25T16:00"
        + " repeats MRU for 10 times"
        + System.lineSeparator()
        + "edit series START Meet from 2025-10-26T08:00 abc \"Updated Meet\""
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    CalendarFilter filter = new CalendarFilter(calendar);

    List<EventReadOnly> list = filter.filter(EventReadOnly::isAllDay);

    for (EventReadOnly event : list) {
      assertFalse(event.isAllDay());
    }

  }

  @Test
  public void testCalendarEditEventInvalidIndex4() {


    String edit = createSeries
        + "edit event subject firstEvent for 2025-10-25T08:00 "
        + "to 2025-10-25T17:00 with NewSubject"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("Invalid command syntax"));

  }


  @Test
  public void testCalendarEditEventInvalidIndex6() {


    String edit = createSeries
        + "edit event subject firstEvent from 2025-10-25T08:00 "
        + "ot 2025-10-25T17:00 with NewSubject"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("Invalid command syntax"));

  }

  @Test
  public void testCalendarEditEventInvalidIndex8() {


    String edit = createSeries
        + "edit event subject firstEvent from 2025-10-25T08:00 "
        + "to 2025-10-25T17:00 wth NewSubject"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("Invalid command syntax"));

  }

  @Test
  public void testCalendarPrintEvents() {


    String edit = createSeries
        + "print events from 2025-06-26T00:00 to 2025-05-30T00:00"
        + System.lineSeparator()
        + "exit";

    StringReader input = new StringReader(edit);

    calendarController = new CalendarControllerImpl(calendar, input, view);
    calendarController.run();

    assertTrue(output.toString().contains("End date cannot be before start date"));

  }


}




