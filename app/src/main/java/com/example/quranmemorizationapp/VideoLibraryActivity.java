package com.example.quranmemorizationapp;


// Imports needed for the activity and Toast message
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import android.content.Intent;
import android.widget.Button;
// This class controls the Video Library screen
public class VideoLibraryActivity extends AppCompatActivity {

    // ArrayList to store videos
    ArrayList<VideoModel> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Runs when the activity starts
        super.onCreate(savedInstanceState);

        // Makes the app use the full screen nicely
        EdgeToEdge.enable(this);

        // Connects this Java class to the XML design file
        setContentView(R.layout.activity_video_library);

        // CREATE VIDEO LIST
        videoList = new ArrayList<>();

        // ADD VIDEOS TO THE LIST
        addVideos();
        Button btnVideo1 = findViewById(R.id.btnVideo1);
        Button btnVideo2 = findViewById(R.id.btnVideo2);

        btnVideo1.setOnClickListener(v -> {
            Intent intent = new Intent(VideoLibraryActivity.this, VideoPlayerActivity.class);
            intent.putExtra("videoUrl", videoList.get(0).getVideoUrl());
            startActivity(intent);
        });

        btnVideo2.setOnClickListener(v -> {
            Intent intent = new Intent(VideoLibraryActivity.this, VideoPlayerActivity.class);
            intent.putExtra("videoUrl", videoList.get(1).getVideoUrl());
            startActivity(intent);
        });

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

    // METHOD TO ADD VIDEO DATA
    private void addVideos() {

        videoList.add(
                new VideoModel(
                        "تعلم سورة الفاتحة",
                        "شرح مبسط للأطفال",
                        "https://youtu.be/rJIsyMuk5rU?si=CYw2r4PMZ8cYo7n6",
                        R.drawable.fatiha_thumb
                )
        );

        videoList.add(
                new VideoModel(
                        "تعلم سورة الإخلاص",
                        "تحفيظ سهل للأطفال",
                        "https://youtu.be/HiqQ5c-haUw?si=FFyhMUXQgUHBcFmj",
                        R.drawable.ikhlas_thumb
                )
        );

        videoList.add(
                new VideoModel(
                        "تعلم سورة الفلق",
                        "تكرار ممتع للأطفال",
                        "https://youtu.be/k2Qg4Yd7kw0?si=VacWyP6EvtC7nMQJ",
                        R.drawable.falaq_thumb
                )
        );

        videoList.add(
                new VideoModel(
                        "تعلم سورة الناس",
                        "تعليم وتحفيظ للأطفال",
                        "https://youtu.be/TyKwwVemYhw?si=Hdbmf0kguVNjwgp3",
                        R.drawable.annas_thumb
                )
        );
    }
}