package com.example.quranmemorizationapp;

// Imports needed for the activity and Toast message
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// This class controls the Video Library screen
public class VideoLibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Runs when the activity starts
        super.onCreate(savedInstanceState);

        // Makes the app use the full screen nicely
        EdgeToEdge.enable(this);

        // Connects this Java class to the XML design file
        setContentView(R.layout.activity_video_library);

        // Shows a small welcome message when the screen opens
        Toast.makeText(this,
                "مرحباً بك في مكتبة الفيديوهات",
                Toast.LENGTH_SHORT).show();

        // Makes sure content does not overlap with system bars
        // like the status bar or navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {

            // Gets system bar sizes
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Adds padding around the screen
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });
    }
}