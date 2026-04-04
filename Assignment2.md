# **Assignment : Media Notes App with Sensor and Notification (Deadline 6th April 2026\)**

1. ### **Objective**

Develop a mobile application that allows users to save notes with images and receive periodic notifications.

2. ### **Problem Statement**

Create an Android application where users can **save notes along with images or videos** and receive periodic notifications reminding them to review their notes.

3. ### **Functional Requirements**

   1. ### **User Interface**

   Create a screen with:

* Note Title  
* Note Description  
* Button: **Capture Image / Select Image**  
* Button: **Save Note**  
* Button: **View Notes**


  2. ### **Database Implementation**

  Create a SQLite database.

  Database name:

  NotesDB\_RollNo

  Example  
     Roll No 30 → `NotesDB_30`

  Table name:

  notes\_RollNo


  Example  
     Roll No 30 → `notes_30`

  Basic fields: id, title, description, image\_path, date


**\*Store note details along with image path.**

3. ### **Table Personalization Rule**

Add **one extra field depending on roll number**.

| Roll No % 4 | Extra Field |
| ----- | ----- |
| 0 | note\_type |
| 1 | priority |
| 2 | reminder\_flag |
| 3 | category |

Example

Roll No 17  
17 % 4 \= 1

Extra field \= `priority`

4. ### **Display Notes**

   Display stored notes using:

* ListView or  
* RecyclerView


  5. ### **Background Task**

  Implement background task using:

* WorkManager  
  or  
* Android Service  
  The task will periodically check saved notes.

  6. ### **Notification Rule**

Notification text depends on roll number.

| Roll No % 3 | Notification Message |
| ----- | ----- |
| 0 | Review your saved notes today |
| 1 | Time to read your notes |
| 2 | Check your notes and stay prepared |

7. ### **Accelerometer Sensor Integration**

   Use the **accelerometer sensor**.

   Example functionality:

* When the phone is **shaken**, display a message: Device motion detected  
  or refresh the notes list.


  8. ### **Notification Feature**

  Generate a notification such as:

     Reminder: Review your saved notes today


# 

# **Submission Requirements**

Students must submit a report in google docs for both assignments:

1. **Short report (2–3 pages)** explaining:  
   * Database structure  
   * Background task  
   * Notification implementation  
2. **Screenshots**  
   * UI  
   * Sensor usage  
   * Media handling  
   * Notification  
3. **Source code**

