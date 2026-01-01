package com.example.grpassignment;

import com.google.firebase.Timestamp;

public class SafetyResource {
    private String id;
    private String title;
    private String type;        // Videos, Articles, Workshops, Legal
    private String category;    // Training, Tech, Travel, etc.
    private String duration;
    private String format;      // video or article
    private String file;        // URL link
    private Timestamp timestamp;

    // Empty constructor for Firebase
    public SafetyResource() {}

    // Constructor
    public SafetyResource(String title, String type, String category, String duration,
                          String format, String file) {
        this.title = title;
        this.type = type;
        this.category = category;
        this.duration = duration;
        this.format = format;
        this.file = file;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
