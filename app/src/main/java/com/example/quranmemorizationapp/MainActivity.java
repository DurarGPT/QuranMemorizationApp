package com.example.quranmemorizationapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnAbout, btnSettings;
    TextView tvTitle, tvSubtitle, tvBottomText;

    SharedPreferences sharedPreferences;

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
        boolean isArabic = sharedPreferences.getBoolean("arabicLanguage", false);

        if (isArabic) {
            tvTitle.setText("قراءتي");
            tvSubtitle.setText("رحلة ممتعة لحفظ القرآن للأطفال");
            btnStart.setText("ابدأ الحفظ");
            btnAbout.setText("حول التطبيق");
            btnSettings.setText("الإعدادات");
            tvBottomText.setText("تعلّم • كرّر • احفظ");
        } else {
            tvTitle.setText("Qiraati");
            tvSubtitle.setText("A joyful Quran memorization journey for children");
            btnStart.setText("Start Memorizing");
            btnAbout.setText("About App");
            btnSettings.setText("Settings");
            tvBottomText.setText("Learn • Repeat • Memorize");
        }
    }
}