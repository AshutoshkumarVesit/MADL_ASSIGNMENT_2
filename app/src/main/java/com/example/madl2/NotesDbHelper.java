package com.example.madl2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class NotesDbHelper extends SQLiteOpenHelper {

    public NotesDbHelper(Context context) {
        super(context, AppConstants.DB_NAME, null, AppConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + AppConstants.TABLE_NAME + " ("
                + AppConstants.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AppConstants.COL_TITLE + " TEXT NOT NULL, "
                + AppConstants.COL_DESCRIPTION + " TEXT, "
                + AppConstants.COL_IMAGE_PATH + " TEXT, "
                + AppConstants.COL_DATE + " TEXT, "
                + AppConstants.COL_PRIORITY + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AppConstants.TABLE_NAME);
        onCreate(db);
    }

    public long insertNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AppConstants.COL_TITLE, note.getTitle());
        values.put(AppConstants.COL_DESCRIPTION, note.getDescription());
        values.put(AppConstants.COL_IMAGE_PATH, note.getImagePath());
        values.put(AppConstants.COL_DATE, note.getDate());
        values.put(AppConstants.COL_PRIORITY, note.getPriority());
        return db.insert(AppConstants.TABLE_NAME, null, values);
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                AppConstants.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                AppConstants.COL_ID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Note note = new Note();
                note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(AppConstants.COL_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(AppConstants.COL_TITLE)));
                note.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(AppConstants.COL_DESCRIPTION)));
                note.setImagePath(cursor.getString(cursor.getColumnIndexOrThrow(AppConstants.COL_IMAGE_PATH)));
                note.setDate(cursor.getString(cursor.getColumnIndexOrThrow(AppConstants.COL_DATE)));
                note.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(AppConstants.COL_PRIORITY)));
                notes.add(note);
            }
            cursor.close();
        }
        return notes;
    }

    public int getNotesCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + AppConstants.TABLE_NAME, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
}
