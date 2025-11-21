package calendar.view;

import java.time.LocalDateTime;

/**
 * Data transfer object for event information to be displayed in the view.
 * This class isolates the view from the model's EventReadOnly interface.
 */
public class EventInfo {
  private final String subject;
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final String description;
  private final String location;
  private final String status;
  private final boolean allDay;
  private final boolean isSeries;

  /**
   * Constructor for EventInfo.
   *
   * @param subject       the event subject
   * @param startDateTime the start date and time
   * @param endDateTime   the end date and time
   * @param description   the description
   * @param location      the location
   * @param status        the status
   * @param allDay        whether it's an all-day event
   * @param isSeries      whether it's part of a series
   */
  public EventInfo(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                   String description, String location, String status, boolean allDay,
                   boolean isSeries) {
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.description = description;
    this.location = location;
    this.status = status;
    this.allDay = allDay;
    this.isSeries = isSeries;
  }

  public String getSubject() {
    return subject;
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  public String getDescription() {
    return description;
  }

  public String getLocation() {
    return location;
  }

  public String getStatus() {
    return status;
  }

  public boolean isAllDay() {
    return allDay;
  }

  public boolean isSeries() {
    return isSeries;
  }
}

