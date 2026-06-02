package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    Button btnPlay, btnBack;
    NumberPicker npRepeat;

    TextView tvStatus, tvSecondTitle, tvSecondSubtitle, tvRepeatLabel, tvSurahLabel;

    EditText etFromAyah, etToAyah;
    AutoCompleteTextView actvSurah;

    SharedPreferences sharedPreferences;
    boolean isArabic = false;

    int selectedSurahIndex = 0;
    int fromAyah = 1;
    int toAyah = 1;

    String[] surahEnglish = {
            "1. Al-Fatiha", "2. Al-Baqarah", "3. Aal-Imran", "4. An-Nisa", "5. Al-Ma'idah",
            "6. Al-An'am", "7. Al-A'raf", "8. Al-Anfal", "9. At-Tawbah", "10. Yunus",
            "11. Hud", "12. Yusuf", "13. Ar-Ra'd", "14. Ibrahim", "15. Al-Hijr",
            "16. An-Nahl", "17. Al-Isra", "18. Al-Kahf", "19. Maryam", "20. Taha",
            "21. Al-Anbiya", "22. Al-Hajj", "23. Al-Mu'minun", "24. An-Nur", "25. Al-Furqan",
            "26. Ash-Shu'ara", "27. An-Naml", "28. Al-Qasas", "29. Al-Ankabut", "30. Ar-Rum",
            "31. Luqman", "32. As-Sajdah", "33. Al-Ahzab", "34. Saba", "35. Fatir",
            "36. Ya-Sin", "37. As-Saffat", "38. Sad", "39. Az-Zumar", "40. Ghafir",
            "41. Fussilat", "42. Ash-Shura", "43. Az-Zukhruf", "44. Ad-Dukhan", "45. Al-Jathiyah",
            "46. Al-Ahqaf", "47. Muhammad", "48. Al-Fath", "49. Al-Hujurat", "50. Qaf",
            "51. Adh-Dhariyat", "52. At-Tur", "53. An-Najm", "54. Al-Qamar", "55. Ar-Rahman",
            "56. Al-Waqi'ah", "57. Al-Hadid", "58. Al-Mujadilah", "59. Al-Hashr", "60. Al-Mumtahanah",
            "61. As-Saff", "62. Al-Jumu'ah", "63. Al-Munafiqun", "64. At-Taghabun", "65. At-Talaq",
            "66. At-Tahrim", "67. Al-Mulk", "68. Al-Qalam", "69. Al-Haqqah", "70. Al-Ma'arij",
            "71. Nuh", "72. Al-Jinn", "73. Al-Muzzammil", "74. Al-Muddaththir", "75. Al-Qiyamah",
            "76. Al-Insan", "77. Al-Mursalat", "78. An-Naba", "79. An-Nazi'at", "80. Abasa",
            "81. At-Takwir", "82. Al-Infitar", "83. Al-Mutaffifin", "84. Al-Inshiqaq", "85. Al-Buruj",
            "86. At-Tariq", "87. Al-A'la", "88. Al-Ghashiyah", "89. Al-Fajr", "90. Al-Balad",
            "91. Ash-Shams", "92. Al-Layl", "93. Ad-Duha", "94. Ash-Sharh", "95. At-Tin",
            "96. Al-Alaq", "97. Al-Qadr", "98. Al-Bayyinah", "99. Az-Zalzalah", "100. Al-Adiyat",
            "101. Al-Qari'ah", "102. At-Takathur", "103. Al-Asr", "104. Al-Humazah", "105. Al-Fil",
            "106. Quraysh", "107. Al-Ma'un", "108. Al-Kawthar", "109. Al-Kafirun", "110. An-Nasr",
            "111. Al-Masad", "112. Al-Ikhlas", "113. Al-Falaq", "114. An-Nas"
    };

    String[] surahArabic = {
            "١. الفاتحة", "٢. البقرة", "٣. آل عمران", "٤. النساء", "٥. المائدة",
            "٦. الأنعام", "٧. الأعراف", "٨. الأنفال", "٩. التوبة", "١٠. يونس",
            "١١. هود", "١٢. يوسف", "١٣. الرعد", "١٤. إبراهيم", "١٥. الحجر",
            "١٦. النحل", "١٧. الإسراء", "١٨. الكهف", "١٩. مريم", "٢٠. طه",
            "٢١. الأنبياء", "٢٢. الحج", "٢٣. المؤمنون", "٢٤. النور", "٢٥. الفرقان",
            "٢٦. الشعراء", "٢٧. النمل", "٢٨. القصص", "٢٩. العنكبوت", "٣٠. الروم",
            "٣١. لقمان", "٣٢. السجدة", "٣٣. الأحزاب", "٣٤. سبأ", "٣٥. فاطر",
            "٣٦. يس", "٣٧. الصافات", "٣٨. ص", "٣٩. الزمر", "٤٠. غافر",
            "٤١. فصلت", "٤٢. الشورى", "٤٣. الزخرف", "٤٤. الدخان", "٤٥. الجاثية",
            "٤٦. الأحقاف", "٤٧. محمد", "٤٨. الفتح", "٤٩. الحجرات", "٥٠. ق",
            "٥١. الذاريات", "٥٢. الطور", "٥٣. النجم", "٥٤. القمر", "٥٥. الرحمن",
            "٥٦. الواقعة", "٥٧. الحديد", "٥٨. المجادلة", "٥٩. الحشر", "٦٠. الممتحنة",
            "٦١. الصف", "٦٢. الجمعة", "٦٣. المنافقون", "٦٤. التغابن", "٦٥. الطلاق",
            "٦٦. التحريم", "٦٧. الملك", "٦٨. القلم", "٦٩. الحاقة", "٧٠. المعارج",
            "٧١. نوح", "٧٢. الجن", "٧٣. المزمل", "٧٤. المدثر", "٧٥. القيامة",
            "٧٦. الإنسان", "٧٧. المرسلات", "٧٨. النبأ", "٧٩. النازعات", "٨٠. عبس",
            "٨١. التكوير", "٨٢. الانفطار", "٨٣. المطففين", "٨٤. الانشقاق", "٨٥. البروج",
            "٨٦. الطارق", "٨٧. الأعلى", "٨٨. الغاشية", "٨٩. الفجر", "٩٠. البلد",
            "٩١. الشمس", "٩٢. الليل", "٩٣. الضحى", "٩٤. الشرح", "٩٥. التين",
            "٩٦. العلق", "٩٧. القدر", "٩٨. البينة", "٩٩. الزلزلة", "١٠٠. العاديات",
            "١٠١. القارعة", "١٠٢. التكاثر", "١٠٣. العصر", "١٠٤. الهمزة", "١٠٥. الفيل",
            "١٠٦. قريش", "١٠٧. الماعون", "١٠٨. الكوثر", "١٠٩. الكافرون", "١١٠. النصر",
            "١١١. المسد", "١١٢. الإخلاص", "١١٣. الفلق", "١١٤. الناس"
    };

    int[] surahAyahCounts = {
            7,286,200,176,120,165,206,75,129,109,123,111,43,52,99,128,111,110,98,135,
            112,78,118,64,77,227,93,88,69,60,34,30,73,54,45,83,182,88,75,85,
            54,53,89,59,37,35,38,29,18,45,60,49,62,55,78,96,29,22,24,13,
            14,11,11,18,12,12,30,52,52,44,28,28,20,56,40,31,50,40,46,42,
            29,19,36,25,22,17,19,26,30,20,15,21,11,8,8,19,5,8,8,11,
            11,8,3,9,5,4,7,3,6,3,5,4,5,6
    };

    int[] surahStartGlobalAyah = {
            1,8,294,494,670,790,955,1161,1236,1365,1474,1597,1708,1751,1803,1902,2030,2141,2251,2349,
            2484,2596,2674,2792,2856,2933,3160,3253,3341,3410,3470,3504,3534,3607,3661,3706,3789,3971,4059,4134,
            4219,4273,4326,4415,4474,4511,4546,4584,4613,4631,4676,4736,4785,4847,4902,4980,5076,5105,5127,5151,
            5164,5178,5189,5200,5218,5230,5242,5272,5324,5376,5420,5448,5476,5496,5552,5592,5623,5673,5713,5759,
            5801,5830,5849,5885,5910,5932,5949,5968,5994,6024,6044,6059,6080,6091,6099,6107,6126,6131,6139,6147,
            6158,6169,6177,6180,6189,6194,6198,6205,6208,6214,6217,6222,6226,6231
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        btnPlay = findViewById(R.id.btnPlay);
        btnBack = findViewById(R.id.btnBack);

        npRepeat = findViewById(R.id.npRepeat);

        tvStatus = findViewById(R.id.tvStatus);
        tvSecondTitle = findViewById(R.id.tvSecondTitle);
        tvSecondSubtitle = findViewById(R.id.tvSecondSubtitle);
        tvRepeatLabel = findViewById(R.id.tvRepeatLabel);
        tvSurahLabel = findViewById(R.id.tvSurahLabel);

        etFromAyah = findViewById(R.id.etFromAyah);
        etToAyah = findViewById(R.id.etToAyah);
        actvSurah = findViewById(R.id.actvSurah);

        sharedPreferences =
                getSharedPreferences(
                        "QiraatiSettings",
                        MODE_PRIVATE
                );

        isArabic =
                sharedPreferences.getBoolean(
                        "arabicLanguage",
                        false
                );

        setupRepeatPicker();
        setupSurahSearch();
        updateLanguage();

        btnBack.setOnClickListener(v -> finish());
        btnPlay.setOnClickListener(v -> startPlaying());
    }

    private void setupSurahSearch() {
        String[] currentList = isArabic ? surahArabic : surahEnglish;

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        currentList
                );

        actvSurah.setAdapter(adapter);
        actvSurah.setText(currentList[0], false);

        actvSurah.setOnItemClickListener((parent, view, position, id) -> {
            selectedSurahIndex = position;
            etFromAyah.setText("");
            etToAyah.setText("");
            tvStatus.setText(
                    isArabic
                            ? "تم اختيار السورة"
                            : "Surah selected"
            );
        });
    }

    private void setupRepeatPicker() {
        npRepeat.setMinValue(1);
        npRepeat.setMaxValue(20);
        npRepeat.setValue(3);
    }

    private void updateLanguage() {

        Typeface arabicFont =
                getResources().getFont(R.font.estedad_regular);

        Typeface englishFont =
                getResources().getFont(R.font.dynapuff_regular);

        Typeface selectedFont =
                isArabic ? arabicFont : englishFont;

        btnBack.setTypeface(selectedFont);
        btnPlay.setTypeface(selectedFont);

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
        }
    }

    private boolean readInputs() {

        String selectedSurahText = actvSurah.getText().toString().trim();
        String[] currentList = isArabic ? surahArabic : surahEnglish;

        selectedSurahIndex = -1;

        for (int i = 0; i < currentList.length; i++) {
            if (currentList[i].equals(selectedSurahText)) {
                selectedSurahIndex = i;
                break;
            }
        }

        if (selectedSurahIndex == -1) {
            Toast.makeText(
                    this,
                    isArabic ? "اختاري سورة صحيحة من القائمة" : "Please choose a valid surah from the list",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        String fromText =
                etFromAyah.getText().toString().trim();

        String toText =
                etToAyah.getText().toString().trim();

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

        fromAyah = Integer.parseInt(fromText);
        toAyah = Integer.parseInt(toText);

        int maxAyah = surahAyahCounts[selectedSurahIndex];

        if (fromAyah < 1 || toAyah < 1 || fromAyah > maxAyah || toAyah > maxAyah) {
            Toast.makeText(
                    this,
                    isArabic
                            ? "رقم الآية خارج نطاق السورة"
                            : "Ayah number is outside this surah range",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

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

    private void startPlaying() {

        if (!readInputs()) return;

        int globalFromAyah =
                surahStartGlobalAyah[selectedSurahIndex] + fromAyah - 1;

        int globalToAyah =
                surahStartGlobalAyah[selectedSurahIndex] + toAyah - 1;

        android.content.Intent intent =
                new android.content.Intent(
                        SecondActivity.this,
                        ThirdActivity.class
                );

        intent.putExtra("SURAH_NUMBER", String.valueOf(selectedSurahIndex + 1));
        intent.putExtra("FROM_AYAH", String.valueOf(globalFromAyah));
        intent.putExtra("TO_AYAH", String.valueOf(globalToAyah));
        intent.putExtra("REPEAT_LIMIT", String.valueOf(npRepeat.getValue()));

        startActivity(intent);
    }
}