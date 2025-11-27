package com.example.grpassignment; // <--- CHECK YOUR PACKAGE NAME

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull; // Import this for @NonNull
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

// 1. IMPLEMENT OnMapReadyCallback
public class PostReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    // UI Components
    private Spinner spinnerType;
    private EditText editDate, editTime, editDescription;
    private ImageView btnDate, btnTime, btnCurrentLocation;
    private Button btnSubmit;

    // Map Variables
    private double selectedLat;
    private double selectedLng;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_report);

        // --- Initialize Views ---
        spinnerType = findViewById(R.id.spinner_incident_type);

        editDate = findViewById(R.id.edit_date);
        editTime = findViewById(R.id.edit_time);
        editDescription = findViewById(R.id.edit_description);

        btnDate = findViewById(R.id.icon_date_picker);
        btnTime = findViewById(R.id.icon_time_picker);

        btnSubmit = findViewById(R.id.btn_submit_report);

        // Back button in toolbar
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        // --- 2. GET COORDINATES & INIT MAP ---
        Intent intent = getIntent();
        if (intent.hasExtra("LAT") && intent.hasExtra("LNG")) {
            selectedLat = intent.getDoubleExtra("LAT", 0);
            selectedLng = intent.getDoubleExtra("LNG", 0);
        }

        // Initialize the Map Fragment (Make sure ID matches your XML)
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_preview_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // --- Setup Date Picker ---
        View.OnClickListener dateClickListener = v -> showDatePicker();
        editDate.setOnClickListener(dateClickListener);
        btnDate.setOnClickListener(dateClickListener);

        // --- Setup Time Picker ---
        View.OnClickListener timeClickListener = v -> showTimePicker();
        editTime.setOnClickListener(timeClickListener);
        btnTime.setOnClickListener(timeClickListener);

        // --- Setup Submit Button ---
        btnSubmit.setOnClickListener(v -> submitReport());
    }

    // --- 3. ADD ON MAP READY METHOD ---
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Create the LatLng object from the passed data
        LatLng location = new LatLng(selectedLat, selectedLng);

        // Add a marker at the selected location
        mMap.addMarker(new MarkerOptions().position(location).title("Incident Location"));

        // Move the camera to the location and zoom in
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));

        // Optional: Disable gestures so user can't scroll away (makes it a static preview)
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    editDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                    int currentHour = (hourOfDay > 12) ? (hourOfDay - 12) : hourOfDay;
                    if (currentHour == 0) currentHour = 12;

                    String minuteString = (minute1 < 10) ? "0" + minute1 : String.valueOf(minute1);
                    editTime.setText(currentHour + ":" + minuteString + " " + amPm);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void submitReport() {
        String type = spinnerType.getSelectedItem().toString();
        String locationString = selectedLat + "," + selectedLng;
        String date = editDate.getText().toString();
        String time = editTime.getText().toString();
        String desc = editDescription.getText().toString();

        if (date.isEmpty() || time.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("TYPE", type);
        resultIntent.putExtra("LOCATION", locationString);
        resultIntent.putExtra("DATE", date);
        resultIntent.putExtra("TIME", time);
        resultIntent.putExtra("DESC", desc);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
