package com.example.grpassignment;

import com.google.firebase.Timestamp;

public class SafetyResource {
    private String id;
    private String title;
    private String type;
    private String category;
    private String duration;
    private String format;
    private String file;

    // Workshop-specific fields
    private Timestamp eventTimestamp;
    private String eventDate;
    private String eventTime;
    private String location;
    private String instructor;
    private int capacity;
    private String description;

    // Empty constructor required for Firebase
    public SafetyResource() {
    }

    // Full constructor
    public SafetyResource(String id, String title, String type, String category,
                          String duration, String format, String file,
                          Timestamp eventTimestamp, String eventDate, String eventTime,
                          String location, String instructor, int capacity, String description) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.category = category;
        this.duration = duration;
        this.format = format;
        this.file = file;
        this.eventTimestamp = eventTimestamp;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.instructor = instructor;
        this.capacity = capacity;
        this.description = description;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Timestamp getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(Timestamp eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SafetyResource{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", duration='" + duration + '\'' +
                ", format='" + format + '\'' +
                ", file='" + file + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", location='" + location + '\'' +
                ", instructor='" + instructor + '\'' +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                '}';
    }
}