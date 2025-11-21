import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import calendar.controller.EventPredicates;
import calendar.model.Event;
import calendar.model.datatypes.EventStatus;
import calendar.model.datatypes.Location;
import calendar.model.datatypes.TypeOfEvent;
import calendar.model.interfaces.EventReadOnly;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Predicate;
import org.junit.Before;
import org.junit.Test;

/**
 * Represents tests for all event predicates which are used for filtering events in a calendar.
 */
public class EventPredicatesTest {

  private EventReadOnly event1;
  private EventReadOnly event2;
  private LocalDateTime start;
  private LocalDateTime end;
  private UUID id;

  /**
   * Initializes two events before every test which are used in most of the tests.
   */
  @Before
  public void setup() {
    start = LocalDateTime.of(2025, 10, 25, 8, 0);
    end = LocalDateTime.of(2025, 10, 25, 17, 0);
    id = UUID.randomUUID();

    event1 = new Event.EventBuilder("Meeting", start)
        .setEndDateTime(end)
        .setDescription("description")
        .setLocation(Location.PHYSICAL)
        .setEventStatus(EventStatus.PUBLIC)
        .setEventType(TypeOfEvent.SINGLE)
        .setEventId(id)
        .build();
    event2 = new Event.EventBuilder("Lunch", start.plusDays(1))
        .setEndDateTime(end.plusDays(1))
        .setDescription("description1")
        .setLocation(Location.ONLINE)
        .setEventStatus(EventStatus.PRIVATE)
        .setEventType(TypeOfEvent.SERIES)
        .setEventId(UUID.randomUUID())
        .build();
  }

  @Test
  public void testBySubject() {
    Predicate<EventReadOnly> p = EventPredicates.bySubject("Meeting");
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testByStartDate() {
    Predicate<EventReadOnly> p = EventPredicates.byStartDate(start);
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testByEndDate() {
    Predicate<EventReadOnly> p = EventPredicates.byEndDate(end);
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testByDescription() {
    Predicate<EventReadOnly> p = EventPredicates.byDescription("description");
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testByLocation() {
    Predicate<EventReadOnly> p = EventPredicates.byLocation(Location.PHYSICAL);
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testByStatus() {
    Predicate<EventReadOnly> p = EventPredicates.byStatus(EventStatus.PUBLIC);
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testByEventType() {
    Predicate<EventReadOnly> p = EventPredicates.byEventType(TypeOfEvent.SINGLE);
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testByEventId() {
    Predicate<EventReadOnly> p = EventPredicates.byEventId(id);
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testBySeries() {
    Predicate<EventReadOnly> p = EventPredicates.bySeries(start, end, "Meeting");
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testBetweenStartAndEnd() {
    LocalDateTime rangeStart = start.minusHours(1);
    LocalDateTime rangeEnd = end.plusHours(1);
    Predicate<EventReadOnly> p = EventPredicates.betweenStartAndEnd(rangeStart, rangeEnd);
    assertTrue(p.test(event1));
    assertFalse(p.test(event2));
  }

  @Test
  public void testBySeriesFullCoverage() {
    EventPredicates ep = new EventPredicates();
    Predicate<EventReadOnly> p = EventPredicates.bySeries(start, end, "Meeting");
    assertTrue(p.test(event1));

    EventReadOnly startMismatch = new Event.EventBuilder("Meeting", start)
        .setEndDateTime(end.plusHours(1))
        .setEventId(UUID.randomUUID())
        .build();
    assertFalse(p.test(startMismatch));

    EventReadOnly subjectMismatch = new Event.EventBuilder("Meeting1", start)
        .setEndDateTime(end)
        .setEventId(UUID.randomUUID())
        .build();
    assertFalse(p.test(subjectMismatch));

    EventReadOnly endMismatch = new Event.EventBuilder("Meeting2", start)
        .setEndDateTime(end.plusHours(1))
        .setEventId(UUID.randomUUID())
        .build();
    assertFalse(p.test(endMismatch));

    EventReadOnly allMismatch = new Event.EventBuilder("Meeting3", start.plusHours(1))
        .setEndDateTime(end.plusDays(1))
        .setEventId(UUID.randomUUID())
        .build();
    assertFalse(p.test(allMismatch));
  }

}
