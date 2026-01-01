package com.example.grpassignment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity4 extends AppCompatActivity {
    private static final String TAG = "MainActivity4";

    private RecyclerView recyclerView;
    private SafetyResourceAdapter adapter;
    private List<SafetyResource> resourceList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    // Filter chips
    private CardView filterAll, filterVideos, filterArticles, filterWorkshops, filterLegal;
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safety_resources);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Setup filter chips
        setupFilterChips();

        // Load resources
        loadResources();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.resourcesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyTextView = findViewById(R.id.emptyTextView);

        filterAll = findViewById(R.id.filter_all);
        filterVideos = findViewById(R.id.filter_shelters);
        filterArticles = findViewById(R.id.filter_articles);
        filterWorkshops = findViewById(R.id.filter_workshop);
        filterLegal = findViewById(R.id.filter_legal);

        resourceList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SafetyResourceAdapter(this, resourceList);
        recyclerView.setAdapter(adapter);
    }

    private void setupFilterChips() {
        filterAll.setOnClickListener(v -> applyFilter("All", filterAll));
        filterVideos.setOnClickListener(v -> applyFilter("Videos", filterVideos));
        filterArticles.setOnClickListener(v -> applyFilter("Articles", filterArticles));
        filterWorkshops.setOnClickListener(v -> applyFilter("Workshops", filterWorkshops));
        filterLegal.setOnClickListener(v -> applyFilter("Legal", filterLegal));
    }

    private void applyFilter(String filterType, CardView selectedCard) {
        currentFilter = filterType;

        Log.d(TAG, "Applying filter: " + filterType);

        // Reset all chips to white background
        resetFilterChips();

        // Set selected chip to purple background
        selectedCard.setCardBackgroundColor(Color.parseColor("#F3E8FF"));

        // Apply filter to adapter
        adapter.filterByType(filterType);

        // Update empty view
        updateEmptyView();
    }

    private void resetFilterChips() {
        filterAll.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        filterVideos.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        filterArticles.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        filterWorkshops.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        filterLegal.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    private void loadResources() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);

        Log.d(TAG, "Loading resources from Firestore...");

        db.collection("safety_resource")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        resourceList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SafetyResource resource = document.toObject(SafetyResource.class);
                            resource.setId(document.getId());
                            resourceList.add(resource);

                            Log.d(TAG, "Loaded resource: " + resource.getTitle() + " - Type: " + resource.getType());
                        }

                        Log.d(TAG, "Total resources loaded: " + resourceList.size());

                        adapter.notifyDataSetChanged();
                        updateEmptyView();

                        Toast.makeText(this, "Loaded " + resourceList.size() + " resources",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Log.e(TAG, "Error loading resources", task.getException());
                        Toast.makeText(this, "Error loading resources: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_LONG).show();
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText("Failed to load resources");
                    }
                });
    }

    private void updateEmptyView() {
        if (adapter.getItemCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setText("No " + (currentFilter.equals("All") ? "" : currentFilter + " ") + "resources found");
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}