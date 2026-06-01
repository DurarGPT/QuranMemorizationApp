package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    // Buttons
    Button btnPlay, btnBack;

    // Repeat counter
    NumberPicker npRepeat;

    // TextViews
    TextView tvStatus, tvSecondTitle, tvSecondSubtitle, tvRepeatLabel;

    // User input fields
    EditText etFromAyah, etToAyah;

    // Save settings like language
    SharedPreferences sharedPreferences;

    // Check if Arabic language is selected
    boolean isArabic = false;

    // Store ayah range
    int fromAyah = 1;
    int toAyah = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect Java file with XML layout
        setContentView(R.layout.activity_second);

        // Connect buttons from XML
        btnPlay = findViewById(R.id.btnPlay);
        btnBack = findViewById(R.id.btnBack);

        // Connect repeat picker
        npRepeat = findViewById(R.id.npRepeat);

        // Connect text views
        tvStatus = findViewById(R.id.tvStatus);
        tvSecondTitle = findViewById(R.id.tvSecondTitle);
        tvSecondSubtitle = findViewById(R.id.tvSecondSubtitle);
        tvRepeatLabel = findViewById(R.id.tvRepeatLabel);

        // Connect edit texts
        etFromAyah = findViewById(R.id.etFromAyah);
        etToAyah = findViewById(R.id.etToAyah);

        // Load saved settings
        sharedPreferences =
                getSharedPreferences(
                        "QiraatiSettings",
                        MODE_PRIVATE
                );

        // Get selected language
        isArabic =
                sharedPreferences.getBoolean(
                        "arabicLanguage",
                        false
                );

        // Setup repeat number picker
        setupRepeatPicker();

        // Update screen language and font
        updateLanguage();

        // Back button closes page
        btnBack.setOnClickListener(v -> finish());

        // Start memorization button
        btnPlay.setOnClickListener(v -> startPlaying());
    }

    // Setup repeat counter range
    private void setupRepeatPicker() {

        npRepeat.setMinValue(1);
        npRepeat.setMaxValue(20);

        // Default repeat count
        npRepeat.setValue(3);
    }

    // Change language and font dynamically
    private void updateLanguage() {

        // Load Arabic and English fonts
        Typeface arabicFont =
                getResources().getFont(R.font.estedad_regular);

        Typeface englishFont =
                getResources().getFont(R.font.dynapuff_regular);

        // Select font depending on language
        Typeface selectedFont =
                isArabic ? arabicFont : englishFont;

        // Apply selected font
        btnBack.setTypeface(selectedFont);
        btnPlay.setTypeface(selectedFont);

        tvStatus.setTypeface(selectedFont);
        tvSecondTitle.setTypeface(selectedFont);
        tvSecondSubtitle.setTypeface(selectedFont);
        tvRepeatLabel.setTypeface(selectedFont);

        etFromAyah.setTypeface(selectedFont);
        etToAyah.setTypeface(selectedFont);

        // Arabic mode
        if (isArabic) {

            btnBack.setText("← رجوع");

            tvSecondTitle.setText("📖 التحكم بالحفظ");

            tvSecondSubtitle.setText(
                    "اختاري نطاق الآيات وعدد مرات التكرار"
            );

            etFromAyah.setHint("من الآية");

            etToAyah.setHint("إلى الآية");

            tvRepeatLabel.setText("عدد التكرار");

            tvStatus.setText("جاهز للبدء");

            btnPlay.setText("▶ تشغيل");

        }

        // English mode
        else {

            btnBack.setText("← Back");

            tvSecondTitle.setText(
                    "📖 Memorization Control"
            );

            tvSecondSubtitle.setText(
                    "Choose your ayah range and repeat count"
            );

            etFromAyah.setHint("From Ayah");

            etToAyah.setHint("To Ayah");

            tvRepeatLabel.setText("Repeat Count");

            tvStatus.setText("Ready to start");

            btnPlay.setText("▶ Play");
        }
    }

    // Read user input and validate it
    private boolean readInputs() {

        String fromText =
                etFromAyah.getText().toString().trim();

        String toText =
                etToAyah.getText().toString().trim();

        // Check if inputs are empty
        if (fromText.isEmpty() || toText.isEmpty()) {

            Toast.makeText(
                    this,
                    isArabic
                            ? "أدخلي من الآية وإلى الآية"
                            : "Please enter From Ayah and To Ayah",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        // Convert text to numbers
        fromAyah = Integer.parseInt(fromText);
        toAyah = Integer.parseInt(toText);

        // Validate ayah range
        if (fromAyah > toAyah) {

            Toast.makeText(
                    this,
                    isArabic
                            ? "رقم البداية يجب أن يكون أصغر من النهاية"
                            : "From Ayah must be smaller than To Ayah",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    // Start memorization screen
    private void startPlaying() {

        // Stop if input is invalid
        if (!readInputs()) return;

        // Open ThirdActivity
        android.content.Intent intent =
                new android.content.Intent(
                        SecondActivity.this,
                        ThirdActivity.class
                );

        // Send selected ayah range
        intent.putExtra(
                "FROM_AYAH",
                String.valueOf(fromAyah)
        );

        intent.putExtra(
                "TO_AYAH",
                String.valueOf(toAyah)
        );

        // Send repeat count
        intent.putExtra(
                "REPEAT_LIMIT",
                String.valueOf(npRepeat.getValue())
        );

        // Move to next screen
        startActivity(intent);
    }
}