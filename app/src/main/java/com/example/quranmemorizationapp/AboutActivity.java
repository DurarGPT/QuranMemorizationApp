package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    Button btnBack;

    TextView tvAboutTitle,
            tvAboutDescription,
            tvFeaturesTitle,
            tvFeaturesList,
            tvWhoWeAreTitle,
            tvTeamNames,
            tvWhyTitle,
            tvWhyDescription;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        btnBack = findViewById(R.id.btnBack);

        tvAboutTitle = findViewById(R.id.tvAboutTitle);
        tvAboutDescription = findViewById(R.id.tvAboutDescription);
        tvFeaturesTitle = findViewById(R.id.tvFeaturesTitle);
        tvFeaturesList = findViewById(R.id.tvFeaturesList);

        tvWhoWeAreTitle = findViewById(R.id.tvWhoWeAreTitle);
        tvTeamNames = findViewById(R.id.tvTeamNames);
        tvWhyTitle = findViewById(R.id.tvWhyTitle);
        tvWhyDescription = findViewById(R.id.tvWhyDescription);

        sharedPreferences =
                getSharedPreferences(
                        "QiraatiSettings",
                        MODE_PRIVATE
                );

        btnBack.setOnClickListener(view -> finish());

        updateLanguage();
    }

    private void updateLanguage() {

        boolean isArabic =
                sharedPreferences.getBoolean(
                        "arabicLanguage",
                        false
                );

        Typeface arabicFont =
                getResources().getFont(R.font.estedad_regular);

        Typeface englishFont =
                getResources().getFont(R.font.dynapuff_regular);

        Typeface selectedFont =
                isArabic ? arabicFont : englishFont;

        btnBack.setTypeface(selectedFont);

        tvAboutTitle.setTypeface(selectedFont);
        tvAboutDescription.setTypeface(selectedFont);
        tvFeaturesTitle.setTypeface(selectedFont);
        tvFeaturesList.setTypeface(selectedFont);
        tvWhoWeAreTitle.setTypeface(selectedFont);
        tvTeamNames.setTypeface(selectedFont);
        tvWhyTitle.setTypeface(selectedFont);
        tvWhyDescription.setTypeface(selectedFont);

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

            tvWhoWeAreTitle.setText("من نحن");

            tvTeamNames.setText(
                    "دُرر • بلقيس • ريماس • روان"
            );

            tvWhyTitle.setText("لماذا صنعنا قراءتي؟");

            tvWhyDescription.setText(
                    "صممنا تطبيق قراءتي لمساعدة الأطفال على حفظ القرآن وتعلمه بطريقة سهلة وممتعة وتفاعلية."
            );

        } else {

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

            tvWhoWeAreTitle.setText("Who We Are");

            tvTeamNames.setText(
                    "Durar • Balqees • Rimas • Rawan"
            );

            tvWhyTitle.setText("Why We Made Qiraati");

            tvWhyDescription.setText(
                    "We created Qiraati to help children memorize and learn the Quran in a simple, enjoyable, and interactive way."
            );
        }
    }
}