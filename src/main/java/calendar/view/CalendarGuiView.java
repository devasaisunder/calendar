package calendar.view;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interface for Calendar Gui View.
 * Extends CalendarView to support both CLI and GUI modes.
 * The view should not access the model directly - it receives data from the controller.
 */
public interface CalendarGuiView extends CalendarView {

  /**
   * Sets the Features interface for the view.
   * This allows the view to request high-level operations from the controller.
   *
   * @param features the Features interface implementation
   */
  void addFeatures(Features features);

  /**
   * Displays the Gui window.
   * This method should be called to show the calendar Gui.
   */
  void display();

  /**
   * Updates the calendar list in the view.
   *
   * @param calendarNames    list of all calendar names
   * @param activeCalendarName the currently active calendar name
   */
  void setCalendars(List<String> calendarNames, String activeCalendarName);

  /**
   * Updates the month view with events.
   *
   * @param month  the month being displayed
   * @param events map of date to list of events for that date
   */
  void setMonthEvents(LocalDate month, Map<LocalDate, List<EventInfo>> events);

  /**
   * Updates the day view with events.
   *
   * @param date   the date being displayed
   * @param events list of events for that day
   */
  void setDayEvents(LocalDate date, List<EventInfo> events);
}

