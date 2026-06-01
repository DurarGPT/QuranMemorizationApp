package com.example.quranmemorizationapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnAbout, btnSettings, btnVideoLibrary;

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
        btnVideoLibrary = findViewById(R.id.btnVideoLibrary);

        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvBottomText = findViewById(R.id.tvBottomText);

        sharedPreferences =
                getSharedPreferences(
                        "QiraatiSettings",
                        MODE_PRIVATE
                );

        dbHelper = new DBHelper(this);

        // INSERT SAMPLE DATA
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
        String progress =
                dbHelper.getLastProgress();

        Log.d("PROGRESS", progress);

        // Open memorization control page
        btnStart.setOnClickListener(view ->
                startActivity(
                        new Intent(
                                MainActivity.this,
                                SecondActivity.class
                        )
                )
        );

        // Open about page
        btnAbout.setOnClickListener(view ->
                startActivity(
                        new Intent(
                                MainActivity.this,
                                AboutActivity.class
                        )
                )
        );

        // Open settings page
        btnSettings.setOnClickListener(view ->
                startActivity(
                        new Intent(
                                MainActivity.this,
                                SettingsActivity.class
                        )
                )
        );

        // Open video library page
        btnVideoLibrary.setOnClickListener(view ->
                startActivity(
                        new Intent(
                                MainActivity.this,
                                VideoLibraryActivity.class
                        )
                )
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update language and font whenever user returns to main page
        updateLanguage();
    }

    private void updateLanguage() {

        boolean isArabic =
                sharedPreferences.getBoolean(
                        "arabicLanguage",
                        false
                );

        // Load Arabic and English fonts
        Typeface arabicFont =
                getResources().getFont(R.font.estedad_regular);

        Typeface englishFont =
                getResources().getFont(R.font.dynapuff_regular);

        // Choose font based on selected language
        Typeface selectedFont =
                isArabic ? arabicFont : englishFont;

        // Apply selected font to all text/buttons on this page
        tvTitle.setTypeface(selectedFont);
        tvSubtitle.setTypeface(selectedFont);
        tvBottomText.setTypeface(selectedFont);

        btnStart.setTypeface(selectedFont);
        btnAbout.setTypeface(selectedFont);
        btnSettings.setTypeface(selectedFont);
        btnVideoLibrary.setTypeface(selectedFont);

        if (isArabic) {

            tvTitle.setText("قراءتي");

            tvSubtitle.setText(
                    "رحلة ممتعة لحفظ القرآن للأطفال"
            );

            btnStart.setText("ابدأ الحفظ");

            btnAbout.setText("حول التطبيق");

            btnSettings.setText("الإعدادات");

            btnVideoLibrary.setText("مكتبة الفيديوهات");

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

            btnVideoLibrary.setText("Video Library");

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
                            "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
                    )
            );

            dbHelper.insertVerse(
                    new Verse(
                            1,
                            2,
                            1,
                            "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ"
                    )
            );

            dbHelper.insertVerse(
                    new Verse(
                            1,
                            3,
                            1,
                            "الرَّحْمَٰنِ الرَّحِيمِ"
                    )
            );

            dbHelper.insertVerse(
                    new Verse(
                            1,
                            4,
                            1,
                            "مَالِكِ يَوْمِ الدِّينِ"
                    )
            );

            dbHelper.insertVerse(
                    new Verse(
                            1,
                            5,
                            1,
                            "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ"
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