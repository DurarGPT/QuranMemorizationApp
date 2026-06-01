package com.example.quranmemorizationapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class VideoLibraryActivity extends AppCompatActivity {

    // Store all videos
    ArrayList<VideoModel> videoList;

    // Buttons
    Button btnBack, btnVideo1, btnVideo2, btnVideo3, btnVideo4;

    // TextViews on the page
    TextView tvLibraryTitle, tvLibrarySubtitle;
    TextView tvVideoTitle1, tvVideoDesc1;
    TextView tvVideoTitle2, tvVideoDesc2;
    TextView tvVideoTitle3, tvVideoDesc3;
    TextView tvVideoTitle4, tvVideoDesc4;

    // Settings
    SharedPreferences sharedPreferences;
    boolean isArabic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // Connect activity to XML layout
        setContentView(R.layout.activity_video_library);

        // Load saved language
        sharedPreferences = getSharedPreferences("QiraatiSettings", MODE_PRIVATE);
        isArabic = sharedPreferences.getBoolean("arabicLanguage", false);

        // Connect buttons
        btnBack = findViewById(R.id.btnBack);
        btnVideo1 = findViewById(R.id.btnVideo1);
        btnVideo2 = findViewById(R.id.btnVideo2);
        btnVideo3 = findViewById(R.id.btnVideo3);
        btnVideo4 = findViewById(R.id.btnVideo4);

        // Connect TextViews
        tvLibraryTitle = findViewById(R.id.tvLibraryTitle);
        tvLibrarySubtitle = findViewById(R.id.tvLibrarySubtitle);

        tvVideoTitle1 = findViewById(R.id.tvVideoTitle1);
        tvVideoDesc1 = findViewById(R.id.tvVideoDesc1);

        tvVideoTitle2 = findViewById(R.id.tvVideoTitle2);
        tvVideoDesc2 = findViewById(R.id.tvVideoDesc2);

        tvVideoTitle3 = findViewById(R.id.tvVideoTitle3);
        tvVideoDesc3 = findViewById(R.id.tvVideoDesc3);

        tvVideoTitle4 = findViewById(R.id.tvVideoTitle4);
        tvVideoDesc4 = findViewById(R.id.tvVideoDesc4);

        // Apply font and language
        updateLanguage();

        // Back button closes current page
        btnBack.setOnClickListener(v -> finish());

        // Create video list
        videoList = new ArrayList<>();

        // Add videos into array
        addVideos();

        // Open videos
        btnVideo1.setOnClickListener(v -> openVideo(0));
        btnVideo2.setOnClickListener(v -> openVideo(1));
        btnVideo3.setOnClickListener(v -> openVideo(2));
        btnVideo4.setOnClickListener(v -> openVideo(3));

        // Welcome message
        Toast.makeText(
                this,
                isArabic ? "مرحباً بك في مكتبة الفيديوهات" : "Welcome to the video library",
                Toast.LENGTH_SHORT
        ).show();

        // Handle phone screen padding
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

    // Update font and text based on selected language
    private void updateLanguage() {

        Typeface arabicFont =
                getResources().getFont(R.font.estedad_regular);

        Typeface englishFont =
                getResources().getFont(R.font.dynapuff_regular);

        Typeface selectedFont =
                isArabic ? arabicFont : englishFont;

        // Apply font to buttons
        btnBack.setTypeface(selectedFont);
        btnVideo1.setTypeface(selectedFont);
        btnVideo2.setTypeface(selectedFont);
        btnVideo3.setTypeface(selectedFont);
        btnVideo4.setTypeface(selectedFont);

        // Apply font to page texts
        tvLibraryTitle.setTypeface(selectedFont);
        tvLibrarySubtitle.setTypeface(selectedFont);

        tvVideoTitle1.setTypeface(selectedFont);
        tvVideoDesc1.setTypeface(selectedFont);

        tvVideoTitle2.setTypeface(selectedFont);
        tvVideoDesc2.setTypeface(selectedFont);

        tvVideoTitle3.setTypeface(selectedFont);
        tvVideoDesc3.setTypeface(selectedFont);

        tvVideoTitle4.setTypeface(selectedFont);
        tvVideoDesc4.setTypeface(selectedFont);

        if (isArabic) {
            btnBack.setText("← رجوع");

            tvLibraryTitle.setText("مكتبة الفيديوهات");
            tvLibrarySubtitle.setText("فيديوهات تعليمية قصيرة تساعد الطفل على الحفظ بطريقة ممتعة");

            tvVideoTitle1.setText("تعلم سورة الفاتحة");
            tvVideoDesc1.setText("شرح قصير ومبسط للأطفال");

            tvVideoTitle2.setText("تعلم سورة الإخلاص");
            tvVideoDesc2.setText("تحفيظ سهل للأطفال");

            tvVideoTitle3.setText("تعلم سورة الفلق");
            tvVideoDesc3.setText("تكرار ممتع للأطفال");

            tvVideoTitle4.setText("تعلم سورة الناس");
            tvVideoDesc4.setText("تعليم وتحفيظ للأطفال");

            btnVideo1.setText("تشغيل الفيديو");
            btnVideo2.setText("تشغيل الفيديو");
            btnVideo3.setText("تشغيل الفيديو");
            btnVideo4.setText("تشغيل الفيديو");

        } else {
            btnBack.setText("← Back");

            tvLibraryTitle.setText("Video Library");
            tvLibrarySubtitle.setText("Short educational videos that help children memorize in a fun way");

            tvVideoTitle1.setText("Learn Surah Al-Fatiha");
            tvVideoDesc1.setText("A short and simple explanation for children");

            tvVideoTitle2.setText("Learn Surah Al-Ikhlas");
            tvVideoDesc2.setText("Easy memorization for children");

            tvVideoTitle3.setText("Learn Surah Al-Falaq");
            tvVideoDesc3.setText("Fun repetition for children");

            tvVideoTitle4.setText("Learn Surah An-Nas");
            tvVideoDesc4.setText("Learning and memorization for children");

            btnVideo1.setText("Play Video");
            btnVideo2.setText("Play Video");
            btnVideo3.setText("Play Video");
            btnVideo4.setText("Play Video");
        }
    }

    // Opens selected video
    private void openVideo(int index) {

        Intent intent = new Intent(
                VideoLibraryActivity.this,
                VideoPlayerActivity.class
        );

        intent.putExtra(
                "videoUrl",
                videoList.get(index).getVideoUrl()
        );

        startActivity(intent);
    }

    // Add videos into list
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