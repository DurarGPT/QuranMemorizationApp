package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
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

    Button btnBack;
    Spinner spLanguage, spTheme;

    TextView tvSettingsTitle, tvLanguageLabel, tvLanguageDesc;
    TextView tvThemeLabel, tvThemeDesc, tvPreviewTitle, tvPreview;

    SharedPreferences sharedPreferences;

    boolean isArabic;
    boolean isDarkMode;

    String[] languagesEnglish = {"English", "العربية"};
    String[] themesEnglish = {"Light Mode", "Dark Mode"};

    String[] languagesArabic = {"English", "العربية"};
    String[] themesArabic = {"الوضع الفاتح", "الوضع الليلي"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack = findViewById(R.id.btnBack);

        spLanguage = findViewById(R.id.spLanguage);
        spTheme = findViewById(R.id.spTheme);

        tvSettingsTitle = findViewById(R.id.tvSettingsTitle);
        tvLanguageLabel = findViewById(R.id.tvLanguageLabel);
        tvLanguageDesc = findViewById(R.id.tvLanguageDesc);
        tvThemeLabel = findViewById(R.id.tvThemeLabel);
        tvThemeDesc = findViewById(R.id.tvThemeDesc);
        tvPreviewTitle = findViewById(R.id.tvPreviewTitle);
        tvPreview = findViewById(R.id.tvPreview);

        sharedPreferences = getSharedPreferences("QiraatiSettings", MODE_PRIVATE);

        isArabic = sharedPreferences.getBoolean("arabicLanguage", false);
        isDarkMode = sharedPreferences.getBoolean("darkMode", false);

        btnBack.setOnClickListener(view -> finish());

        setupSpinners();
        updateLanguageText();
    }

    private void setupSpinners() {
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                isArabic ? languagesArabic : languagesEnglish
        );
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLanguage.setAdapter(languageAdapter);
        spLanguage.setSelection(isArabic ? 1 : 0);

        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                isArabic ? themesArabic : themesEnglish
        );
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTheme.setAdapter(themeAdapter);
        spTheme.setSelection(isDarkMode ? 1 : 0);

        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstRun = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstRun) {
                    firstRun = false;
                    return;
                }

                isArabic = position == 1;
                sharedPreferences.edit().putBoolean("arabicLanguage", isArabic).apply();

                updateLanguageText();
                setupSpinners();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstRun = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstRun) {
                    firstRun = false;
                    return;
                }

                isDarkMode = position == 1;
                sharedPreferences.edit().putBoolean("darkMode", isDarkMode).apply();

                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateLanguageText() {
        if (isArabic) {
            btnBack.setText("رجوع");
            tvSettingsTitle.setText("الإعدادات");

            tvLanguageLabel.setText("اللغة");
            tvLanguageDesc.setText("اختاري اللغة المفضلة");

            tvThemeLabel.setText("المظهر");
            tvThemeDesc.setText("اختاري المظهر المفضل");

            tvPreviewTitle.setText("معاينة");
            tvPreview.setText("هكذا سيظهر التطبيق بناءً على الإعدادات.");
        } else {
            btnBack.setText("Back");
            tvSettingsTitle.setText("Settings");

            tvLanguageLabel.setText("Language");
            tvLanguageDesc.setText("Choose your preferred language");

            tvThemeLabel.setText("Theme");
            tvThemeDesc.setText("Choose your preferred theme");

            tvPreviewTitle.setText("Preview");
            tvPreview.setText("This is how the app will look based on your settings.");
        }
    }
}