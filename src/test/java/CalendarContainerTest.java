import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import calendar.controller.CalendarControllerImpl;
import calendar.model.AdvancedCalendarImpl;
import calendar.model.CalendarContainerImpl;
import calendar.model.CalendarImpl;
import calendar.model.Event;
import calendar.model.interfaces.AdvancedCalendar;
import calendar.model.interfaces.CalendarContainer;
import calendar.model.interfaces.CalendarEditable;
import calendar.model.interfaces.EventReadOnly;
import calendar.view.CalendarViewImpl;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * This test verifies that the CalendarContainer implementation correctly
 * manages multiple calendar instances.
 * It ensures proper behavior for calendar creation, updates and validation.
 * These tests validate that:
 * New calendars can be added and retrieved correctly.
 * Duplicate calendar names are not allowed.
 * Calendars time zones can be updated.
 * Updating a calendar’s timezone adjusts associated event times correctly.
 * Invalid property, time zones, or calendar names raise appropriate exceptions.
 */
public class CalendarContainerTest {

  CalendarEditable calendar;
  Appendable output;
  CalendarControllerImpl calendarController;
  CalendarViewImpl view;
  StringReader inputReader;

  /**
   * Initializes all the required objects before every test to test edit command.
   * It initializes certain common commands needed for most of the tests.
   */
  @Before
  public void setUp() {

    calendar = new CalendarImpl();
    output = new StringBuilder();
    view = new CalendarViewImpl(output);

  }

  @Test
  public void testCalendarManagerAddNewCalendar() {

    CalendarContainer calendarContainer = new CalendarContainerImpl();

    AdvancedCalendar advancedCalendar =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("first",
            ZoneId.of("Africa/Cairo")).build();


    calendarContainer.addCalendar("first", advancedCalendar);

    Map<String, AdvancedCalendar> calendarMap = calendarContainer.getCalendars();
    assertEquals(1, calendarMap.size());
    assertEquals("first", calendarMap.get("first").getName());

  }

  @Test
  public void testCalendarManagerCreateDuplicateName() {
    CalendarContainer calendarContainer = new CalendarContainerImpl();

    AdvancedCalendar advancedCalendar =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("first",
            ZoneId.of("Africa/Cairo")).build();

    AdvancedCalendar advancedCalendar2 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("first",
            ZoneId.of("Africa/Cairo")).build();


    calendarContainer.addCalendar("first", advancedCalendar);
    try {
      calendarContainer.addCalendar("first", advancedCalendar);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }

    Map<String, AdvancedCalendar> calendarMap = calendarContainer.getCalendars();
    assertEquals(1, calendarMap.size());
    assertEquals("first", calendarMap.get("first").getName());
    assertTrue(calendarMap.containsKey("first"));
  }

  @Test
  public void testCalendarManagerUpdateName() {
    CalendarContainer calendarContainer = new CalendarContainerImpl();

    AdvancedCalendar advancedCalendar =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("first",
            ZoneId.of("Africa/Cairo")).build();


    calendarContainer.addCalendar("first", advancedCalendar);

    calendarContainer.updateCalendar("first", "name", "FirstNewName");


    Map<String, AdvancedCalendar> calendarMap = calendarContainer.getCalendars();
    assertEquals(1, calendarMap.size());
    assertFalse(calendarMap.containsKey("first"));
    assertTrue(calendarMap.containsKey("FirstNewName"));
  }

  @Test
  public void testCalendarManagerUpdateDuplicateName() {
    CalendarContainer calendarContainer = new CalendarContainerImpl();

    AdvancedCalendar advancedCalendar =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("first",
            ZoneId.of("Africa/Cairo")).build();
    AdvancedCalendarImpl advancedCalendar2 =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("second",
            ZoneId.of("Africa/Cairo")).build();


    calendarContainer.addCalendar("first", advancedCalendar);
    calendarContainer.addCalendar("second", advancedCalendar2);

    try {
      calendarContainer.updateCalendar("second", "name", "first");
      assert false;
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Calendar with the given name already exists"));
    }
  }

  @Test
  public void testCalendarManagerUpdateInvalidTimezone() {
    CalendarContainer calendarContainer = new CalendarContainerImpl();

    AdvancedCalendar advancedCalendar =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("first",
            ZoneId.of("Africa/Cairo")).build();

    calendarContainer.addCalendar("first", advancedCalendar);

    try {
      calendarContainer.updateCalendar("first", "timezone", "timezone");
      assert false;
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("New time zone is Invalid"));
    }
  }

  @Test
  public void testCalendarManagerUpdateTimezone() {

    AdvancedCalendar advancedCalendar =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("first",
            ZoneId.of("America/New_York")).build();

    LocalDateTime startTime = LocalDateTime.of(2025, 11, 11, 10, 0);
    LocalDateTime endTime = LocalDateTime.of(2025, 11, 11, 11, 0);

    EventReadOnly event1 = new Event.EventBuilder("Event1", startTime)
        .setEndDateTime(endTime)
        .build();

    EventReadOnly event2 = new Event.EventBuilder("Event2", startTime.plusHours(3))
        .setEndDateTime(endTime.plusHours(3))
        .build();

    advancedCalendar.addEvent(event1);
    advancedCalendar.addEvent(event2);

    CalendarContainer calendarContainer = new CalendarContainerImpl();
    calendarContainer.addCalendar("first", advancedCalendar);

    List<EventReadOnly> events = calendarContainer.getCalendars().get("first").getEvents(
        LocalDateTime.of(2025, 11, 11, 10, 0),
        LocalDateTime.of(2025, 11, 11, 13, 0));


    //Lets Validate initial start Date time.
    assertEquals("2025-11-11T10:00", events.get(0).getStartDateTime().toString());
    assertEquals("2025-11-11T11:00", events.get(0).getEndDateTime().toString());
    assertEquals("2025-11-11T13:00", events.get(1).getStartDateTime().toString());
    assertEquals("2025-11-11T14:00", events.get(1).getEndDateTime().toString());


    calendarContainer.updateCalendar("first", "timezone", "Asia/Kolkata");

    List<EventReadOnly> updatedEvents = calendarContainer.getCalendars().get("first").getEvents(
        LocalDateTime.of(2025, 11, 11, 10, 0),
        LocalDateTime.of(2025, 11, 12, 14, 0));

    assertEquals("Asia/Kolkata",
        calendarContainer.getCalendars().get("first").getZoneId().toString());


    assertEquals("2025-11-11T20:30", updatedEvents.get(0).getStartDateTime().toString());
    assertEquals("2025-11-11T21:30", updatedEvents.get(0).getEndDateTime().toString());
    assertEquals("2025-11-11T23:30", updatedEvents.get(1).getStartDateTime().toString());
    assertEquals("2025-11-12T00:30", updatedEvents.get(1).getEndDateTime().toString());
  }

  @Test
  public void testCalendarManagerUpdateInvalidProperty() {
    CalendarContainer calendarContainer = new CalendarContainerImpl();

    AdvancedCalendar advancedCalendar =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("first",
            ZoneId.of("America/New_York")).build();

    calendarContainer.addCalendar("first", advancedCalendar);

    try {
      calendarContainer.updateCalendar("first", "xyz", "first");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Invalid property value for property xyz"));
    }

  }

  @Test
  public void testCalendarManagerUpdateInvalidCalendarName() {
    CalendarContainer calendarContainer = new CalendarContainerImpl();

    AdvancedCalendar advancedCalendar =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("first",
            ZoneId.of("America/New_York")).build();

    calendarContainer.addCalendar("first", advancedCalendar);

    try {
      calendarContainer.updateCalendar("f", "name", "first");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Calendar with name f does not exist"));
    }

  }

  @Test
  public void testCalendarManagerInvalidSetActive() {
    CalendarContainer calendarContainer = new CalendarContainerImpl();

    try {
      calendarContainer.setActiveCalendar("first");
      assert false;
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Calendar with name first does not exist"));
    }
  }

  @Test
  public void testRemoveEvent() {
    EventReadOnly event = new Event.EventBuilder(
        "Meeting",
        LocalDateTime.of(2025, 10, 25, 10, 0)).build();
    AdvancedCalendarImpl calendar =
        new AdvancedCalendarImpl.AdvancedCalendarBuilder("Office", ZoneId.of("UTC"))
            .build();
    calendar.addEvent(event);
    assertTrue(calendar.getEvents(
        LocalDateTime.of(2025, 10, 25, 0, 0),
        LocalDateTime.of(2025, 10, 25, 23, 59)
    ).contains(event));
    calendar.removeEvent(event);
    List<EventReadOnly> remaining = calendar.getEvents(
        LocalDateTime.of(2025, 10, 25, 0, 0),
        LocalDateTime.of(2025, 10, 25, 23, 59)
    );
    assertFalse(remaining.contains(event));
  }

  @Test
  public void testUpdateCalendarDuplicateNameThrowsAndRestores() {
    CalendarContainerImpl container = new CalendarContainerImpl();

    AdvancedCalendar cal1 =
        new AdvancedCalendarImpl
            .AdvancedCalendarBuilder("Cal1", ZoneId.of("UTC"))
            .build();
    AdvancedCalendar cal2 =
        new AdvancedCalendarImpl
            .AdvancedCalendarBuilder("Cal2", ZoneId.of("UTC"))
            .build();

    container.addCalendar("Cal1", cal1);
    container.addCalendar("Cal2", cal2);

    try {
      container.updateCalendar("Cal1", "name", "Cal2");
      assert false;
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("Calendar with the given name already exists"));
    }

    Map<String, AdvancedCalendar> calendars = container.getCalendars();
    assertTrue(calendars.containsKey("Cal1"));
    assertTrue(calendars.containsKey("Cal2"));
    assertEquals(cal1, calendars.get("Cal1"));
    assertEquals(cal2, calendars.get("Cal2"));
  }
}
