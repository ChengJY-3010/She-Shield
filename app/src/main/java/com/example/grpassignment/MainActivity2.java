package com.example.grpassignment; // CHECK THIS: Make sure this matches your package name

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity2 extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int FINE_PERMISSION_CODE = 1;

    private Marker currentMarker;
    private Button btnConfirmReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community_report);

        // Initialize the button
        btnConfirmReport = findViewById(R.id.btn_confirm_report);


        // Initialize the Location Client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check if we have permission to access location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
        }

        // LISTENER: When user taps the map
        mMap.setOnMapClickListener(point -> {
            // 1. Remove the old marker if it exists
            if (currentMarker != null) {
                currentMarker.remove();
            }

            // 2. Add a new marker at the clicked location
            currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title("Selected Location"));

            // 3. Animate camera to the new point
            mMap.animateCamera(CameraUpdateFactory.newLatLng(point));

            // 4. Show the "Report This Location" button
            if (btnConfirmReport != null) {
                btnConfirmReport.setVisibility(View.VISIBLE);

                // 5. Set the button action to open the Report Form
                btnConfirmReport.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity2.this, PostReportActivity.class);
                    // Pass the coordinates to the form
                    intent.putExtra("LAT", point.latitude);
                    intent.putExtra("LNG", point.longitude);
                    startActivity(intent);
                });
            }
        });
    }


    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // 1. Enable the Blue Dot on the map
        mMap.setMyLocationEnabled(true);

        // 2. Get the last known location and move the camera
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());

                    // NEW: Save the initial marker to our variable
                    currentMarker = mMap.addMarker(new MarkerOptions().position(currentLoc).title("You are Here"));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15.0f));
                }
            }
        });
    }

    // Handle the result of the permission request popup
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(this, "Location permission is denied, please allow it in settings.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
