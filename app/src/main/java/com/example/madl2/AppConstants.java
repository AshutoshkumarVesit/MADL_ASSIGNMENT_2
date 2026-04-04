package com.example.madl2;

public final class AppConstants {
    public static final String DB_NAME = "NotesDB_53";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "notes_53";

    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_IMAGE_PATH = "image_path";
    public static final String COL_DATE = "date";
    public static final String COL_PRIORITY = "priority";

    public static final String WORK_NAME = "notes_periodic_reminder";
    public static final String NOTIFICATION_CHANNEL_ID = "notes_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "Notes Reminder";
    public static final int NOTIFICATION_ID = 5302;

    public static final String NOTIFICATION_MESSAGE = "Check your notes and stay prepared";

    private AppConstants() {
    }
}
