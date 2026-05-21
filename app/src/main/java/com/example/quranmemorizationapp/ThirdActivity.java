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

    Button btnPlay, btnPause, btnNext, btnPrevious;

    TextView tvDisplay, tvPageInfo, tvLastProgress;

    ImageView imgPage;

    int startAyah, endAyah, repeatLimit;

    int currentAyah;

    int currentRepeatCount = 1;

    int currentSurahNumber = 1;

    int currentAyahInSurah = 1;

    int currentPageNumber = 1;

    Handler handler = new Handler();

    boolean isRunning = false;

    MediaPlayer mediaPlayer;

    String currentAudioUrl = "";

    String currentAyahText = "";

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_third);

        btnPlay = findViewById(R.id.btnPlay);

        btnPause = findViewById(R.id.btnPause);

        btnNext = findViewById(R.id.btnNext);

        btnPrevious = findViewById(R.id.btnPrevious);

        tvDisplay = findViewById(R.id.tvDisplay);

        tvPageInfo = findViewById(R.id.tvPageInfo);

        tvLastProgress = findViewById(R.id.tvLastProgress);

        imgPage = findViewById(R.id.imgPage);

        dbHelper = new DBHelper(this);

        updateLastProgressText();

        try {

            startAyah =
                    Integer.parseInt(
                            getIntent().getStringExtra("FROM_AYAH")
                    );

            endAyah =
                    Integer.parseInt(
                            getIntent().getStringExtra("TO_AYAH")
                    );

            repeatLimit =
                    Integer.parseInt(
                            getIntent().getStringExtra("REPEAT_LIMIT")
                    );

            currentAyah = startAyah;

        } catch (Exception e) {

            startAyah = 1;

            endAyah = 5;

            repeatLimit = 1;

            currentAyah = startAyah;
        }

        // GIRL 4: Load the first ayah display from SQLite, then use API for audio/fallback.
        loadAyahFromApi(currentAyah);

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

        btnNext.setOnClickListener(v -> {

            if (currentAyah < endAyah) {

                currentAyah++;
                currentRepeatCount = 1;

                updateUI();
            }
        });
        btnPrevious.setOnClickListener(v -> {

            if (currentAyah > startAyah) {

                currentAyah--;
                currentRepeatCount = 1;

                updateUI();
            }
        });
    }

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

    private void updateUI() {

        loadAyahFromApi(currentAyah);
    }

    private void loadAyahFromApi(int ayahNumber) {

        boolean displayedFromSqlite = loadAyahFromSqlite(ayahNumber);

        String url =
                "https://api.alquran.cloud/v1/ayah/"
                        + ayahNumber +
                        "/editions/quran-uthmani,ar.alafasy";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request =
                new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,

                        response -> {

                            try {

                                JSONArray data =
                                        response.getJSONArray("data");

                                JSONObject textObject =
                                        data.getJSONObject(0);

                                JSONObject audioObject =
                                        data.getJSONObject(1);

                                currentAudioUrl =
                                        audioObject.getString("audio");

                                if (!displayedFromSqlite) {

                                    currentAyahText =
                                            textObject.getString("text");

                                    JSONObject surahObject =
                                            textObject.optJSONObject("surah");

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
                                            "خطأ بالبيانات: "
                                                    + e.getMessage()
                                    );
                                }
                            }

                        },

                        error -> {

                            if (!displayedFromSqlite) {
                                tvDisplay.setText(
                                        "خطأ API: "
                                                + error.toString()
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

    private void renderAyahDisplay() {

        tvDisplay.setText(
                "﴿ " + currentAyahText + " ﴾"
                        + "\n\nالسورة: " + currentSurahNumber
                        + "\nالآية: " + currentAyahInSurah
                        + "\nالتكرار: " + currentRepeatCount + " / " + repeatLimit
        );

        tvPageInfo.setText(
                "صفحة المصحف: " + currentPageNumber
                        + " | الآية الحالية: " + currentAyahInSurah
        );
    }

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

    private void saveCurrentProgress() {

        dbHelper.saveProgress(currentSurahNumber, currentAyahInSurah);
        updateLastProgressText();
    }

    private void updateLastProgressText() {

        if (tvLastProgress != null && dbHelper != null) {
            tvLastProgress.setText(
                    "آخر موضع محفوظ: " + dbHelper.getLastProgress()
            );
        }
    }

    private void playAudio(String audioUrl) {

        try {

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

    @Override
    protected void onDestroy() {

        super.onDestroy();

        handler.removeCallbacksAndMessages(null);

        if (mediaPlayer != null) {

            mediaPlayer.release();
        }
    }
}
