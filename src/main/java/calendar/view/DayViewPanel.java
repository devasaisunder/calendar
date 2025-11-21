package calendar.view;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Day view panel component displaying events for the selected day.
 */
public class DayViewPanel extends JPanel {
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
  
  private JLabel dayLabel;
  private JTextArea dayEventsArea;
  private LocalDate selectedDate;
  private List<EventInfo> currentDayEvents;

  /**
   * Constructor for DayViewPanel.
   */
  public DayViewPanel() {
    super(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("Day View"));
    setPreferredSize(new java.awt.Dimension(300, 0));
    initializeComponents();
    this.currentDayEvents = new java.util.ArrayList<>();
  }

  private void initializeComponents() {
    dayLabel = new JLabel("Selected Date: " + LocalDate.now().format(DATE_FORMAT));
    dayLabel.setFont(dayLabel.getFont().deriveFont(14f));

    dayEventsArea = new JTextArea(15, 25);
    dayEventsArea.setEditable(false);
    dayEventsArea.setLineWrap(true);
    dayEventsArea.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(dayEventsArea);

    add(dayLabel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
  }

  /**
   * Updates the day view with events.
   *
   * @param date the date to display
   * @param events the events for that day
   */
  public void updateDayView(LocalDate date, List<EventInfo> events) {
    this.selectedDate = date;
    this.currentDayEvents = events != null ? events : new java.util.ArrayList<>();
    refreshDayView();
  }

  private void refreshDayView() {
    if (dayEventsArea == null) {
      return;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("Events on ").append(selectedDate.format(DATE_FORMAT)).append("\n\n");

    if (currentDayEvents == null || currentDayEvents.isEmpty()) {
      sb.append("No events scheduled.");
    } else {
      for (EventInfo event : currentDayEvents) {
        sb.append("• ").append(event.getSubject()).append("\n");
        if (!event.isAllDay()) {
          sb.append("  Time: ")
              .append(event.getStartDateTime().toLocalTime().format(TIME_FORMAT))
              .append(" - ")
              .append(event.getEndDateTime().toLocalTime().format(TIME_FORMAT))
              .append("\n");
        } else {
          sb.append("  All Day Event\n");
        }
        if (event.getLocation() != null && !event.getLocation().equals("UNKNOWN")) {
          sb.append("  Location: ").append(event.getLocation()).append("\n");
        }
        if (event.getDescription() != null && !event.getDescription().isEmpty()
            && !event.getDescription().equals("No description given")) {
          sb.append("  Description: ").append(event.getDescription()).append("\n");
        }
        sb.append("\n");
      }
    }

    dayEventsArea.setText(sb.toString());
    
    if (dayLabel != null) {
      dayLabel.setText("Selected Date: " + selectedDate.format(DATE_FORMAT));
    }
  }
}

