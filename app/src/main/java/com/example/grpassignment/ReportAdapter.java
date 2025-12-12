package com.example.grpassignment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private Context context;
    private List<Report> reportList;

    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);

        holder.txtTitle.setText(report.getType());
        
        // Format location and time for display
        String displayLoc = "Location: " + report.getLocation() + " | " + report.getTime();
        holder.txtLocationTime.setText(displayLoc);

        // If media URI exists, try to load it
        if (report.getMediaUri() != null) {
            holder.imgReport.setImageURI(Uri.parse(report.getMediaUri()));
        } else {
            // Set a default image
            holder.imgReport.setImageResource(R.drawable.screenshot_2025_11_18_130845_removebg_preview); 
        }

        // Handle "View Details" click
        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReportDetailActivity.class);
            intent.putExtra("report", report); // Pass the report object

            // CRITICAL FIX: Add this flag to grant URI permission to the new activity
            if (report.getMediaUri() != null) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView imgReport;
        TextView txtTitle, txtLocationTime;
        Button btnViewDetails;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            imgReport = itemView.findViewById(R.id.img_report);
            txtTitle = itemView.findViewById(R.id.txt_report_title);
            txtLocationTime = itemView.findViewById(R.id.txt_report_location_time);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
    }
}