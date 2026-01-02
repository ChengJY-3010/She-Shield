package com.example.grpassignment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SafetyResourceAdapter extends RecyclerView.Adapter<SafetyResourceAdapter.ViewHolder> {

    private final List<SafetyResource> resources;
    private final List<SafetyResource> filteredResources;
    private final Context context;

    public SafetyResourceAdapter(Context context, List<SafetyResource> resources) {
        this.context = context;
        this.resources = new ArrayList<>();
        this.filteredResources = new ArrayList<>();
        Log.d("SafetyResourceAdapter", "Constructor called");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_safety_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SafetyResource resource = filteredResources.get(position);

        holder.titleTextView.setText(resource.getTitle());
        holder.categoryTextView.setText(resource.getCategory());
        holder.durationTextView.setText(resource.getDuration());

        // Set icon based on type
        setIconAndColor(holder, resource.getType());

        // Set category badge color
        setCategoryColor(holder.categoryBadge, holder.categoryTextView, resource.getCategory());

        // Click listener to open resource
        holder.cardView.setOnClickListener(v -> openResource(resource));
    }

    @Override
    public int getItemCount() {
        return filteredResources.size();
    }

    private void setIconAndColor(ViewHolder holder, String type) {
        switch (type) {
            case "Video":
                holder.iconCard.setCardBackgroundColor(Color.parseColor("#FEE2E2"));
                holder.iconImageView.setImageResource(R.drawable.image_removebg_preview__26_);
                break;
            case "Article":
                holder.iconCard.setCardBackgroundColor(Color.parseColor("#DBEAFE"));
                holder.iconImageView.setImageResource(R.drawable.image_removebg_preview__24_);
                break;
            case "Workshop":
                holder.iconCard.setCardBackgroundColor(Color.parseColor("#FEE2E2"));
                holder.iconImageView.setImageResource(R.drawable.workshop);
                break;
            case "Legal":
                holder.iconCard.setCardBackgroundColor(Color.parseColor("#DBEAFE"));
                holder.iconImageView.setImageResource(R.drawable.legal);
                break;
        }
    }

    private void setCategoryColor(CardView badgeCard, TextView badgeText, String category) {
        badgeCard.setCardBackgroundColor(Color.parseColor("#D1FAE5"));
        badgeText.setTextColor(Color.parseColor("#047857"));
        badgeText.setText(category);
    }

    public void updateList(List<SafetyResource> newList) {
        Log.d("SafetyResourceAdapter", "🔥 ===== UPDATE LIST START =====");
        Log.d("SafetyResourceAdapter", "🔥 newList size: " + (newList != null ? newList.size() : "NULL"));

        if (newList != null && !newList.isEmpty()) {
            Log.d("SafetyResourceAdapter", "🔥 First item in newList: " + newList.get(0).getTitle());
            Log.d("SafetyResourceAdapter", "🔥 First item type: " + newList.get(0).getType());
        }

        this.resources.clear();
        if (newList != null) {
            this.resources.addAll(newList);
        }

        this.filteredResources.clear();
        if (newList != null) {
            this.filteredResources.addAll(newList);
        }

        Log.d("SafetyResourceAdapter", "🔥 After update - resources.size(): " + this.resources.size());
        Log.d("SafetyResourceAdapter", "🔥 After update - filteredResources.size(): " + this.filteredResources.size());

        notifyDataSetChanged();
        Log.d("SafetyResourceAdapter", "🔥 ===== UPDATE LIST END =====");
    }

    public void filterByType(String type) {
        Log.d("SafetyResourceAdapter", "=== FILTER DEBUG START ===");
        Log.d("SafetyResourceAdapter", "Filter requested: " + type);
        Log.d("SafetyResourceAdapter", "Total resources before filter: " + resources.size());

        filteredResources.clear();

        if (type.equalsIgnoreCase("All")) {
            filteredResources.addAll(resources);
            Log.d("SafetyResourceAdapter", "Showing ALL resources");
        } else {
            for (SafetyResource resource : resources) {
                Log.d("SafetyResourceAdapter", "Checking: " + resource.getTitle() +
                        " | Type: '" + resource.getType() + "' | Filter: '" + type + "'");

                if (resource.getType() != null && resource.getType().equalsIgnoreCase(type)) {
                    filteredResources.add(resource);
                    Log.d("SafetyResourceAdapter", "✓ MATCHED!");
                } else {
                    Log.d("SafetyResourceAdapter", "✗ Not matched");
                }
            }
        }

        Log.d("SafetyResourceAdapter", "Filtered resources count: " + filteredResources.size());
        Log.d("SafetyResourceAdapter", "=== FILTER DEBUG END ===");

        notifyDataSetChanged();
    }

    private void openResource(SafetyResource resource) {
        // Workshop navigation - goes to RegistrationActivity
        if ("Workshop".equals(resource.getType())) {
            Intent intent = new Intent(context, RegistrationActivity.class);
            intent.putExtra("RESOURCE_TITLE", resource.getTitle());
            intent.putExtra("EVENT_DATE", resource.getEventDate());
            intent.putExtra("EVENT_TIME", resource.getEventTime());
            intent.putExtra("LOCATION", resource.getLocation());
            intent.putExtra("INSTRUCTOR", resource.getInstructor());
            intent.putExtra("CAPACITY", resource.getCapacity());
            intent.putExtra("DESCRIPTION", resource.getDescription());
            // Don't use any flags that clear the back stack
            context.startActivity(intent);
            Log.d("SafetyResourceAdapter", "Opening workshop registration: " + resource.getTitle());
        }
        // Video, Article, Legal - opens external URL if available
        else if (resource.getFile() != null && !resource.getFile().isEmpty()) {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resource.getFile()));
                browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                context.startActivity(browserIntent);
                Log.d("SafetyResourceAdapter", "Opening external URL: " + resource.getFile());
            } catch (Exception e) {
                Log.e("SafetyResourceAdapter", "Error opening URL: " + e.getMessage());
            }
        } else {
            Log.d("SafetyResourceAdapter", "No URL available for: " + resource.getTitle());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView, iconCard, categoryBadge;
        ImageView iconImageView;
        TextView titleTextView, categoryTextView, durationTextView;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            iconCard = itemView.findViewById(R.id.iconCard);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            categoryBadge = itemView.findViewById(R.id.categoryBadge);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
        }
    }
}