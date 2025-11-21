package calendar.controller;

import calendar.model.interfaces.CalendarContainer;
import calendar.view.CalendarGuiView;
import calendar.view.EventInfo;
import calendar.view.Features;
import calendar.view.ViewListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Calendar GUI application.
 * Implements ViewListener to handle all user interactions from the view.
 * All business logic is handled here, not in the view.
 */
public class CalendarGuiController implements ViewListener {
  private final CalendarContainer container;
  private final CalendarFeatures features;
  private final CalendarGuiView view;
  private LocalDate currentMonth;
  private LocalDate selectedDate;

  /**
   * Constructor for CalendarGuiController.
   *
   * @param container the calendar container (model)
   * @param view      the GUI view
   */
  public CalendarGuiController(CalendarContainer container, CalendarGuiView view) {
    this.container = container;
    this.features = new CalendarFeatures(container);
    this.view = view;
    this.currentMonth = LocalDate.now();
    this.selectedDate = LocalDate.now();
    
    // Register this controller as a listener to the view
    view.addViewListener(this);
  }

  /**
   * Starts the application by displaying the view and requesting initial data.
   */
  public void go() {
    setupViewForDialogs();
    view.display();
    handleRefresh();
  }

  @Override
  public void handleSwitchCalendar(String calendarName) {
    String result = features.switchCalendar(calendarName);
    if (isError(result)) {
      view.renderError(result);
    } else {
      handleRefresh();
    }
  }

  @Override
  public void handleCreateCalendar(String name, String timezone) {
    String result = features.createCalendar(name, timezone);
    if (isError(result)) {
      view.renderError(result);
    } else {
      handleRefresh();
    }
  }

  @Override
  public void handleEditCalendar(String calendarName, String property, String newValue) {
    String result = features.editCalendar(calendarName, property, newValue);
    if (isError(result)) {
      view.renderError(result);
    } else {
      handleRefresh();
    }
  }

  @Override
  public void handlePreviousMonth() {
    currentMonth = currentMonth.minusMonths(1);
    handleRefresh();
  }

  @Override
  public void handleNextMonth() {
    currentMonth = currentMonth.plusMonths(1);
    handleRefresh();
  }

  @Override
  public void handleDateSelected(LocalDate date) {
    this.selectedDate = date;
    handleRefresh();
  }

  @Override
  public void handleCreateEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                String description, String location, String status,
                                boolean isRepeating, String repeatDays, LocalDate repeatEndDate) {
    try {
      String result = features.createEvent(subject, startDateTime, endDateTime, description,
          location, status, isRepeating, repeatDays, repeatEndDate);
      if (isError(result)) {
        view.renderError(result);
      } else {
        handleRefresh();
      }
    } catch (Exception e) {
      view.renderError("Error: " + e.getMessage());
    }
  }

  @Override
  public void handleEditEvent(String property, String subject, LocalDateTime startDateTime,
                              LocalDateTime endDateTime, String newValue, String scope) {
    try {
      String result = features.editEvent(property, subject, startDateTime, endDateTime, newValue, scope);
      if (isError(result)) {
        view.renderError(result);
      } else {
        handleRefresh();
      }
    } catch (Exception e) {
      view.renderError("Error: " + e.getMessage());
    }
  }

  @Override
  public void handleDeleteEvent(String subject, LocalDateTime startDateTime,
                                 LocalDateTime endDateTime, String scope) {
    try {
      String result = features.deleteEvent(subject, startDateTime, endDateTime, scope);
      if (isError(result)) {
        view.renderError(result);
      } else {
        handleRefresh();
      }
    } catch (Exception e) {
      view.renderError("Error: " + e.getMessage());
    }
  }

  @Override
  public void handleExportCalendar(String fileName) {
    String result = features.exportCalendar(fileName);
    if (isError(result)) {
      view.renderError(result);
    }
    // No popup on success
  }

  @Override
  public void handleRefresh() {
    // Update current month and selected date from view
    currentMonth = view.getCurrentMonth();
    selectedDate = view.getSelectedDate();

    // Get data from model
    List<String> calendarNames = features.getCalendarNames();
    String activeCalendarName = features.getActiveCalendarName();
    String timezone = features.getActiveCalendarTimezone();

    // Update view with data
    view.setCalendars(calendarNames, activeCalendarName);
    
    Map<LocalDate, List<EventInfo>> monthEvents = features.getEventsForMonth(currentMonth);
    view.setMonthEvents(currentMonth, monthEvents);
    
    List<EventInfo> dayEvents = features.getEventsForDay(selectedDate);
    view.setDayEvents(selectedDate, dayEvents);

    // Update timezone and selected date in top panel
    view.getTopPanel().updateTimezone(timezone);
    view.getTopPanel().updateSelectedDate(selectedDate);
    view.getTopPanel().updateMonthYear(currentMonth);
  }

  /**
   * Gets the Features interface for use by dialogs.
   * This allows dialogs to query data without directly accessing the model.
   * Note: This is for read-only queries. All write operations go through ViewListener events.
   *
   * @return the Features interface
   */
  public Features getFeatures() {
    return features;
  }

  /**
   * Sets the Features interface in the view for dialogs to use.
   * This allows dialogs to query data.
   */
  public void setupViewForDialogs() {
    view.setFeaturesForDialogs(features);
  }

  /**
   * Gets the selected date.
   *
   * @return the selected date
   */
  public LocalDate getSelectedDate() {
    return selectedDate;
  }

  // Helper to identify error results while avoiding direct null checks
  private static boolean isError(String result) {
    return result != null && (result.startsWith("Error") || result.contains("Failed") || result.contains("Error"));
  }
}
