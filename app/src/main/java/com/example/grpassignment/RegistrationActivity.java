package com.example.grpassignment;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";

    private Spinner workshopSpinner;
    private EditText nameInput, emailInput;
    private Button registerBtn;
    private ImageView backBtn;
    private FirebaseFirestore db;

    // Store workshop display strings and their corresponding IDs
    private List<String> workshopDisplayList;
    private List<String> workshopIdList;
    private Map<String, WorkshopData> workshopDataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        workshopDisplayList = new ArrayList<>();
        workshopIdList = new ArrayList<>();
        workshopDataMap = new HashMap<>();

        // Initialize Views
        workshopSpinner = findViewById(R.id.workshopSpinner);
        nameInput = findViewById(R.id.userName);
        emailInput = findViewById(R.id.userEmail);
        registerBtn = findViewById(R.id.btnRegister);
        backBtn = findViewById(R.id.backBtn);

        // Setup Toolbar Back Button
        backBtn.setOnClickListener(v -> finish());

        // Setup Spinner with placeholder
        workshopDisplayList.add("Loading workshops...");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                workshopDisplayList
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workshopSpinner.setAdapter(spinnerAdapter);

        // Load workshops from Firestore
        loadWorkshopsFromFirestore(spinnerAdapter);

        // Handle Registration
        registerBtn.setOnClickListener(v -> handleRegistration());
    }

    private void loadWorkshopsFromFirestore(ArrayAdapter<String> spinnerAdapter) {
        db.collection("safety_resource")
                .whereEqualTo("type", "Workshop")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        workshopDisplayList.clear();
                        workshopIdList.clear();
                        workshopDataMap.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String title = document.getString("title");
                            String eventDate = document.getString("eventDate");
                            String eventTime = document.getString("eventTime");
                            String location = document.getString("location");
                            String instructor = document.getString("instructor");
                            String description = document.getString("description");

                            // Get capacity safely
                            int capacity = 0;
                            Object capacityObj = document.get("capacity");
                            if (capacityObj instanceof Long) {
                                capacity = ((Long) capacityObj).intValue();
                            } else if (capacityObj instanceof Integer) {
                                capacity = (Integer) capacityObj;
                            }

                            // Create display string with title, date, and time
                            String displayText = title;
                            if (eventDate != null && !eventDate.isEmpty()) {
                                displayText += " - " + eventDate;
                            }
                            if (eventTime != null && !eventTime.isEmpty()) {
                                displayText += " @ " + eventTime;
                            }

                            // Store the data
                            workshopDisplayList.add(displayText);
                            workshopIdList.add(id);
                            workshopDataMap.put(id, new WorkshopData(
                                    id, title, eventDate, eventTime, location,
                                    instructor, capacity, description
                            ));

                            Log.d(TAG, "Loaded workshop: " + displayText);
                        }

                        if (workshopDisplayList.isEmpty()) {
                            workshopDisplayList.add("No workshops available");
                        }

                        spinnerAdapter.notifyDataSetChanged();

                        // Auto-select the workshop passed from previous screen
                        String passedTitle = getIntent().getStringExtra("RESOURCE_TITLE");
                        if (passedTitle != null) {
                            // Find matching workshop by title
                            for (int i = 0; i < workshopDisplayList.size(); i++) {
                                if (workshopDisplayList.get(i).startsWith(passedTitle)) {
                                    workshopSpinner.setSelection(i);
                                    break;
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching workshops", task.getException());
                        Toast.makeText(this, "Failed to load workshops", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleRegistration() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        int selectedPosition = workshopSpinner.getSelectedItemPosition();
        String selectedDisplayText = workshopSpinner.getSelectedItem() != null ?
                workshopSpinner.getSelectedItem().toString() : "";

        // Validations
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            nameInput.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            emailInput.requestFocus();
            return;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            emailInput.requestFocus();
            return;
        }

        if (selectedDisplayText.equals("Loading workshops...") ||
                selectedDisplayText.equals("No workshops available")) {
            Toast.makeText(this, "Please select a valid workshop", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the workshop ID and data
        if (selectedPosition < 0 || selectedPosition >= workshopIdList.size()) {
            Toast.makeText(this, "Please select a valid workshop", Toast.LENGTH_SHORT).show();
            return;
        }

        String workshopId = workshopIdList.get(selectedPosition);
        WorkshopData workshopData = workshopDataMap.get(workshopId);

        if (workshopData == null) {
            Toast.makeText(this, "Error: Workshop data not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent double submission
        registerBtn.setEnabled(false);

        // Prepare data for Firebase
        Map<String, Object> registration = new HashMap<>();
        registration.put("userName", name);
        registration.put("userEmail", email);
        registration.put("workshopId", workshopId);
        registration.put("workshopTitle", workshopData.title);
        registration.put("eventDate", workshopData.eventDate);
        registration.put("eventTime", workshopData.eventTime);
        registration.put("location", workshopData.location);
        registration.put("instructor", workshopData.instructor);
        registration.put("capacity", workshopData.capacity);
        registration.put("registrationTime", Timestamp.now());
        registration.put("status", "registered"); // Add status field

        Log.d(TAG, "Attempting to register for: " + workshopData.title);

        // Save to Firestore collection
        db.collection("workshop_registrations")
                .add(registration)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Registration successful! Document ID: " + documentReference.getId());
                    Toast.makeText(RegistrationActivity.this,
                            "Successfully registered for " + workshopData.title + "!",
                            Toast.LENGTH_LONG).show();

                    // Clear form
                    nameInput.setText("");
                    emailInput.setText("");

                    // Return to previous screen after a short delay
                    nameInput.postDelayed(() -> finish(), 1500);
                })
                .addOnFailureListener(e -> {
                    registerBtn.setEnabled(true); // Re-enable button on failure
                    Log.e(TAG, "Registration failed", e);

                    String errorMessage = "Registration failed: ";
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("PERMISSION_DENIED")) {
                            errorMessage += "Permission denied. Please check Firestore security rules.";
                        } else {
                            errorMessage += e.getMessage();
                        }
                    }

                    Toast.makeText(RegistrationActivity.this,
                            errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    // Helper class to store workshop data
    private static class WorkshopData {
        String id;
        String title;
        String eventDate;
        String eventTime;
        String location;
        String instructor;
        int capacity;
        String description;

        WorkshopData(String id, String title, String eventDate, String eventTime,
                     String location, String instructor, int capacity, String description) {
            this.id = id;
            this.title = title;
            this.eventDate = eventDate;
            this.eventTime = eventTime;
            this.location = location;
            this.instructor = instructor;
            this.capacity = capacity;
            this.description = description;
        }
    }

}