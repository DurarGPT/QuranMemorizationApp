package com.example.quranmemorizationapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    Button btnPlay, btnPause, btnNext, btnPrevious;
    TextView tvDisplay;

    int startAyah, endAyah, repeatLimit;
    int currentAyah;
    int currentRepeatCount = 1;

    Handler handler = new Handler();
    boolean isRunning = false; // التحكم في حالة التشغيل

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        tvDisplay = findViewById(R.id.tvDisplay);

        // استقبال البيانات
        try {
            startAyah = Integer.parseInt(getIntent().getStringExtra("FROM_AYAH"));
            endAyah = Integer.parseInt(getIntent().getStringExtra("TO_AYAH"));
            repeatLimit = Integer.parseInt(getIntent().getStringExtra("REPEAT_LIMIT"));
            currentAyah = startAyah;
        } catch (Exception e) {
            startAyah = 1; endAyah = 5; repeatLimit = 1;
            currentAyah = startAyah;
        }

        // الصفحة ستبدأ بالنص الأصلي في XML ولن يبدأ التكرار تلقائياً

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRunning) {
                    isRunning = true;
                    runRepetitionLogic();
                    Toast.makeText(ThirdActivity.this, "بدء الحفظ...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = false;
                handler.removeCallbacksAndMessages(null);
                Toast.makeText(ThirdActivity.this, "توقف مؤقت", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentAyah < endAyah) {
                currentAyah++;
                updateUI();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentAyah > startAyah) {
                currentAyah--;
                updateUI();
            }
        });
    }

    private void runRepetitionLogic() {
        if (!isRunning) return;

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (currentRepeatCount <= repeatLimit) {
                    if (currentAyah <= endAyah) {
                        updateUI();
                        currentAyah++;
                        handler.postDelayed(this, 3000); // تكرار كل 3 ثوانٍ
                    } else {
                        currentRepeatCount++;
                        currentAyah = startAyah;
                        handler.post(this);
                    }
                } else {
                    tvDisplay.setText("تم الانتهاء من الحفظ!");
                    isRunning = false;
                }
            }
        });
    }

    private void updateUI() {
        if (tvDisplay != null) {
            tvDisplay.setText("دورة رقم: " + currentRepeatCount +
                    "\nالآية الحالية: " + currentAyah);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}