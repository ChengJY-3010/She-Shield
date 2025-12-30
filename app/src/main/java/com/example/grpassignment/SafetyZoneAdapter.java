package com.example.grpassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// NOTE: We are not importing any GeoPoint class to avoid ambiguity.

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SafetyZoneAdapter extends RecyclerView.Adapter<SafetyZoneAdapter.ViewHolder> {

    private List<SafetyZone> list = new ArrayList<>();
    // 1. Be explicit: Use the fully qualified name for the osmdroid GeoPoint.
    private org.osmdroid.util.GeoPoint userLocation;

    public void setData(List<SafetyZone> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    // 2. Be explicit: Use the fully qualified name for the osmdroid GeoPoint here as well.
    public void setUserLocation(org.osmdroid.util.GeoPoint location) {
        this.userLocation = location;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_safety_zone, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SafetyZone zone = list.get(position);
        holder.tvName.setText(zone.name);

        if (userLocation != null && zone.geolocation != null) {
            // Here, zone.geolocation is known to be com.google.firebase.firestore.GeoPoint from the SafetyZone class.

            // 3. Explicitly create a new osmdroid GeoPoint for the calculation.
            org.osmdroid.util.GeoPoint zonePoint = new org.osmdroid.util.GeoPoint(
                    zone.geolocation.getLatitude(),
                    zone.geolocation.getLongitude()
            );

            double distanceInMeters = userLocation.distanceToAsDouble(zonePoint);

            String distanceText;
            if (distanceInMeters < 1000) {
                distanceText = String.format(Locale.US, "📍 %.0f m", distanceInMeters);
            } else {
                double distanceInKm = distanceInMeters / 1000.0;
                distanceText = String.format(Locale.US, "📍 %.1f km", distanceInKm);
            }
            holder.tvDistance.setText(distanceText);
        } else {
            holder.tvDistance.setText("📍 Calculating…");
        }

        if (zone.is24hour) {
            holder.tvIs24hr.setText("24/7");
        } else {
            holder.tvIs24hr.setText("Limited");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvDistance, tvIs24hr;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvIs24hr = itemView.findViewById(R.id.tvIs24hr);
        }
    }
}
