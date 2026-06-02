package com.example.quranmemorizationapp;

// DURAR'S PART: Imports needed for navigation, settings, fonts, and UI.
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

    // DURAR'S PART: Main page buttons shown on the home screen.
    Button btnStart, btnAbout, btnSettings, btnVideoLibrary;

    // DURAR'S PART: Main page text elements.
    TextView tvTitle, tvSubtitle, tvBottomText;

    // DURAR'S PART: Stores user settings such as selected language.
    SharedPreferences sharedPreferences;

    // Shared/Phase 3 part: database helper used to insert, fetch, and save Quran progress.
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect this Java file to the main screen XML design.
        setContentView(R.layout.activity_main);

        // DURAR'S PART: Connect buttons from activity_main.xml to Java.
        btnStart = findViewById(R.id.btnStart);
        btnAbout = findViewById(R.id.btnAbout);
        btnSettings = findViewById(R.id.btnSettings);
        btnVideoLibrary = findViewById(R.id.btnVideoLibrary);

        // DURAR'S PART: Connect home page text from XML to Java.
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvBottomText = findViewById(R.id.tvBottomText);

        // DURAR'S PART: Open the saved settings file.
        // This is used to remember if the user selected Arabic or English.
        sharedPreferences =
                getSharedPreferences(
                        "QiraatiSettings",
                        MODE_PRIVATE
                );

        // Shared/Phase 3 part: create database helper object.
        dbHelper = new DBHelper(this);

        // Shared/Phase 3 part: insert sample Quran verses into SQLite if database is empty.
        seedDatabase();

        // Shared/Phase 3 part: load all verses from SQLite and print them in Logcat.
        loadVerses();

        // Shared/Phase 3 part: test fetching verses from ayah 1 to ayah 2.
        ArrayList<Verse> rangeVerses =
                dbHelper.getVersesInRange(1, 2);

        // Print range result in Logcat for testing.
        for (Verse v : rangeVerses) {
            Log.d("RANGE_VERSE", v.textAr);
        }

        // Shared/Phase 3 part: save sample user progress locally.
        dbHelper.saveProgress(1, 2);

        // Shared/Phase 3 part: get saved progress from database.
        String progress =
                dbHelper.getLastProgress();

        // Print progress in Logcat for testing.
        Log.d("PROGRESS", progress);

        // DURAR'S PART: Start button opens the memorization control page.
        btnStart.setOnClickListener(view ->
                startActivity(
                        new Intent(
                                MainActivity.this,
                                SecondActivity.class
                        )
                )
        );

        // DURAR'S PART: About button opens the About page.
        btnAbout.setOnClickListener(view ->
                startActivity(
                        new Intent(
                                MainActivity.this,
                                AboutActivity.class
                        )
                )
        );

        // DURAR'S PART: Settings button opens the Settings page.
        btnSettings.setOnClickListener(view ->
                startActivity(
                        new Intent(
                                MainActivity.this,
                                SettingsActivity.class
                        )
                )
        );

        // DURAR'S PART: Video Library button opens the video library page.
        // This is the integration/linking part, not necessarily the full video library feature.
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

        // DURAR'S PART:
        // Every time the user returns to the home page,
        // update the language and font based on Settings.
        updateLanguage();
    }

    // DURAR'S PART:
    // This method changes all home page text between Arabic and English,
    // and also changes the font depending on the selected language.
    private void updateLanguage() {

        // Read selected language from SharedPreferences.
        boolean isArabic =
                sharedPreferences.getBoolean(
                        "arabicLanguage",
                        false
                );

        // DURAR'S PART: Load Arabic font.
        Typeface arabicFont =
                getResources().getFont(R.font.estedad_regular);

        // DURAR'S PART: Load English font.
        Typeface englishFont =
                getResources().getFont(R.font.dynapuff_regular);

        // DURAR'S PART:
        // If Arabic is selected, use Estedad.
        // If English is selected, use DynaPuff.
        Typeface selectedFont =
                isArabic ? arabicFont : englishFont;

        // DURAR'S PART: Apply selected font to all TextViews.
        tvTitle.setTypeface(selectedFont);
        tvSubtitle.setTypeface(selectedFont);
        tvBottomText.setTypeface(selectedFont);

        // DURAR'S PART: Apply selected font to all Buttons.
        btnStart.setTypeface(selectedFont);
        btnAbout.setTypeface(selectedFont);
        btnSettings.setTypeface(selectedFont);
        btnVideoLibrary.setTypeface(selectedFont);

        // DURAR'S PART: Arabic mode text.
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

        }

        // DURAR'S PART: English mode text.
        else {

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

    // Shared/Phase 3 part:
    // This method inserts sample Quran verses into the local SQLite database.
    private void seedDatabase() {

        // Prevent duplicate inserts.
        // If the database already has verses, do not insert them again.
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

    // Shared/Phase 3 part:
    // This method fetches all verses from SQLite and prints them in Logcat.
    private void loadVerses() {

        ArrayList<Verse> verses =
                dbHelper.getAllVerses();

        for (Verse v : verses) {
            Log.d("VERSE", v.textAr);
        }
    }
}