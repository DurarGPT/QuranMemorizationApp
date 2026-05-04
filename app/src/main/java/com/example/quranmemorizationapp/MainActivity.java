package com.example.quranmemorizationapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnStart, btnAbout;
    TextView tvAyahDisplay;


    int currentAyah = 1;
    int endAyah = 5;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnAbout = findViewById(R.id.btnAbout);

        tvAyahDisplay = findViewById(R.id.tvCurrentAyah);


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRepeatingAyat();

            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAboutDialog();
            }
        });
    }


    private void startRepeatingAyat() {
        currentAyah = 1;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (currentAyah <= endAyah) {

                    if (tvAyahDisplay != null) {
                        tvAyahDisplay.setText("الآية الحالية: " + currentAyah);
                    }

                    Toast.makeText(MainActivity.this, "تكرار الآية: " + currentAyah, Toast.LENGTH_SHORT).show();

                    currentAyah++;

                    handler.postDelayed(this, 3000);
                } else {
                    if (tvAyahDisplay != null) tvAyahDisplay.setText("تم الانتهاء من التكرار");
                    handler.removeCallbacks(this);
                }
            }
        };
        handler.post(runnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.menu_about) {
            showAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("About App");
        builder.setMessage("Noor Al-Tifl helps children memorize Quran verses by selecting a verse range and repeating it in a simple way.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}