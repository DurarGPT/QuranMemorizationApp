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

public class ThirdActivity extends AppCompatActivity {

    Button btnPlay, btnPause, btnNext, btnPrevious;

    TextView tvDisplay;

    ImageView imgPage;

    int startAyah, endAyah, repeatLimit;

    int currentAyah;

    int currentRepeatCount = 1;

    Handler handler = new Handler();

    boolean isRunning = false;

    MediaPlayer mediaPlayer;

    String currentAudioUrl = "";

    String currentAyahText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_third);

        btnPlay = findViewById(R.id.btnPlay);

        btnPause = findViewById(R.id.btnPause);

        btnNext = findViewById(R.id.btnNext);

        btnPrevious = findViewById(R.id.btnPrevious);

        tvDisplay = findViewById(R.id.tvDisplay);

        imgPage = findViewById(R.id.imgPage);

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

        // تحميل أول آية وصورة مباشرة
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

            tvDisplay.setText(
                    "﴿ " + currentAyahText + " ﴾"
                            + "\n\nالآية: " + currentAyah
                            + "\nالتكرار: " + currentRepeatCount + " / " + repeatLimit
            );

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

                                currentAyahText =
                                        textObject.getString("text");

                                currentAudioUrl =
                                        audioObject.getString("audio");

                                // رابط صورة صحيح
                                String imageUrl =
                                        "https://cdn.islamic.network/quran/images/"
                                                + ayahNumber +
                                                ".png";

                                Glide.with(this)
                                        .load(imageUrl)
                                        .into(imgPage);

                                tvDisplay.setText(

                                        "﴿ "
                                                + currentAyahText
                                                + " ﴾"

                                                + "\n\n"

                                                + "الآية: "
                                                + ayahNumber

                                                + "\n"

                                                + "التكرار: "
                                                + currentRepeatCount
                                );

                            } catch (Exception e) {

                                tvDisplay.setText(
                                        "خطأ بالبيانات: "
                                                + e.getMessage()
                                );
                            }

                        },

                        error -> {

                            tvDisplay.setText(
                                    "خطأ API: "
                                            + error.toString()
                            );

                            Toast.makeText(
                                    this,
                                    error.toString(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                );

        queue.add(request);
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