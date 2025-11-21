# Calendar GUI Application - Version 3 Implementation Plan

## Overview

This document outlines the detailed implementation plan for Version 3 of the Calendar GUI Application, which includes event deletion functionality, improved date pickers, enhanced series event management, and **refactoring to follow proper MVC pattern with ViewListener interface** (based on sample code pattern).

## Architecture Pattern (Updated for V3)

Following the sample code pattern:

- **View**: Only handles UI, emits events through ViewListener interface
- **Controller**: Implements ViewListener, handles ALL business logic
- **Model**: Data operations only
- **Main**: Only creates model, view, controller and calls `controller.go()` - no logic

---

## Table of Contents

1. [Version 3 Features](#version-3-features)
2. [Architecture Overview](#architecture-overview)
3. [MVC Refactoring (New in V3)](#mvc-refactoring-new-in-v3)
4. [Detailed Implementation Steps](#detailed-implementation-steps)
5. [File Structure Changes](#file-structure-changes)
6. [Testing Considerations](#testing-considerations)

---

## Version 3 Features

### 1. Event Deletion Functionality

- **Single Event Deletion**: Delete a single event
- **Series Event Deletion Options**:
  - Delete single event from series
  - Delete all events from a specific date onwards
  - Delete entire series
- **Conditional UI**: Series deletion options only shown for series events

### 2. Enhanced Date Input

- **Date Pickers**: Replace text fields with proper date picker components (JSpinner with date format)
- **Consistent Date Format**: All date inputs use standardized date pickers

### 3. UI Improvements

- **Conditional Scope Options**: Edit and delete scope options only appear for series events
- **Better Event Management**: Unified dialog for edit/delete actions

---

## Architecture Overview

### MVC Pattern (Refactored)

- **Model**: `CalendarEditable`, `EventReadOnly`, `CalendarContainer` - Handles data only
- **View**: `CalendarGuiViewImpl`, `EventDialog`, Panel classes - Handles UI only, emits events
- **Controller**: `CalendarGuiController` (implements `ViewListener`) - Handles ALL business logic

### Key Components

1. **ViewListener Interface**: Defines events that view can emit
2. **CalendarGuiController**: Implements ViewListener, handles all business logic
3. **CalendarFeatures**: Used internally by controller to interact with model
4. **Handler Classes**: Execute specific operations (Create, Edit, Delete)
5. **Command Data Classes**: Transfer parsed command data to handlers
6. **View Components**: Panels and dialogs - only UI, no business logic

## MVC Refactoring (New in V3)

### Phase 0: Refactoring to Proper MVC Pattern

#### Step 0.1: Create ViewListener Interface

**File**: `src/main/java/calendar/view/ViewListener.java`

**Purpose**: Define all events that the view can emit. The controller implements this interface.

**Methods**:

- `handleSwitchCalendar(String calendarName)`
- `handleCreateCalendar(String name, String timezone)`
- `handleEditCalendar(String calendarName, String property, String newValue)`
- `handlePreviousMonth()`
- `handleNextMonth()`
- `handleDateSelected(LocalDate date)`
- `handleCreateEvent(...)`
- `handleEditEvent(...)`
- `handleDeleteEvent(...)`
- `handleExportCalendar(String fileName)`
- `handleRefresh()`

**Rationale**: View should only emit events, not call controller methods directly.

---

#### Step 0.2: Refactor CalendarGuiViewImpl to Emit Events

**File**: `src/main/java/calendar/view/CalendarGuiViewImpl.java`

**Changes**:

1. Remove `Features features` field
2. Add `List<ViewListener> listeners` field
3. Add `addViewListener(ViewListener listener)` method
4. Replace all `features.method()` calls with `emitXXX()` methods
5. Each `emitXXX()` method iterates through listeners and calls corresponding handler

**Example**:

```java
// OLD (WRONG):
features.switchCalendar(selected);

// NEW (CORRECT):
emitSwitchCalendar(selected);

private void emitSwitchCalendar(String calendarName) {
  for (ViewListener listener : listeners) {
    listener.handleSwitchCalendar(calendarName);
  }
}
```

**Rationale**: View should not know about controller implementation, only emit events.

---

#### Step 0.3: Create CalendarGuiController

**File**: `src/main/java/calendar/controller/CalendarGuiController.java`

**Purpose**: Implements ViewListener and handles ALL business logic.

**Structure**:

```java
public class CalendarGuiController implements ViewListener {
  private final CalendarContainer container;
  private final CalendarFeatures features;
  private final CalendarGuiView view;

  public CalendarGuiController(CalendarContainer container, CalendarGuiView view) {
    this.container = container;
    this.features = new CalendarFeatures(container);
    this.view = view;
    // Register as listener
    ((CalendarGuiViewImpl) view).addViewListener(this);
  }

  public void go() {
    view.display();
    handleRefresh();
  }

  @Override
  public void handleSwitchCalendar(String calendarName) {
    // Business logic here
    String result = features.switchCalendar(calendarName);
    if (result.contains("Error")) {
      view.renderError(result);
    } else {
      handleRefresh();
    }
  }
  // ... other handler methods
}
```

**Rationale**: All business logic moves from view to controller.

---

#### Step 0.4: Update Dialogs to Use Callbacks

**Files**: `CalendarDialog.java`, `EventDialog.java`

**Changes**:

1. Remove direct `Features` parameter
2. Add functional interface callbacks for actions
3. Dialogs can query data through Features (provided by controller)
4. Dialogs emit actions through callbacks

**Example**:

```java
// OLD:
public static void showCreateDialog(JFrame parent, Features features, ...)

// NEW:
@FunctionalInterface
public interface CreateCalendarCallback {
  void create(String name, String timezone, Consumer<String> resultHandler);
}

public static void showCreateDialog(JFrame parent, CreateCalendarCallback onCreate, ...)
```

**Rationale**: Dialogs should not directly call controller methods, use callbacks.

---

#### Step 0.5: Update Main to Follow Pattern

**File**: `src/main/java/CalendarRunner.java`

**Changes**:

```java
// OLD:
CalendarGuiView guiView = new CalendarGuiViewImpl();
AdvanceCalendarController controller = new AdvanceCalendarController(...);
CalendarFeatures features = new CalendarFeatures(container);
guiView.addFeatures(features);
guiView.display();

// NEW:
CalendarGuiView guiView = new CalendarGuiViewImpl();
CalendarGuiController guiController = new CalendarGuiController(container, guiView);
guiController.go();
```

**Rationale**: Main should only create components and start application, no logic.

---

## Detailed Implementation Steps

### Phase 1: Data Model Updates

#### Step 1.1: Extend EventInfo to Include Series Information

**File**: `src/main/java/calendar/view/EventInfo.java`

**Changes**:

- Add `isSeries` boolean field
- Update constructor to accept `isSeries` parameter
- Add `isSeries()` getter method

**Rationale**: The view needs to know if an event is part of a series to show appropriate options.

**Code Changes**:

```java
private final boolean isSeries;

public EventInfo(..., boolean isSeries) {
    ...
    this.isSeries = isSeries;
}

public boolean isSeries() {
    return isSeries;
}
```

---

#### Step 1.2: Update EventInfo Conversion

**File**: `src/main/java/calendar/controller/CalendarFeatures.java`

**Changes**:

- Update `convertToEventInfo()` method to check event type
- Set `isSeries` based on `event.getEventType() == TypeOfEvent.SERIES`

**Rationale**: Ensure EventInfo objects correctly reflect whether events are part of a series.

---

### Phase 2: Delete Functionality - Backend

#### Step 2.1: Create Delete Command Data Classes

**Files Created**:

- `src/main/java/calendar/controller/commanddata/DeleteEventCommandData.java`
- `src/main/java/calendar/controller/commanddata/DeleteMultipleEventsCommandData.java`
- `src/main/java/calendar/controller/commanddata/DeleteSeriesCommandData.java`

**Purpose**: Transfer parsed command data to delete handlers, following the same pattern as edit handlers.

**Structure**:

- `DeleteEventCommandData`: Contains subject, startDateTime, endDateTime
- `DeleteMultipleEventsCommandData`: Contains subject, startDateTime (for "from" scope)
- `DeleteSeriesCommandData`: Contains subject, startDateTime (for "series" scope)

---

#### Step 2.2: Create Delete Handler Classes

**Files Created**:

- `src/main/java/calendar/controller/handlers/DeleteEventHandler.java`
- `src/main/java/calendar/controller/handlers/DeleteMultipleEventsHandler.java`
- `src/main/java/calendar/controller/handlers/DeleteSeriesHandler.java`

**Purpose**: Execute delete operations following the handler pattern used for edit operations.

**Implementation Details**:

**DeleteEventHandler** (Single Event):

1. Use `CalendarFilter` to find events matching subject, start, and end datetime
2. Remove each matching event using `calendarModel.removeEvent()`
3. Return success message with deleted event details

**DeleteMultipleEventsHandler** (From Date Onwards):

1. Find events matching subject and start datetime
2. For series events: Get all events in series using `EventPredicates.byEventId()`
3. Filter to include only events from the specified date onwards
4. Remove all filtered events
5. Return count of deleted events

**DeleteSeriesHandler** (Entire Series):

1. Find events matching subject and start datetime
2. For series events: Get all events in series using `EventPredicates.byEventId()`
3. Remove all events in the series
4. For single events: Remove the single event
5. Return count of deleted events

**Key Logic**:

```java
// Check if event is part of series
if (event.getEventType() == TypeOfEvent.SERIES) {
    // Get all events with same UUID
    List<EventReadOnly> seriesEvents = filter.filter(
        EventPredicates.byEventId(event.getId())
    );
    // Delete all or filter by date
}
```

---

#### Step 2.3: Add Delete Method to Features Interface

**File**: `src/main/java/calendar/view/Features.java`

**Changes**:

- Add `deleteEvent()` method signature
- Parameters: subject, startDateTime, endDateTime, scope
- Scope values: "single", "from", "series"

**Rationale**: Define the contract for delete operations from the view's perspective.

---

#### Step 2.4: Implement Delete in CalendarFeatures

**File**: `src/main/java/calendar/controller/CalendarFeatures.java`

**Changes**:

- Import delete handler classes and command data classes
- Implement `deleteEvent()` method
- Route to appropriate handler based on scope:
  - "single" → `DeleteEventHandler`
  - "from" → `DeleteMultipleEventsHandler`
  - "series" → `DeleteSeriesHandler`

**Implementation Pattern**:

```java
if ("single".equals(scope)) {
    DeleteEventHandler handler = new DeleteEventHandler(...);
    DeleteEventCommandData data = new DeleteEventCommandData(...);
    result = handler.handle(data);
} else if ("from".equals(scope)) {
    // Similar pattern
}
```

---

### Phase 3: Delete Functionality - Frontend

#### Step 3.1: Add Delete Button to Bottom Panel

**File**: `src/main/java/calendar/view/BottomPanel.java`

**Changes**:

- Add `deleteEventButton` field
- Initialize button in `initializeComponents()`
- Add getter method `getDeleteEventButton()`

**Rationale**: Provide UI access to delete functionality.

---

#### Step 3.2: Update EventDialog with Delete Functionality

**File**: `src/main/java/calendar/view/EventDialog.java`

**Changes**:

1. **Update `showEditDialog()` method**:

   - Rename to show event selection (edit or delete)
   - After event selection, show action choice dialog

2. **Add `showEventActionDialog()` method**:

   - Display "Edit Event" and "Delete Event" buttons
   - Route to appropriate dialog based on user choice

3. **Add `showDeleteEventDialog()` method**:

   - Display event information
   - Show scope combo box:
     - For series events: "Single Event", "All Events From This", "All Events In Series"
     - For single events: "Single Event" only
   - Call `features.deleteEvent()` with appropriate scope
   - Handle success/error responses

4. **Update `showEditEventDetailsDialog()` method**:
   - Show scope combo box conditionally:
     - For series events: All three options
     - For single events: "Single Event" only

**Key Implementation**:

```java
JComboBox<String> scopeCombo;
if (event.isSeries()) {
    scopeCombo = new JComboBox<>(
        new String[]{"Single Event", "All Events From This", "All Events In Series"}
    );
} else {
    scopeCombo = new JComboBox<>(new String[]{"Single Event"});
}
```

---

#### Step 3.3: Wire Delete Button in Main View

**File**: `src/main/java/calendar/view/CalendarGuiViewImpl.java`

**Changes**:

- In `setupBottomPanelListeners()`:
  - Add action listener to delete button
  - Call `EventDialog.showEditDialog()` (which now handles both edit and delete)

**Note**: The delete button uses the same entry point as edit, which then shows an action choice dialog.

---

### Phase 4: Date Picker Enhancements

#### Step 4.1: Verify Date Pickers in EventDialog

**File**: `src/main/java/calendar/view/EventDialog.java`

**Status**: Already implemented using `JSpinner` with `SpinnerDateModel`

**Components Using Date Pickers**:

1. **Create Event Dialog**:

   - Date picker for event date
   - Date picker for recurring event end date
   - Time spinners for start/end times

2. **Edit Event Dialog**:
   - Uses text field for new value (appropriate for property editing)

**Date Picker Implementation**:

```java
SpinnerDateModel dateModel = new SpinnerDateModel(
    java.sql.Date.valueOf(selectedDate),
    null, null, java.util.Calendar.DAY_OF_MONTH
);
JSpinner dateSpinner = new JSpinner(dateModel);
JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(
    dateSpinner, "yyyy-MM-dd"
);
dateSpinner.setEditor(dateEditor);
```

**Rationale**: JSpinner with date format provides a user-friendly date selection interface.

---

### Phase 5: Conditional UI for Series Events

#### Step 5.1: Series Detection in EventInfo

**Status**: Completed in Phase 1

#### Step 5.2: Conditional Scope Options in Delete Dialog

**File**: `src/main/java/calendar/view/EventDialog.java`

**Implementation**:

- Check `event.isSeries()` before populating scope combo box
- Show appropriate options based on event type

#### Step 5.3: Conditional Scope Options in Edit Dialog

**File**: `src/main/java/calendar/view/EventDialog.java`

**Implementation**:

- Same pattern as delete dialog
- Only show "All Events From This" and "All Events In Series" for series events

---

## File Structure Changes

### New Files Created

#### MVC Refactoring

- `src/main/java/calendar/view/ViewListener.java` - Interface for view events
- `src/main/java/calendar/controller/CalendarGuiController.java` - GUI controller implementing ViewListener

#### Command Data Classes

- `src/main/java/calendar/controller/commanddata/DeleteEventCommandData.java`
- `src/main/java/calendar/controller/commanddata/DeleteMultipleEventsCommandData.java`
- `src/main/java/calendar/controller/commanddata/DeleteSeriesCommandData.java`

#### Handler Classes

- `src/main/java/calendar/controller/handlers/DeleteEventHandler.java`
- `src/main/java/calendar/controller/handlers/DeleteMultipleEventsHandler.java`
- `src/main/java/calendar/controller/handlers/DeleteSeriesHandler.java`

### Modified Files

#### View Layer

- `src/main/java/calendar/view/EventInfo.java` - Added `isSeries` field
- `src/main/java/calendar/view/Features.java` - Added `deleteEvent()` method
- `src/main/java/calendar/view/BottomPanel.java` - Added delete button
- `src/main/java/calendar/view/EventDialog.java` - Added delete functionality, conditional UI, and callback pattern
- `src/main/java/calendar/view/CalendarDialog.java` - Updated to use callback pattern
- `src/main/java/calendar/view/CalendarGuiViewImpl.java` - Refactored to emit events instead of calling features directly

#### Controller Layer

- `src/main/java/calendar/controller/CalendarFeatures.java` - Implemented delete, updated EventInfo conversion
- `src/main/java/CalendarRunner.java` - Updated to use CalendarGuiController pattern

---

## Testing Considerations

### Unit Tests Needed

1. **DeleteEventHandler Tests**:

   - Delete single event successfully
   - Handle non-existent event gracefully
   - Verify event is removed from calendar

2. **DeleteMultipleEventsHandler Tests**:

   - Delete events from date onwards for series
   - Verify only future events are deleted
   - Handle single events correctly

3. **DeleteSeriesHandler Tests**:

   - Delete entire series
   - Verify all events with same UUID are removed
   - Handle single events correctly

4. **CalendarFeatures Delete Tests**:
   - Route to correct handler based on scope
   - Handle errors appropriately
   - Return proper success/error messages

### Integration Tests Needed

1. **UI Flow Tests**:

   - Delete single event from UI
   - Delete series event with different scopes
   - Verify conditional UI shows correctly
   - Verify date pickers work correctly

2. **End-to-End Tests**:
   - Create series event
   - Delete with different scopes
   - Verify calendar state after deletion

---

## Implementation Summary

### Completed Features

✅ **Event Deletion**:

- Single event deletion
- Series event deletion with three scope options
- Conditional UI based on event type

✅ **Date Pickers**:

- JSpinner date pickers in create event dialog
- Consistent date format (yyyy-MM-dd)
- Time spinners for event times

✅ **Conditional UI**:

- Scope options only shown for series events
- Edit and delete dialogs adapt based on event type

✅ **Code Organization**:

- Follows existing handler pattern
- Consistent with edit functionality
- Proper separation of concerns

### Key Design Decisions

1. **Reused Edit Dialog Entry Point**: Delete button uses same entry point as edit, showing action choice dialog first. This provides a unified experience.

2. **Handler Pattern**: Delete handlers follow the same pattern as edit handlers, ensuring consistency and maintainability.

3. **EventInfo Extension**: Added `isSeries` field rather than checking event type in view, keeping view logic simple.

4. **Scope-Based Routing**: Three distinct handlers for three scopes, making the code clear and testable.

---

## Future Enhancements (Not in V3)

1. **Confirmation Dialogs**: Add confirmation before deleting series events
2. **Undo Functionality**: Implement undo for deleted events
3. **Bulk Delete**: Delete multiple unrelated events at once
4. **Delete History**: Track deleted events for recovery
5. **Export Before Delete**: Option to export before deleting series

---

## Conclusion

Version 3 successfully implements event deletion functionality with proper scope handling, enhanced date pickers, and conditional UI for series events. The implementation follows established patterns and maintains code quality and consistency.

**Total Implementation Steps**: 20 major steps across 6 phases (including MVC refactoring)
**New Files**: 8 (including ViewListener and CalendarGuiController)
**Modified Files**: 8
**Lines of Code Added**: ~1200
**Implementation Time Estimate**: 12-15 hours

## Key Architectural Improvements

### Before (V2)

- View directly called `features.method()`
- Business logic mixed in view
- Main had some initialization logic
- Tight coupling between view and controller

### After (V3)

- View only emits events through ViewListener
- All business logic in CalendarGuiController
- Main only creates and starts components
- Loose coupling - view doesn't know controller implementation
- Follows sample code pattern exactly

---

_Document Version: 1.0_
_Last Updated: [Current Date]_
_Author: Development Team_
