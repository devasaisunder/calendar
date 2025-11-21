package calendar.view;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Bottom panel component for action buttons.
 */
public class BottomPanel extends JPanel {
  private JButton createEventButton;
  private JButton editEventButton;
  private JButton deleteEventButton;
  private JButton exportButton;

  /**
   * Constructor for BottomPanel.
   */
  public BottomPanel() {
    super(new FlowLayout());
    setBorder(new EmptyBorder(10, 10, 10, 10));
    initializeComponents();
  }

  private void initializeComponents() {
    createEventButton = new JButton("Create Event");
    editEventButton = new JButton("Edit Event");
    deleteEventButton = new JButton("Delete Event");
    exportButton = new JButton("Export Calendar");

    add(createEventButton);
    add(editEventButton);
    add(deleteEventButton);
    add(exportButton);
  }

  /**
   * Gets the create event button.
   *
   * @return the create event button
   */
  public JButton getCreateEventButton() {
    return createEventButton;
  }

  /**
   * Gets the edit event button.
   *
   * @return the edit event button
   */
  public JButton getEditEventButton() {
    return editEventButton;
  }

  /**
   * Gets the delete event button.
   *
   * @return the delete event button
   */
  public JButton getDeleteEventButton() {
    return deleteEventButton;
  }

  /**
   * Gets the export button.
   *
   * @return the export button
   */
  public JButton getExportButton() {
    return exportButton;
  }
}

