import static org.junit.Assert.assertEquals;

import calendar.model.datatypes.EventProperties;
import calendar.model.datatypes.EventStatus;
import calendar.model.datatypes.Location;
import calendar.model.datatypes.UserStatus;
import org.junit.Test;

/**
 * Represents tests for various enums used including EventProperties, EventStatus, Location,
 * TypeOfEvent, and userStatus.
 */
public class EnumDataTypeTest {

  @Test
  public void testGetEventPropertySubject() {
    assertEquals(EventProperties.SUBJECT, EventProperties.SUBJECT.getEventProperty("subject"));
  }

  @Test
  public void testGetEventPropertyStart() {
    assertEquals(EventProperties.START_DATA_TIME,
        EventProperties.SUBJECT.getEventProperty("start"));
  }

  @Test
  public void testGetEventPropertyEnd() {
    assertEquals(EventProperties.END_DATA_TIME,
        EventProperties.SUBJECT.getEventProperty("end"));
  }

  @Test
  public void testGetEventPropertyDescription() {
    assertEquals(EventProperties.DESCRIPTION,
        EventProperties.SUBJECT.getEventProperty("description"));
  }

  @Test
  public void testGetEventPropertyLocation() {
    assertEquals(EventProperties.LOCATION,
        EventProperties.SUBJECT.getEventProperty("location"));
  }

  @Test
  public void testGetEventPropertyStatus() {
    assertEquals(EventProperties.EVENT_STATUS,
        EventProperties.SUBJECT.getEventProperty("status"));
  }

  @Test
  public void testGetEventPropertyCaseInsensitive() {
    assertEquals(EventProperties.SUBJECT,
        EventProperties.EVENT_STATUS.getEventProperty("SUBJECT"));
  }

  @Test
  public void testGetEventProperty_invalidProperty() {
    try {
      assertEquals(EventProperties.SUBJECT,
          EventProperties.EVENT_STATUS.getEventProperty("subjects"));
      assert false;
    } catch (IllegalArgumentException e) {
      assert true;
    }
  }

  @Test
  public void testGetLocationPhysical() {
    assertEquals(Location.PHYSICAL, Location.getLocation("physical"));
  }

  @Test
  public void testGetLocationOnline() {
    assertEquals(Location.ONLINE, Location.getLocation("online"));
  }

  @Test
  public void testGetLocationUnknown() {
    assertEquals(Location.UNKNOWN, Location.getLocation("random"));
  }

  @Test
  public void testGetEventStatusPublic() {
    assertEquals(EventStatus.PUBLIC, EventStatus.getEventStatus("public"));
  }

  @Test
  public void testGetEventStatusPrivate() {
    assertEquals(EventStatus.PRIVATE, EventStatus.getEventStatus("private"));
  }

  @Test
  public void testGetEventStatusRandom() {
    assertEquals(EventStatus.UNKNOWN, EventStatus.getEventStatus("random"));
  }

  @Test
  public void testUserStatusValues() {
    UserStatus[] userStatus = UserStatus.values();
    assertEquals(2, userStatus.length);
    assertEquals(UserStatus.BUSY, userStatus[0]);
    assertEquals(UserStatus.AVAILABLE, userStatus[1]);
  }
}
