package com.example.grpassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SafetyResourcesFragment extends Fragment {

    private static final String TAG = "SafetyResourcesFragment";

    private RecyclerView recyclerView;
    private SafetyResourceAdapter adapter;
    private List<SafetyResource> resourceList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView emptyTextView;

    // Filter chips
    private CardView filterAll, filterVideos, filterArticles, filterWorkshops, filterLegal;
    private String currentFilter = "All";

    // Workshops
    private List<SafetyResource> upcomingWorkshops;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safety_resources, container, false);

        // Firebase initialization
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);

        // Initialize UI components
        recyclerView = view.findViewById(R.id.resourcesRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        filterAll = view.findViewById(R.id.filter_all);
        filterVideos = view.findViewById(R.id.filter_shelters);
        filterArticles = view.findViewById(R.id.filter_articles);
        filterWorkshops = view.findViewById(R.id.filter_workshop);
        filterLegal = view.findViewById(R.id.filter_legal);

        resourceList = new ArrayList<>();
        upcomingWorkshops = new ArrayList<>();

        setupRecyclerView();
        setupFilterChips();
        loadResources();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SafetyResourceAdapter(getContext(), resourceList);
        recyclerView.setAdapter(adapter);
    }

    private void setupFilterChips() {
        filterAll.setOnClickListener(v -> applyFilter("All", filterAll));
        filterVideos.setOnClickListener(v -> applyFilter("Video", filterVideos));
        filterArticles.setOnClickListener(v -> applyFilter("Article", filterArticles));
        filterWorkshops.setOnClickListener(v -> applyFilter("Workshop", filterWorkshops));
        filterLegal.setOnClickListener(v -> applyFilter("Legal", filterLegal));
    }

    private void applyFilter(String filterType, CardView selectedCard) {
        currentFilter = filterType;
        resetFilterChips();
        selectedCard.setCardBackgroundColor(0xFFF3E8FF); // Highlight

        if (adapter != null) {
            adapter.filterByType(filterType);
            updateEmptyView();
        }
    }

    private void resetFilterChips() {
        int white = 0xFFFFFFFF;
        filterAll.setCardBackgroundColor(white);
        filterVideos.setCardBackgroundColor(white);
        filterArticles.setCardBackgroundColor(white);
        filterWorkshops.setCardBackgroundColor(white);
        filterLegal.setCardBackgroundColor(white);
    }

    private void loadResources() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);

        db.collection("safety_resource")
                .get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful() && task.getResult() != null) {
                        resourceList.clear();
                        upcomingWorkshops.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SafetyResource resource = document.toObject(SafetyResource.class);
                            resource.setId(document.getId());
                            resourceList.add(resource);

                            // Collect workshops
                            if ("Workshop".equals(resource.getType())) {
                                upcomingWorkshops.add(resource);
                            }
                        }

                        // Sort workshops by date
                        Collections.sort(upcomingWorkshops, (w1, w2) -> {
                            if (w1.getEventTimestamp() != null && w2.getEventTimestamp() != null) {
                                return w1.getEventTimestamp().compareTo(w2.getEventTimestamp());
                            }
                            return 0;
                        });

                        adapter.updateList(resourceList);
                        adapter.filterByType("All");

                        updateEmptyView();
                    } else {
                        Toast.makeText(getContext(),
                                "Failed to load resources. Check internet connection.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateEmptyView() {
        if (adapter == null || adapter.getItemCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setText("No " + (currentFilter.equals("All") ? "" : currentFilter + " ") + "resources found");
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
