package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    Button btnBack;
    TextView tvAboutTitle, tvAboutDescription, tvFeaturesTitle, tvFeaturesList;

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

        sharedPreferences = getSharedPreferences("QiraatiSettings", MODE_PRIVATE);

        btnBack.setOnClickListener(view -> finish());

        updateLanguage();
    }

    private void updateLanguage() {
        boolean isArabic = sharedPreferences.getBoolean("arabicLanguage", false);

        if (isArabic) {
            btnBack.setText("رجوع");
            tvAboutTitle.setText("حول قراءتي");
            tvAboutDescription.setText("قراءتي هو تطبيق بسيط ومناسب للأطفال يساعدهم على حفظ القرآن بطريقة ممتعة وهادئة ومنظمة.");
            tvFeaturesTitle.setText("المميزات");
            tvFeaturesList.setText("• تكرار الآيات\n• التحكم بسهولة بالحفظ\n• الوضع الليلي\n• دعم العربية والإنجليزية\n• تصميم مناسب للأطفال");
        } else {
            btnBack.setText("Back");
            tvAboutTitle.setText("About Qiraati");
            tvAboutDescription.setText("Qiraati is a simple and child-friendly Quran memorization app designed to help children memorize Quran verses in a calming and enjoyable way.");
            tvFeaturesTitle.setText("Features");
            tvFeaturesList.setText("• Ayah repetition\n• Easy memorization controls\n• Dark mode support\n• Arabic and English support\n• Child-friendly design");
        }
    }
}