# Assignment 2 Report - Media Notes App with Sensor and Notification

Name: ____________________  
Roll Number: 58  
Course: MAD Lab / Android Development  
Submission Date: 6 April 2026

---

## 1. Objective

The objective of this assignment is to develop an Android mobile application that allows users to:

1. Create and save notes.
2. Attach media (image) with each note.
3. Store note details in a SQLite database.
4. Display saved notes in a list format using RecyclerView.
5. Run a periodic background task using WorkManager.
6. Show reminder notifications based on roll-number logic.
7. Use accelerometer sensor input to trigger an in-app action.

The developed app satisfies these requirements and is implemented for roll number 58.

---

## 2. Problem Statement Summary

The assignment requires a media note-taking app that stores title, description, media path, and date in SQLite, includes one personalized field based on roll number, and reminds users periodically with notifications.

For roll number 58:

- 58 % 4 = 1 -> extra database field is reminder_flag.
- 58 % 3 = 2 -> notification message is: Time to read your notes.

---

## 3. Application Overview

The application has two major screens:

1. Main screen (note creation screen)
2. View Notes screen (list of stored notes)

The main screen provides:

- Note Title input
- Note Description input
- Reminder Flag selector (extra field for roll 58)
- Capture Image / Select Image action
- Save Note button
- View Notes button

The View Notes screen displays all saved notes in a RecyclerView card-style layout and supports sensor-based refresh using phone shake detection.

---

## 4. Database Structure (SQLite)

### 4.1 Database and Table Naming

As required by the assignment:

- Database name: NotesDB_58
- Table name: notes_58

### 4.2 Table Fields

The app stores each note with these columns:

1. id (INTEGER PRIMARY KEY AUTOINCREMENT)
2. title (TEXT NOT NULL)
3. description (TEXT)
4. image_path (TEXT)
5. date (TEXT)
6. reminder_flag (TEXT)  <- personalized field for roll 58

### 4.3 Table Creation Query

The table is created using this SQL structure:

CREATE TABLE notes_58 (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  title TEXT NOT NULL,
  description TEXT,
  image_path TEXT,
  date TEXT,
  reminder_flag TEXT
)

### 4.4 Database Operations Implemented

The SQLite helper class implements core operations:

- insertNote(Note note): Inserts a new note record.
- getAllNotes(): Returns all notes sorted by latest first.
- getNotesCount(): Returns total number of saved notes (used by Worker before notifying).

This structure ensures all required data is persisted and retrievable for UI and background reminder logic.

---

## 5. Media Handling

The app supports adding image media to notes in two ways:

1. Capture Image from camera
2. Select Image from gallery/documents

Implementation points:

- Camera launch is handled with Activity Result API.
- Gallery selection uses OpenDocument contract.
- Selected image URI/path is stored in image_path column.
- The image preview appears on the main screen before saving.
- In notes list, image is loaded into each card if available.

Note: The current implementation focuses on image support and satisfies the UI requirement Capture Image / Select Image.

---

## 6. Display Notes (RecyclerView)

Saved notes are displayed on a separate View Notes screen using RecyclerView.

Each note item card shows:

- Title
- Description
- Reminder Flag
- Date/time
- Thumbnail image (if image_path exists)

Additional UI behavior:

- Empty-state message is shown when no notes are stored.
- Card layout uses a light, readable, Keep-inspired style.
- Page spacing and item margins are adjusted for better display on modern phones.

---

## 7. Background Task Implementation

The app uses WorkManager to run a periodic background task.

### 7.1 Scheduling

- A unique periodic worker is scheduled when the app opens.
- Interval used: 15 minutes (minimum allowed for periodic WorkManager jobs).

### 7.2 Worker Logic

In each execution cycle:

1. Worker checks the database note count.
2. If no notes are available, it exits without notification.
3. If notes exist, it prepares and sends a reminder notification.
4. For Android 13+, it first checks POST_NOTIFICATIONS permission.

### 7.3 Why WorkManager

WorkManager is chosen because it is lifecycle-aware, reliable for deferred background tasks, and compatible with modern Android power optimizations.

---

## 8. Notification Implementation

### 8.1 Channel Setup

A notification channel is created for Android 8+ devices to ensure reminders are delivered correctly.

### 8.2 Message Rule for Roll 58

By assignment rule:

- 58 % 3 = 2
- Notification message: Time to read your notes

### 8.3 Notification Content

- Title: Reminder
- Text: Time to read your notes
- Tap action: opens notes list screen

This fulfills the roll-dependent notification requirement.

---

## 9. Accelerometer Sensor Integration

The accelerometer is integrated on the View Notes screen.

Behavior:

1. App registers an accelerometer listener while screen is active.
2. When shake threshold is detected, app displays message:
   Device motion detected
3. The notes list refreshes automatically after shake.

This satisfies the assignment requirement of sensor-based interaction.

---

## 10. Testing Summary

The following validations were performed:

1. App compiles and installs in debug mode.
2. Note save flow works with title, description, reminder_flag, image path, and date.
3. Notes are retrieved and listed through RecyclerView.
4. Background worker schedules periodically.
5. Notification appears when notes exist and permission is granted.
6. Shake gesture triggers motion message and list refresh.

---

## 11. Required Screenshots for Submission

Insert the following screenshots in the final Google Doc under this section.

### Figure 1: Main UI

- Show note title, description, reminder_flag, and all three buttons.
- Suggested caption: Main screen with note input and actions.

### Figure 2: Media Handling

- Show image selected/captured preview on main screen.
- Suggested caption: Image capture/select integrated with note form.

### Figure 3: Notes List (RecyclerView)

- Show at least 2 saved notes with visible fields.
- Suggested caption: Stored notes displayed using RecyclerView cards.

### Figure 4: Accelerometer Usage

- Show Device motion detected toast/message after shake.
- Suggested caption: Accelerometer-based shake detection and refresh.

### Figure 5: Notification

- Show reminder notification on notification tray.
- Suggested caption: Periodic WorkManager notification based on roll rule.

### Figure 6: Database Evidence (Optional but Recommended)

- Show table contents from Database Inspector / Device Explorer.
- Suggested caption: SQLite table notes_58 with stored note records.

---

## 12. Conclusion

The Media Notes application was successfully implemented according to assignment requirements for roll number 58. The app combines local data storage, media attachment, RecyclerView display, periodic WorkManager reminders, and accelerometer-based interaction in a complete Android workflow.

The implementation demonstrates practical use of:

- SQLite (data persistence)
- Activity Result APIs (media acquisition)
- RecyclerView (dynamic UI rendering)
- WorkManager + Notifications (background reminders)
- SensorManager (accelerometer integration)

Hence, the app meets the assignment objective and submission expectations.
