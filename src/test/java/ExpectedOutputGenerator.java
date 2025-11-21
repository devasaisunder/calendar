import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate expected event dates for recurring calendar events.
 */
public class ExpectedOutputGenerator {

  /**
   * Generates all dates on which an event is supposed to be created when given create command
   * with repeat n times.
   *
   * @param start      start date of events creation
   * @param daysString weekdays on which event to be created
   * @param n          no.of events to create
   * @return returns a list of all local date times on which event is supposed to be created
   */
  protected static List<LocalDateTime> getTimesDates(LocalDateTime start, String daysString,
                                                     int n) {
    List<LocalDateTime> dates = new ArrayList<>();
    LocalDateTime currentDateTime = start;
    List<DayOfWeek> onDays = populateDaysOfWeek(daysString);
    while (n > 0) {
      if (onDays.contains(currentDateTime.getDayOfWeek())) {
        dates.add(currentDateTime);
        n--;
      }
      currentDateTime = currentDateTime.plusDays(1);
    }
    return dates;
  }

  /**
   * Generates all dates on which an event is supposed to be created when given create command
   * with a until date.
   *
   * @param start      start date of events creation
   * @param daysString weekdays on which event to be created
   * @param until      date time until which events are supposed to be created
   * @return returns a list of all local date times on which event is supposed to be created
   */
  protected static List<LocalDateTime> getUntilDates(LocalDateTime start,
                                                     String daysString, LocalDateTime until) {
    List<LocalDateTime> dates = new ArrayList<>();
    LocalDateTime currentDateTime = start;
    List<DayOfWeek> onDays = populateDaysOfWeek(daysString);
    while (!currentDateTime.isAfter(until)) {
      if (onDays.contains(currentDateTime.getDayOfWeek())) {
        dates.add(currentDateTime);
      }
      currentDateTime = currentDateTime.plusDays(1);
    }

    return dates;
  }

  /**
   * Given a weekdays string it populates a list of type DayOfWeek corresponding to each character
   * in days string where 'M' is Monday, 'T' is Tuesday, 'W' is Wednesday, 'R' is Thursday,
   * 'F' is Friday, 'S' is Saturday, and 'U' is Sunday.
   */
  protected static List<DayOfWeek> populateDaysOfWeek(String daysString) {
    List<DayOfWeek> days = new ArrayList<>();

    for (char c : daysString.toUpperCase().toCharArray()) {
      switch (c) {
        case 'M':
          days.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          days.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          days.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          days.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          days.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          days.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          days.add(DayOfWeek.SUNDAY);
          break;
        default:
          throw new IllegalArgumentException("Invalid day character: " + c);
      }
    }
    return days;
  }
}
