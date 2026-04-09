package com.example.madl2;

public class Note {
    private long id;
    private String title;
    private String description;
    private String imagePath;
    private String date;
    private String reminderFlag;

    public Note() {
    }

    public Note(long id, String title, String description, String imagePath, String date, String reminderFlag) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.date = date;
        this.reminderFlag = reminderFlag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReminderFlag() {
        return reminderFlag;
    }

    public void setReminderFlag(String reminderFlag) {
        this.reminderFlag = reminderFlag;
    }
}
