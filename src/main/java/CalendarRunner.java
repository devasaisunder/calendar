//import calendar.controller.AdvanceCalendarController;
//import calendar.controller.CalendarController;
//import calendar.model.CalendarContainerImpl;
//import calendar.model.CalendarImpl;
//import calendar.model.interfaces.CalendarContainer;
//import calendar.model.interfaces.CalendarEditable;
//import calendar.view.CalendarGuiView;
//import calendar.view.CalendarGuiViewImpl;
//import calendar.view.CalendarView;
//import calendar.view.CalendarViewImpl;
//import java.awt.EventQueue;
//
///**
// * Program runner.
// */
//public class CalendarRunner {
//  private static final CalendarContainer container = new CalendarContainerImpl();
//  private static final CalendarEditable calendar = new CalendarImpl();
//
//  /**
//   * Main method for the Calendar project.
//   * Initializes the input source, sets up the controller, and runs the application.
//   * Supports three modes:
//   * - No arguments: GUI mode
//   * - --mode interactive: Interactive text mode
//   * - --mode headless filename: Headless script mode
//   *
//   * @param args Command-line arguments (used to determine interactive, headless, or GUI mode)
//   */
//  public static void main(String[] args) {
//    // GUI mode: no arguments
//    if (args.length == 0) {
//      EventQueue.invokeLater(() -> {
//        try {
//          // Initialize default calendar before creating view
//          // This ensures the container has a default calendar set up
//          try {
//            if (container.getCalendars().isEmpty()) {
//              calendar.model.AdvancedCalendarImpl defaultCal =
//                  new calendar.model.AdvancedCalendarImpl
//                      .AdvancedCalendarBuilder("default", java.time.ZoneId.systemDefault())
//                      .setCalendar(calendar)
//                      .build();
//              container.addCalendar("default", defaultCal);
//              container.setActiveCalendar("default");
//            }
//          } catch (Exception e) {
//            // Calendar might already exist, ignore
//          }
//
//          CalendarGuiView guiView = new CalendarGuiViewImpl();
//          // Create GUI controller that handles all logic
//          calendar.controller.CalendarGuiController guiController =
//              new calendar.controller.CalendarGuiController(container, guiView);
//          // Start the application
//          guiController.go();
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      });
//      return;
//    }
//
//    // CLI modes: interactive or headless
//    try {
//      Readable input = InputFactory.getInput(args);
//      CalendarView view = new CalendarViewImpl(System.out);
//      CalendarController controller = new AdvanceCalendarController(container, input, view);
//      controller.run();
//    } catch (IllegalArgumentException e) {
//      System.err.println("Error: " + e.getMessage());
//      System.err.println("Usage:");
//      System.err.println("  java -jar JARNAME.jar                    # GUI mode");
//      System.err.println("  java -jar JARNAME.jar --mode interactive # Interactive text mode");
//      System.err.println("  java -jar JARNAME.jar --mode headless <filename> # Headless mode");
//      System.exit(1);
//    }
//  }
//}


import calendar.controller.AdvanceCalendarController;
import calendar.controller.CalendarController;
import calendar.model.CalendarContainerImpl;
import calendar.model.CalendarImpl;
import calendar.model.interfaces.CalendarContainer;
import calendar.model.interfaces.CalendarEditable;
import calendar.view.CalendarGuiView;
import calendar.view.CalendarGuiViewImpl;
import calendar.view.CalendarView;
import calendar.view.CalendarViewImpl;

/**
 * Program runner.
 */
public class CalendarRunner {
  private static final CalendarContainer container = new CalendarContainerImpl();
  private static final CalendarEditable calendar = new CalendarImpl();

  /**
   * Main method for the Calendar project.
   * Initializes the input source, sets up the controller, and runs the application.
   * Supports three modes:
   * - No arguments: GUI mode
   * - --mode interactive: Interactive text mode
   * - --mode headless filename: Headless script mode
   *
   * @param args Command-line arguments
   */
  public static void main(String[] args) {
    // GUI mode: no arguments
    if (args.length == 0) {
      try {
        // Initialize default calendar before creating view
        try {
          if (container.getCalendars().isEmpty()) {
            calendar.model.AdvancedCalendarImpl defaultCal =
                new calendar.model.AdvancedCalendarImpl
                    .AdvancedCalendarBuilder("default", java.time.ZoneId.systemDefault())
                    .setCalendar(calendar)
                    .build();
            container.addCalendar("default", defaultCal);
            container.setActiveCalendar("default");
          }
        } catch (Exception e) {
          // Calendar might already exist, ignore
        }

        CalendarGuiView guiView = new CalendarGuiViewImpl();
        calendar.controller.CalendarGuiController guiController =
            new calendar.controller.CalendarGuiController(container, guiView);

        // Start application directly (no EventQueue)
        guiController.go();

      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

    // CLI modes: interactive or headless
    try {
      Readable input = InputFactory.getInput(args);
      CalendarView view = new CalendarViewImpl(System.out);
      CalendarController controller = new AdvanceCalendarController(container, input, view);
      controller.run();
    } catch (IllegalArgumentException e) {
      System.err.println("Error: " + e.getMessage());
      System.err.println("Usage:");
      System.err.println("  java -jar JARNAME.jar                    # GUI mode");
      System.err.println("  java -jar JARNAME.jar --mode interactive # Interactive text mode");
      System.err.println("  java -jar JARNAME.jar --mode headless <filename> # Headless mode");
      System.exit(1);
    }
  }
}
