package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    // Back button
    Button btnBack;

    // Spinners (dropdown menu )
    Spinner spLanguage, spTheme;

    // TextViews
    TextView tvSettingsTitle,
            tvLanguageLabel,
            tvLanguageDesc,
            tvThemeLabel,
            tvThemeDesc;

    // Shared preferences
    SharedPreferences sharedPreferences;

    // Settings states
    boolean isArabic;
    boolean isDarkMode;

    // English spinner items
    String[] languagesEnglish = {"English", "العربية"};
    String[] themesEnglish = {"Light Mode", "Dark Mode"};

    // Arabic spinner items
    String[] languagesArabic = {"English", "العربية"};
    String[] themesArabic = {"الوضع الفاتح", "الوضع الليلي"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect Java file with XML layout
        setContentView(R.layout.activity_settings);

        // Connect button
        btnBack = findViewById(R.id.btnBack);

        // Connect spinners
        spLanguage = findViewById(R.id.spLanguage);
        spTheme = findViewById(R.id.spTheme);

        // Connect text views
        tvSettingsTitle = findViewById(R.id.tvSettingsTitle);
        tvLanguageLabel = findViewById(R.id.tvLanguageLabel);
        tvLanguageDesc = findViewById(R.id.tvLanguageDesc);
        tvThemeLabel = findViewById(R.id.tvThemeLabel);
        tvThemeDesc = findViewById(R.id.tvThemeDesc);

        // Load saved settings
        sharedPreferences =
                getSharedPreferences(
                        "QiraatiSettings",
                        MODE_PRIVATE
                );

        // Get saved language and theme
        isArabic =
                sharedPreferences.getBoolean(
                        "arabicLanguage",
                        false
                );

        isDarkMode =
                sharedPreferences.getBoolean(
                        "darkMode",
                        false
                );

        // Back button closes page
        btnBack.setOnClickListener(view -> finish());

        // Setup dropdown menus
        setupSpinners();

        // Update language and font
        updateLanguageText();
    }

    // Setup language and theme spinners
    private void setupSpinners() {

        // Language spinner
        ArrayAdapter<String> languageAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        isArabic
                                ? languagesArabic
                                : languagesEnglish
                );

        languageAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spLanguage.setAdapter(languageAdapter);

        spLanguage.setSelection(isArabic ? 1 : 0);

        // Theme spinner
        ArrayAdapter<String> themeAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        isArabic
                                ? themesArabic
                                : themesEnglish
                );

        themeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spTheme.setAdapter(themeAdapter);

        spTheme.setSelection(isDarkMode ? 1 : 0);

        // Language selection listener
        spLanguage.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    boolean firstRun = true;

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        // Prevent spinner auto trigger
                        if (firstRun) {

                            firstRun = false;

                            return;
                        }

                        // Save selected language
                        isArabic = position == 1;

                        sharedPreferences.edit()
                                .putBoolean(
                                        "arabicLanguage",
                                        isArabic
                                )
                                .apply();

                        // Refresh UI
                        updateLanguageText();

                        setupSpinners();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );

        // Theme selection listener
        spTheme.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    boolean firstRun = true;

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        // Prevent spinner auto trigger
                        if (firstRun) {

                            firstRun = false;

                            return;
                        }

                        // Save selected theme
                        isDarkMode = position == 1;

                        sharedPreferences.edit()
                                .putBoolean(
                                        "darkMode",
                                        isDarkMode
                                )
                                .apply();

                        // Apply dark/light mode
                        if (isDarkMode) {

                            AppCompatDelegate.setDefaultNightMode(
                                    AppCompatDelegate.MODE_NIGHT_YES
                            );

                        } else {

                            AppCompatDelegate.setDefaultNightMode(
                                    AppCompatDelegate.MODE_NIGHT_NO
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );
    }

    // Update language and font dynamically
    private void updateLanguageText() {

        // Load fonts
        Typeface arabicFont =
                getResources().getFont(
                        R.font.estedad_regular
                );

        Typeface englishFont =
                getResources().getFont(
                        R.font.dynapuff_regular
                );

        // Select font based on language
        Typeface selectedFont =
                isArabic
                        ? arabicFont
                        : englishFont;

        // Apply font
        btnBack.setTypeface(selectedFont);

        tvSettingsTitle.setTypeface(selectedFont);

        tvLanguageLabel.setTypeface(selectedFont);

        tvLanguageDesc.setTypeface(selectedFont);

        tvThemeLabel.setTypeface(selectedFont);

        tvThemeDesc.setTypeface(selectedFont);

        // Arabic mode
        if (isArabic) {

            btnBack.setText("رجوع");

            tvSettingsTitle.setText("الإعدادات");

            tvLanguageLabel.setText("اللغة");

            tvLanguageDesc.setText(
                    "اختاري اللغة المفضلة"
            );

            tvThemeLabel.setText("المظهر");

            tvThemeDesc.setText(
                    "اختاري المظهر المفضل"
            );

        }

        // English mode
        else {

            btnBack.setText("Back");

            tvSettingsTitle.setText("Settings");

            tvLanguageLabel.setText("Language");

            tvLanguageDesc.setText(
                    "Choose your preferred language"
            );

            tvThemeLabel.setText("Theme");

            tvThemeDesc.setText(
                    "Choose your preferred theme"
            );
        }
    }
}
