package com.example.quranmemorizationapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class VideoLibraryActivity extends AppCompatActivity {

    ArrayList<VideoModel> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_video_library);

        videoList = new ArrayList<>();

        addVideos();

        Button btnVideo1 = findViewById(R.id.btnVideo1);
        Button btnVideo2 = findViewById(R.id.btnVideo2);
        Button btnVideo3 = findViewById(R.id.btnVideo3);
        Button btnVideo4 = findViewById(R.id.btnVideo4);

        btnVideo1.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VideoLibraryActivity.this,
                    VideoPlayerActivity.class
            );

            intent.putExtra(
                    "videoUrl",
                    videoList.get(0).getVideoUrl()
            );

            startActivity(intent);
        });

        btnVideo2.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VideoLibraryActivity.this,
                    VideoPlayerActivity.class
            );

            intent.putExtra(
                    "videoUrl",
                    videoList.get(1).getVideoUrl()
            );

            startActivity(intent);
        });

        btnVideo3.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VideoLibraryActivity.this,
                    VideoPlayerActivity.class
            );

            intent.putExtra(
                    "videoUrl",
                    videoList.get(2).getVideoUrl()
            );

            startActivity(intent);
        });

        btnVideo4.setOnClickListener(v -> {
            Intent intent = new Intent(
                    VideoLibraryActivity.this,
                    VideoPlayerActivity.class
            );

            intent.putExtra(
                    "videoUrl",
                    videoList.get(3).getVideoUrl()
            );

            startActivity(intent);
        });

        Toast.makeText(
                this,
                "مرحباً بك في مكتبة الفيديوهات",
                Toast.LENGTH_SHORT
        ).show();

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    private void addVideos() {

        videoList.add(
                new VideoModel(
                        "تعلم سورة الفاتحة",
                        "شرح مبسط للأطفال",
                        "https://youtu.be/rJIsyMuk5rU",
                        R.drawable.fatiha_thumb
                )
        );

        videoList.add(
                new VideoModel(
                        "تعلم سورة الإخلاص",
                        "تحفيظ سهل للأطفال",
                        "https://youtu.be/HiqQ5c-haUw",
                        R.drawable.ikhlas_thumb
                )
        );

        videoList.add(
                new VideoModel(
                        "تعلم سورة الفلق",
                        "تكرار ممتع للأطفال",
                        "https://youtu.be/k2Qg4Yd7kw0",
                        R.drawable.falaq_thumb
                )
        );

        videoList.add(
                new VideoModel(
                        "تعلم سورة الناس",
                        "تعليم وتحفيظ للأطفال",
                        "https://youtu.be/TyKwwVemYhw",
                        R.drawable.annas_thumb
                )
        );
    }
}