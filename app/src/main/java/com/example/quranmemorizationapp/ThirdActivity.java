package com.example.quranmemorizationapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

    Button btnBack, btnPlay, btnPause, btnReplay, btnPrevious;
    ImageButton btnMenu;

    TextView tvTitle, tvDisplay, tvPageInfo;

    ImageView imgPage;

    DBHelper dbHelper;

    SharedPreferences sharedPreferences;
    boolean isArabic;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);

        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnReplay = findViewById(R.id.btnReplay);
        btnPrevious = findViewById(R.id.btnPrevious);

        tvTitle = findViewById(R.id.tvTitle);
        tvDisplay = findViewById(R.id.tvDisplay);
        tvPageInfo = findViewById(R.id.tvPageInfo);

        imgPage = findViewById(R.id.imgPage);

        dbHelper = new DBHelper(this);

        sharedPreferences = getSharedPreferences("QiraatiSettings", MODE_PRIVATE);
        isArabic = sharedPreferences.getBoolean("arabicLanguage", false);

        updateLanguage();
        setupMenu();

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

        loadAyahFromApi(currentAyah);

        btnBack.setOnClickListener(view -> finish());

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

    private void setupMenu() {
        btnMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, btnMenu);

            popupMenu.getMenuInflater()
                    .inflate(R.menu.popup_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menuHome) {
                    startActivity(new Intent(this, MainActivity.class));

                } else if (item.getItemId() == R.id.menuAbout) {
                    startActivity(new Intent(this, AboutActivity.class));

                } else if (item.getItemId() == R.id.menuSettings) {
                    startActivity(new Intent(this, SettingsActivity.class));

                } else if (item.getItemId() == R.id.menuVideos) {
                    startActivity(new Intent(this, VideoLibraryActivity.class));
                }

                return true;
            });

            popupMenu.show();
        });
    }

    private void updateLanguage() {

        Typeface arabicFont = getResources().getFont(R.font.estedad_regular);
        Typeface englishFont = getResources().getFont(R.font.dynapuff_regular);
        Typeface selectedFont = isArabic ? arabicFont : englishFont;

        tvTitle.setTypeface(selectedFont);
        tvDisplay.setTypeface(selectedFont);
        tvPageInfo.setTypeface(selectedFont);

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

                        showCompletionDialog();
                    }
                }
            }, 8000);
        }
    }

    private void showCompletionDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        if (isArabic) {
            builder.setTitle("🌟 أحسنت!");
            builder.setMessage("لقد أنهيت جلسة الحفظ بنجاح");
            builder.setPositiveButton("إغلاق", (dialog, which) -> dialog.dismiss());
        } else {
            builder.setTitle("🌟 Great Job!");
            builder.setMessage("You finished this memorization session successfully");
            builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        }

        builder.show();
    }

    private void updateUI() {
        loadAyahFromApi(currentAyah);
    }

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

        String[] surahNamesEnglish = {
                "Al-Fatiha","Al-Baqarah","Aal-Imran","An-Nisa","Al-Ma'idah",
                "Al-An'am","Al-A'raf","Al-Anfal","At-Tawbah","Yunus",
                "Hud","Yusuf","Ar-Ra'd","Ibrahim","Al-Hijr",
                "An-Nahl","Al-Isra","Al-Kahf","Maryam","Taha",
                "Al-Anbiya","Al-Hajj","Al-Mu'minun","An-Nur","Al-Furqan",
                "Ash-Shu'ara","An-Naml","Al-Qasas","Al-Ankabut","Ar-Rum",
                "Luqman","As-Sajdah","Al-Ahzab","Saba","Fatir",
                "Ya-Sin","As-Saffat","Sad","Az-Zumar","Ghafir",
                "Fussilat","Ash-Shura","Az-Zukhruf","Ad-Dukhan","Al-Jathiyah",
                "Al-Ahqaf","Muhammad","Al-Fath","Al-Hujurat","Qaf",
                "Adh-Dhariyat","At-Tur","An-Najm","Al-Qamar","Ar-Rahman",
                "Al-Waqi'ah","Al-Hadid","Al-Mujadilah","Al-Hashr","Al-Mumtahanah",
                "As-Saff","Al-Jumu'ah","Al-Munafiqun","At-Taghabun","At-Talaq",
                "At-Tahrim","Al-Mulk","Al-Qalam","Al-Haqqah","Al-Ma'arij",
                "Nuh","Al-Jinn","Al-Muzzammil","Al-Muddaththir","Al-Qiyamah",
                "Al-Insan","Al-Mursalat","An-Naba","An-Nazi'at","Abasa",
                "At-Takwir","Al-Infitar","Al-Mutaffifin","Al-Inshiqaq","Al-Buruj",
                "At-Tariq","Al-A'la","Al-Ghashiyah","Al-Fajr","Al-Balad",
                "Ash-Shams","Al-Layl","Ad-Duha","Ash-Sharh","At-Tin",
                "Al-Alaq","Al-Qadr","Al-Bayyinah","Az-Zalzalah","Al-Adiyat",
                "Al-Qari'ah","At-Takathur","Al-Asr","Al-Humazah","Al-Fil",
                "Quraysh","Al-Ma'un","Al-Kawthar","Al-Kafirun","An-Nasr",
                "Al-Masad","Al-Ikhlas","Al-Falaq","An-Nas"
        };

        String[] surahNamesArabic = {
                "الفاتحة","البقرة","آل عمران","النساء","المائدة",
                "الأنعام","الأعراف","الأنفال","التوبة","يونس",
                "هود","يوسف","الرعد","إبراهيم","الحجر",
                "النحل","الإسراء","الكهف","مريم","طه",
                "الأنبياء","الحج","المؤمنون","النور","الفرقان",
                "الشعراء","النمل","القصص","العنكبوت","الروم",
                "لقمان","السجدة","الأحزاب","سبأ","فاطر",
                "يس","الصافات","ص","الزمر","غافر",
                "فصلت","الشورى","الزخرف","الدخان","الجاثية",
                "الأحقاف","محمد","الفتح","الحجرات","ق",
                "الذاريات","الطور","النجم","القمر","الرحمن",
                "الواقعة","الحديد","المجادلة","الحشر","الممتحنة",
                "الصف","الجمعة","المنافقون","التغابن","الطلاق",
                "التحريم","الملك","القلم","الحاقة","المعارج",
                "نوح","الجن","المزمل","المدثر","القيامة",
                "الإنسان","المرسلات","النبأ","النازعات","عبس",
                "التكوير","الانفطار","المطففين","الانشقاق","البروج",
                "الطارق","الأعلى","الغاشية","الفجر","البلد",
                "الشمس","الليل","الضحى","الشرح","التين",
                "العلق","القدر","البينة","الزلزلة","العاديات",
                "القارعة","التكاثر","العصر","الهمزة","الفيل",
                "قريش","الماعون","الكوثر","الكافرون","النصر",
                "المسد","الإخلاص","الفلق","الناس"
        };

        String surahName;

        if (currentSurahNumber >= 1 && currentSurahNumber <= 114) {
            surahName = isArabic
                    ? surahNamesArabic[currentSurahNumber - 1]
                    : surahNamesEnglish[currentSurahNumber - 1];
        } else {
            surahName = isArabic ? "غير معروفة" : "Unknown";
        }

        if (isArabic) {
            tvDisplay.setText(
                    "﴿ " + currentAyahText + " ﴾"
                            + "\n\nالسورة: " + surahName
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
                            + "\n\nSurah: " + surahName
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
    }

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