package calendar.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Implementation of CalendarGuiView using Java Swing.
 * Provides a month view calendar with event management capabilities.
 * The view only handles UI and emits events through ViewListener interface.
 */
public class CalendarGuiViewImpl extends JFrame implements CalendarGuiView {
  private LocalDate currentMonth;
  private LocalDate selectedDate;
  private Map<String, Color> calendarColors;
  private int colorIndex;
  private final List<ViewListener> listeners;
  private Map<LocalDate, List<EventInfo>> currentMonthEvents;
  private List<EventInfo> currentDayEvents;
  private Features featuresForDialogs; // For dialogs to query data only
  
  // Panel components
  private TopPanel topPanel;
  private MonthViewPanel monthViewPanel;
  private DayViewPanel dayViewPanel;
  private BottomPanel bottomPanel;
  
  private static final Color[] CALENDAR_COLORS = {
      new Color(173, 216, 230), // Light Blue
      new Color(255, 182, 193), // Light Pink
      new Color(144, 238, 144), // Light Green
      new Color(255, 218, 185), // Peach
      new Color(221, 160, 221), // Plum
      new Color(176, 224, 230), // Powder Blue
      new Color(255, 228, 181)  // Moccasin
  };

  /**
   * Constructor for CalendarGuiViewImpl.
   */
  public CalendarGuiViewImpl() {
    this.currentMonth = LocalDate.now();
    this.selectedDate = LocalDate.now();
    this.calendarColors = new HashMap<>();
    this.colorIndex = 0;
    this.listeners = new ArrayList<>();
    this.currentMonthEvents = new HashMap<>();
    this.currentDayEvents = new ArrayList<>();
    initializeGui();
  }

  /**
   * Adds a ViewListener to receive events from this view.
   *
   * @param listener the ViewListener to add
   */
  public void addViewListener(ViewListener listener) {
    listeners.add(Objects.requireNonNull(listener));
  }

  private void initializeGui() {
    setTitle("Calendar Application");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Top panel with calendar selection and navigation
    topPanel = new TopPanel();
    setupTopPanelListeners();
    add(topPanel, BorderLayout.NORTH);

    // Center panel with month view and day view
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    
    monthViewPanel = new MonthViewPanel();
    setupMonthViewListeners();
    centerPanel.add(monthViewPanel, BorderLayout.CENTER);
    
    dayViewPanel = new DayViewPanel();
    centerPanel.add(dayViewPanel, BorderLayout.EAST);
    
    add(centerPanel, BorderLayout.CENTER);

    // Bottom panel with action buttons
    bottomPanel = new BottomPanel();
    setupBottomPanelListeners();
    add(bottomPanel, BorderLayout.SOUTH);

    pack();
    setSize(1000, 700);
    setLocationRelativeTo(null);
  }

  private void setupTopPanelListeners() {
    // Calendar combo box listener
    topPanel.getCalendarComboBox().addActionListener(e -> {
      String selected = (String) topPanel.getCalendarComboBox().getSelectedItem();
      if (selected != null) {
        emitSwitchCalendar(selected);
      }
    });

    // Create calendar button
    topPanel.getCreateCalendarButton().addActionListener(e -> {
      CalendarDialog.showCreateDialog(this, (name, timezone) -> {
        emitCreateCalendar(name, timezone);
      }, this::emitRefresh);
    });

    // Edit calendar button
    topPanel.getEditCalendarButton().addActionListener(e -> {
      if (featuresForDialogs != null) {
        String activeCalendarName = featuresForDialogs.getActiveCalendarName();
        String currentTimezone = featuresForDialogs.getActiveCalendarTimezone();
        CalendarDialog.showEditDialog(this, () -> activeCalendarName, () -> currentTimezone,
            (calendarName, property, newValue, resultHandler) -> {
              emitEditCalendar(calendarName, property, newValue);
              resultHandler.accept(null); // Assume success, controller will show errors
            }, this::emitRefresh);
      }
    });

    // Previous month button
    topPanel.getPrevButton().addActionListener(e -> {
      currentMonth = currentMonth.minusMonths(1);
      topPanel.updateMonthYear(currentMonth);
      emitPreviousMonth();
    });

    // Next month button
    topPanel.getNextButton().addActionListener(e -> {
      currentMonth = currentMonth.plusMonths(1);
      topPanel.updateMonthYear(currentMonth);
      emitNextMonth();
    });
  }

  private void setupMonthViewListeners() {
    // Set up date selection callback
    monthViewPanel.setDateSelectionCallback(date -> {
      selectedDate = date;
      topPanel.updateSelectedDate(selectedDate);
      emitDateSelected(date);
    });
  }

  private void setupBottomPanelListeners() {
    // Create event button
    bottomPanel.getCreateEventButton().addActionListener(e -> {
      EventDialog.showCreateDialog(this, selectedDate, 
          (subject, start, end, desc, loc, status, repeat, days, endDate, resultHandler) -> {
            // Emit event - controller will handle
            emitCreateEvent(subject, start, end, desc, loc, status, repeat, days, endDate);
            // Controller will handle result through refresh
            resultHandler.accept(null); // Assume success, controller will show errors
          }, this::emitRefresh);
    });

    // Edit event button
    bottomPanel.getEditEventButton().addActionListener(e -> {
      if (featuresForDialogs != null) {
        EventDialog.showEditDialog(this, featuresForDialogs::getEventsForDay, selectedDate,
            (prop, subj, start, end, val, scope, resultHandler) -> {
              emitEditEvent(prop, subj, start, end, val, scope);
              resultHandler.accept("Success");
            },
            (subj, start, end, scope, resultHandler) -> {
              emitDeleteEvent(subj, start, end, scope);
              resultHandler.accept("Success");
            },
            this::emitRefresh);
      }
    });

    // Delete event button
    bottomPanel.getDeleteEventButton().addActionListener(e -> {
      if (featuresForDialogs != null) {
        EventDialog.showEditDialog(this, featuresForDialogs::getEventsForDay, selectedDate,
            (prop, subj, start, end, val, scope, resultHandler) -> {
              emitEditEvent(prop, subj, start, end, val, scope);
              resultHandler.accept("Success");
            },
            (subj, start, end, scope, resultHandler) -> {
              emitDeleteEvent(subj, start, end, scope);
              resultHandler.accept("Success");
            },
            this::emitRefresh);
      }
    });

    // Export button
    bottomPanel.getExportButton().addActionListener(e -> {
      showExportDialog();
    });
  }

  private void updateCalendarComboBox(List<String> calendarNames, String activeCalendarName) {
    if (topPanel == null) {
      return;
    }
    
    // Update calendar colors
    for (String name : calendarNames) {
      if (!calendarColors.containsKey(name)) {
        calendarColors.put(name, CALENDAR_COLORS[colorIndex % CALENDAR_COLORS.length]);
        colorIndex++;
      }
    }
    
    topPanel.updateCalendarComboBox(calendarNames, activeCalendarName);
    
    // Update month view calendar color
    if (activeCalendarName != null) {
      Color calendarColor = calendarColors.getOrDefault(activeCalendarName, Color.WHITE);
      monthViewPanel.setCalendarColor(calendarColor);
    }
  }

  // Event emission methods
  private void emitSwitchCalendar(String calendarName) {
    for (ViewListener listener : listeners) {
      listener.handleSwitchCalendar(calendarName);
    }
  }

  private void emitCreateCalendar(String name, String timezone) {
    for (ViewListener listener : listeners) {
      listener.handleCreateCalendar(name, timezone);
    }
  }

  private void emitEditCalendar(String calendarName, String property, String newValue) {
    for (ViewListener listener : listeners) {
      listener.handleEditCalendar(calendarName, property, newValue);
    }
  }

  private void emitPreviousMonth() {
    for (ViewListener listener : listeners) {
      listener.handlePreviousMonth();
    }
  }

  private void emitNextMonth() {
    for (ViewListener listener : listeners) {
      listener.handleNextMonth();
    }
  }

  private void emitDateSelected(LocalDate date) {
    for (ViewListener listener : listeners) {
      listener.handleDateSelected(date);
    }
  }

  private void emitCreateEvent(String subject, java.time.LocalDateTime startDateTime,
                               java.time.LocalDateTime endDateTime, String description,
                               String location, String status, boolean isRepeating,
                               String repeatDays, LocalDate repeatEndDate) {
    for (ViewListener listener : listeners) {
      listener.handleCreateEvent(subject, startDateTime, endDateTime, description,
          location, status, isRepeating, repeatDays, repeatEndDate);
    }
  }

  private void emitEditEvent(String property, String subject,
                             java.time.LocalDateTime startDateTime,
                             java.time.LocalDateTime endDateTime, String newValue, String scope) {
    for (ViewListener listener : listeners) {
      listener.handleEditEvent(property, subject, startDateTime, endDateTime, newValue, scope);
    }
  }

  private void emitDeleteEvent(String subject, java.time.LocalDateTime startDateTime,
                               java.time.LocalDateTime endDateTime, String scope) {
    for (ViewListener listener : listeners) {
      listener.handleDeleteEvent(subject, startDateTime, endDateTime, scope);
    }
  }

  private void emitExportCalendar(String fileName) {
    for (ViewListener listener : listeners) {
      listener.handleExportCalendar(fileName);
    }
  }

  private void emitRefresh() {
    for (ViewListener listener : listeners) {
      listener.handleRefresh();
    }
  }

  private void showExportDialog() {
    javax.swing.JDialog dialog = new javax.swing.JDialog(this, "Export Calendar", true);
    dialog.setSize(400, 150);
    dialog.setLayout(new BorderLayout());

    javax.swing.JPanel panel = new javax.swing.JPanel();
    panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    panel.add(new javax.swing.JLabel("File Name (with extension .csv or .ical):"));
    javax.swing.JTextField fileNameField = new javax.swing.JTextField("calendar_export.csv");
    panel.add(fileNameField);

    javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
    javax.swing.JButton exportButton = new javax.swing.JButton("Export");
    exportButton.addActionListener(e -> {
      try {
        String fileName = fileNameField.getText().trim();
        if (fileName.isEmpty()) {
          renderError("File name cannot be empty");
          return;
        }
        emitExportCalendar(fileName);
        dialog.dispose();
      } catch (Exception ex) {
        renderError("Failed to export: " + ex.getMessage());
      }
    });
    buttonPanel.add(exportButton);

    javax.swing.JButton cancelButton = new javax.swing.JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());
    buttonPanel.add(cancelButton);

    panel.add(buttonPanel);
    dialog.add(panel);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    // Deprecated - use addViewListener instead
    // Kept for backward compatibility
  }

  /**
   * Gets the current month being displayed.
   *
   * @return the current month
   */
  public LocalDate getCurrentMonth() {
    return currentMonth;
  }

  /**
   * Gets the selected date.
   *
   * @return the selected date
   */
  public LocalDate getSelectedDate() {
    return selectedDate;
  }

  /**
   * Gets the top panel.
   *
   * @return the top panel
   */
  public TopPanel getTopPanel() {
    return topPanel;
  }

  /**
   * Sets Features interface for dialogs to use for querying data.
   * This is set by the controller and used only for read operations in dialogs.
   *
   * @param features the Features interface
   */
  public void setFeaturesForDialogs(Features features) {
    this.featuresForDialogs = features;
  }

  @Override
  public void display() {
    setVisible(true);
    // Initial refresh will be triggered by addFeatures
  }

  @Override
  public void setCalendars(List<String> calendarNames, String activeCalendarName) {
    updateCalendarComboBox(calendarNames, activeCalendarName);
  }

  @Override
  public void setMonthEvents(LocalDate month, Map<LocalDate, List<EventInfo>> events) {
    if (month.equals(currentMonth)) {
      this.currentMonthEvents = events;
      monthViewPanel.updateMonthView(month, events);
      monthViewPanel.updateSelectedDate(selectedDate);
    }
  }

  @Override
  public void setDayEvents(LocalDate date, List<EventInfo> events) {
    if (date.equals(selectedDate)) {
      this.currentDayEvents = events;
      dayViewPanel.updateDayView(date, events);
    }
  }

  @Override
  public void render(String message) {
    // Removed - no popups on success
    // Only show errors
  }

  @Override
  public void renderError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error",
        JOptionPane.ERROR_MESSAGE);
  }
}
