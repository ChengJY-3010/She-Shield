package com.example.grpassignment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {

    private MapView mapPreview;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker selectedLocationMarker;
    private Button btnConfirmReport;
    private ImageButton btnZoomIn, btnZoomOut;

    // Report List
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> reportList = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // OSM Droid configuration
        Context ctx = requireContext().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        btnConfirmReport = view.findViewById(R.id.btn_confirm_report);
        mapPreview = view.findViewById(R.id.map_view);
        btnZoomIn = view.findViewById(R.id.btn_zoom_in);
        btnZoomOut = view.findViewById(R.id.btn_zoom_out);
        recyclerView = view.findViewById(R.id.recycler_reports);

        setupMap();
        setupZoomControls();
        setupRecyclerView();
        loadReportsFromFirestore(); // Load reports from Firestore

        btnConfirmReport.setOnClickListener(v -> {
            if (selectedLocationMarker != null) {
                Intent intent = new Intent(getActivity(), PostReportActivity.class);
                intent.putExtra("latitude", selectedLocationMarker.getPosition().getLatitude());
                intent.putExtra("longitude", selectedLocationMarker.getPosition().getLongitude());
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadReportsFromFirestore() {
        db.collection("reports")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Show newest reports first
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("ReportFragment", "Listen failed.", e);
                        return;
                    }

                    List<Report> newReports = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Report report = doc.toObject(Report.class);
                        newReports.add(report);
                    }
                    reportList.clear();
                    reportList.addAll(newReports);
                    reportAdapter.notifyDataSetChanged();
                });
    }

    private void setupRecyclerView() {
        reportAdapter = new ReportAdapter(getContext(), reportList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(reportAdapter);
    }

    private void setupMap() {
        if (mapPreview != null) {
            mapPreview.setMultiTouchControls(true);
            mapPreview.setBuiltInZoomControls(false);
            mapPreview.getController().setZoom(15.0);
            GeoPoint defaultPoint = new GeoPoint(3.1390, 101.6869);
            mapPreview.getController().setCenter(defaultPoint);
        }

        enableMyLocation();

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                updateSelectedLocation(p);
                return true;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) { return false; }
        };
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(mReceive);
        mapPreview.getOverlays().add(OverlayEvents);
    }

    private void setupZoomControls() {
        if (btnZoomIn != null) {
            btnZoomIn.setOnClickListener(v -> {
                if (mapPreview != null) mapPreview.getController().zoomIn();
            });
        }
        if (btnZoomOut != null) {
            btnZoomOut.setOnClickListener(v -> {
                if (mapPreview != null) mapPreview.getController().zoomOut();
            });
        }
    }

    private void updateSelectedLocation(GeoPoint p) {
        if (selectedLocationMarker != null) mapPreview.getOverlays().remove(selectedLocationMarker);
        selectedLocationMarker = new Marker(mapPreview);
        selectedLocationMarker.setPosition(p);
        selectedLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        selectedLocationMarker.setTitle("Selected Location");
        mapPreview.getOverlays().add(selectedLocationMarker);
        btnConfirmReport.setVisibility(View.VISIBLE);
        mapPreview.invalidate();
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapPreview);
            myLocationOverlay.enableMyLocation();
            myLocationOverlay.enableFollowLocation();
            mapPreview.getOverlays().add(myLocationOverlay);

            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    GeoPoint currentGeo = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapPreview.getController().setCenter(currentGeo);
                }
            });
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (mapPreview != null) mapPreview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapPreview != null) mapPreview.onPause();
    }
}
