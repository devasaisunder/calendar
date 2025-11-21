package calendar.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interface for high-level features that the view can request from the controller.
 * This interface encapsulates application-specific events rather than low-level UI events.
 * The controller implements this interface to handle user requests.
 */
public interface Features {

  /**
   * Request to create a new calendar.
   *
   * @param name     the calendar name
   * @param timezone the timezone (IANA format)
   * @return success message or error message
   */
  String createCalendar(String name, String timezone);

  /**
   * Request to edit a calendar property.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     the property to edit (name or timezone)
   * @param newValue     the new value
   * @return success message or error message
   */
  String editCalendar(String calendarName, String property, String newValue);

  /**
   * Request to switch to a different calendar.
   *
   * @param calendarName the name of the calendar to switch to
   * @return success message or error message
   */
  String switchCalendar(String calendarName);

  /**
   * Request to create a new event.
   *
   * @param subject       the event subject
   * @param startDateTime the start date and time
   * @param endDateTime   the end date and time
   * @param description   the event description
   * @param location      the location (PHYSICAL, ONLINE, UNKNOWN)
   * @param status        the status (PUBLIC, PRIVATE, UNKNOWN)
   * @param isRepeating   whether this is a repeating event
   * @param repeatDays    the days to repeat on (e.g., "MWF") - null if not repeating
   * @param repeatEndDate the end date for repeating - null if not repeating
   * @return success message or error message
   */
  String createEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                     String description, String location, String status,
                     boolean isRepeating, String repeatDays, LocalDate repeatEndDate);

  /**
   * Request to edit an event.
   *
   * @param property      the property to edit
   * @param subject       the event subject
   * @param startDateTime the event start date and time
   * @param endDateTime   the event end date and time (for single event edits)
   * @param newValue      the new value
   * @param scope         the edit scope: "single", "from", or "series"
   * @return success message or error message
   */
  String editEvent(String property, String subject, LocalDateTime startDateTime,
                   LocalDateTime endDateTime, String newValue, String scope);

  /**
   * Request to export the calendar.
   *
   * @param fileName the file name to export to
   * @return success message with file path or error message
   */
  String exportCalendar(String fileName);

  /**
   * Request to get all calendar names.
   *
   * @return list of calendar names
   */
  List<String> getCalendarNames();

  /**
   * Request to get the active calendar name.
   *
   * @return the active calendar name, or null if none
   */
  String getActiveCalendarName();

  /**
   * Request to get events for a specific month.
   *
   * @param month the month to get events for
   * @return map of date to list of event info
   */
  Map<LocalDate, List<EventInfo>> getEventsForMonth(LocalDate month);

  /**
   * Request to get events for a specific day.
   *
   * @param date the date to get events for
   * @return list of event info for that day
   */
  List<EventInfo> getEventsForDay(LocalDate date);

  /**
   * Request to get the timezone of the active calendar.
   *
   * @return the timezone ID as a string, or null if no active calendar
   */
  String getActiveCalendarTimezone();

  /**
   * Request to delete an event.
   *
   * @param subject       the event subject
   * @param startDateTime the event start date and time
   * @param endDateTime   the event end date and time
   * @param scope         the delete scope: "single", "from", or "series"
   * @return success message or error message
   */
  String deleteEvent(String subject, LocalDateTime startDateTime,
                     LocalDateTime endDateTime, String scope);
}

