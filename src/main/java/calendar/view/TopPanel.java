package calendar.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Top panel component for calendar selection, navigation, and timezone display.
 */
public class TopPanel extends JPanel {
  private static final DateTimeFormatter MONTH_YEAR_FORMAT =
      DateTimeFormatter.ofPattern("MMMM yyyy");
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  
  private JComboBox<String> calendarComboBox;
  private JLabel monthYearLabel;
  private JLabel selectedDateLabel;
  private JLabel timezoneLabel;
  private Features features;
  private LocalDate currentMonth;
  private LocalDate selectedDate;
  private JButton createCalendarButton;
  private JButton editCalendarButton;
  private JButton prevButton;
  private JButton nextButton;

  /**
   * Constructor for TopPanel.
   */
  public TopPanel() {
    super(new BorderLayout());
    setBorder(new EmptyBorder(10, 10, 10, 10));
    this.currentMonth = LocalDate.now();
    this.selectedDate = LocalDate.now();
    initializeComponents();
  }

  private void initializeComponents() {
    // Calendar selection panel (left)
    JPanel calendarSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    calendarSelectPanel.add(new JLabel("Calendar:"));
    calendarComboBox = new JComboBox<>();
    calendarSelectPanel.add(calendarComboBox);

    createCalendarButton = new JButton("New Calendar");
    calendarSelectPanel.add(createCalendarButton);

    editCalendarButton = new JButton("Edit Calendar");
    calendarSelectPanel.add(editCalendarButton);

    add(calendarSelectPanel, BorderLayout.WEST);

    // Month navigation panel (center)
    JPanel navPanel = new JPanel(new FlowLayout());
    prevButton = new JButton("◀ Previous");
    navPanel.add(prevButton);

    monthYearLabel = new JLabel("", SwingConstants.CENTER);
    monthYearLabel.setFont(monthYearLabel.getFont().deriveFont(18f));
    monthYearLabel.setText(currentMonth.format(MONTH_YEAR_FORMAT));
    navPanel.add(monthYearLabel);

    nextButton = new JButton("Next ▶");
    navPanel.add(nextButton);

    add(navPanel, BorderLayout.CENTER);

    // Right panel with selected date and timezone
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    selectedDateLabel = new JLabel("Selected: " + selectedDate.format(DATE_FORMAT));
    selectedDateLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
    rightPanel.add(selectedDateLabel);

    timezoneLabel = new JLabel("Timezone: -");
    rightPanel.add(timezoneLabel);

    add(rightPanel, BorderLayout.EAST);
  }

  /**
   * Sets the Features interface for this panel.
   *
   * @param features the Features interface
   */
  public void setFeatures(Features features) {
    this.features = features;
  }

  /**
   * Gets the calendar combo box.
   *
   * @return the calendar combo box
   */
  public JComboBox<String> getCalendarComboBox() {
    return calendarComboBox;
  }

  /**
   * Gets the create calendar button.
   *
   * @return the create calendar button
   */
  public JButton getCreateCalendarButton() {
    return createCalendarButton;
  }

  /**
   * Gets the edit calendar button.
   *
   * @return the edit calendar button
   */
  public JButton getEditCalendarButton() {
    return editCalendarButton;
  }

  /**
   * Gets the previous month button.
   *
   * @return the previous month button
   */
  public JButton getPrevButton() {
    return prevButton;
  }

  /**
   * Gets the next month button.
   *
   * @return the next month button
   */
  public JButton getNextButton() {
    return nextButton;
  }

  /**
   * Updates the calendar combo box.
   *
   * @param calendarNames the list of calendar names
   * @param activeCalendarName the active calendar name
   */
  public void updateCalendarComboBox(List<String> calendarNames, String activeCalendarName) {
    if (calendarComboBox == null) {
      return;
    }
    calendarComboBox.removeAllItems();
    for (String name : calendarNames) {
      calendarComboBox.addItem(name);
    }
    if (activeCalendarName != null && calendarNames.contains(activeCalendarName)) {
      calendarComboBox.setSelectedItem(activeCalendarName);
    }
  }

  /**
   * Updates the month year label.
   *
   * @param month the current month
   */
  public void updateMonthYear(LocalDate month) {
    this.currentMonth = month;
    if (monthYearLabel != null) {
      monthYearLabel.setText(month.format(MONTH_YEAR_FORMAT));
    }
  }

  /**
   * Updates the selected date label.
   *
   * @param date the selected date
   */
  public void updateSelectedDate(LocalDate date) {
    this.selectedDate = date;
    if (selectedDateLabel != null) {
      selectedDateLabel.setText("Selected: " + date.format(DATE_FORMAT));
    }
  }

  /**
   * Updates the timezone label.
   *
   * @param timezone the timezone string
   */
  public void updateTimezone(String timezone) {
    if (timezoneLabel != null) {
      timezoneLabel.setText("Timezone: " + (timezone != null ? timezone : "-"));
    }
  }

  /**
   * Gets the current month.
   *
   * @return the current month
   */
  public LocalDate getCurrentMonth() {
    return currentMonth;
  }
}

