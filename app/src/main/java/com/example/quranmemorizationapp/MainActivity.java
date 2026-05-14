package com.example.quranmemorizationapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnAbout, btnSettings;
    TextView tvTitle, tvSubtitle, tvBottomText;

    SharedPreferences sharedPreferences;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnAbout = findViewById(R.id.btnAbout);
        btnSettings = findViewById(R.id.btnSettings);

        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvBottomText = findViewById(R.id.tvBottomText);

        sharedPreferences = getSharedPreferences("QiraatiSettings", MODE_PRIVATE);

        dbHelper = new DBHelper(this);

        // INSERT SAMPLE DATA ONLY ONCE
        seedDatabase();

        // LOAD ALL VERSES
        loadVerses();

        // TEST RANGE RETRIEVAL
        ArrayList<Verse> rangeVerses =
                dbHelper.getVersesInRange(1, 2);

        for (Verse v : rangeVerses) {
            Log.d("RANGE_VERSE", v.textAr);
        }

        // SAVE USER PROGRESS
        dbHelper.saveProgress(1, 2);

        // GET USER PROGRESS
        String progress = dbHelper.getLastProgress();

        Log.d("PROGRESS", progress);

        btnStart.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, SecondActivity.class))
        );

        btnAbout.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, AboutActivity.class))
        );

        btnSettings.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguage();
    }

    private void updateLanguage() {

        boolean isArabic =
                sharedPreferences.getBoolean("arabicLanguage", false);

        if (isArabic) {

            tvTitle.setText("قراءتي");

            tvSubtitle.setText(
                    "رحلة ممتعة لحفظ القرآن للأطفال"
            );

            btnStart.setText("ابدأ الحفظ");

            btnAbout.setText("حول التطبيق");

            btnSettings.setText("الإعدادات");

            tvBottomText.setText(
                    "تعلّم • كرّر • احفظ"
            );

        } else {

            tvTitle.setText("Qiraati");

            tvSubtitle.setText(
                    "A joyful Quran memorization journey for children"
            );

            btnStart.setText("Start Memorizing");

            btnAbout.setText("About App");

            btnSettings.setText("Settings");

            tvBottomText.setText(
                    "Learn • Repeat • Memorize"
            );
        }
    }

    private void seedDatabase() {

        // PREVENT DUPLICATE INSERTS
        if (dbHelper.getAllVerses().isEmpty()) {

            dbHelper.insertVerse(
                    new Verse(
                            1,
                            1,
                            1,
                            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            "https://example.com/audio1.mp3",
                            "https://example.com/page1.jpg"
                    )
            );

            dbHelper.insertVerse(
                    new Verse(
                            1,
                            2,
                            1,
                            "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                            "https://example.com/audio2.mp3",
                            "https://example.com/page1.jpg"
                    )
            );

            dbHelper.insertVerse(
                    new Verse(
                            1,
                            3,
                            1,
                            "الرَّحْمَٰنِ الرَّحِيمِ",
                            "https://example.com/audio3.mp3",
                            "https://example.com/page1.jpg"
                    )
            );

            dbHelper.insertVerse(
                    new Verse(
                            1,
                            4,
                            1,
                            "مَالِكِ يَوْمِ الدِّينِ",
                            "https://example.com/audio4.mp3",
                            "https://example.com/page1.jpg"
                    )
            );

            dbHelper.insertVerse(
                    new Verse(
                            1,
                            5,
                            1,
                            "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                            "https://example.com/audio5.mp3",
                            "https://example.com/page1.jpg"
                    )
            );
        }
    }

    private void loadVerses() {

        ArrayList<Verse> verses =
                dbHelper.getAllVerses();

        for (Verse v : verses) {

            Log.d("VERSE", v.textAr);
        }
    }
}