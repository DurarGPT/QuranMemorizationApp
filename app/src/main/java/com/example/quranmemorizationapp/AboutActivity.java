package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    // Back button
    Button btnBack;

    // TextViews
    TextView tvAboutTitle,
            tvAboutDescription,
            tvFeaturesTitle,
            tvFeaturesList;

    // Save settings like language
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect Java file to XML layout
        setContentView(R.layout.activity_about);

        // Connect button
        btnBack = findViewById(R.id.btnBack);

        // Connect text views
        tvAboutTitle = findViewById(R.id.tvAboutTitle);
        tvAboutDescription = findViewById(R.id.tvAboutDescription);
        tvFeaturesTitle = findViewById(R.id.tvFeaturesTitle);
        tvFeaturesList = findViewById(R.id.tvFeaturesList);

        // Load saved settings
        sharedPreferences =
                getSharedPreferences(
                        "QiraatiSettings",
                        MODE_PRIVATE
                );

        // Back button closes page
        btnBack.setOnClickListener(view -> finish());

        // Update page language and font
        updateLanguage();
    }

    // Updates language and font dynamically
    private void updateLanguage() {

        boolean isArabic =
                sharedPreferences.getBoolean(
                        "arabicLanguage",
                        false
                );

        // Load fonts
        Typeface arabicFont =
                getResources().getFont(R.font.estedad_regular);

        Typeface englishFont =
                getResources().getFont(R.font.dynapuff_regular);

        // Select font depending on language
        Typeface selectedFont =
                isArabic ? arabicFont : englishFont;

        // Apply font to all text and buttons
        btnBack.setTypeface(selectedFont);

        tvAboutTitle.setTypeface(selectedFont);
        tvAboutDescription.setTypeface(selectedFont);
        tvFeaturesTitle.setTypeface(selectedFont);
        tvFeaturesList.setTypeface(selectedFont);

        // Arabic mode
        if (isArabic) {

            btnBack.setText("رجوع");

            tvAboutTitle.setText("حول قراءتي");

            tvAboutDescription.setText(
                    "قراءتي هو تطبيق بسيط ومناسب للأطفال يساعدهم على حفظ القرآن بطريقة ممتعة وهادئة ومنظمة."
            );

            tvFeaturesTitle.setText("المميزات");

            tvFeaturesList.setText(
                    "• تكرار الآيات\n" +
                            "• التحكم بسهولة بالحفظ\n" +
                            "• الوضع الليلي\n" +
                            "• دعم العربية والإنجليزية\n" +
                            "• تصميم مناسب للأطفال"
            );

        }

        // English mode
        else {

            btnBack.setText("Back");

            tvAboutTitle.setText("About Qiraati");

            tvAboutDescription.setText(
                    "Qiraati is a simple and child-friendly Quran memorization app designed to help children memorize Quran verses in a calming and enjoyable way."
            );

            tvFeaturesTitle.setText("Features");

            tvFeaturesList.setText(
                    "• Ayah repetition\n" +
                            "• Easy memorization controls\n" +
                            "• Dark mode support\n" +
                            "• Arabic and English support\n" +
                            "• Child-friendly design"
            );
        }
    }
}