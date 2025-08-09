package com.example.noteplus.model;

public class Note {
    private int id;
    private String title;
    private String date;

    public Note(int id, String title, String date) {
        this.id = id;
        this.title = title;
        this.date = date;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
}