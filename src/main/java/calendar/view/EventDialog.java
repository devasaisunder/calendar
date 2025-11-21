package calendar.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;

/**
 * Dialog for creating and editing events.
 */
public class EventDialog {
  private static final java.time.format.DateTimeFormatter TIME_FORMAT =
      java.time.format.DateTimeFormatter.ofPattern("HH:mm");

  /**
   * Functional interface for creating events.
   */
  @FunctionalInterface
  public interface CreateEventCallback {
    void create(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                String description, String location, String status,
                boolean isRepeating, String repeatDays, LocalDate repeatEndDate,
                java.util.function.Consumer<String> resultHandler);
  }

  /**
   * Shows a dialog for creating a new event.
   *
   * @param parent the parent frame
   * @param selectedDate the currently selected date
   * @param onCreate callback to handle event creation
   * @param onSuccess callback when dialog should refresh
   */
  public static void showCreateDialog(JFrame parent, LocalDate selectedDate,
                                      CreateEventCallback onCreate, Runnable onSuccess) {
    JDialog dialog = new JDialog(parent, "Create Event", true);
    dialog.setSize(500, 650);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Subject
    panel.add(new JLabel("Subject:"));
    JTextField subjectField = new JTextField(30);
    panel.add(subjectField);

    // Date picker
    panel.add(new JLabel("Date:"));
    SpinnerDateModel dateModel = new SpinnerDateModel(
        java.sql.Date.valueOf(selectedDate),
        null, null, java.util.Calendar.DAY_OF_MONTH);
    JSpinner dateSpinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
    dateSpinner.setEditor(dateEditor);
    panel.add(dateSpinner);

    // All day checkbox
    JPanel allDayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JCheckBox allDayCheck = new JCheckBox("All Day Event");
    allDayPanel.add(allDayCheck);
    panel.add(allDayPanel);

    // Start time
    panel.add(new JLabel("Start Time:"));
    java.util.Calendar startCal = new java.util.GregorianCalendar();
    startCal.set(java.util.Calendar.HOUR_OF_DAY, 9);
    startCal.set(java.util.Calendar.MINUTE, 0);
    startCal.set(java.util.Calendar.SECOND, 0);
    startCal.set(java.util.Calendar.MILLISECOND, 0);
    SpinnerDateModel startTimeModel = new SpinnerDateModel(
        startCal.getTime(), null, null, java.util.Calendar.HOUR_OF_DAY);
    JSpinner startTimeSpinner = new JSpinner(startTimeModel);
    JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
    startTimeSpinner.setEditor(startTimeEditor);
    panel.add(startTimeSpinner);

    // End time
    panel.add(new JLabel("End Time:"));
    java.util.Calendar endCal = new java.util.GregorianCalendar();
    endCal.set(java.util.Calendar.HOUR_OF_DAY, 10);
    endCal.set(java.util.Calendar.MINUTE, 0);
    endCal.set(java.util.Calendar.SECOND, 0);
    endCal.set(java.util.Calendar.MILLISECOND, 0);
    SpinnerDateModel endTimeModel = new SpinnerDateModel(
        endCal.getTime(), null, null, java.util.Calendar.HOUR_OF_DAY);
    JSpinner endTimeSpinner = new JSpinner(endTimeModel);
    JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
    endTimeSpinner.setEditor(endTimeEditor);
    panel.add(endTimeSpinner);

    // Description
    panel.add(new JLabel("Description:"));
    JTextArea descriptionArea = new JTextArea(3, 30);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    JScrollPane descScroll = new JScrollPane(descriptionArea);
    panel.add(descScroll);

    // Location
    panel.add(new JLabel("Location:"));
    JComboBox<String> locationCombo = new JComboBox<>(
        new String[]{"UNKNOWN", "PHYSICAL", "ONLINE"});
    panel.add(locationCombo);

    // Status
    panel.add(new JLabel("Status:"));
    JComboBox<String> statusCombo = new JComboBox<>(
        new String[]{"UNKNOWN", "PUBLIC", "PRIVATE"});
    panel.add(statusCombo);

    // Recurring event options
    panel.add(new JLabel("Repeat:"));
    JCheckBox recurringCheck = new JCheckBox("Repeat");
    panel.add(recurringCheck);

    // Day of week checkboxes
    JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    daysPanel.add(new JLabel("Repeat on:"));
    JCheckBox monCheck = new JCheckBox("Mon");
    JCheckBox tueCheck = new JCheckBox("Tue");
    JCheckBox wedCheck = new JCheckBox("Wed");
    JCheckBox thuCheck = new JCheckBox("Thu");
    JCheckBox friCheck = new JCheckBox("Fri");
    JCheckBox satCheck = new JCheckBox("Sat");
    JCheckBox sunCheck = new JCheckBox("Sun");
    daysPanel.add(monCheck);
    daysPanel.add(tueCheck);
    daysPanel.add(wedCheck);
    daysPanel.add(thuCheck);
    daysPanel.add(friCheck);
    daysPanel.add(satCheck);
    daysPanel.add(sunCheck);
    daysPanel.setEnabled(false);
    panel.add(daysPanel);

    // End date for recurring events
    panel.add(new JLabel("End Date (for recurring events):"));
    SpinnerDateModel endDateModel = new SpinnerDateModel(
        java.sql.Date.valueOf(selectedDate.plusMonths(1)),
        null, null, java.util.Calendar.DAY_OF_MONTH);
    JSpinner endDateSpinner = new JSpinner(endDateModel);
    JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
    endDateSpinner.setEditor(endDateEditor);
    endDateSpinner.setEnabled(false);
    panel.add(endDateSpinner);

    // Enable/disable time fields based on all day
    allDayCheck.addActionListener(e -> {
      boolean enabled = !allDayCheck.isSelected();
      startTimeSpinner.setEnabled(enabled);
      endTimeSpinner.setEnabled(enabled);
    });

    // Enable/disable recurring options
    recurringCheck.addActionListener(e -> {
      boolean enabled = recurringCheck.isSelected();
      daysPanel.setEnabled(enabled);
      for (java.awt.Component comp : daysPanel.getComponents()) {
        if (comp instanceof JCheckBox) {
          comp.setEnabled(enabled);
        }
      }
      endDateSpinner.setEnabled(enabled);
    });

    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton createButton = new JButton("Create");
    createButton.addActionListener(e -> {
      try {
        String subject = subjectField.getText().trim();
        if (subject.isEmpty()) {
          javax.swing.JOptionPane.showMessageDialog(dialog, "Subject cannot be empty",
              "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
          return;
        }

        // Get date from spinner
        java.util.Date dateValue = (java.util.Date) dateSpinner.getValue();
        LocalDate date = new java.sql.Date(dateValue.getTime()).toLocalDate();

        boolean allDay = allDayCheck.isSelected();
        LocalTime startTime;
        LocalTime endTime;

        if (allDay) {
          startTime = LocalTime.of(8, 0);
          endTime = LocalTime.of(17, 0);
        } else {
          java.util.Date startTimeValue = (java.util.Date) startTimeSpinner.getValue();
          java.util.Date endTimeValue = (java.util.Date) endTimeSpinner.getValue();
          java.util.Calendar cal = new java.util.GregorianCalendar();
          cal.setTime(startTimeValue);
          startTime = LocalTime.of(cal.get(java.util.Calendar.HOUR_OF_DAY),
              cal.get(java.util.Calendar.MINUTE));
          cal.setTime(endTimeValue);
          endTime = LocalTime.of(cal.get(java.util.Calendar.HOUR_OF_DAY),
              cal.get(java.util.Calendar.MINUTE));
        }

        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        if (endDateTime.isBefore(startDateTime)) {
          javax.swing.JOptionPane.showMessageDialog(dialog, "End time cannot be before start time",
              "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
          return;
        }

        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
          description = "No description given";
        }

        String location = (String) locationCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();

        boolean isRepeating = recurringCheck.isSelected();
        String repeatDays = null;
        LocalDate repeatEndDate = null;

        if (isRepeating) {
          // Build days string from checkboxes
          StringBuilder daysBuilder = new StringBuilder();
          if (monCheck.isSelected()) {
            daysBuilder.append("M");
          }
          if (tueCheck.isSelected()) {
            daysBuilder.append("T");
          }
          if (wedCheck.isSelected()) {
            daysBuilder.append("W");
          }
          if (thuCheck.isSelected()) {
            daysBuilder.append("R");
          }
          if (friCheck.isSelected()) {
            daysBuilder.append("F");
          }
          if (satCheck.isSelected()) {
            daysBuilder.append("S");
          }
          if (sunCheck.isSelected()) {
            daysBuilder.append("U");
          }

          repeatDays = daysBuilder.toString();
          if (repeatDays.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(dialog,
                "Please select at least one day for recurring events", "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
          }

          // Get end date from spinner
          java.util.Date endDateValue = (java.util.Date) endDateSpinner.getValue();
          repeatEndDate = new java.sql.Date(endDateValue.getTime()).toLocalDate();
        }

        if (onCreate != null) {
          onCreate.create(subject, startDateTime, endDateTime, description, location, status,
              isRepeating, repeatDays, repeatEndDate, (result) -> {
                if (result != null && (result.contains("Error") || result.contains("Failed"))) {
                  javax.swing.JOptionPane.showMessageDialog(dialog, result, "Error",
                      javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                  dialog.dispose();
                  if (onSuccess != null) {
                    onSuccess.run();
                  }
                }
              });
        }
      } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(dialog, "Failed to create event: " + ex.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
      }
    });
    buttonPanel.add(createButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());
    buttonPanel.add(cancelButton);

    JScrollPane scrollPane = new JScrollPane(panel);
    dialog.add(scrollPane, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }

  /**
   * Functional interface for getting events for a day.
   */
  @FunctionalInterface
  public interface GetEventsCallback {
    List<EventInfo> getEventsForDay(LocalDate date);
  }

  /**
   * Functional interface for editing events.
   */
  @FunctionalInterface
  public interface EditEventCallback {
    void edit(String property, String subject, LocalDateTime startDateTime,
              LocalDateTime endDateTime, String newValue, String scope,
              java.util.function.Consumer<String> resultHandler);
  }

  /**
   * Functional interface for deleting events.
   */
  @FunctionalInterface
  public interface DeleteEventCallback {
    void delete(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                String scope, java.util.function.Consumer<String> resultHandler);
  }

  /**
   * Shows a dialog for editing or deleting an event.
   *
   * @param parent the parent frame
   * @param getEvents callback to get events for a day
   * @param selectedDate the currently selected date
   * @param onEdit callback to handle event editing
   * @param onDelete callback to handle event deletion
   * @param onSuccess callback when event is edited/deleted successfully
   */
  public static void showEditDialog(JFrame parent, GetEventsCallback getEvents,
                                    LocalDate selectedDate, EditEventCallback onEdit,
                                    DeleteEventCallback onDelete, Runnable onSuccess) {
    if (getEvents == null) {
      javax.swing.JOptionPane.showMessageDialog(parent, "Features not available", "Error",
          javax.swing.JOptionPane.ERROR_MESSAGE);
      return;
    }

    List<EventInfo> events = getEvents.getEventsForDay(selectedDate);
    if (events == null || events.isEmpty()) {
      javax.swing.JOptionPane.showMessageDialog(parent, "No events on selected date", "Error",
          javax.swing.JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Show event selection dialog
    String[] eventStrings = new String[events.size()];
    for (int i = 0; i < events.size(); i++) {
      EventInfo event = events.get(i);
      String timeStr = event.isAllDay() ? "All Day"
          : event.getStartDateTime().toLocalTime().format(TIME_FORMAT);
      eventStrings[i] = timeStr + " - " + event.getSubject();
    }

    String selected = (String) javax.swing.JOptionPane.showInputDialog(
        parent,
        "Select event to edit or delete:",
        "Event Actions",
        javax.swing.JOptionPane.QUESTION_MESSAGE,
        null,
        eventStrings,
        eventStrings[0]);

    if (selected == null) {
      return;
    }

    int index = -1;
    for (int i = 0; i < eventStrings.length; i++) {
      if (eventStrings[i].equals(selected)) {
        index = i;
        break;
      }
    }

    if (index == -1) {
      return;
    }

    EventInfo selectedEvent = events.get(index);
    showEventActionDialog(parent, selectedEvent, onEdit, onDelete, onSuccess);
  }

  /**
   * Shows a dialog to choose between edit and delete actions.
   *
   * @param parent the parent frame
   * @param event the selected event
   * @param onEdit callback to handle event editing
   * @param onDelete callback to handle event deletion
   * @param onSuccess callback when action is completed successfully
   */
  private static void showEventActionDialog(JFrame parent, EventInfo event,
                                            EditEventCallback onEdit, DeleteEventCallback onDelete,
                                            Runnable onSuccess) {
    JDialog dialog = new JDialog(parent, "Event Actions", true);
    dialog.setSize(400, 200);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    panel.add(new JLabel("Event: " + event.getSubject()));
    panel.add(new JLabel("Date: " + event.getStartDateTime().toLocalDate().toString()));

    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton editButton = new JButton("Edit Event");
    editButton.addActionListener(e -> {
      dialog.dispose();
      showEditEventDetailsDialog(parent, event, onEdit, onSuccess);
    });
    buttonPanel.add(editButton);

    JButton deleteButton = new JButton("Delete Event");
    deleteButton.addActionListener(e -> {
      dialog.dispose();
      showDeleteEventDialog(parent, event, onDelete, onSuccess);
    });
    buttonPanel.add(deleteButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());
    buttonPanel.add(cancelButton);

    panel.add(buttonPanel);
    dialog.add(panel);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }

  /**
   * Shows a dialog for deleting an event with scope options.
   *
   * @param parent the parent frame
   * @param event the event to delete
   * @param onDelete callback to handle event deletion
   * @param onSuccess callback when event is deleted successfully
   */
  private static void showDeleteEventDialog(JFrame parent, EventInfo event,
                                            DeleteEventCallback onDelete, Runnable onSuccess) {
    JDialog dialog = new JDialog(parent, "Delete Event", true);
    dialog.setSize(400, 250);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    panel.add(new JLabel("Event: " + event.getSubject()));
    panel.add(new JLabel("Date: " + event.getStartDateTime().toLocalDate().toString()));

    panel.add(new JLabel("Delete Scope:"));
    JComboBox<String> scopeCombo;
    if (event.isSeries()) {
      scopeCombo = new JComboBox<>(
          new String[]{"Single Event", "All Events From This", "All Events In Series"});
    } else {
      scopeCombo = new JComboBox<>(new String[]{"Single Event"});
    }
    panel.add(scopeCombo);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(e -> {
      try {
        String scope = (String) scopeCombo.getSelectedItem();
        String scopeString;
        if ("Single Event".equals(scope)) {
          scopeString = "single";
        } else if ("All Events From This".equals(scope)) {
          scopeString = "from";
        } else {
          scopeString = "series";
        }

        if (onDelete != null) {
          onDelete.delete(event.getSubject(), event.getStartDateTime(), event.getEndDateTime(),
              scopeString, (result) -> {
                if (result != null && (result.contains("Error") || result.contains("Failed"))) {
                  javax.swing.JOptionPane.showMessageDialog(dialog, result, "Error",
                      javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                  dialog.dispose();
                  if (onSuccess != null) {
                    onSuccess.run();
                  }
                }
              });
        }
      } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(dialog, "Failed to delete event: " + ex.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
      }
    });
    buttonPanel.add(deleteButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());
    buttonPanel.add(cancelButton);

    panel.add(buttonPanel);
    dialog.add(panel);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }

  private static void showEditEventDetailsDialog(JFrame parent, EventInfo event,
                                                 EditEventCallback onEdit, Runnable onSuccess) {
    JDialog dialog = new JDialog(parent, "Edit Event", true);
    dialog.setSize(500, 500);
    dialog.setLayout(new BorderLayout());

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    panel.add(new JLabel("Property to Edit:"));
    JComboBox<String> propertyCombo = new JComboBox<>(
        new String[]{"subject", "start", "end", "description", "location", "status"});
    panel.add(propertyCombo);

    panel.add(new JLabel("New Value:"));
    JTextField valueField = new JTextField(30);
    panel.add(valueField);

    panel.add(new JLabel("Edit Scope:"));
    JComboBox<String> scopeCombo;
    if (event.isSeries()) {
      scopeCombo = new JComboBox<>(
          new String[]{"Single Event", "All Events From This", "All Events In Series"});
    } else {
      scopeCombo = new JComboBox<>(new String[]{"Single Event"});
    }
    panel.add(scopeCombo);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton saveButton = new JButton("Save");
    saveButton.addActionListener(e -> {
      try {
        String newValue = valueField.getText().trim();
        if (newValue.isEmpty()) {
          javax.swing.JOptionPane.showMessageDialog(dialog, "Value cannot be empty",
              "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
          return;
        }

        String scope = (String) scopeCombo.getSelectedItem();
        String scopeString;
        if ("Single Event".equals(scope)) {
          scopeString = "single";
        } else if ("All Events From This".equals(scope)) {
          scopeString = "from";
        } else {
          scopeString = "series";
        }
        String property = (String) propertyCombo.getSelectedItem();
        if (onEdit != null) {
          onEdit.edit(property, event.getSubject(), event.getStartDateTime(),
              event.getEndDateTime(), newValue, scopeString, (result) -> {
                if (result != null && (result.contains("Error") || result.contains("Failed"))) {
                  javax.swing.JOptionPane.showMessageDialog(dialog, result, "Error",
                      javax.swing.JOptionPane.ERROR_MESSAGE);
                } else {
                  dialog.dispose();
                  if (onSuccess != null) {
                    onSuccess.run();
                  }
                }
              });
        }
      } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(dialog, "Failed to edit event: " + ex.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
      }
    });
    buttonPanel.add(saveButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());
    buttonPanel.add(cancelButton);

    panel.add(buttonPanel);
    dialog.add(panel);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }
}

