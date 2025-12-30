package com.example.grpassignment;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

public class SafetyZone {

    public String name;
    public GeoPoint geolocation;
    public boolean is24hour;
    public String type;
    public String phone;
    public String imageUrl; // Field for the image URL

    @Exclude
    public double distanceToUser;

    public SafetyZone() {}

}
