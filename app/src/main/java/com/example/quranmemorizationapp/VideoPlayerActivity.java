package com.example.quranmemorizationapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String videoUrl =
                getIntent().getStringExtra("videoUrl");

        if (videoUrl != null && !videoUrl.isEmpty()) {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(videoUrl)
                    );

            startActivity(intent);

            finish();

        } else {

            Toast.makeText(
                    this,
                    "No video found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
        }
    }
}