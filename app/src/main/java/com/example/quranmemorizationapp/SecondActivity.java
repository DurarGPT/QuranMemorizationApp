package com.example.quranmemorizationapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    NumberPicker npFrom, npTo, npRepeat;
    Button btnStartRepeat, btnStop;
    TextView tvResult;

    Handler handler = new Handler();
    int currentAyah;
    int repeatCount;
    int maxRepeat;
    boolean isRunning = false;

    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        npFrom = findViewById(R.id.npFrom);
        npTo = findViewById(R.id.npTo);
        npRepeat = findViewById(R.id.npRepeat);
        btnStartRepeat = findViewById(R.id.btnStartRepeat);
        btnStop = findViewById(R.id.btnStop);
        tvResult = findViewById(R.id.tvResult);

        setupNumberPickers();

        btnStartRepeat.setOnClickListener(v -> startRepeating());

        btnStop.setOnClickListener(v -> stopRepeating());
    }

    private void setupNumberPickers() {
        npFrom.setMinValue(1);
        npFrom.setMaxValue(286);

        npTo.setMinValue(1);
        npTo.setMaxValue(286);

        npRepeat.setMinValue(1);
        npRepeat.setMaxValue(20);
    }

    private void startRepeating() {

        int from = npFrom.getValue();
        int to = npTo.getValue();

        if (from > to) {
            tvResult.setText("Invalid range");
            return;
        }

        currentAyah = from;
        repeatCount = 0;
        maxRepeat = npRepeat.getValue();
        isRunning = true;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;

                tvResult.setText("Ayah: " + currentAyah + " | Repeat: " + (repeatCount + 1));

                repeatCount++;

                if (repeatCount >= maxRepeat) {
                    repeatCount = 0;
                    currentAyah++;

                    if (currentAyah > to) {
                        stopRepeating();
                        tvResult.setText("Finished ✅");
                        return;
                    }
                }

                handler.postDelayed(this, 1500);
            }
        };

        handler.post(runnable);
    }

    private void stopRepeating() {
        isRunning = false;
        handler.removeCallbacks(runnable);
    }
}