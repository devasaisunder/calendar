package calendar.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Month view panel component displaying the calendar grid.
 */
public class MonthViewPanel extends JPanel {
  private static final String[] DAYS_OF_WEEK = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  private static final DateTimeFormatter MONTH_YEAR_FORMAT =
      DateTimeFormatter.ofPattern("MMMM yyyy");
  
  private JPanel calendarPanel;
  private LocalDate currentMonth;
  private LocalDate selectedDate;
  private Map<LocalDate, List<EventInfo>> currentMonthEvents;
  private Map<String, Color> calendarColors;
  private Color currentCalendarColor;
  private java.util.function.Consumer<LocalDate> dateSelectionCallback;

  /**
   * Constructor for MonthViewPanel.
   */
  public MonthViewPanel() {
    super(new BorderLayout());
    setBorder(BorderFactory.createTitledBorder("Month View"));
    calendarPanel = new JPanel(new GridLayout(0, 7));
    add(calendarPanel, BorderLayout.CENTER);
    this.currentMonthEvents = new java.util.HashMap<>();
    this.calendarColors = new java.util.HashMap<>();
    this.selectedDate = java.time.LocalDate.now();
    this.currentMonth = java.time.LocalDate.now();
  }

  /**
   * Sets the callback for date selection.
   *
   * @param callback the callback to call when a date is selected
   */
  public void setDateSelectionCallback(java.util.function.Consumer<LocalDate> callback) {
    this.dateSelectionCallback = callback;
  }

  /**
   * Sets the selected date.
   *
   * @param date the selected date
   */
  public void setSelectedDate(LocalDate date) {
    this.selectedDate = date;
    // Refresh to update highlighting if we're viewing the same month
    if (currentMonth != null && date.getYear() == currentMonth.getYear()
        && date.getMonth() == currentMonth.getMonth()) {
      refreshMonthView();
    }
  }

  /**
   * Sets the calendar color for the active calendar.
   *
   * @param color the calendar color
   */
  public void setCalendarColor(Color color) {
    this.currentCalendarColor = color;
  }

  /**
   * Updates the month view.
   *
   * @param month the month to display
   * @param events the events for the month
   */
  public void updateMonthView(LocalDate month, Map<LocalDate, List<EventInfo>> events) {
    this.currentMonth = month;
    this.currentMonthEvents = events != null ? events : new java.util.HashMap<>();
    refreshMonthView();
  }

  /**
   * Updates the selected date (for highlighting).
   *
   * @param date the newly selected date
   */
  public void updateSelectedDate(LocalDate date) {
    this.selectedDate = date;
    // Refresh to update highlighting
    refreshMonthView();
  }

  private void refreshMonthView() {
    if (calendarPanel == null) {
      return;
    }
    calendarPanel.removeAll();

    // Add day headers
    for (String day : DAYS_OF_WEEK) {
      JLabel header = new JLabel(day, SwingConstants.CENTER);
      header.setBorder(BorderFactory.createLineBorder(Color.GRAY));
      header.setOpaque(true);
      header.setBackground(Color.LIGHT_GRAY);
      calendarPanel.add(header);
    }

    // Get first day of month and adjust to start of week (Sunday = 0)
    LocalDate firstDay = currentMonth.withDayOfMonth(1);
    int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Convert to 0-6 (Sun-Sat)

    // Add empty cells for days before month starts
    for (int i = 0; i < dayOfWeek; i++) {
      calendarPanel.add(new JPanel());
    }

    // Add day cells
    int daysInMonth = currentMonth.lengthOfMonth();
    Color calendarColor = currentCalendarColor != null ? currentCalendarColor : Color.WHITE;

    for (int day = 1; day <= daysInMonth; day++) {
      LocalDate date = currentMonth.withDayOfMonth(day);
      JPanel dayPanel = createDayButton(date, currentMonthEvents, calendarColor);
      calendarPanel.add(dayPanel);
    }

    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  private JPanel createDayButton(LocalDate date, Map<LocalDate, List<EventInfo>> allEvents,
                                    Color calendarColor) {
    JPanel dayPanel = new JPanel(new BorderLayout());
    dayPanel.setPreferredSize(new Dimension(100, 100));
    dayPanel.setBorder(BorderFactory.createLineBorder(
        date.equals(selectedDate) ? Color.BLUE : Color.GRAY,
        date.equals(selectedDate) ? 3 : 1));

    // Day number label at top
    JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER);
    dayLabel.setFont(dayLabel.getFont().deriveFont(14f));
    if (date.equals(LocalDate.now())) {
      dayLabel.setForeground(Color.RED);
    }
    dayPanel.add(dayLabel, BorderLayout.NORTH);

    // Events panel
    JPanel eventsPanel = new JPanel();
    eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
    eventsPanel.setOpaque(false);
    eventsPanel.setBorder(new EmptyBorder(2, 2, 2, 2));

    List<EventInfo> dayEvents = allEvents.getOrDefault(date, new ArrayList<>());
    int maxEventsToShow = 3; // Show max 3 events, then "X more"
    int eventsToShow = Math.min(dayEvents.size(), maxEventsToShow);

    for (int i = 0; i < eventsToShow; i++) {
      EventInfo event = dayEvents.get(i);
      String eventText = event.getSubject();
      // Truncate long event names
      if (eventText.length() > 20) {
        eventText = eventText.substring(0, 17) + "...";
      }
      JLabel eventLabel = new JLabel(eventText);
      eventLabel.setOpaque(true);
      eventLabel.setBackground(new Color(255, 235, 59)); // Yellow like in the image
      eventLabel.setForeground(Color.BLACK);
      eventLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
      eventLabel.setFont(eventLabel.getFont().deriveFont(9f));
      eventLabel.setAlignmentX(0.0f);
      eventLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
      eventsPanel.add(eventLabel);
    }

    if (dayEvents.size() > maxEventsToShow) {
      JLabel moreLabel = new JLabel((dayEvents.size() - maxEventsToShow) + " more");
      moreLabel.setFont(moreLabel.getFont().deriveFont(8f));
      moreLabel.setForeground(Color.GRAY);
      eventsPanel.add(moreLabel);
    }

    dayPanel.add(eventsPanel, BorderLayout.CENTER);
    dayPanel.setBackground(Color.WHITE);
//    dayPanel.setOpaque(true);

    // Make the panel clickable
    dayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        if (dateSelectionCallback != null) {
          dateSelectionCallback.accept(date);
        }
      }
    });

    return dayPanel;
  }

  /**
   * Gets the day panel at the specified date.
   * Used for adding click listeners.
   *
   * @param date the date
   * @return the day panel, or null if not found
   */
  public JPanel getDayPanel(LocalDate date) {
    // This is a helper method - the actual click handling is done in the main view
    return null;
  }
}

