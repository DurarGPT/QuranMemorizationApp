package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    Button btnPlay, btnPause, btnNext, btnPrev, btnBack;
    NumberPicker npRepeat;
    TextView tvStatus, tvSecondTitle, tvSecondSubtitle, tvRepeatLabel;
    EditText etFromAyah, etToAyah;

    SharedPreferences sharedPreferences;
    boolean isArabic = false;

    int currentAyah = 1;
    int repeatCount = 0;
    int maxRepeat = 1;
    int fromAyah = 1;
    int toAyah = 1;

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
        btnBack = findViewById(R.id.btnBack);

        npRepeat = findViewById(R.id.npRepeat);

        tvStatus = findViewById(R.id.tvStatus);
        tvSecondTitle = findViewById(R.id.tvSecondTitle);
        tvSecondSubtitle = findViewById(R.id.tvSecondSubtitle);
        tvRepeatLabel = findViewById(R.id.tvRepeatLabel);

        etFromAyah = findViewById(R.id.etFromAyah);
        etToAyah = findViewById(R.id.etToAyah);

        sharedPreferences = getSharedPreferences("QiraatiSettings", MODE_PRIVATE);
        isArabic = sharedPreferences.getBoolean("arabicLanguage", false);

        setupRepeatPicker();
        updateLanguage();

        btnBack.setOnClickListener(v -> finish());
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

    private void updateLanguage() {
        if (isArabic) {
            btnBack.setText("← رجوع");
            tvSecondTitle.setText("📖 التحكم بالحفظ");
            tvSecondSubtitle.setText("اختاري نطاق الآيات وعدد مرات التكرار");
            etFromAyah.setHint("من الآية");
            etToAyah.setHint("إلى الآية");
            tvRepeatLabel.setText("عدد التكرار");
            tvStatus.setText("جاهز للبدء");

            btnPlay.setText("▶ تشغيل");
            btnPause.setText("⏸ إيقاف مؤقت");
            btnNext.setText("➡ التالي");
            btnPrev.setText("⬅ السابق");
        } else {
            btnBack.setText("← Back");
            tvSecondTitle.setText("📖 Memorization Control");
            tvSecondSubtitle.setText("Choose your ayah range and repeat count");
            etFromAyah.setHint("From Ayah");
            etToAyah.setHint("To Ayah");
            tvRepeatLabel.setText("Repeat Count");
            tvStatus.setText("Ready to start");

            btnPlay.setText("▶ Play");
            btnPause.setText("⏸ Pause");
            btnNext.setText("➡ Next");
            btnPrev.setText("⬅ Previous");
        }
    }

    private boolean readInputs() {
        String fromText = etFromAyah.getText().toString().trim();
        String toText = etToAyah.getText().toString().trim();

        if (fromText.isEmpty() || toText.isEmpty()) {
            Toast.makeText(this,
                    isArabic ? "أدخلي من الآية وإلى الآية" : "Please enter From Ayah and To Ayah",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        fromAyah = Integer.parseInt(fromText);
        toAyah = Integer.parseInt(toText);

        if (fromAyah > toAyah) {
            Toast.makeText(this,
                    isArabic ? "رقم البداية يجب أن يكون أصغر من النهاية" : "From Ayah must be smaller than To Ayah",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        maxRepeat = npRepeat.getValue();
        return true;
    }

    private void startPlaying() {
        if (!readInputs()) return;

        currentAyah = fromAyah;
        repeatCount = 0;
        isPlaying = true;

        handler.removeCallbacksAndMessages(null);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isPlaying) return;

                tvStatus.setText(
                        isArabic
                                ? "الآية " + currentAyah + "  •  التكرار " + (repeatCount + 1) + "/" + maxRepeat
                                : "Ayah " + currentAyah + "  •  Repeat " + (repeatCount + 1) + "/" + maxRepeat
                );

                repeatCount++;

                if (repeatCount >= maxRepeat) {
                    repeatCount = 0;
                    currentAyah++;

                    if (currentAyah > toAyah) {
                        currentAyah = fromAyah;
                    }
                }

                handler.postDelayed(this, 1500);
            }
        };

        handler.post(runnable);
    }

    private void pausePlaying() {
        isPlaying = false;

        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }

        Toast.makeText(this,
                isArabic ? "تم الإيقاف مؤقتًا" : "Paused",
                Toast.LENGTH_SHORT).show();
    }

    private void nextAyah() {
        if (!readInputs()) return;

        if (currentAyah < toAyah) {
            currentAyah++;
        } else {
            currentAyah = fromAyah;
        }

        repeatCount = 0;
        tvStatus.setText(isArabic ? "الآية " + currentAyah : "Ayah " + currentAyah);
    }

    private void previousAyah() {
        if (!readInputs()) return;

        if (currentAyah > fromAyah) {
            currentAyah--;
        } else {
            currentAyah = toAyah;
        }

        repeatCount = 0;
        tvStatus.setText(isArabic ? "الآية " + currentAyah : "Ayah " + currentAyah);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}