package calendar.model;

import calendar.model.interfaces.AdvancedCalendar;
import calendar.model.interfaces.CalendarContainer;
import calendar.model.interfaces.EditCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages multiple calendars and allows adding, updating,
 * deleting, and switching between active calendars.
 */
public class CalendarContainerImpl implements CalendarContainer {

  private final Map<String, AdvancedCalendar> calendars;
  private AdvancedCalendar activeCalendar;

  /**
   * Creates a new CalendarManager with an empty set of calendars.
   */
  public CalendarContainerImpl() {
    calendars = new HashMap<>();
  }

  @Override
  public void addCalendar(String name, AdvancedCalendar advancedCalendar) {
    if (calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar with name " + name + " already exists");
    }
    calendars.put(name, advancedCalendar);
  }


  @Override
  public void updateCalendar(String name, String property, String newValue) {
    if (!calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar with name " + name + " does not exist");
    }
    AdvancedCalendar oldCalendar = this.calendars.get(name);
    Map<String, EditCalendar> modifyCalendarMap = new HashMap<>();
    modifyCalendarMap.put("name", new EditCalendarName());
    modifyCalendarMap.put("timezone", new EditCalendarTimeZone());
    AdvancedCalendar calendar = calendars.get(name);
    EditCalendar editCall = modifyCalendarMap.getOrDefault(property, null);
    if (editCall == null) {
      throw new IllegalArgumentException("Invalid property value for property " + property);
    }

    AdvancedCalendar newCalendar = editCall.edit(calendar, newValue);
    try {
      this.deleteCalendar(name);
      this.addCalendar(newCalendar.getName(), newCalendar);
    } catch (IllegalArgumentException e) {
      this.addCalendar(oldCalendar.getName(), oldCalendar);
      throw new IllegalArgumentException("Calendar with the given name already exists" + newValue);
    }
  }

  private void deleteCalendar(String name) {
    calendars.remove(name);
  }


  @Override
  public AdvancedCalendar getActiveCalendar() {
    if (activeCalendar == null) {
      return this.calendars.get("default");
    }
    return this.activeCalendar;
  }

  @Override
  public void setActiveCalendar(String name) {
    if (!calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar with name " + name + " does not exist");
    }
    this.activeCalendar = calendars.get(name);
  }

  @Override
  public Map<String, AdvancedCalendar> getCalendars() {
    return new HashMap<>(this.calendars);
  }
}
