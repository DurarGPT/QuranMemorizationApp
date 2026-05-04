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
    boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        tvDisplay = findViewById(R.id.tvDisplay);


        try {
            startAyah = Integer.parseInt(getIntent().getStringExtra("FROM_AYAH"));
            endAyah = Integer.parseInt(getIntent().getStringExtra("TO_AYAH"));
            repeatLimit = Integer.parseInt(getIntent().getStringExtra("REPEAT_LIMIT"));
            currentAyah = startAyah;
        } catch (Exception e) {

            startAyah = 1; endAyah = 5; repeatLimit = 1;
            currentAyah = startAyah;
        }


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPaused) {
                    isPaused = false;
                    runRepetitionLogic();
                    Toast.makeText(ThirdActivity.this, "تم الاستئناف", Toast.LENGTH_SHORT).show();
                } else {
                    currentAyah = startAyah;
                    currentRepeatCount = 1;
                    runRepetitionLogic();
                }
            }
        });


        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPaused = true;
                handler.removeCallbacksAndMessages(null);
                Toast.makeText(ThirdActivity.this, "إيقاف مؤقت", Toast.LENGTH_SHORT).show();
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAyah < endAyah) {
                    currentAyah++;
                    updateUI();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAyah > startAyah) {
                    currentAyah--;
                    updateUI();
                }
            }
        });
    }

    private void runRepetitionLogic() {
        if (isPaused) return;

        Runnable runnable = new Runnable() {
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
                    tvDisplay.setText("تم الانتهاء!");
                    handler.removeCallbacks(this);
                }
            }
        };
        handler.post(runnable);
    }

    private void updateUI() {
        if (tvDisplay != null) {
            tvDisplay.setText("التكرار: " + currentRepeatCount + " / " + repeatLimit +
                    "\nالآية الحالية: " + currentAyah);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}