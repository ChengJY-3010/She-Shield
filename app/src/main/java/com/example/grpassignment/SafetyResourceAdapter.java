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
import android.widget.Toast;

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
        if (resources != null) {
            this.resources.addAll(resources);
            this.filteredResources.addAll(resources);
        }
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

        setIconAndColor(holder, resource.getType());
        setCategoryColor(holder.categoryBadge, holder.categoryTextView, resource.getCategory());

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
        this.resources.clear();
        this.filteredResources.clear();
        if (newList != null) {
            this.resources.addAll(newList);
            this.filteredResources.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public void filterByType(String type) {
        filteredResources.clear();
        if (type.equalsIgnoreCase("All")) {
            filteredResources.addAll(resources);
        } else {
            for (SafetyResource resource : resources) {
                if (resource.getType() != null && resource.getType().equalsIgnoreCase(type)) {
                    filteredResources.add(resource);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void openResource(SafetyResource resource) {
        if ("Workshop".equals(resource.getType())) {
            // Open RegistrationActivity
            Intent intent = new Intent(context, RegistrationActivity.class);
            intent.putExtra("RESOURCE_TITLE", resource.getTitle());
            intent.putExtra("EVENT_DATE", resource.getEventDate());
            intent.putExtra("EVENT_TIME", resource.getEventTime());
            intent.putExtra("LOCATION", resource.getLocation());
            intent.putExtra("INSTRUCTOR", resource.getInstructor());
            intent.putExtra("CAPACITY", resource.getCapacity());
            intent.putExtra("DESCRIPTION", resource.getDescription());
            context.startActivity(intent);
        } else if (resource.getFile() != null && !resource.getFile().isEmpty()) {
            try {
                Uri uri = Uri.parse(resource.getFile());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Force open in browser
                browserIntent.setPackage("com.android.chrome");
                if (browserIntent.resolveActivity(context.getPackageManager()) == null) {
                    browserIntent.setPackage(null); // fallback to any browser
                }

                context.startActivity(browserIntent);
            } catch (Exception e) {
                Toast.makeText(context, "Cannot open link", Toast.LENGTH_SHORT).show();
                Log.e("SafetyResourceAdapter", "Error opening URL: " + e.getMessage());
            }
        } else {
            Toast.makeText(context, "No link available for this resource", Toast.LENGTH_SHORT).show();
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
