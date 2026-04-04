# Media Notes App (Roll No: 53)

Android app for Assignment 2: save notes with images, view them in a list, get periodic reminders, and use accelerometer shake detection.

## Tech Stack

- Language: Java
- UI: XML layouts + RecyclerView
- Database: SQLite (SQLiteOpenHelper)
- Background work: WorkManager
- Notifications: NotificationCompat + channel
- Sensor: Accelerometer (SensorManager)

## Assignment Mapping

- Roll number: `53`
- `53 % 4 = 1` -> extra DB field: `priority`
- `53 % 3 = 2` -> notification text: `Check your notes and stay prepared`

## Features

- Add note with:
  - Title
  - Description
  - Priority
  - Image (capture from camera or select from gallery)
- Save note to SQLite
- View notes in RecyclerView cards
- Shake on notes screen -> shows `Device motion detected` and refreshes list
- Periodic reminder notification using WorkManager (15 min periodic work)

## Database

- DB name: `NotesDB_53`
- Table name: `notes_53`
- Columns:
  - `id` (INTEGER PRIMARY KEY AUTOINCREMENT)
  - `title` (TEXT NOT NULL)
  - `description` (TEXT)
  - `image_path` (TEXT)
  - `date` (TEXT)
  - `priority` (TEXT)

## Project Structure

- App entry: `app/src/main/java/com/example/madl2/MainActivity.java`
- Notes list: `app/src/main/java/com/example/madl2/NotesListActivity.java`
- DB helper: `app/src/main/java/com/example/madl2/NotesDbHelper.java`
- Worker: `app/src/main/java/com/example/madl2/NotificationWorker.java`
- Main UI: `app/src/main/res/layout/activity_main.xml`
- Notes UI: `app/src/main/res/layout/activity_notes_list.xml`

## Build and Run

1. Connect Android device or start emulator.
2. Build debug APK:

```bash
./gradlew assembleDebug
```

3. Install on connected device:

```bash
./gradlew installDebug
```

## Permissions Used

- `CAMERA`
- `POST_NOTIFICATIONS` (Android 13+)

## Notes

- Notification appears only if at least one note exists in DB.
- WorkManager timing is approximate and may be delayed by Android battery optimizations.
- SQLite journal file (e.g., `NotesDB_53-journal`) is expected behavior.
