package calendar.view;

import java.awt.BorderLayout;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Dialog for creating and editing calendars.
 */
public class CalendarDialog {
  private static List<String> getAllZoneIds() {
    Set<String> zoneIds = ZoneId.getAvailableZoneIds();
    List<String> sortedZoneIds = new ArrayList<>(zoneIds);
    Collections.sort(sortedZoneIds);
    return sortedZoneIds;
  }

  /**
   * Shows a dialog for creating a new calendar.
   *
   * @param parent the parent frame
   * @param onCreate callback with (name, timezone) when create is clicked
   * @param onSuccess callback when dialog should refresh
   */
  public static void showCreateDialog(JFrame parent, BiConsumer<String, String> onCreate,
                                      Runnable onSuccess) {
    JDialog dialog = new JDialog(parent, "Create New Calendar", true);
    dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
    dialog.setSize(400, 250);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    panel.add(new JLabel("Calendar Name:"));
    JTextField nameField = new JTextField(20);
    panel.add(nameField);

    panel.add(new JLabel("Timezone:"));
    List<String> zoneIds = getAllZoneIds();
    JComboBox<String> timezoneCombo = new JComboBox<>(zoneIds.toArray(new String[0]));
    timezoneCombo.setEditable(false);
    // Set default to system timezone
    String systemTimezone = ZoneId.systemDefault().getId();
    if (zoneIds.contains(systemTimezone)) {
      timezoneCombo.setSelectedItem(systemTimezone);
    }
    panel.add(timezoneCombo);

    JPanel buttonPanel = new JPanel(new java.awt.FlowLayout());
    JButton createButton = new JButton("Create");
    createButton.addActionListener(e -> {
      try {
        String name = nameField.getText().trim();
        String timezone = (String) timezoneCombo.getSelectedItem();
        if (name.isEmpty()) {
          javax.swing.JOptionPane.showMessageDialog(dialog, "Calendar name cannot be empty",
              "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (onCreate != null) {
          onCreate.accept(name, timezone);
          dialog.dispose();
          if (onSuccess != null) {
            onSuccess.run();
          }
        }
      } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(dialog, "Invalid timezone: " + ex.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
      }
    });
    buttonPanel.add(createButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());
    buttonPanel.add(cancelButton);

    panel.add(buttonPanel);
    dialog.add(panel);
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);
  }

  /**
   * Functional interface for editing calendars.
   */
  @FunctionalInterface
  public interface EditCalendarCallback {
    void edit(String calendarName, String property, String newValue,
              java.util.function.Consumer<String> resultHandler);
  }

  /**
   * Shows a dialog for editing a calendar.
   *
   * @param parent the parent frame
   * @param getActiveCalendarName supplier for active calendar name
   * @param getCurrentTimezone supplier for current timezone
   * @param onEdit callback to handle calendar editing
   * @param onSuccess callback when dialog should refresh
   */
  public static void showEditDialog(JFrame parent, java.util.function.Supplier<String> getActiveCalendarName,
                                    java.util.function.Supplier<String> getCurrentTimezone,
                                    EditCalendarCallback onEdit, Runnable onSuccess) {
    String activeCalendarName = getActiveCalendarName != null ? getActiveCalendarName.get() : null;
    String currentTimezone = getCurrentTimezone != null ? getCurrentTimezone.get() : null;
    
    if (activeCalendarName == null) {
      javax.swing.JOptionPane.showMessageDialog(parent, "No calendar selected", "Error",
          javax.swing.JOptionPane.ERROR_MESSAGE);
      return;
    }

    JDialog dialog = new JDialog(parent, "Edit Calendar", true);
    dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
    dialog.setSize(400, 300);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    panel.add(new JLabel("Property to Edit:"));
    JComboBox<String> propertyCombo = new JComboBox<>(new String[]{"name", "timezone"});
    panel.add(propertyCombo);

    panel.add(new JLabel("New Value:"));
    JTextField valueField = new JTextField(20);
    JComboBox<String> timezoneCombo = new JComboBox<>();
    List<String> zoneIds = getAllZoneIds();
    timezoneCombo.setModel(new javax.swing.DefaultComboBoxModel<>(
        zoneIds.toArray(new String[0])));
    timezoneCombo.setEditable(false);
    if (currentTimezone != null && zoneIds.contains(currentTimezone)) {
      timezoneCombo.setSelectedItem(currentTimezone);
    }
    timezoneCombo.setVisible(false);
    panel.add(valueField);
    panel.add(timezoneCombo);

    // Show/hide appropriate field based on property selection
    propertyCombo.addActionListener(e -> {
      String property = (String) propertyCombo.getSelectedItem();
      if ("timezone".equals(property)) {
        valueField.setVisible(false);
        timezoneCombo.setVisible(true);
      } else {
        valueField.setVisible(true);
        timezoneCombo.setVisible(false);
      }
    });

    JPanel buttonPanel = new JPanel(new java.awt.FlowLayout());
    JButton saveButton = new JButton("Save");
    saveButton.addActionListener(e -> {
      try {
        String property = (String) propertyCombo.getSelectedItem();
        String newValue;
        if ("timezone".equals(property)) {
          newValue = (String) timezoneCombo.getSelectedItem();
        } else {
          newValue = valueField.getText().trim();
        }
        if (newValue == null || newValue.isEmpty()) {
          javax.swing.JOptionPane.showMessageDialog(dialog, "Value cannot be empty", "Error",
              javax.swing.JOptionPane.ERROR_MESSAGE);
          return;
        }
        if (onEdit != null) {
          onEdit.edit(activeCalendarName, property, newValue, (result) -> {
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
        javax.swing.JOptionPane.showMessageDialog(dialog, "Failed to edit calendar: " + ex.getMessage(),
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

