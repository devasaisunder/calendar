import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import calendar.controller.CalendarFilter;
import calendar.controller.EventPredicates;
import calendar.model.CalendarImpl;
import calendar.model.Event;
import calendar.model.datatypes.EventStatus;
import calendar.model.datatypes.Location;
import calendar.model.datatypes.TypeOfEvent;
import calendar.model.interfaces.CalendarEditable;
import calendar.model.interfaces.EventReadOnly;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Represents tests for CalendarImpl Class.
 * It tests the functionality of a Calendar like adding, editing, removing an event,
 * and generating csv file and printing with all existing events.
 */
public class CalendarModelTest {
  private CalendarEditable calendar;
  private EventReadOnly event;
  private CalendarFilter filter = new CalendarFilter(calendar);

  /**
   * Initializes a calendar model and a filter object to assert validity of outputs.
   */
  @Before
  public void setUp() {
    calendar = new CalendarImpl();
    filter = new CalendarFilter(calendar);
  }

  @Test
  public void testAddEvent() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
        .build();

    calendar.addEvent(event);
    CalendarFilter filter = new CalendarFilter(calendar);
    List<EventReadOnly> filteredEvents = filter.filter(EventPredicates.bySubject("Meeting")
        .and(EventPredicates.byStartDate(LocalDateTime.of(2025, 07, 13, 12, 12))));
    assertTrue(filteredEvents.contains(event));
  }

  @Test
  public void testWrongEventCreation() {

    try {
      EventReadOnly event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
          .setEndDateTime(LocalDateTime.of(2025, 7, 12, 12, 12))
          .build();
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }

  }

  @Test
  public void testNullEventInitialization() {
    try {
      EventReadOnly event = new Event.EventBuilder(null, null).build();
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }

  }

  @Test
  public void testNullEventInitializationWithSubjectNull() {
    try {
      EventReadOnly event = new Event.EventBuilder("Hellow", null).build();
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }

  }

  @Test
  public void testEventWithDescription() {
    event = new Event.EventBuilder("Meeting",
        LocalDateTime.of(2025, 12, 12, 12, 12))
        .setDescription("description")
        .build();

    calendar.addEvent(event);
    CalendarFilter filter = new CalendarFilter(calendar);
    List<EventReadOnly> filteredEvents = filter.filter(EventPredicates.bySubject("Meeting")
        .and(EventPredicates.byStartDate(LocalDateTime.of(2025, 12, 12, 12, 12)))
        .and(EventPredicates.byDescription("description")));
    assertTrue(filteredEvents.contains(event));
  }

  @Test
  public void testEventWithLocation() {
    event = new Event.EventBuilder("Meeting",
        LocalDateTime.of(2025, 12, 12, 12, 12))
        .setLocation(Location.ONLINE)
        .build();

    calendar.addEvent(event);
    CalendarFilter filter = new CalendarFilter(calendar);
    List<EventReadOnly> filteredEvents = filter.filter(EventPredicates.bySubject("Meeting")
        .and(EventPredicates.byStartDate(LocalDateTime.of(2025, 12, 12, 12, 12)))
        .and(EventPredicates.byLocation(Location.ONLINE)));
    assertTrue(filteredEvents.contains(event));
  }

  @Test
  public void testEventWithEventStatus() {
    event = new Event.EventBuilder("Meeting",
        LocalDateTime.of(2025, 12, 12, 12, 12))
        .setEventStatus(EventStatus.PUBLIC)
        .build();

    calendar.addEvent(event);
    CalendarFilter filter = new CalendarFilter(calendar);
    List<EventReadOnly> filteredEvents = filter.filter(EventPredicates.bySubject("Meeting")
        .and(EventPredicates.byStartDate(LocalDateTime.of(2025, 12, 12, 12, 12)))
        .and(EventPredicates.byStatus(EventStatus.PUBLIC)));
    System.out.println(event.toString());
    assertTrue(filteredEvents.contains(event));
  }

  @Test
  public void testEventWithEventType() {
    event = new Event.EventBuilder("Meeting",
        LocalDateTime.of(2025, 12, 12, 12, 12))
        .setEventType(TypeOfEvent.SINGLE)
        .build();

    calendar.addEvent(event);
    CalendarFilter filter = new CalendarFilter(calendar);
    List<EventReadOnly> filteredEvents = filter.filter(EventPredicates.bySubject("Meeting")
        .and(EventPredicates.byStartDate(LocalDateTime.of(2025, 12, 12, 12, 12)))
        .and(EventPredicates.byEventType(TypeOfEvent.SINGLE)));
    assertTrue(filteredEvents.contains(event));
  }

  @Test
  public void testEventWithEventId() {
    UUID uuid = UUID.randomUUID();
    event = new Event.EventBuilder("Meeting",
        LocalDateTime.of(2025, 12, 12, 12, 12))
        .setEventId(uuid)
        .build();

    calendar.addEvent(event);
    CalendarFilter filter = new CalendarFilter(calendar);
    List<EventReadOnly> filteredEvents = filter.filter(EventPredicates.bySubject("Meeting")
        .and(EventPredicates.byStartDate(LocalDateTime.of(2025, 12, 12, 12, 12)))
        .and(EventPredicates.byEventId(uuid)));
    assertTrue(filteredEvents.contains(event));
  }

  @Test
  public void testEventToString() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
        .build();

    EventReadOnly event1 = new Event.EventBuilder("Meeting",
        LocalDateTime.of(2025, 12, 12, 12, 12))
        .setLocation(Location.ONLINE)
        .build();

    System.out.println(event.toString());
    System.out.println(event1.toString());

    assertEquals("Subject: Meeting, Start: 2025-07-13T12:12, End: 2025-07-13T13:12, "
        + "Description: No description given, Type: SINGLE, AllDayEvent: false", event.toString());
    assertEquals("Subject: Meeting, Start: 2025-12-12T12:12, End: 2025-12-12T13:12, "
            + "Description: No description given, "
            + "Location: ONLINE, Type: SINGLE, AllDayEvent: false",
        event1.toString());
  }

  @Test
  public void testEventNotEquals() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
        .build();

    EventReadOnly event1 = new Event.EventBuilder("Meeting",
        LocalDateTime.of(2025, 12, 12, 12, 12))
        .setLocation(Location.ONLINE)
        .build();

    assertNotEquals(event, event1);
  }

  @Test
  public void testEventEquals() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
        .build();

    EventReadOnly event1 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
        .build();

    assertEquals(event, event1);
  }

  @Test
  public void testEventEqualsWithOtherObject() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
        .build();
    assertNotEquals(event, calendar);
  }

  @Test
  public void testEventHashCodeEquals() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
        .build();

    EventReadOnly event1 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
        .build();

    assertEquals(event.hashCode(), event1.hashCode());
  }

  @Test
  public void testEventHashCodeNotEquals() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 7, 13, 12, 12))
        .build();

    EventReadOnly event1 = new Event.EventBuilder("Meeting",
        LocalDateTime.of(2025, 12, 12, 12, 12))
        .setLocation(Location.ONLINE)
        .build();

    assertNotEquals(event.hashCode(), event1.hashCode());
  }

  @Test
  public void testEqualsSameReference() {
    EventReadOnly event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 10, 0))
        .build();
    assertEquals(event, event);
  }

  @Test
  public void testEqualsSubjectDifferent() {
    EventReadOnly event1 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 11, 0))
        .build();
    EventReadOnly event2 = new Event.EventBuilder("Call", LocalDateTime.of(2025, 10, 28, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 11, 0))
        .build();
    assertNotEquals(event1, event2);
  }

  @Test
  public void testEqualsStartDateTimeDifferent() {
    EventReadOnly event1 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 26, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 11, 0))
        .build();
    EventReadOnly event2 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 11, 0))
        .build();
    assertNotEquals(event1, event2);
  }

  @Test
  public void testEqualsEndDateTimeDifferent() {
    EventReadOnly event1 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 30, 11, 0))
        .build();
    EventReadOnly event2 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 11, 0))
        .build();
    assertNotEquals(event1, event2);
  }

  @Test
  public void testAddConflictingEvents() {
    EventReadOnly event1 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 30, 11, 0))
        .build();
    EventReadOnly event2 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 30, 11, 0))
        .build();

    calendar.addEvent(event1);
    try {
      calendar.addEvent(event2);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }

  }

  @Test
  public void testAddNonConflictingEvents() {
    EventReadOnly event1 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 30, 11, 0))
        .build();
    EventReadOnly event2 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 30, 11, 0))
        .build();

    calendar.addEvent(event1);
    try {
      calendar.addEvent(event2);
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testGetEvent() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setDescription("Team meeting")
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = calendar.getEvents(
        LocalDateTime.of(2025, 10, 28, 9, 0),
        LocalDateTime.of(2025, 10, 28, 10, 0));
    assertEquals(1, events.size());
    assertEquals(event, events.get(0));
  }

  //  @Test
  //  public void testGetEventsAllEquals() {
  //    event = new Event.eventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 0, 0))
  //        .setDescription("Team meeting")
  //        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 23, 59))
  //        .setLocation(Location.UNKNOWN)
  //        .setEventStatus(EventStatus.UNKNOWN)
  //        .setEventType(TypeOfEvent.SINGLE)
  //        .setAllDay(false)
  //        .build();
  //    calendar.addEvent(event);
  //    List<Event> events = calendar.getEvents(LocalDateTime.of(2025, 10, 28, 0, 0),
  //        LocalDateTime.of(2025, 10, 28, 23, 58));
  //    assertEquals(0, events.size());
  //  assertEquals(event, events.get(0));
  //  }

  @Test
  public void testEditEventSingle() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = calendar.getEvents(LocalDateTime.of(2025, 10, 28, 9, 0),
        LocalDateTime.of(2025, 10, 28, 10, 0));
    calendar.editEvent(events, "subject", "Online Meeting");

    List<EventReadOnly> filteredEvents = filter.filter(EventPredicates.bySubject("Online Meeting"));
    assertTrue(filteredEvents.get(0).getSubject().equals("Online Meeting"));
  }

  @Test
  public void testEditEventMultiple() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    EventReadOnly event1 =
        new Event.EventBuilder("Other Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
            .setEndDateTime(LocalDateTime.of(2025, 11, 28, 10, 0))
            .setDescription("Team meeting")
            .setLocation(Location.UNKNOWN)
            .setEventStatus(EventStatus.UNKNOWN)
            .setEventType(TypeOfEvent.SINGLE)
            .setAllDay(false)
            .build();
    calendar.addEvent(event);
    calendar.addEvent(event1);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    events.add(event1);
    calendar.editEvent(events, "subject", "Online Meeting");
    List<EventReadOnly> filteredEvents = filter.filter(EventPredicates.bySubject("Online Meeting"));
    for (EventReadOnly event : filteredEvents) {
      assertTrue(filteredEvents.get(0).getSubject().equals("Online Meeting"));
    }
  }

  /*
    @Test
    public void testEditEventMultipleStartTime() {
      event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
          .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
          .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    EventReadOnly event1 = new Event
    .EventBuilder("Other Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 11, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    calendar.addEvent(event1);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    events.add(event1);
    calendar.editEvent(events, "start", "2025-12-12T13:12");
    List<EventReadOnly> filteredEvents =
        filter.filter(EventPredicates.byStartDate(LocalDateTime.of(2025, 12, 12, 13, 12)));
    assertTrue(filteredEvents.contains(event));
    assertTrue(filteredEvents.contains(event1));
  }
   */

  @Test
  public void testEditEventSetAllDayTrue() {
    EventReadOnly event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 30, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 30, 17, 0))
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    calendar.editEvent(events, "start", "2025-10-30T08:00");

    List<EventReadOnly> filteredEvents =
        filter.filter(EventPredicates.byAllDay(true));
    assertTrue(filteredEvents.get(0).isAllDay());
  }

  @Test
  public void testEditEventAllDayNotTriggered() {
    EventReadOnly event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 30, 10, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 30, 18, 0))
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    calendar.editEvent(events, "start", "2025-10-30T08:00");

    List<EventReadOnly> filteredEvents =
        filter.filter(EventPredicates.byAllDay(true));
    assertFalse(filteredEvents.contains(event));
  }

  @Test
  public void testEditEventMultipleEndTime() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    EventReadOnly event1 =
        new Event.EventBuilder("Other Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
            .setEndDateTime(LocalDateTime.of(2025, 11, 28, 10, 0))
            .setDescription("Team meeting")
            .setLocation(Location.UNKNOWN)
            .setEventStatus(EventStatus.UNKNOWN)
            .setEventType(TypeOfEvent.SINGLE)
            .setAllDay(false)
            .build();
    calendar.addEvent(event);
    calendar.addEvent(event1);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    events.add(event1);
    calendar.editEvent(events, "end", "2025-12-12T13:12");
    List<EventReadOnly> filteredEvents =
        filter.filter(EventPredicates.byEndDate(LocalDateTime.of(2025, 12, 12, 13, 12)));
    for (EventReadOnly event : filteredEvents) {
      assertEquals(event.getEndDateTime(), LocalDateTime.of(2025, 12, 12, 13, 12));
    }
  }

  @Test
  public void testEditEventSetEndAllDayTrue() {
    EventReadOnly event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 30, 8, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 30, 18, 0))
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    calendar.editEvent(events, "end", "2025-10-30T17:00");

    List<EventReadOnly> filteredEvents =
        filter.filter(EventPredicates.byAllDay(true));
    assertTrue(filteredEvents.get(0).isAllDay());
  }

  @Test
  public void testEditEventEndAllDayNotTriggered() {
    EventReadOnly event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 30, 8, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 30, 18, 0))
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    calendar.editEvent(events, "end", "2025-10-30T19:00");

    List<EventReadOnly> filteredEvents =
        filter.filter(EventPredicates.byAllDay(true));
    assertFalse(filteredEvents.contains(event));
  }

  @Test
  public void testEditEventMultipleLocation() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    EventReadOnly event1 =
        new Event.EventBuilder("Other Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
            .setEndDateTime(LocalDateTime.of(2025, 11, 28, 10, 0))
            .setDescription("Team meeting")
            .setLocation(Location.UNKNOWN)
            .setEventStatus(EventStatus.UNKNOWN)
            .setEventType(TypeOfEvent.SINGLE)
            .setAllDay(false)
            .build();
    calendar.addEvent(event);
    calendar.addEvent(event1);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    events.add(event1);
    calendar.editEvent(events, "location", "physical");
    List<EventReadOnly> filteredEvents =
        filter.filter(EventPredicates.byLocation(Location.PHYSICAL));
    assertTrue(filteredEvents.contains(event));
    assertTrue(filteredEvents.contains(event1));
  }

  @Test
  public void testEditEventMultipleDescriptions() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    EventReadOnly event1 =
        new Event.EventBuilder("Other Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
            .setEndDateTime(LocalDateTime.of(2025, 11, 28, 10, 0))
            .setDescription("Team meeting")
            .setLocation(Location.UNKNOWN)
            .setEventStatus(EventStatus.UNKNOWN)
            .setEventType(TypeOfEvent.SINGLE)
            .setAllDay(false)
            .build();
    calendar.addEvent(event);
    calendar.addEvent(event1);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    events.add(event1);
    calendar.editEvent(events, "description", "Individual");
    List<EventReadOnly> filteredEvents =
        filter.filter(EventPredicates.byDescription("Individual"));
    assertTrue(filteredEvents.contains(event));
    assertTrue(filteredEvents.contains(event1));
  }

  @Test
  public void testEditEventMultipleStatus() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    EventReadOnly event1 =
        new Event.EventBuilder("Other Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
            .setEndDateTime(LocalDateTime.of(2025, 11, 28, 10, 0))
            .setDescription("Team meeting")
            .setLocation(Location.UNKNOWN)
            .setEventStatus(EventStatus.UNKNOWN)
            .setEventType(TypeOfEvent.SINGLE)
            .setAllDay(false)
            .build();
    calendar.addEvent(event);
    calendar.addEvent(event1);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    events.add(event1);
    calendar.editEvent(events, "status", "public");
    List<EventReadOnly> filteredEvents =
        filter.filter(EventPredicates.byStatus(EventStatus.PUBLIC));
    assertTrue(filteredEvents.contains(event));
    assertTrue(filteredEvents.contains(event1));
  }

  @Test
  public void testEditSingleWithWrongProperty() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    try {
      calendar.editEvent(List.of(event), "sub", "Online Meeting");
      assert false;
    } catch (UnsupportedOperationException e) {
      assert true;
    }
  }

  @Test
  public void testEditMultipleWithWrongProperty() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    EventReadOnly event1 =
        new Event.EventBuilder("Other Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
            .setEndDateTime(LocalDateTime.of(2025, 11, 28, 10, 0))
            .setDescription("Team meeting")
            .setLocation(Location.UNKNOWN)
            .setEventStatus(EventStatus.UNKNOWN)
            .setEventType(TypeOfEvent.SINGLE)
            .setAllDay(false)
            .build();
    try {
      calendar.editEvent(List.of(event, event1), "sub", "Online Meeting");
      assert false;
    } catch (UnsupportedOperationException e) {
      assert true;
    }
  }

  /*
  @Test
  public void testEditSingleEventOfSeries() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SERIES)
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    calendar.editEvent(events, "start", "2025-12-12T13:12");
    List<EventReadOnly> filteredEvents = filter
    .filter(EventPredicates.byEventType(TypeOfEvent.SINGLE));
    assertTrue(filteredEvents.contains(event));
  }
   */

  /*
  @Test
  public void testEditSingleEventOfSeriesTwo() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = new ArrayList<>();
    events.add(event);
    calendar.editEvent(events, "start", "2025-12-12T13:12");
    List<EventReadOnly> filteredEvents = filter
        .filter(EventPredicates.byEventType(TypeOfEvent.SINGLE));
    assertTrue(filteredEvents.contains(event));
  }
   */

  @Test
  public void testRemoveEvent() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    EventReadOnly event1 = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 9, 0))
        .setEndDateTime(LocalDateTime.of(2025, 11, 28, 10, 0))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    calendar.addEvent(event1);
    List<EventReadOnly> filteredEvents = filter.filter(EventPredicates.bySubject("Meeting"));
    assertEquals(2, filteredEvents.size());
    assertTrue(filteredEvents.contains(event));
    assertTrue(filteredEvents.contains(event1));
    calendar.removeEvent(event);
    filteredEvents = filter.filter(EventPredicates.bySubject("Meeting"));
    assertEquals(1, filteredEvents.size());
    assertTrue(filteredEvents.contains(event1));
  }

  @Test
  public void testGetEventsDayLong() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 23, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 23, 59))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = calendar.getEvents(LocalDateTime.of(2025, 10, 28, 0, 0),
        LocalDateTime.of(2025, 10, 28, 23, 59));
    assertTrue(events.contains(event));
  }

  @Test
  public void testGetEventsDayLongTwo() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 0, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 23, 58))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = calendar.getEvents(LocalDateTime.of(2026, 10, 28, 0, 0),
        LocalDateTime.of(2026, 10, 28, 23, 59));
    assertFalse(events.contains(event));
  }

  @Test
  public void testGetEventsDayLongThree() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 0, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 23, 58))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    calendar.addEvent(event);
    List<EventReadOnly> events = calendar.getEvents(LocalDateTime.of(2026, 10, 28, 0, 0),
        LocalDateTime.of(2026, 10, 29, 23, 59));
    assertFalse(events.contains(event));
  }

  @Test
  public void testGetEventsReturnEmpty() {
    List<EventReadOnly> events = calendar.getEvents(LocalDateTime.of(2026, 10, 28, 0, 0),
        LocalDateTime.of(2026, 10, 28, 23, 59));
    assertEquals(0, events.size());
  }

  @Test
  public void testGetEventsDayLongFour() {
    event = new Event.EventBuilder("Meeting", LocalDateTime.of(2025, 10, 28, 0, 0))
        .setEndDateTime(LocalDateTime.of(2025, 10, 28, 23, 58))
        .setDescription("Team meeting")
        .setLocation(Location.UNKNOWN)
        .setEventStatus(EventStatus.UNKNOWN)
        .setEventType(TypeOfEvent.SINGLE)
        .setAllDay(false)
        .build();
    List<EventReadOnly> events = calendar.getEvents(LocalDateTime.of(2026, 10, 28, 0, 0),
        LocalDateTime.of(2026, 10, 28, 23, 58));
    assertFalse(events.contains(event));
  }

  @Test
  public void testGetEventsDayLongFive() {
    List<EventReadOnly> events = calendar.getEvents(LocalDateTime.of(2026, 10, 28, 0, 0),
        LocalDateTime.of(2026, 10, 28, 23, 58));
    assertTrue(events.isEmpty());
  }

  @Test
  public void testIsBusyNoEvents() {
    CalendarImpl calendar = new CalendarImpl();
    LocalDateTime now = LocalDateTime.now();
    assertFalse(calendar.isBusy(now));
  }

  @Test
  public void testIsBusyExactStartMatch() {
    CalendarImpl calendar = new CalendarImpl();
    LocalDateTime start = LocalDateTime.of(2025, 10, 29, 10, 0);
    EventReadOnly event = new Event.EventBuilder("Meeting", start)
        .setEndDateTime(start.plusHours(2))
        .build();
    calendar.addEvent(event);
    assertTrue(calendar.isBusy(start));
  }

  @Test
  public void testIsBusyBetweenStartAndEnd() {
    CalendarImpl calendar = new CalendarImpl();
    LocalDateTime start = LocalDateTime.of(2025, 10, 29, 10, 0);
    LocalDateTime end = start.plusHours(2);
    EventReadOnly event = new Event.EventBuilder("Online Meeting", start)
        .setEndDateTime(end)
        .build();
    calendar.addEvent(event);
    LocalDateTime mid = start.plusHours(1);
    assertTrue(calendar.isBusy(mid));
  }

  @Test
  public void testIsBusyNotInAnyEvent() {
    CalendarImpl calendar = new CalendarImpl();
    LocalDateTime start = LocalDateTime.of(2025, 10, 29, 10, 0);
    EventReadOnly event = new Event.EventBuilder("Public Event", start)
        .setEndDateTime(start.plusHours(1))
        .build();
    calendar.addEvent(event);
    LocalDateTime afterEvent = start.plusHours(3);
    assertFalse(calendar.isBusy(afterEvent));
  }

  @Test
  public void testIsBusyMultipleEventsOneOverlap() {
    CalendarImpl calendar = new CalendarImpl();

    LocalDateTime firstStart = LocalDateTime.of(2025, 10, 29, 9, 0);
    LocalDateTime firstEnd = firstStart.plusHours(1);
    EventReadOnly event1 = new Event.EventBuilder("Online", firstStart)
        .setEndDateTime(firstEnd)
        .build();
    LocalDateTime secondStart = LocalDateTime.of(2025, 10, 29, 12, 0);
    LocalDateTime secondEnd = secondStart.plusHours(2);
    EventReadOnly event2 = new Event.EventBuilder("Meeting", secondStart)
        .setEndDateTime(secondEnd)
        .build();
    calendar.addEvent(event1);
    calendar.addEvent(event2);
    assertTrue(calendar.isBusy(LocalDateTime.of(2025, 10, 29, 13, 0)));
  }
}
