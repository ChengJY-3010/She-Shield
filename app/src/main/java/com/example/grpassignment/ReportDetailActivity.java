package com.example.grpassignment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ReportDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        // --- Setup Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar_report_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Find views
        ImageView imageView = findViewById(R.id.detail_image_view);
        VideoView videoView = findViewById(R.id.detail_video_view);
        TextView noMediaText = findViewById(R.id.no_media_text);
        TextView incidentType = findViewById(R.id.detail_incident_type);
        TextView severity = findViewById(R.id.detail_severity);
        TextView dateTime = findViewById(R.id.detail_date_time);
        TextView location = findViewById(R.id.detail_location);
        TextView description = findViewById(R.id.detail_description);
        TextView anonymousStatus = findViewById(R.id.detail_anonymous_status);

        // Get data from intent
        Report report = (Report) getIntent().getSerializableExtra("report");

        if (report != null) {
            incidentType.setText("Incident Type: " + report.getType());
            severity.setText("Severity: " + report.getSeverity());
            dateTime.setText("Date & Time: " + report.getDate() + " " + report.getTime());
            location.setText("Location: " + report.getLocation());
            description.setText(report.getDescription());

            if (report.isAnonymous()) {
                anonymousStatus.setText("Reported Anonymously");
            } else {
                anonymousStatus.setVisibility(View.GONE);
            }

            // --- More Robust Media Handling ---
            String mediaUriString = report.getMediaUri();
            if (mediaUriString != null) {
                try {
                    Uri mediaUri = Uri.parse(mediaUriString);
                    String mimeType = getContentResolver().getType(mediaUri);

                    noMediaText.setVisibility(View.GONE);
                    
                    if (mimeType != null && mimeType.startsWith("video/")) {
                        imageView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(mediaUri);
                        MediaController mediaController = new MediaController(this);
                        mediaController.setAnchorView(videoView);
                        videoView.setMediaController(mediaController);
                        // Removed auto-start, user can press play on the controller
                    } else {
                        // Default to image view for image MIME types or if type is unknown
                        videoView.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageURI(mediaUri);
                    }
                } catch (Exception e) {
                    Log.e("ReportDetailActivity", "Error loading media: " + e.getMessage());
                    noMediaText.setText("Error loading media");
                    noMediaText.setVisibility(View.VISIBLE);
                }
            } else {
                noMediaText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // This will take you back to the previous screen
        return true;
    }
}
