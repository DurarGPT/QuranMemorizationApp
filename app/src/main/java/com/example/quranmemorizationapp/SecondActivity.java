package com.example.quranmemorizationapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    // Buttons used on the memorization setup screen.
    private Button btnPlay, btnBack, btnContinueProgress;

    // Top menu button.
    private ImageButton btnMenu;

    // NumberPicker lets the user choose how many times each ayah repeats.
    private NumberPicker npRepeat;

    // TextViews used for the screen title, subtitle, labels, and status text.
    private TextView tvStatus, tvSecondTitle, tvSecondSubtitle, tvRepeatLabel, tvSurahLabel;

    // EditTexts for the ayah range entered by the user.
    private EditText etFromAyah, etToAyah;

    // Searchable dropdown for choosing a Surah.
    private AutoCompleteTextView actvSurah;

    // SharedPreferences reads the language setting saved in SettingsActivity.
    private SharedPreferences sharedPreferences;

    // SQLite helper used for the Continue Last Progress feature.
    private DBHelper dbHelper;

    // true = Arabic mode, false = English mode.
    private boolean isArabic = false;

    // The selected Surah index. Index 0 means Surah 1, index 1 means Surah 2, etc.
    private int selectedSurahIndex = 0;

    // The ayah range typed by the user inside the selected Surah.
    private int fromAyah = 1;
    private int toAyah = 1;

    // English Surah display list, for example: "1. Al-Faatiha".
    private final ArrayList<String> surahEnglish = new ArrayList<>();

    // Arabic Surah display list, for example: "١. الفاتحة".
    private final ArrayList<String> surahArabic = new ArrayList<>();

    // Stores the number of ayahs in every Surah.
    private final ArrayList<Integer> surahAyahCounts = new ArrayList<>();

    // Stores the first global Quran ayah number for every Surah.
    // Example: Surah 1 starts at global ayah 1, Surah 2 starts at global ayah 8.
    private final ArrayList<Integer> surahStartGlobalAyah = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Connect Java variables to the views in activity_second.xml.
        btnPlay = findViewById(R.id.btnPlay);
        btnContinueProgress = findViewById(R.id.btnContinueProgress);
        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);

        npRepeat = findViewById(R.id.npRepeat);

        tvStatus = findViewById(R.id.tvStatus);
        tvSecondTitle = findViewById(R.id.tvSecondTitle);
        tvSecondSubtitle = findViewById(R.id.tvSecondSubtitle);
        tvRepeatLabel = findViewById(R.id.tvRepeatLabel);
        tvSurahLabel = findViewById(R.id.tvSurahLabel);

        etFromAyah = findViewById(R.id.etFromAyah);
        etToAyah = findViewById(R.id.etToAyah);
        actvSurah = findViewById(R.id.actvSurah);

        // Create the database helper once, so all methods use the same helper object.
        dbHelper = new DBHelper(this);

        // Read the language selected by the user.
        sharedPreferences = getSharedPreferences("QiraatiSettings", MODE_PRIVATE);
        isArabic = sharedPreferences.getBoolean("arabicLanguage", false);

        // Prepare the repeat picker, language text, menu, and Surah API list.
        setupRepeatPicker();
        updateLanguage();
        setupMenu();
        fetchSurahsFromApi();

        // Back returns to the previous activity.
        btnBack.setOnClickListener(v -> finish());

        // Play starts a new memorization session after validating the inputs.
        btnPlay.setOnClickListener(v -> startPlaying());

        // Continue opens ThirdActivity from the last saved position in SQLite.
        btnContinueProgress.setOnClickListener(v -> continueLastProgress());
    }

    private void setupMenu() {
        btnMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, btnMenu);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

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

    private void setupRepeatPicker() {
        // User can repeat each ayah from 1 to 20 times.
        npRepeat.setMinValue(1);
        npRepeat.setMaxValue(20);
        npRepeat.setValue(3);
    }

    private void updateLanguage() {
        Typeface arabicFont = ResourcesCompat.getFont(this, R.font.estedad_regular);
        Typeface englishFont = ResourcesCompat.getFont(this, R.font.dynapuff_regular);
        Typeface selectedFont = isArabic ? arabicFont : englishFont;

        btnBack.setTypeface(selectedFont);
        btnPlay.setTypeface(selectedFont);
        btnContinueProgress.setTypeface(selectedFont);
        tvStatus.setTypeface(selectedFont);
        tvSecondTitle.setTypeface(selectedFont);
        tvSecondSubtitle.setTypeface(selectedFont);
        tvRepeatLabel.setTypeface(selectedFont);
        tvSurahLabel.setTypeface(selectedFont);
        etFromAyah.setTypeface(selectedFont);
        etToAyah.setTypeface(selectedFont);
        actvSurah.setTypeface(selectedFont);

        if (isArabic) {
            btnBack.setText("← رجوع");
            tvSecondTitle.setText("📖 التحكم بالحفظ");
            tvSecondSubtitle.setText("اختاري السورة ونطاق الآيات وعدد مرات التكرار");
            tvSurahLabel.setText("اختاري السورة");
            actvSurah.setHint("ابحثي عن السورة");
            etFromAyah.setHint("من الآية");
            etToAyah.setHint("إلى الآية");
            tvRepeatLabel.setText("عدد التكرار");
            tvStatus.setText("جاهز للبدء");
            btnPlay.setText("▶ تشغيل");
            btnContinueProgress.setText("متابعة آخر تقدم");
        } else {
            btnBack.setText("← Back");
            tvSecondTitle.setText("📖 Memorization Control");
            tvSecondSubtitle.setText("Choose the surah, ayah range, and repeat count");
            tvSurahLabel.setText("Choose Surah");
            actvSurah.setHint("Search Surah");
            etFromAyah.setHint("From Ayah");
            etToAyah.setHint("To Ayah");
            tvRepeatLabel.setText("Repeat Count");
            tvStatus.setText("Ready to start");
            btnPlay.setText("▶ Play");
            btnContinueProgress.setText("Continue Last Progress");
        }
    }

    private void fetchSurahsFromApi() {
        String url = "https://api.alquran.cloud/v1/surah";
        tvStatus.setText(isArabic ? "جاري تحميل السور..." : "Loading Surahs...");

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");

                        surahEnglish.clear();
                        surahArabic.clear();
                        surahAyahCounts.clear();
                        surahStartGlobalAyah.clear();

                        int globalStartAyah = 1;

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject surah = data.getJSONObject(i);

                            int number = surah.getInt("number");
                            String englishName = surah.getString("englishName");
                            String arabicName = surah.getString("name");
                            int ayahCount = surah.getInt("numberOfAyahs");

                            surahEnglish.add(number + ". " + englishName);
                            surahArabic.add(toArabicNumber(number) + ". " + arabicName);
                            surahAyahCounts.add(ayahCount);
                            surahStartGlobalAyah.add(globalStartAyah);

                            globalStartAyah += ayahCount;
                        }

                        setupSurahSearch();
                        tvStatus.setText(isArabic ? "جاهز للبدء" : "Ready to start");
                    } catch (Exception e) {
                        tvStatus.setText(isArabic ? "خطأ في تحميل السور" : "Error loading Surahs");
                        Toast.makeText(this,
                                isArabic ? "حدث خطأ في قراءة بيانات السور" : "Error reading Surah data",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    tvStatus.setText(isArabic ? "تأكدي من الاتصال بالإنترنت" : "Check your internet connection");
                    Toast.makeText(this,
                            isArabic ? "تعذر تحميل السور من الإنترنت" : "Could not load Surahs from API",
                            Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(request);
    }

    private void setupSurahSearch() {
        ArrayList<String> currentList = isArabic ? surahArabic : surahEnglish;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                currentList
        );

        actvSurah.setAdapter(adapter);
        actvSurah.setThreshold(0);

        actvSurah.setOnClickListener(v -> actvSurah.showDropDown());
        actvSurah.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) actvSurah.showDropDown();
        });

        if (!currentList.isEmpty()) {
            actvSurah.setText(currentList.get(0), false);
            selectedSurahIndex = 0;
        }

        actvSurah.setOnItemClickListener((parent, view, position, id) -> {
            selectedSurahIndex = position;
            etFromAyah.setText("");
            etToAyah.setText("");
            tvStatus.setText(isArabic ? "تم اختيار السورة" : "Surah selected");
        });
    }

    private boolean readInputs() {
        if (surahEnglish.isEmpty() || surahArabic.isEmpty() || surahAyahCounts.isEmpty()) {
            Toast.makeText(this,
                    isArabic ? "انتظري حتى يتم تحميل السور" : "Please wait until Surahs finish loading",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        String selectedSurahText = actvSurah.getText().toString().trim();
        ArrayList<String> currentList = isArabic ? surahArabic : surahEnglish;

        selectedSurahIndex = -1;
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).equals(selectedSurahText)) {
                selectedSurahIndex = i;
                break;
            }
        }

        if (selectedSurahIndex == -1) {
            Toast.makeText(this,
                    isArabic ? "اختاري سورة صحيحة من القائمة" : "Please choose a valid Surah from the list",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        String fromText = etFromAyah.getText().toString().trim();
        String toText = etToAyah.getText().toString().trim();

        if (fromText.isEmpty() || toText.isEmpty()) {
            Toast.makeText(this,
                    isArabic ? "أدخلي من الآية وإلى الآية" : "Please enter From Ayah and To Ayah",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            fromAyah = Integer.parseInt(convertArabicDigitsToEnglish(fromText));
            toAyah = Integer.parseInt(convertArabicDigitsToEnglish(toText));
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    isArabic ? "أدخلي أرقامًا صحيحة للآيات" : "Please enter valid ayah numbers",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int maxAyah = surahAyahCounts.get(selectedSurahIndex);

        if (fromAyah < 1 || toAyah < 1 || fromAyah > maxAyah || toAyah > maxAyah) {
            Toast.makeText(this,
                    isArabic ? "رقم الآية خارج نطاق السورة" : "Ayah number is outside this Surah range",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fromAyah > toAyah) {
            Toast.makeText(this,
                    isArabic ? "رقم البداية يجب أن يكون أصغر من النهاية" : "From Ayah must be smaller than To Ayah",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void startPlaying() {
        if (!readInputs()) return;

        // Convert from Surah-local ayah numbers to global Quran ayah numbers.
        int globalFromAyah = surahStartGlobalAyah.get(selectedSurahIndex) + fromAyah - 1;
        int globalToAyah = surahStartGlobalAyah.get(selectedSurahIndex) + toAyah - 1;

        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
        intent.putExtra("SURAH_NUMBER", selectedSurahIndex + 1);
        intent.putExtra("FROM_AYAH", globalFromAyah);
        intent.putExtra("TO_AYAH", globalToAyah);
        intent.putExtra("REPEAT_LIMIT", npRepeat.getValue());
        intent.putExtra("CONTINUE_MODE", false);
        startActivity(intent);
    }

    private void continueLastProgress() {
        DBHelper.Progress progress = dbHelper.getLastProgress();

        if (progress == null) {
            Toast.makeText(this,
                    isArabic ? "لا يوجد تقدم محفوظ" : "No saved progress found",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
        intent.putExtra("SURAH_NUMBER", progress.surahNumber);
        intent.putExtra("FROM_AYAH", progress.globalAyahNumber);
        intent.putExtra("TO_AYAH", 6236);
        intent.putExtra("REPEAT_LIMIT", progress.repeatLimit);
        intent.putExtra("CONTINUE_MODE", true);
        startActivity(intent);
    }

    private String convertArabicDigitsToEnglish(String input) {
        return input
                .replace("٠", "0")
                .replace("١", "1")
                .replace("٢", "2")
                .replace("٣", "3")
                .replace("٤", "4")
                .replace("٥", "5")
                .replace("٦", "6")
                .replace("٧", "7")
                .replace("٨", "8")
                .replace("٩", "9");
    }

    private String toArabicNumber(int number) {
        String englishNumber = String.valueOf(number);
        String[] arabicDigits = {"٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩"};
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < englishNumber.length(); i++) {
            int digit = Character.getNumericValue(englishNumber.charAt(i));
            result.append(arabicDigits[digit]);
        }

        return result.toString();
    }
}