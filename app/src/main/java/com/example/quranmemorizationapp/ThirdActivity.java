package com.example.quranmemorizationapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!isRunning) {

                    isRunning = true;

                    runRepetitionLogic();

                    Toast.makeText(
                            ThirdActivity.this,
                            "بدء الحفظ...",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

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
            }
        });

        btnNext.setOnClickListener(v -> {

            if (currentAyah < endAyah) {

                currentAyah++;

                updateUI();
            }
        });

        btnPrevious.setOnClickListener(v -> {

            if (currentAyah > startAyah) {

                currentAyah--;

                updateUI();
            }
        });
    }

    private void runRepetitionLogic() {

        if (!isRunning) return;

        handler.post(new Runnable() {

            @Override
            public void run() {

                if (currentRepeatCount <= repeatLimit) {

                    if (currentAyah <= endAyah) {

                        updateUI();

                        currentAyah++;

                        handler.postDelayed(this, 8000);

                    } else {

                        currentRepeatCount++;

                        currentAyah = startAyah;

                        handler.post(this);
                    }

                } else {

                    tvDisplay.setText("تم الانتهاء من الحفظ!");

                    isRunning = false;
                }
            }
        });
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

                                String imageUrl =
                                        "https://raw.githubusercontent.com/QuranHub/quran-pages-images/main/ayah/warsh/"
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

                                playAudio(currentAudioUrl);

                            } catch (Exception e) {

                                tvDisplay.setText(
                                        "خطأ بالبيانات: "
                                                + e.getMessage()
                                );
                            }

                        },

                        error -> tvDisplay.setText(
                                "خطأ API: "
                                        + error.getMessage()
                        )

                );

        queue.add(request);
    }

    private void playAudio(String audioUrl) {

        try {

            if (mediaPlayer != null) {

                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setDataSource(audioUrl);

            mediaPlayer.prepare();

            mediaPlayer.start();

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