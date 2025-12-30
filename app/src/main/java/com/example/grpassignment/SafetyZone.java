package com.example.grpassignment;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Exclude;

public class SafetyZone {

    public String name;
    public GeoPoint geolocation;
    public boolean is24hour;
    public String type;
    public String phone;

    // This field will not be read from or saved to Firestore.
    // We will use it to store the calculated distance for sorting.
    @Exclude
    public double distanceToUser;

    public SafetyZone() {}

}
