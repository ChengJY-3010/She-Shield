package com.example.grpassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SafetyZoneAdapter extends RecyclerView.Adapter<SafetyZoneAdapter.ViewHolder> {

    private List<SafetyZone> list = new ArrayList<>();
    private org.osmdroid.util.GeoPoint userLocation;

    public void setData(List<SafetyZone> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

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

        // --- LOAD IMAGE WITH GLIDE ---
        if (zone.imageUrl != null && !zone.imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(zone.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Placeholder while loading
                    .error(android.R.drawable.ic_dialog_alert) // Image to show if URL is invalid
                    .into(holder.iconType);
        }
        // --------------------------
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvDistance, tvIs24hr;
        ImageView iconType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvIs24hr = itemView.findViewById(R.id.tvIs24hr);
            iconType = itemView.findViewById(R.id.iconType);
        }
    }
}
