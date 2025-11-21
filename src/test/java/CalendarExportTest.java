import static org.testng.AssertJUnit.assertTrue;

import calendar.controller.AdvanceCalendarController;
import calendar.controller.CalendarController;
import calendar.model.CalendarContainerImpl;
import calendar.model.interfaces.CalendarContainer;
import calendar.view.CalendarView;
import calendar.view.CalendarViewImpl;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import org.junit.Test;

/**
 * Tests for exporting calendars in different formats.
 * These tests check if:
 * Calendars and events can be exported correctly to iCal and CSV files.
 * The exported files contain the expected data and structure.
 * Invalid file names throws an error.
 */

public class CalendarExportTest {
  CalendarContainer container = new CalendarContainerImpl();
  Appendable out = new StringBuilder();
  CalendarView view = new CalendarViewImpl(out);
  Readable inputStream;


  @Test
  public void icalExportTest() throws IOException {



    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "export cal IcalExportTest.ical";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    File file = new File("IcalExportTest.ical");

    String content = Files.readString(file.toPath());

    assertTrue(content.contains("BEGIN:VCALENDAR"));
    assertTrue(content.contains("VERSION:2.0"));
    assertTrue(content.contains("BEGIN:VEVENT"));
    assertTrue(content.contains("PRODID:-//MyCalendarApp//EN"));
    assertTrue(content.contains("END:VEVENT"));
    assertTrue(content.contains("END:VCALENDAR"));


    assertTrue(content.contains("SUMMARY:Hello Meet"));
    assertTrue(content.contains("DTSTART:20251012T080000"));
    assertTrue(content.contains("DTEND:20251012T170000"));
    assertTrue(content.contains("DTSTART:20251212T080000"));



  }

  @Test
  public void testCsvExport() throws IOException {


    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "export cal testCSVExport.csv";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    File file = new File("testCSVExport.csv");

    String content = Files.readString(file.toPath());


    assertTrue(content.contains("Subject,StartDate,StartTime"));


    assertTrue(content.contains("Hello Meet,10/12/2025"));
    assertTrue(content.contains("8:00AM"));
    assertTrue(content.contains("Hello Meet,12/12/2025"));
    assertTrue(content.contains("8:00AM"));
  }

  @Test
  public void testCsvExportInvalidFileName() {


    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "export cal testCSVExport.c";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    assertTrue(out.toString().contains("Invalid file type"));
  }

  @Test
  public void testIcalExportInvalidFileName() {


    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "export cal testCSVExport";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    assertTrue(out.toString().contains("Invalid file type"));
  }

  @Test
  public void testExportInvalidCommand() {


    String in = "create calendar --name Work --timezone America/New_York"
        + System.lineSeparator()
        + "use calendar --name Work"
        + System.lineSeparator()
        + "create event \"Hello Meet\" on 2025-10-12 repeats TUF until 2025-12-12"
        + System.lineSeparator()
        + "export cal testCSVExport.csv to IcalExportTest";

    inputStream = new StringReader(in);

    CalendarController controller = new AdvanceCalendarController(container, inputStream, view);

    controller.run();

    assertTrue(out.toString().contains("Invalid export command"));
  }


}
