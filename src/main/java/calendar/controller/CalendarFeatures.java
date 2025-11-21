package calendar.controller;

import calendar.controller.commanddata.CreateCalendarCommandData;
import calendar.controller.commanddata.CreateCommandData;
import calendar.controller.commanddata.DeleteEventCommandData;
import calendar.controller.commanddata.DeleteMultipleEventsCommandData;
import calendar.controller.commanddata.DeleteSeriesCommandData;
import calendar.controller.commanddata.EditCalendarCommandData;
import calendar.controller.commanddata.EditEventCommandData;
import calendar.controller.commanddata.EditMultipleEventsCommandData;
import calendar.controller.commanddata.EditSeriesCommandData;
import calendar.controller.commanddata.ExportCommandData;
import calendar.controller.handlers.CreateCalendarHandler;
import calendar.controller.handlers.CreateEventHandler;
import calendar.controller.handlers.DeleteEventHandler;
import calendar.controller.handlers.DeleteMultipleEventsHandler;
import calendar.controller.handlers.DeleteSeriesHandler;
import calendar.controller.handlers.EditCalendarHandler;
import calendar.controller.handlers.EditEventHandler;
import calendar.controller.handlers.EditMultipleEventsHandler;
import calendar.controller.handlers.EditSeriesHandler;
import calendar.controller.handlers.ExportEventHandler;
import calendar.model.interfaces.AdvancedCalendar;
import calendar.model.interfaces.CalendarContainer;
import calendar.model.interfaces.EventReadOnly;
import calendar.view.EventInfo;
import calendar.view.Features;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of Features interface for calendar operations.
 * This class handles all model interactions and provides data to the view.
 */
public class CalendarFeatures implements Features {
  private final CalendarContainer container;

  /**
   * Constructor for CalendarFeatures.
   *
   * @param container the calendar container
   */
  public CalendarFeatures(CalendarContainer container) {
    this.container = Objects.requireNonNull(container);
  }

  @Override
  public String createCalendar(String name, String timezone) {
    try {
      ZoneId zoneId = ZoneId.of(timezone);
      CreateCalendarHandler handler = new CreateCalendarHandler(container);
      CreateCalendarCommandData data = new CreateCalendarCommandData(name, zoneId);
      return handler.handle(data);
    } catch (Exception e) {
      return "Error: Failed to create calendar: " + e.getMessage();
    }
  }

  @Override
  public String editCalendar(String calendarName, String property, String newValue) {
    try {
      EditCalendarHandler handler = new EditCalendarHandler(container);
      EditCalendarCommandData data = new EditCalendarCommandData(calendarName, property, newValue);
      return handler.handle(data);
    } catch (Exception e) {
      return "Error: Failed to edit calendar: " + e.getMessage();
    }
  }

  @Override
  public String switchCalendar(String calendarName) {
    try {
      container.setActiveCalendar(calendarName);
      return "Switched to calendar: " + calendarName;
    } catch (Exception e) {
      return "Error: Failed to switch calendar: " + e.getMessage();
    }
  }

  @Override
  public String createEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                            String description, String location, String status,
                            boolean isRepeating, String repeatDays, LocalDate repeatEndDate) {
    try {
      AdvancedCalendar activeCalendar = container.getActiveCalendar();
      if (activeCalendar == null) {
        return "Error: No active calendar";
      }

      CreateEventHandler handler = new CreateEventHandler(activeCalendar.getCalendar());
      CreateCommandData data;

      if (isRepeating && repeatDays != null && repeatEndDate != null) {
        data = new CreateCommandData(subject, startDateTime, endDateTime,
            repeatDays, "until", repeatEndDate.format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")), true);
      } else {
        data = new CreateCommandData(subject, startDateTime, endDateTime, true);
      }

      String result = handler.handle(data);

      // Update event properties if needed
      List<EventReadOnly> createdEvents = activeCalendar.getCalendar()
          .getEvents(startDateTime, endDateTime.plusDays(30));
      for (EventReadOnly event : createdEvents) {
        if (event.getSubject().equals(subject)
            && event.getStartDateTime().equals(startDateTime)) {
          List<EventReadOnly> eventsToEdit = new ArrayList<>();
          eventsToEdit.add(event);
          if (!description.equals("No description given")) {
            activeCalendar.getCalendar().editEvent(eventsToEdit, "description", description);
          }
          if (!location.equals("UNKNOWN")) {
            activeCalendar.getCalendar().editEvent(eventsToEdit, "location", location);
          }
          if (!status.equals("UNKNOWN")) {
            activeCalendar.getCalendar().editEvent(eventsToEdit, "status", status);
          }
          break;
        }
      }

      return result;
    } catch (Exception e) {
      return "Error: Failed to create event: " + e.getMessage();
    }
  }

  @Override
  public String editEvent(String property, String subject, LocalDateTime startDateTime,
                          LocalDateTime endDateTime, String newValue, String scope) {
    try {
      AdvancedCalendar activeCalendar = container.getActiveCalendar();
      if (activeCalendar == null) {
        return "Error: No active calendar";
      }

      String result;
      if ("single".equals(scope)) {
        EditEventHandler handler = new EditEventHandler(activeCalendar.getCalendar());
        EditEventCommandData data = new EditEventCommandData(
            property, subject, startDateTime, endDateTime, newValue);
        result = handler.handle(data);
      } else if ("from".equals(scope)) {
        EditMultipleEventsHandler handler = new EditMultipleEventsHandler(
            activeCalendar.getCalendar());
        EditMultipleEventsCommandData data = new EditMultipleEventsCommandData(
            property, subject, startDateTime, newValue);
        result = handler.handle(data);
      } else {
        EditSeriesHandler handler = new EditSeriesHandler(activeCalendar.getCalendar());
        EditSeriesCommandData data = new EditSeriesCommandData(
            property, subject, startDateTime, newValue);
        result = handler.handle(data);
      }
      return result;
    } catch (Exception e) {
      return "Error: Failed to edit event: " + e.getMessage();
    }
  }

  @Override
  public String exportCalendar(String fileName) {
    try {
      AdvancedCalendar activeCalendar = container.getActiveCalendar();
      if (activeCalendar == null) {
        return "Error: No active calendar";
      }
      ExportEventHandler handler = new ExportEventHandler(activeCalendar.getCalendar());
      ExportCommandData data = new ExportCommandData(fileName);
      return handler.handle(data);
    } catch (Exception e) {
      return "Error: Failed to export: " + e.getMessage();
    }
  }

  @Override
  public List<String> getCalendarNames() {
    return new ArrayList<>(container.getCalendars().keySet());
  }

  @Override
  public String getActiveCalendarName() {
    AdvancedCalendar activeCalendar = container.getActiveCalendar();
    return activeCalendar != null ? activeCalendar.getName() : null;
  }

  @Override
  public Map<LocalDate, List<EventInfo>> getEventsForMonth(LocalDate month) {
    Map<LocalDate, List<EventInfo>> result = new HashMap<>();
    AdvancedCalendar activeCalendar = container.getActiveCalendar();
    if (activeCalendar == null) {
      return result;
    }

    LocalDate firstDay = month.withDayOfMonth(1);
    LocalDate lastDay = month.withDayOfMonth(month.lengthOfMonth());
    LocalDateTime start = firstDay.atStartOfDay();
    LocalDateTime end = lastDay.atTime(23, 59, 59);

    List<EventReadOnly> events = activeCalendar.getCalendar().getEvents(start, end);
    for (EventReadOnly event : events) {
      LocalDate eventDate = event.getStartDateTime().toLocalDate();
      result.computeIfAbsent(eventDate, k -> new ArrayList<>())
          .add(convertToEventInfo(event));
    }

    return result;
  }

  @Override
  public List<EventInfo> getEventsForDay(LocalDate date) {
    List<EventInfo> result = new ArrayList<>();
    AdvancedCalendar activeCalendar = container.getActiveCalendar();
    if (activeCalendar == null) {
      return result;
    }

    LocalDateTime dayStart = date.atStartOfDay();
    LocalDateTime dayEnd = date.atTime(23, 59, 59);
    List<EventReadOnly> events = activeCalendar.getCalendar().getEvents(dayStart, dayEnd);

    for (EventReadOnly event : events) {
      result.add(convertToEventInfo(event));
    }

    return result;
  }

  @Override
  public String getActiveCalendarTimezone() {
    AdvancedCalendar activeCalendar = container.getActiveCalendar();
    return activeCalendar != null ? activeCalendar.getZoneId().getId() : null;
  }

  @Override
  public String deleteEvent(String subject, LocalDateTime startDateTime,
                            LocalDateTime endDateTime, String scope) {
    try {
      AdvancedCalendar activeCalendar = container.getActiveCalendar();
      if (activeCalendar == null) {
        return "Error: No active calendar";
      }

      String result;
      if ("single".equals(scope)) {
        DeleteEventHandler handler = new DeleteEventHandler(activeCalendar.getCalendar());
        DeleteEventCommandData data = new DeleteEventCommandData(
            subject, startDateTime, endDateTime);
        result = handler.handle(data);
      } else if ("from".equals(scope)) {
        DeleteMultipleEventsHandler handler = new DeleteMultipleEventsHandler(
            activeCalendar.getCalendar());
        DeleteMultipleEventsCommandData data = new DeleteMultipleEventsCommandData(
            subject, startDateTime);
        result = handler.handle(data);
      } else {
        DeleteSeriesHandler handler = new DeleteSeriesHandler(activeCalendar.getCalendar());
        DeleteSeriesCommandData data = new DeleteSeriesCommandData(subject, startDateTime);
        result = handler.handle(data);
      }
      return result;
    } catch (Exception e) {
      return "Error: Failed to delete event: " + e.getMessage();
    }
  }

  /**
   * Converts an EventReadOnly to EventInfo for the view.
   *
   * @param event the event to convert
   * @return EventInfo object
   */
  private EventInfo convertToEventInfo(EventReadOnly event) {
    return new EventInfo(
        event.getSubject(),
        event.getStartDateTime(),
        event.getEndDateTime(),
        event.getDescription(),
        event.getLocation().name(),
        event.getEventStatus().name(),
        event.isAllDay(),
        event.getEventType() == calendar.model.datatypes.TypeOfEvent.SERIES
    );
  }
}

