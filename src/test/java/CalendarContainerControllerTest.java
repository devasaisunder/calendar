import static org.junit.Assert.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import calendar.controller.AdvanceCalendarController;
import calendar.controller.CalendarController;
import calendar.model.AdvancedCalendarImpl;
import calendar.model.CalendarContainerImpl;
import calendar.model.interfaces.AdvancedCalendar;
import calendar.model.interfaces.CalendarContainer;
import calendar.model.interfaces.CalendarEditable;
import calendar.model.interfaces.EventReadOnly;
import calendar.view.CalendarView;
import calendar.view.CalendarViewImpl;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.junit.Test;

/**
 * Tests for the AdvanceCalendar commands through AdvanceCalendarController class.
 * This test verifies.
 * Creation of calendars with valid and invalid parameters.
 * Handling of duplicate calendar names.
 * use calendar command.
 * Editing calendar properties such as name and timezone.
 * Validation of invalid commands and incorrect arguments.
 * Proper events time updates after timezone changes.
 */
public class CalendarContainerControllerTest {

  CalendarContainer container = new CalendarContainerImpl();
  Appendable out = new StringBuilder();
  CalendarView view = new CalendarViewImpl(out);
  Readable inputStream;


  @Test
  public void testCalendarManagerCreate() {
    String in = "create calendar --name Work --timezone America/New_York";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    assertTrue(out.toString().contains("Calendar with name Work has been successfully created"));
    assertEquals(2, container.getCalendars().size());
    assertTrue(container.getCalendars().containsKey("Work"));
    assertEquals("Work", container.getCalendars().get("Work").getName());
    assertEquals("America/New_York", container.getCalendars().get("Work").getZoneId().toString());
  }

  @Test
  public void testCalendarManagerCreateDuplicate() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "create calendar --name Work --timezone America/New_York";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    assertTrue(out.toString().contains("Calendar with name Work has been successfully created"));
    assertTrue(out.toString().contains("Calendar with name Work already exists"));

  }

  @Test
  public void testCalendarManagerCreateInvalidTimeZone() {
    String in = "create calendar --name Work --timezone EST";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);


    controller.run();
    assertTrue(out.toString().contains("Invalid timezone"));
  }

  @Test
  public void testCalendarManagerCreateInvalidCreate1() {
    String in = "create calendar --name Work --timezone America/New_York new command";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();
    assertTrue(out.toString().contains("Invalid create calendar command"));
  }

  @Test
  public void testCalendarManagerCreateInvalidCreate2() {
    String in = "create calendar --n Work --timezone America/New_York";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();
    assertTrue(out.toString().contains("Invalid create calendar command"));
  }

  @Test
  public void testCalendarManagerCreateInvalidCreate3() {
    String in = "create calendar --name Work --time America/New_York";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();
    assertTrue(out.toString().contains("Invalid create calendar command"));
  }

  @Test
  public void testCalendarManagerCreateInvalidCreate4() {
    String in = "create calendar --name Work --time IST";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();
    assertTrue(out.toString().contains("Invalid create calendar command"));
  }

  @Test
  public void testCalendarManagerUseCommandTestWithDefault() {
    String in = "use calendar --name default";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    assertTrue(out.toString().contains("Now you are using calendar default"));
  }

  @Test
  public void testCalendarManagerUseCommandTest() {

    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    System.out.println(out.toString());
    assertTrue(out.toString().contains("Now you are using calendar Work"));
  }

  @Test
  public void testCalendarManagerUseCommandInvalidName() {

    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name newWork";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    assertTrue(out.toString().contains("Calendar with name newWork does not exist"));
  }

  @Test
  public void testCalendarManagerInvalidLengthUseCommand() {

    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name newWork set newWork";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    assertTrue(out.toString().contains("Invalid command."));
  }

  @Test
  public void testCalendarManagerInvalidLengthUseCommand2() {

    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --n newWork set";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    assertTrue(out.toString().contains("Invalid command."));
  }

  @Test
  public void testEditBothSameTimeZone() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "edit calendar --name Work --property timezone America/New_York";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("The specified time zone is "
        + "same as the existing calendar timezone."));
  }

  @Test
  public void testEditName() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "edit calendar --name Work --property name NewCalendar";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("Calendar name edited successfully"));
    assertTrue(container.getCalendars().containsKey("NewCalendar"));
  }

  @Test
  public void testEditTimeZone() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Multiple Events\" from 2025-06-26T10:00 to"
        + " 2025-06-26T11:00 repeats MRU for 5 times"
        + System.lineSeparator()
        + "edit calendar --name Work --property timezone Asia/Kolkata"
        + System.lineSeparator()
        + "print events from 2025-06-26T10:00 to 2025-07-06T23:00";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("Calendar timezone edited successfully"));
    assertEquals("Asia/Kolkata", container.getCalendars().get("Work")
        .getZoneId().toString());

    LocalDateTime startTime = LocalDateTime.of(2025, 6, 26, 19, 30);
    LocalDateTime eventEventTime = LocalDateTime.of(2025, 6, 26, 20, 30);
    LocalDateTime endTime = LocalDateTime.of(2025, 7, 6, 12, 0);

    List<EventReadOnly> list = container.getCalendars().get("Work")
        .getCalendar().getEvents(startTime, endTime);

    List<LocalDateTime> newStartDateTimes =
        ExpectedOutputGenerator.getTimesDates(startTime, "MRU", 5);


    for (EventReadOnly event : list) {
      assertTrue(newStartDateTimes.contains(event.getStartDateTime()));
    }

  }

  @Test
  public void testEditTimeZoneInvalid() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Multiple Events\" from 2025-06-26T10:00 to"
        + " 2025-06-26T11:00 repeats MRU for 5 times"
        + System.lineSeparator()
        + "edit calendar --name Work --property timezone Asia";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("New time zone is Invalid"));
  }

  @Test
  public void testEditTimeZoneDuplicateName() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "create calendar --name School --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Multiple Events\" from 2025-06-26T10:00 to"
        + " 2025-06-26T11:00 repeats MRU for 5 times"
        + System.lineSeparator()
        + "edit calendar --name Work --property name School";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    assertTrue(out.toString().contains("Calendar with the given name already existsSchool"));
  }

  @Test
  public void testEditInvalidProperty() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Multiple Events\" from 2025-06-26T10:00 to"
        + " 2025-06-26T11:00 repeats MRU for 5 times"
        + System.lineSeparator()
        + "edit calendar --name Work --property timezone Asia";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("New time zone is Invalid"));

  }

  @Test
  public void testEditInvalidEditCommand1() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Multiple Events\" from 2025-06-26T10:00 to"
        + " 2025-06-26T11:00 repeats MRU for 5 times"
        + System.lineSeparator()
        + "edit calendar --nam Work --property timezone Asia";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("Invalid edit calendar command"));

  }

  @Test
  public void testEditInvalidEditCommand2() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Multiple Events\" from 2025-06-26T10:00 to"
        + " 2025-06-26T11:00 repeats MRU for 5 times"
        + System.lineSeparator()
        + "edit calendar --name Work --proper timezone Asia";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("Invalid edit calendar command"));

  }

  @Test
  public void testEditInvalidEditCommand3() {
    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Multiple Events\" from 2025-06-26T10:00 to"
        + " 2025-06-26T11:00 repeats MRU for 5 times"
        + System.lineSeparator()
        + "edit calendar --name Work --property timezone Asia India";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("Invalid edit calendar command"));

  }


  @Test
  public void farewellMessageTest() {
    String in = "exit";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("Thank you for using this program!"));
  }

  @Test
  public void testAddCalendar() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    AdvancedCalendarImpl cal1 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Cal1", ZoneId.of("UTC"))
            .build();
    container.addCalendar("Cal1", cal1);
    assertEquals(cal1, container.getCalendars().get("Cal1"));
  }

  @Test
  public void testAddDuplicateCalendarThrows() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    AdvancedCalendarImpl cal1 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Cal1", ZoneId.of("UTC"))
            .build();
    container.addCalendar("Cal1", cal1);

    try {
      container.addCalendar("Cal1", cal1);
      assert false;
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar with name Cal1 already exists", e.getMessage());
    }
  }

  @Test
  public void testUpdateCalendarName() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    AdvancedCalendarImpl cal1 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Cal1", ZoneId.of("UTC"))
            .build();
    container.addCalendar("Cal1", cal1);

    container.updateCalendar("Cal1", "name", "NewCal1");
    assertTrue(container.getCalendars().containsKey("NewCal1"));
    assertFalse(container.getCalendars().containsKey("Cal1"));
  }

  @Test
  public void testUpdateCalendarTimeZone() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    AdvancedCalendarImpl cal1 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Cal1", ZoneId.of("UTC"))
            .build();
    container.addCalendar("Cal1", cal1);

    container.updateCalendar("Cal1", "timezone", "America/New_York");
    AdvancedCalendar updated = container.getCalendars().get("Cal1");
    assertEquals(ZoneId.of("America/New_York"), updated.getZoneId());
  }

  @Test
  public void testUpdateCalendarInvalidPropertyThrows() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    AdvancedCalendarImpl cal1 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Cal1", ZoneId.of("UTC"))
            .build();
    container.addCalendar("Cal1", cal1);

    try {
      container.updateCalendar("Cal1", "invalidProperty", "value");
      assert false;
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid property value for property invalidProperty", e.getMessage());
    }
  }

  @Test
  public void testUpdateCalendarCatchBlock() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    AdvancedCalendarImpl cal1 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Cal1", ZoneId.of("UTC"))
            .build();

    AdvancedCalendarImpl cal2 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Cal2", ZoneId.of("UTC"))
            .build();
    container.addCalendar("Cal1", cal1);
    container.addCalendar("Cal2", cal2);

    try {
      container.updateCalendar("Cal1", "name", "Cal2");
      assert false;
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Calendar with the given name already exists"));
    }
  }

  @Test
  public void testGetActiveCalendarDefault() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    AdvancedCalendarImpl defaultCal =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("default", ZoneId.of("UTC"))
            .build();
    container.addCalendar("default", defaultCal);
    AdvancedCalendar active = container.getActiveCalendar();
    assertEquals(defaultCal, active);
  }

  @Test
  public void testSetActiveCalendar() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    AdvancedCalendarImpl cal1 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Cal1", ZoneId.of("UTC"))
            .build();
    container.addCalendar("Cal1", cal1);
    container.setActiveCalendar("Cal1");
    assertEquals(cal1, container.getActiveCalendar());
  }

  @Test
  public void testSetActiveCalendarInvalid() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    try {
      container.setActiveCalendar("NonExistent");
      assert false;
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar with name NonExistent does not exist", e.getMessage());
    }
  }

  @Test
  public void testGetCalendarsReturnsCopy() {
    CalendarContainerImpl container = new CalendarContainerImpl();
    AdvancedCalendar cal1 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Cal1", ZoneId.of("UTC"))
            .build();
    AdvancedCalendarImpl defaultCal =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("default", ZoneId.of("UTC"))
            .build();
    container.addCalendar("Cal1", cal1);
    container.addCalendar("default", defaultCal);

    Map<String, AdvancedCalendar> calendars = container.getCalendars();
    assertEquals(2, calendars.size());
    calendars.remove("Cal1");
    assertTrue(container.getCalendars().containsKey("Cal1"));
  }

  @Test
  public void welcomeMessageTest() {
    String in = "exit";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    System.out.println(out.toString());
    assertTrue(out.toString().contains("Welcome to the calendar!"));
    assertTrue(out.toString().contains("Commands Supported on individual calendars:"));
    assertTrue(out.toString().contains("use calendar --name <name-of-calendar>"));
    assertTrue(out.toString().contains("exit"));
  }


  @Test
  public void useCommandTest() {
    String in = "use calendar --nam Work";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    assertTrue(out.toString().contains("Invalid command."));
  }

  @Test
  public void useCommandTest2() {
    String in = "use calendar --name Work newWork";

    inputStream = new StringReader(in);
    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);
    controller.run();
    assertTrue(out.toString().contains("Invalid command."));
  }
}
