package com.example.noteplus.model;

public class Note {
    private int id;
    private String title;
    private String date;
    private String content;

    public Note(int id, String title, String date, String content) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public Note(String title, String date, String content) {
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getContent() { return content; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setContent(String content) { this.content = content; }
}
