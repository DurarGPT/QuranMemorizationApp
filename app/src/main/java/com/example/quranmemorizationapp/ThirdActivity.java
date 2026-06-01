package com.example.quranmemorizationapp;

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

    // Text display views
    TextView tvDisplay, tvPageInfo, tvLastProgress;

    // Mushaf page image
    ImageView imgPage;

    // Ayah range received from SecondActivity
    int startAyah, endAyah, repeatLimit;

    // Current ayah being displayed
    int currentAyah;

    // Repeat counter
    int currentRepeatCount = 1;

    // Current Quran information
    int currentSurahNumber = 1;
    int currentAyahInSurah = 1;
    int currentPageNumber = 1;

    // Handler controls repetition timing
    Handler handler = new Handler();

    // Checks if repetition is currently running
    boolean isRunning = false;

    // Plays ayah audio
    MediaPlayer mediaPlayer;

    // Stores current audio URL and ayah text
    String currentAudioUrl = "";
    String currentAyahText = "";

    // SQLite helper
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Connect Java activity to XML layout
        setContentView(R.layout.activity_third);

        // Connect buttons
        btnBack = findViewById(R.id.btnBack);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnReplay = findViewById(R.id.btnReplay);
        btnPrevious = findViewById(R.id.btnPrevious);

        // Connect text views
        tvDisplay = findViewById(R.id.tvDisplay);
        tvPageInfo = findViewById(R.id.tvPageInfo);
        tvLastProgress = findViewById(R.id.tvLastProgress);

        // Connect image view
        imgPage = findViewById(R.id.imgPage);

        // Initialize database helper
        dbHelper = new DBHelper(this);

        // Show saved progress
        updateLastProgressText();

        // Receive ayah range and repeat count from SecondActivity
        try {

            startAyah = Integer.parseInt(
                    getIntent().getStringExtra("FROM_AYAH")
            );

            endAyah = Integer.parseInt(
                    getIntent().getStringExtra("TO_AYAH")
            );

            repeatLimit = Integer.parseInt(
                    getIntent().getStringExtra("REPEAT_LIMIT")
            );

            currentAyah = startAyah;

        } catch (Exception e) {

            // Default values if intent data is missing
            startAyah = 1;
            endAyah = 5;
            repeatLimit = 1;
            currentAyah = startAyah;
        }

        // Load first ayah display
        loadAyahFromApi(currentAyah);

        // Back button closes this page
        btnBack.setOnClickListener(view -> finish());

        // Play button starts memorization repetition
        btnPlay.setOnClickListener(view -> {

            if (!isRunning) {

                isRunning = true;

                playAudio(currentAudioUrl);

                runRepetitionLogic();

                Toast.makeText(
                        ThirdActivity.this,
                        "بدء الحفظ...",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Pause button stops repetition and pauses audio
        btnPause.setOnClickListener(view -> {

            isRunning = false;

            handler.removeCallbacksAndMessages(null);

            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }

            Toast.makeText(
                    ThirdActivity.this,
                    "توقف مؤقت",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Replay button repeats the same current ayah
        btnReplay.setOnClickListener(v -> {

            currentRepeatCount = 1;

            renderAyahDisplay();

            playAudio(currentAudioUrl);

            Toast.makeText(
                    ThirdActivity.this,
                    "تمت إعادة تشغيل الآية",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Previous button moves to the previous ayah
        btnPrevious.setOnClickListener(v -> {

            if (currentAyah > startAyah) {

                currentAyah--;
                currentRepeatCount = 1;

                updateUI();

            } else {

                Toast.makeText(
                        ThirdActivity.this,
                        "هذه أول آية في النطاق",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // Controls automatic ayah repetition
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

                        tvDisplay.setText("تم الانتهاء من الحفظ!");
                    }
                }

            }, 8000);
        }
    }

    // Refreshes the current ayah display
    private void updateUI() {
        loadAyahFromApi(currentAyah);
    }

    // Loads ayah data from SQLite first, then API for audio/fallback
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

                                // Only update text/page from API if SQLite did not have it
                                if (!displayedFromSqlite) {

                                    currentAyahText = textObject.getString("text");

                                    JSONObject surahObject =
                                            textObject.optJSONObject("surah");

                                    if (surahObject != null) {

                                        currentSurahNumber =
                                                surahObject.optInt(
                                                        "number",
                                                        currentSurahNumber
                                                );
                                    }

                                    currentAyahInSurah =
                                            textObject.optInt(
                                                    "numberInSurah",
                                                    ayahNumber
                                            );

                                    currentPageNumber =
                                            textObject.optInt(
                                                    "page",
                                                    currentPageNumber
                                            );

                                    loadMushafPageImage(currentPageNumber);

                                    saveCurrentProgress();

                                    renderAyahDisplay();

                                } else {

                                    updateLastProgressText();
                                }

                            } catch (Exception e) {

                                if (!displayedFromSqlite) {

                                    tvDisplay.setText(
                                            "خطأ بالبيانات: " + e.getMessage()
                                    );
                                }
                            }
                        },

                        error -> {

                            if (!displayedFromSqlite) {

                                tvDisplay.setText(
                                        "خطأ API: " + error.toString()
                                );
                            }

                            Toast.makeText(
                                    this,
                                    error.toString(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );

        queue.add(request);
    }

    // Loads ayah from local SQLite database
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

    // Updates Arabic text shown on screen
    private void renderAyahDisplay() {

        tvDisplay.setText(
                "﴿ " + currentAyahText + " ﴾"
                        + "\n\nالسورة: "
                        + toArabicNumbers(currentSurahNumber)

                        + "\nالآية: "
                        + toArabicNumbers(currentAyahInSurah)

                        + "\nالتكرار: "
                        + toArabicNumbers(currentRepeatCount)

                        + " / "
                        + toArabicNumbers(repeatLimit)
        );

        tvPageInfo.setText(
                "صفحة المصحف: "
                        + toArabicNumbers(currentPageNumber)

                        + " | الآية الحالية: "
                        + toArabicNumbers(currentAyahInSurah)
        );
    }

    // Loads Mushaf page image dynamically using page number
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

    // Saves current progress in SQLite
    private void saveCurrentProgress() {

        dbHelper.saveProgress(
                currentSurahNumber,
                currentAyahInSurah
        );

        updateLastProgressText();
    }

    // Shows last saved progress in Arabic
    private void updateLastProgressText() {

        if (tvLastProgress != null && dbHelper != null) {

            String progress = dbHelper.getLastProgress();

            progress = progress
                    .replace("No Progress", "لا يوجد تقدم محفوظ")
                    .replace("Last Read: Surah", "السورة")
                    .replace("Ayah", "، الآية");

            tvLastProgress.setText(
                    "آخر موضع محفوظ: " + progress
            );
        }
    }

    // Plays current ayah audio
    private void playAudio(String audioUrl) {

        try {

            if (audioUrl == null || audioUrl.isEmpty()) {

                Toast.makeText(
                        this,
                        "لم يتم تحميل الصوت بعد",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (mediaPlayer != null) {

                mediaPlayer.stop();

                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioStreamType(
                    android.media.AudioManager.STREAM_MUSIC
            );

            mediaPlayer.setDataSource(audioUrl);

            mediaPlayer.setOnPreparedListener(mp -> {

                mp.start();

                Toast.makeText(
                        this,
                        "يعمل الصوت الآن",
                        Toast.LENGTH_SHORT
                ).show();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {

                Toast.makeText(
                        this,
                        "فشل تشغيل الصوت",
                        Toast.LENGTH_LONG
                ).show();

                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "خطأ بالصوت: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
    // Convert English numbers to Arabic numbers
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

        // Stop delayed repetition tasks
        handler.removeCallbacksAndMessages(null);

        // Release audio resources
        if (mediaPlayer != null) {

            mediaPlayer.release();
        }
    }
}