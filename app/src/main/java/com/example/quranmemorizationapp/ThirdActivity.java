package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class ThirdActivity extends AppCompatActivity {

    // Buttons
    Button btnBack, btnPlay, btnPause, btnReplay, btnPrevious;

    // TextViews
    TextView tvTitle, tvDisplay, tvPageInfo, tvLastProgress;

    // Mushaf page image
    ImageView imgPage;

    // Database helper
    DBHelper dbHelper;

    // Language settings
    SharedPreferences sharedPreferences;
    boolean isArabic;

    // Ayah range from SecondActivity
    int startAyah, endAyah, repeatLimit;

    // Current ayah information
    int currentAyah;
    int currentRepeatCount = 1;
    int currentSurahNumber = 1;
    int currentAyahInSurah = 1;
    int currentPageNumber = 1;

    // Audio and repetition control
    Handler handler = new Handler();
    boolean isRunning = false;
    MediaPlayer mediaPlayer;

    // Current ayah content
    String currentAudioUrl = "";
    String currentAyahText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // Connect buttons
        btnBack = findViewById(R.id.btnBack);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnReplay = findViewById(R.id.btnReplay);
        btnPrevious = findViewById(R.id.btnPrevious);

        // Connect TextViews
        tvTitle = findViewById(R.id.tvTitle);
        tvDisplay = findViewById(R.id.tvDisplay);
        tvPageInfo = findViewById(R.id.tvPageInfo);
        tvLastProgress = findViewById(R.id.tvLastProgress);

        // Connect image
        imgPage = findViewById(R.id.imgPage);

        // Initialize database
        dbHelper = new DBHelper(this);

        // Read saved language from SettingsActivity
        sharedPreferences = getSharedPreferences("QiraatiSettings", MODE_PRIVATE);
        isArabic = sharedPreferences.getBoolean("arabicLanguage", false);

        // Apply correct language and font
        updateLanguage();

        // Show saved progress
        updateLastProgressText();

        // Receive selected ayah range and repeat count
        try {
            startAyah = Integer.parseInt(getIntent().getStringExtra("FROM_AYAH"));
            endAyah = Integer.parseInt(getIntent().getStringExtra("TO_AYAH"));
            repeatLimit = Integer.parseInt(getIntent().getStringExtra("REPEAT_LIMIT"));
            currentAyah = startAyah;
        } catch (Exception e) {
            startAyah = 1;
            endAyah = 5;
            repeatLimit = 1;
            currentAyah = startAyah;
        }

        // Load first ayah
        loadAyahFromApi(currentAyah);

        // Back button closes this screen
        btnBack.setOnClickListener(view -> finish());

        // Play button starts repetition
        btnPlay.setOnClickListener(view -> {
            if (!isRunning) {
                isRunning = true;

                playAudio(currentAudioUrl);
                runRepetitionLogic();

                Toast.makeText(
                        ThirdActivity.this,
                        isArabic ? "بدء الحفظ..." : "Memorization started...",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Pause button stops repetition
        btnPause.setOnClickListener(view -> {
            isRunning = false;
            handler.removeCallbacksAndMessages(null);

            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }

            Toast.makeText(
                    ThirdActivity.this,
                    isArabic ? "توقف مؤقت" : "Paused",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Replay button replays the current ayah
        btnReplay.setOnClickListener(v -> {
            currentRepeatCount = 1;
            renderAyahDisplay();
            playAudio(currentAudioUrl);

            Toast.makeText(
                    ThirdActivity.this,
                    isArabic ? "تمت إعادة تشغيل الآية" : "Ayah replayed",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Previous button moves to previous ayah
        btnPrevious.setOnClickListener(v -> {
            if (currentAyah > startAyah) {
                currentAyah--;
                currentRepeatCount = 1;
                updateUI();
            } else {
                Toast.makeText(
                        ThirdActivity.this,
                        isArabic ? "هذه أول آية في النطاق" : "This is the first ayah in the range",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // Apply selected language + selected font
    private void updateLanguage() {

        Typeface arabicFont = getResources().getFont(R.font.estedad_regular);
        Typeface englishFont = getResources().getFont(R.font.dynapuff_regular);
        Typeface selectedFont = isArabic ? arabicFont : englishFont;

        tvTitle.setTypeface(selectedFont);
        tvDisplay.setTypeface(selectedFont);
        tvPageInfo.setTypeface(selectedFont);
        tvLastProgress.setTypeface(selectedFont);

        btnBack.setTypeface(selectedFont);
        btnPlay.setTypeface(selectedFont);
        btnPause.setTypeface(selectedFont);
        btnReplay.setTypeface(selectedFont);
        btnPrevious.setTypeface(selectedFont);

        if (isArabic) {
            tvTitle.setText("التحكم بالحفظ");

            btnBack.setText("← رجوع");
            btnPlay.setText("تشغيل");
            btnPause.setText("إيقاف مؤقت");
            btnReplay.setText("إعادة تشغيل الآية");
            btnPrevious.setText("الآية السابقة");
        } else {
            tvTitle.setText("Memorization Control");

            btnBack.setText("← Back");
            btnPlay.setText("Play");
            btnPause.setText("Pause");
            btnReplay.setText("Replay Ayah");
            btnPrevious.setText("Previous Ayah");
        }
    }

    // Automatic repetition logic
    private void runRepetitionLogic() {
        if (!isRunning) return;

        if (currentRepeatCount <= repeatLimit) {
            renderAyahDisplay();
            playAudio(currentAudioUrl);

            handler.postDelayed(() -> {
                if (!isRunning) return;

                currentRepeatCount++;

                if (currentRepeatCount <= repeatLimit) {
                    runRepetitionLogic();
                } else {
                    currentRepeatCount = 1;

                    if (currentAyah < endAyah) {
                        currentAyah++;
                        loadAyahFromApi(currentAyah);

                        handler.postDelayed(() -> {
                            if (isRunning) {
                                runRepetitionLogic();
                            }
                        }, 1500);

                    } else {
                        isRunning = false;
                        saveCurrentProgress();

                        tvDisplay.setText(
                                isArabic
                                        ? "تم الانتهاء من الحفظ!"
                                        : "Memorization completed!"
                        );
                    }
                }
            }, 8000);
        }
    }

    // Reload current ayah
    private void updateUI() {
        loadAyahFromApi(currentAyah);
    }

    // Load ayah data from SQLite first, then API for audio and fallback
    private void loadAyahFromApi(int ayahNumber) {
        boolean displayedFromSqlite = loadAyahFromSqlite(ayahNumber);

        String url =
                "https://api.alquran.cloud/v1/ayah/"
                        + ayahNumber
                        + "/editions/quran-uthmani,ar.alafasy";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        response -> {
                            try {
                                JSONArray data = response.getJSONArray("data");

                                JSONObject textObject = data.getJSONObject(0);
                                JSONObject audioObject = data.getJSONObject(1);

                                currentAudioUrl = audioObject.getString("audio");

                                if (!displayedFromSqlite) {
                                    currentAyahText = textObject.getString("text");

                                    JSONObject surahObject = textObject.optJSONObject("surah");

                                    if (surahObject != null) {
                                        currentSurahNumber =
                                                surahObject.optInt("number", currentSurahNumber);
                                    }

                                    currentAyahInSurah =
                                            textObject.optInt("numberInSurah", ayahNumber);

                                    currentPageNumber =
                                            textObject.optInt("page", currentPageNumber);

                                    loadMushafPageImage(currentPageNumber);
                                    saveCurrentProgress();
                                    renderAyahDisplay();
                                } else {
                                    updateLastProgressText();
                                }

                            } catch (Exception e) {
                                if (!displayedFromSqlite) {
                                    tvDisplay.setText(
                                            isArabic
                                                    ? "خطأ بالبيانات: " + e.getMessage()
                                                    : "Data error: " + e.getMessage()
                                    );
                                }
                            }
                        },
                        error -> {
                            if (!displayedFromSqlite) {
                                tvDisplay.setText(
                                        isArabic
                                                ? "خطأ API: " + error.toString()
                                                : "API error: " + error.toString()
                                );
                            }

                            Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                );

        queue.add(request);
    }

    // Load ayah from local SQLite database
    private boolean loadAyahFromSqlite(int ayahNumber) {
        Verse verse = dbHelper.getVerseByAyahNumber(ayahNumber);

        if (verse == null) {
            return false;
        }

        currentSurahNumber = verse.surahNumber;
        currentAyahInSurah = verse.ayahNumber;
        currentPageNumber = verse.pageNumber;
        currentAyahText = verse.textAr;

        loadMushafPageImage(currentPageNumber);
        saveCurrentProgress();
        renderAyahDisplay();

        return true;
    }

    // Display ayah information based on selected language
    private void renderAyahDisplay() {
        if (isArabic) {
            tvDisplay.setText(
                    "﴿ " + currentAyahText + " ﴾"
                            + "\n\nالسورة: " + toArabicNumbers(currentSurahNumber)
                            + "\nالآية: " + toArabicNumbers(currentAyahInSurah)
                            + "\nالتكرار: " + toArabicNumbers(currentRepeatCount)
                            + " / " + toArabicNumbers(repeatLimit)
            );

            tvPageInfo.setText(
                    "صفحة المصحف: " + toArabicNumbers(currentPageNumber)
                            + " | الآية الحالية: " + toArabicNumbers(currentAyahInSurah)
            );
        } else {
            tvDisplay.setText(
                    "﴿ " + currentAyahText + " ﴾"
                            + "\n\nSurah: " + currentSurahNumber
                            + "\nAyah: " + currentAyahInSurah
                            + "\nRepeat: " + currentRepeatCount
                            + " / " + repeatLimit
            );

            tvPageInfo.setText(
                    "Mushaf Page: " + currentPageNumber
                            + " | Current Ayah: " + currentAyahInSurah
            );
        }
    }

    // Load Mushaf page image from page number
    private void loadMushafPageImage(int pageNumber) {
        if (pageNumber <= 0) {
            imgPage.setImageResource(R.drawable.ic_launcher_background);
            return;
        }

        String imageUrl = String.format(
                Locale.US,
                "https://quran.ksu.edu.sa/png_big/%d.png",
                pageNumber
        );

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.card_background)
                .error(R.drawable.ic_launcher_background)
                .into(imgPage);
    }

    // Save progress locally
    private void saveCurrentProgress() {
        dbHelper.saveProgress(currentSurahNumber, currentAyahInSurah);
        updateLastProgressText();
    }

    // Show saved progress in selected language
    private void updateLastProgressText() {
        if (tvLastProgress != null && dbHelper != null) {
            String progress = dbHelper.getLastProgress();

            if (isArabic) {
                progress = progress
                        .replace("No Progress", "لا يوجد تقدم محفوظ")
                        .replace("Last Read: Surah", "السورة")
                        .replace("Ayah", "، الآية");

                tvLastProgress.setText("آخر موضع محفوظ: " + progress);
            } else {
                tvLastProgress.setText("Last saved progress: " + progress);
            }
        }
    }

    // Play current ayah audio
    private void playAudio(String audioUrl) {
        try {
            if (audioUrl == null || audioUrl.isEmpty()) {
                Toast.makeText(
                        this,
                        isArabic ? "لم يتم تحميل الصوت بعد" : "Audio has not loaded yet",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioUrl);

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();

                Toast.makeText(
                        this,
                        isArabic ? "يعمل الصوت الآن" : "Audio is playing now",
                        Toast.LENGTH_SHORT
                ).show();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(
                        this,
                        isArabic ? "فشل تشغيل الصوت" : "Audio playback failed",
                        Toast.LENGTH_LONG
                ).show();

                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    isArabic ? "خطأ بالصوت: " + e.getMessage() : "Audio error: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // Convert English digits to Arabic digits
    private String toArabicNumbers(int number) {
        return String.valueOf(number)
                .replace("0", "٠")
                .replace("1", "١")
                .replace("2", "٢")
                .replace("3", "٣")
                .replace("4", "٤")
                .replace("5", "٥")
                .replace("6", "٦")
                .replace("7", "٧")
                .replace("8", "٨")
                .replace("9", "٩");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacksAndMessages(null);

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}