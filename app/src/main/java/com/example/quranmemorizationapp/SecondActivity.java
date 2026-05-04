package com.example.quranmemorizationapp;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    Button btnPlay, btnPause, btnNext, btnPrev;
    NumberPicker npRepeat;
    TextView tvStatus;

    int currentAyah = 1;
    int repeatCount = 0;
    int maxRepeat = 1;

    boolean isPlaying = false;

    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        npRepeat = findViewById(R.id.npRepeat);
        tvStatus = findViewById(R.id.tvStatus);

        setupRepeatPicker();

        btnPlay.setOnClickListener(v -> startPlaying());

        btnPause.setOnClickListener(v -> pausePlaying());

        btnNext.setOnClickListener(v -> nextAyah());

        btnPrev.setOnClickListener(v -> previousAyah());
    }

    private void setupRepeatPicker() {
        npRepeat.setMinValue(1);
        npRepeat.setMaxValue(20);
        npRepeat.setValue(3);
    }

    private void startPlaying() {
        maxRepeat = npRepeat.getValue();
        isPlaying = true;

        runnable = new Runnable() {
            @Override
            public void run() {

                if (!isPlaying) return;

                tvStatus.setText("Ayah: " + currentAyah + " | Repeat: " + (repeatCount + 1));

                repeatCount++;

                if (repeatCount >= maxRepeat) {
                    repeatCount = 0;
                    currentAyah++;
                }

                handler.postDelayed(this, 1500);
            }
        };

        handler.post(runnable);
    }

    private void pausePlaying() {
        isPlaying = false;
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
    }

    private void nextAyah() {
        currentAyah++;
        repeatCount = 0;
        tvStatus.setText("Ayah: " + currentAyah);
    }

    private void previousAyah() {
        if (currentAyah > 1) {
            currentAyah--;
            repeatCount = 0;
            tvStatus.setText("Ayah: " + currentAyah);
        }
    }
}