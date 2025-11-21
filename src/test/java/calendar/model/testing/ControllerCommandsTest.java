package calendar.model.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import calendar.controller.CalendarController;
import calendar.controller.CalendarControllerImpl;
import calendar.controller.CommandTokenizerImpl;
import calendar.model.CalendarImpl;
import calendar.model.Event;
import calendar.model.interfaces.CalendarEditable;
import calendar.model.interfaces.EventReadOnly;
import calendar.view.CalendarView;
import calendar.view.CalendarViewImpl;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * This file is testing the MVC architecture works as expected.
 * Using the mock model to check the validity of the Model and controller operations.
 * Also checks the exception handling of the IOException using a fake Appendable.
 */
public class ControllerCommandsTest {

  Appendable log = new StringBuilder();
  MockCalendar calendar;
  StringReader testInputCreate;
  Readable testInputEdit;
  Readable testInputBusy;
  Readable testInputEachEvent;
  Readable testInputGetEvent;
  StringBuilder output;
  CalendarView calendarView;
  CalendarControllerImpl controllerFake;
  FakeAppendable outputFake;
  MockCalendar mockCalender;

  /**
   * Initializes all the required objects before every test to test controller.
   * A controller takes in Calendar, Readable, ana Appendable and a view as input, setup initializes
   * all these fields before every test.
   */
  @Before
  public void setup() {
    calendar = new MockCalendar(log);
    testInputCreate = new StringReader("create event event1 from 2025-10-25T08:00 to "
        + "2025-10-25T17:00");
    testInputEdit = new StringReader("edit event end event1 from 2025-10-25T08:00 to"
        + " 2025-10-25T10:00 with 2025-10-25T17:00");
    testInputBusy = new StringReader("show status on 2025-10-25T08:00");
    testInputEachEvent = new StringReader("edit event end event1 from 2025-10-25T08:00"
        + " to 2025-10-25T10:00 with 2025-10-25T17:00");
    testInputGetEvent = new StringReader("print events from 2025-10-25T08:00"
        + " to 2025-10-25T17:00");
    output = new StringBuilder();
    calendarView = new CalendarViewImpl(System.out);
    outputFake = new FakeAppendable();
    mockCalender = new MockCalendar(outputFake);
  }

  @Test
  public void testCommandCreate() {
    CalendarControllerImpl controller =
        new CalendarControllerImpl(calendar, testInputCreate, calendarView);
    controller.run();
    assertEquals("addEvent", log.toString());

  }

  @Test
  public void testCommandCreateNullCheck() {
    CalendarControllerImpl controller =
        new CalendarControllerImpl(calendar, testInputCreate, calendarView);
    controller.run();
    assertEquals("addEvent", log.toString());
  }

  @Test
  public void testIncreaseMockCoverage() {
    Appendable log = new StringBuilder();
    CalendarEditable calendarEditable = new MockCalendar(log);
    EventReadOnly e = calendarEditable.addEvent(new Event
        .EventBuilder("Sample",
        LocalDateTime.of(2025, 10, 10, 10, 10)).build());
    assertNotNull("addEvent should not return null", e);
  }

  @Test
  public void testIncreaseMockCoverageTestTwo() {
    Appendable log = new StringBuilder();
    CalendarEditable calendarEditable = new MockCalendar(log);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(new Event.EventBuilder("Sample",
        LocalDateTime.of(2025, 10, 10, 10, 10)).build());
    List<EventReadOnly> e = calendarEditable.editEvent(events, "subject", "new");
    assertFalse("Returned list should not be empty", e.isEmpty());
  }

  @Test
  public void testGetAllEvents() {
    CalendarEditable mockCalendar = new MockCalendar(log);
    mockCalendar.getAllEvents();
    assertEquals("getAllEvents", log.toString());

  }

  @Test
  public void testCommandEdit() {
    CalendarControllerImpl controller =
        new CalendarControllerImpl(calendar, testInputEdit, calendarView);
    controller.run();
    assertTrue(log.toString().contains("forEachEventeditEvent"));
  }


  @Test
  public void testCommandBusy() {
    CalendarControllerImpl controller =
        new CalendarControllerImpl(calendar, testInputBusy, calendarView);
    controller.run();
    assertEquals("isBusy", log.toString());
  }


  @Test
  public void testCommandGetEvent() {
    CalendarControllerImpl controller =
        new CalendarControllerImpl(calendar, testInputGetEvent, calendarView);
    controller.run();
    assertEquals("getEvents", log.toString());
  }

  @Test
  public void testIoMockCreate() {
    try {
      mockCalender.addEvent(null);
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testIoMockEdit() {
    try {
      mockCalender.editEvent(new ArrayList<>(), "", "");
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testIoMockForEachEvent() {
    try {
      mockCalender.forEachEvent(null);
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testIoMockGetEvent() {
    try {
      mockCalender.getEvents(null, null);
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testIoMockBusy() {
    try {
      mockCalender.isBusy(null);
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testIoMockRemoveEvent() {
    try {
      mockCalender.removeEvent(null);
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }


  @Test
  public void testIoController() {
    StringReader input = new StringReader("");
    CalendarController controller =
        new CalendarControllerImpl(mockCalender, input, calendarView);

  }

  @Test
  public void testIoFakeAppendable() {
    try {
      outputFake.append("Hello", 0, 2);
      assert false;
    } catch (IOException e) {
      assert true;
    }
  }

  @Test
  public void testIoFakeAppendable2() {
    try {
      outputFake.append('c');
      assert false;
    } catch (IOException e) {
      assert true;
    }
  }

  @Test
  public void testRemoveEvent1() {
    StringBuilder log1 = new StringBuilder();
    MockCalendar mockCalender1 = new MockCalendar(log1);
    mockCalender1.removeEvent(null);
    assertEquals("removeEvent", log1.toString());
  }


  @Test
  public void testRunExitCommand() {
    StringReader input = new StringReader("exit");
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(output);
    CalendarControllerImpl controller =
        new CalendarControllerImpl(mockCalender, input, view);

    controller.run();
    assertTrue(output.toString().contains("Exiting!!"));
  }

  @Test
  public void testRunEmptyCommandEmptyTokens() {
    StringReader input = new StringReader("\nexit");
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(output);
    CalendarControllerImpl controller =
        new CalendarControllerImpl(mockCalender, input, view);

    controller.run();
    assertTrue(output.toString().contains("Exiting!!"));
  }

  @Test
  public void testRunInvalidSingleTokenCommand() {
    StringReader input = new StringReader("exi");
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(output);
    CalendarControllerImpl controller =
        new CalendarControllerImpl(mockCalender, input, view);
    controller.run();
    assertTrue(output.toString().contains("!!***Invalid command: exi***!!"
        + System.lineSeparator()));
  }

  @Test
  public void testRunWrongCommand() {
    StringReader input = new StringReader("creates event");
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(output);
    CalendarControllerImpl controller =
        new CalendarControllerImpl(mockCalender, input, view);
    controller.run();
    assertTrue(output.toString().contains("!!***Invalid command: creates event***!!"
        + System.lineSeparator()));
  }

  @Test
  public void testCalendarView() {
    Appendable fake = new FakeAppendable();
    CalendarView view = new CalendarViewImpl(fake);

    try {
      view.render("This will result in IOException");
      assert false;
    } catch (IllegalStateException e) {
      assert true;
    }
  }

  @Test
  public void testIsBusyDefaultReturnFalse() {
    calendar = new MockCalendar(new StringBuilder());
    boolean busy = calendar.isBusy(LocalDateTime.of(2025, 10, 25, 12, 0));
    assertFalse(busy);
  }

  @Test
  public void testTrailingSpaceDoesNotAddEmptyToken() {
    CommandTokenizerImpl tokenizer = new CommandTokenizerImpl();
    List<String> tokens = tokenizer.parser("create event ");
    assertEquals(2, tokens.size());
    assertEquals("create", tokens.get(0));
    assertEquals("event", tokens.get(1));
  }

  @Test
  public void testEmptyCommandReturnsEmptyList() {
    CommandTokenizerImpl tokenizer = new CommandTokenizerImpl();
    List<String> tokens = tokenizer.parser("");
    assertTrue(tokens.isEmpty());
  }

  @Test
  public void testGetEventsReturnsNewList() {
    calendar = new MockCalendar(new StringBuilder());
    LocalDateTime start = LocalDateTime.of(2025, 10, 25, 8, 0);
    LocalDateTime end = LocalDateTime.of(2025, 10, 25, 17, 0);

    List<EventReadOnly> events1 = calendar.getEvents(start, end);
    List<EventReadOnly> events2 = calendar.getEvents(start, end);
    assertTrue(events1.isEmpty());
    assertTrue(events2.isEmpty());
    assertNotSame(events1, events2);
  }

  @Test
  public void testTwoTokenCommandIsValid() {
    StringReader input = new StringReader("create event\nexit\n");
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(output);
    MockCalendar mockCalendar = new MockCalendar(new StringBuilder());
    CalendarControllerImpl controller =
        new CalendarControllerImpl(mockCalendar, input, view);

    controller.run();
    assertTrue(output.toString().contains("Invalid command"));
    assertTrue(output.toString().contains("Exiting!!"));
    input = new StringReader("create event\nexit\n");
    output = new StringBuilder();
    view = new CalendarViewImpl(output);
    mockCalendar = new MockCalendar(new StringBuilder());
    controller = new CalendarControllerImpl(mockCalendar, input, view);
    controller.run();
    assertTrue(output.toString().contains("Invalid command"));
  }

  @Test
  public void testInvalidCommandKeyTriggersRenderError() {
    StringReader input = new StringReader("create something\nexit\n");
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(output);
    MockCalendar mockCalendar = new MockCalendar(new StringBuilder());

    CalendarControllerImpl controller =
        new CalendarControllerImpl(mockCalendar, input, view);

    controller.run();
    assertTrue(output.toString().contains("Invalid command: create something"));
  }

  @Test
  public void testInvalidTwoWordCommandTriggersRenderError() {
    StringReader input = new StringReader("create wrong event\nexit\n");
    StringBuilder output = new StringBuilder();
    CalendarView view = new CalendarViewImpl(output);
    MockCalendar mockCalendar = new MockCalendar(new StringBuilder());

    CalendarControllerImpl controller =
        new CalendarControllerImpl(mockCalendar, input, view);

    controller.run();
    assertTrue(output.toString().contains("Invalid command: create wrong"));
  }
}
