package calendar.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * A typed day panel that holds a date and renders events for that date.
 * It encapsulates click handling so callers don't need to cast or use instanceof.
 */
public class DayPanel extends JPanel {
  private final java.time.LocalDate date;

  public DayPanel(java.time.LocalDate date,
                  List<EventInfo> dayEvents,
                  boolean selected,
                  Color calendarColor,
                  java.util.function.Consumer<java.time.LocalDate> onClick) {
    super(new BorderLayout());
    this.date = date;

    setPreferredSize(new Dimension(100, 100));
    setBorder(BorderFactory.createLineBorder(
        selected ? Color.BLUE : Color.GRAY,
        selected ? 3 : 1));

    Color bg = calendarColor != null ? calendarColor : Color.WHITE;
    setBackground(bg);

    // Day number label at top
    JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()), SwingConstants.CENTER);
    dayLabel.setFont(dayLabel.getFont().deriveFont(14f));
    if (date.equals(java.time.LocalDate.now())) {
      dayLabel.setForeground(Color.RED);
    }
    add(dayLabel, BorderLayout.NORTH);

    // Events panel
    JPanel eventsPanel = new JPanel();
    eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
    eventsPanel.setOpaque(false);
    eventsPanel.setBorder(new EmptyBorder(2, 2, 2, 2));

    int maxEventsToShow = 3;
    int eventsToShow = Math.min(dayEvents.size(), maxEventsToShow);

    // Shared listener for all subcomponents
    java.awt.event.MouseListener clickListener = new java.awt.event.MouseListener() {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent e) {
        onClick.accept(date);
      }

      @Override public void mousePressed(java.awt.event.MouseEvent e) { }
      @Override public void mouseReleased(java.awt.event.MouseEvent e) { }
      @Override public void mouseEntered(java.awt.event.MouseEvent e) { }
      @Override public void mouseExited(java.awt.event.MouseEvent e) { }
    };

    // Attach to main panel and header so clicks anywhere work
    this.addMouseListener(clickListener);
    dayLabel.addMouseListener(clickListener);

    for (int i = 0; i < eventsToShow; i++) {
      EventInfo event = dayEvents.get(i);
      String eventText = event.getSubject();
      if (eventText.length() > 20) {
        eventText = eventText.substring(0, 17) + "...";
      }
      JLabel eventLabel = new JLabel(eventText);
      eventLabel.setOpaque(true);
      eventLabel.setBackground(new Color(255, 235, 59));
      eventLabel.setForeground(Color.BLACK);
      eventLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
      eventLabel.setFont(eventLabel.getFont().deriveFont(9f));
      eventLabel.setAlignmentX(0.0f);
      eventLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
      eventLabel.addMouseListener(clickListener);
      eventsPanel.add(eventLabel);
    }

    if (dayEvents.size() > maxEventsToShow) {
      JLabel moreLabel = new JLabel((dayEvents.size() - maxEventsToShow) + " more");
      moreLabel.setFont(moreLabel.getFont().deriveFont(8f));
      moreLabel.setForeground(Color.GRAY);
      moreLabel.addMouseListener(clickListener);
      eventsPanel.add(moreLabel);
    }

    add(eventsPanel, BorderLayout.CENTER);
  }
}
