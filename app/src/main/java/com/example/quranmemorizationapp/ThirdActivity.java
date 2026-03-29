package com.example.quranmemorizationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    Button btnPlay;
    Button btnPause;
    Button btnNext;
    Button btnPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // ربط الأزرار
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);

        // زر Play
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(
                        ThirdActivity.this,
                        "Play button clicked",
                        Toast.LENGTH_SHORT
                ).show();

            }
        });

        // زر Pause
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(
                        ThirdActivity.this,
                        "Pause button clicked",
                        Toast.LENGTH_SHORT
                ).show();

            }
        });

        // زر Next
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(
                        ThirdActivity.this,
                        "Next Ayah",
                        Toast.LENGTH_SHORT
                ).show();

            }
        });

        // زر Previous
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(
                        ThirdActivity.this,
                        "Previous Ayah",
                        Toast.LENGTH_SHORT
                ).show();

            }
        });

    }
}