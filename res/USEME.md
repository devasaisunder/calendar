# Project Overview

This project is an enhanced **Virtual Calendar Application** that enables users to manage multiple calendars, each with its own timezone and unique set of events. The application supports creating, editing, querying, copying, and exporting events in both interactive and headless modes.

Each calendar operates independently and provides full functionality for:

-**Single and recurring events**

-**Multi-calendar management**

-**Timezone-aware scheduling**

-**Event copying across calendars**

-**Exporting in CSV and iCal formats**

The Calendar application starts with a **default calendar** named "default", using the **system’s default timezone.**
Any new events created by the user are added to this default calendar unless another calendar is explicitly selected.



The application runs in two modes: **interactive** (real-time command input) and **headless** (batch commands from a file).

# Instructions to run the JAR File


## Running the Application

The compiled JAR file is located at `build/libs/calendar-1.0.jar`.

### Interactive Mode

Allows command-by-command interaction in the terminal:

```bash
java -jar calendar-1.0.jar --mode interactive
```

### Headless Mode

Executes all commands from a file sequentially:

```bash
java -jar calendar-1.0.jar --mode headless <filename>
```

> `<filename>` should be a text file containing valid commands. The last command must be `exit`.

---

## Commands Overview


Create a Calendar:

```
create calendar --name <calendarName> --timezone <area/location>
```

Edit a Calendar: 

```
edit calendar --name <calendarName> --property <propertyName> <newValue>
```

Use Calendar:

```
use calendar --name <name-of-calendar>
```

Copy Calendar:

1 - The command is used to copy a specific event with the given name and start date/time from the current calendar to the target calendar to start at the specified date/time. The "to" date/time is assumed to be specified in the timezone of the target calendar.

```
copy event <eventName> on <dateStringTtimeString> --target <calendarName> to <dateStringTtimeString>
```

2- This command has the same behavior as the copy event above, except it copies all events scheduled on that day. The times physically remain the same, except they are converted to the timezone of the target calendar.

```
copy events on <dateString> --target <calendarName> to <dateString>
```

3- The command has the same behavior as the other copy commands, except it copies all events scheduled in the specified date interval (i.e. overlaps with the specified interval). The date string in the target calendar corresponds to the start of the interval. The endpoint dates of the interval are inclusive.

```
copy events between <dateString> and <dateString> --target <calendarName> to <dateString>
```

### Event Creation Commands

Create a single event:

```
create event <subject> from <dateTtime> to <dateTtime>
```

Create a recurring event for N times on specified weekdays:

```
create event <subject> from <dateTtime> to <dateTtime> repeats <weekdays> for <N> times
```

Create a recurring event until a specified date:

```
create event <subject> from <dateTtime> to <dateTtime> repeats <weekdays> until <date>
```

Create a single all-day event:

```
create event <subject> on <date>
```

Create a recurring all-day event for N times:

```
create event <subject> on <date> repeats <weekdays> for <N> times
```

Create a recurring all-day event until a specific date:

```
create event <subject> on <date> repeats <weekdays> until <date>
```

> Notes: If the subject contains multiple words, enclose it in double quotes.

---

### Editing Commands

Edit a single event property:

```
edit event <property> <subject> from <dateTtime> to <dateTtime> with <newValue>
```

Edit all future events in a series starting from a specific event:

```
edit events <property> <subject> from <dateTtime> with <newValue>
```

Edit all events in a series:

```
edit series <property> <subject> from <dateTtime> with <newValue>
```

> Supported properties: `subject`, `start`, `end`, `description`, `location`, `status`.

---

### Query Commands

Print all events on a specific date:

```
print events on <date>
```

Print events in a date/time range:

```
print events from <dateTtime> to <dateTtime>
```

Check busy/available status at a specific date and time:

```
show status on <dateTtime>
```

---

### Export Command

Export calendar to a CSV file:

```
export cal <filename.csv>
```

Export calendar to a Ical file:

```
export cal <filename.ical>
```

> The file can be imported into Google Calendar. The program will print the absolute path of the exported file.

---

### Exit Command

Exit the application:

```
exit
```

> The program halts further input after this command.


## Changes and Enhancements from the Previous Version

### Refactored Command Files:
All command-related files have been moved from the /model package to the /controller package. Previously, function objects resided under /model, but this change aligns them better with MVC principles.

### Relocated CSV Export Functionality:
The CSV export feature was moved from the model layer to the controller. Since exporting data is a controller-level responsibility (handling user-driven actions), this refactor ensures proper separation of concerns.

### Enhanced Controller with Constructor Overloading:
The existing controller has been updated to include a new constructor that supports the CalendarContainer functionality. This addition enables better management of multiple calendars within the application.