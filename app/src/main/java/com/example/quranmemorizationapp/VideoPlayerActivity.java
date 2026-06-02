package com.example.quranmemorizationapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
// Rimas Part
public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

       // Get video URL from previous activity
        String videoUrl =
                getIntent().getStringExtra("videoUrl");

        if (videoUrl != null && !videoUrl.isEmpty()) {
         // Open video using an external application
            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(videoUrl)
                    );
            // Launch YouTube or browser
            startActivity(intent);
            // Close current activity after opening the video
            finish();

        } else {
         // Display message when no video URL is available
            Toast.makeText(
                    this,
                    "No video found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }
    }
}