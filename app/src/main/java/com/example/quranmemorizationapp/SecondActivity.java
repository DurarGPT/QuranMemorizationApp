package com.example.quranmemorizationapp;

// Intent is used to move from this activity to another activity.
import android.content.Intent;

// SharedPreferences is used to read the user's saved settings, such as Arabic/English language.
import android.content.SharedPreferences;

// Typeface is used to change the font depending on the selected language.
import android.graphics.Typeface;

// Bundle is used by Android to pass saved activity state into onCreate().
import android.os.Bundle;

// These imports are Android UI components used in this screen.
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

// AppCompatActivity is the base class for this Activity.
import androidx.appcompat.app.AppCompatActivity;

// Volley imports are used to call the AlQuran Cloud API.
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

// JSON imports are used to read the API response.
import org.json.JSONArray;
import org.json.JSONObject;

// ArrayList is used because the Surah data is now loaded dynamically from the API.
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    // Buttons on the screen.
    Button btnPlay, btnBack;

    // Menu button at the top of the screen.
    ImageButton btnMenu;

    // NumberPicker lets the user choose how many times the selected ayahs repeat.
    NumberPicker npRepeat;

    // TextViews used for labels, title, subtitle, and status messages.
    TextView tvStatus, tvSecondTitle, tvSecondSubtitle, tvRepeatLabel, tvSurahLabel;

    // EditTexts for the starting and ending ayah numbers.
    EditText etFromAyah, etToAyah;

    // AutoCompleteTextView is the searchable Surah picker/dropdown.
    AutoCompleteTextView actvSurah;

    // Used to read saved language settings from SettingsActivity.
    SharedPreferences sharedPreferences;

    // false means English mode, true means Arabic mode.
    boolean isArabic = false;

    // Stores which Surah the user selected. Index 0 means Surah 1, index 1 means Surah 2, etc.
    int selectedSurahIndex = 0;

    // These store the ayah range entered by the user.
    int fromAyah = 1;
    int toAyah = 1;

    // This list stores the English Surah names from the API.
    // Example: "1. Al-Faatiha"
    ArrayList<String> surahEnglish = new ArrayList<>();

    // This list stores the Arabic Surah names from the API.
    // Example: "١. الفاتحة"
    ArrayList<String> surahArabic = new ArrayList<>();

    // This list stores how many ayahs each Surah has.
    // Example: Al-Fatiha has 7 ayahs, so index 0 stores 7.
    ArrayList<Integer> surahAyahCounts = new ArrayList<>();

    // This list stores the global ayah starting number for each Surah.
    // This is needed because ThirdActivity uses global Quran ayah numbers.
    // Example: Surah 1 starts at global ayah 1, Surah 2 starts at global ayah 8.
    ArrayList<Integer> surahStartGlobalAyah = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Calls the parent Activity setup first. Android requires this.
        super.onCreate(savedInstanceState);

        // Connects this Java file to activity_second.xml.
        setContentView(R.layout.activity_second);

        // Connect the Java variables to the XML views using their IDs.
        btnPlay = findViewById(R.id.btnPlay);
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

        // Open the same settings file used in the rest of the app.
        sharedPreferences = getSharedPreferences("QiraatiSettings", MODE_PRIVATE);

        // Read the saved language choice.
        // If arabicLanguage is true, the screen becomes Arabic.
        // If arabicLanguage is false, the screen stays English.
        isArabic = sharedPreferences.getBoolean("arabicLanguage", false);

        // Set up the repeat picker from 1 to 20.
        setupRepeatPicker();

        // Apply the correct language texts and fonts before loading data.
        updateLanguage();

        // Set up the popup menu navigation.
        setupMenu();

        // Load Surah names and ayah counts from AlQuran Cloud API.
        // This replaces the old hardcoded Surah arrays.
        fetchSurahsFromApi();

        // Back button closes this screen and returns to the previous screen.
        btnBack.setOnClickListener(v -> finish());

        // Play button validates the inputs, then sends the user to ThirdActivity.
        btnPlay.setOnClickListener(v -> startPlaying());
    }

    private void setupMenu() {
        // When the menu icon is clicked, show a popup menu.
        btnMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, btnMenu);

            // Load the menu items from res/menu/popup_menu.xml.
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

            // Decide what happens when each menu item is clicked.
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

                // true means the click was handled successfully.
                return true;
            });

            // Actually display the popup menu on the screen.
            popupMenu.show();
        });
    }

    private void fetchSurahsFromApi() {
        // API endpoint that returns all 114 Surahs with names and ayah counts.
        String url = "https://api.alquran.cloud/v1/surah";

        // Tell the user that the app is loading the Surah list.
        tvStatus.setText(isArabic ? "جاري تحميل السور..." : "Loading Surahs...");

        // Create a Volley request queue. This manages the internet request.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a GET request to the API.
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // The API stores the Surah list inside the "data" array.
                        JSONArray data = response.getJSONArray("data");

                        // Clear old data before adding the new API data.
                        surahEnglish.clear();
                        surahArabic.clear();
                        surahAyahCounts.clear();
                        surahStartGlobalAyah.clear();

                        // This variable tracks the first global ayah number of each Surah.
                        int globalStartAyah = 1;

                        // Loop through all Surahs returned by the API.
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject surah = data.getJSONObject(i);

                            // Get Surah number, English name, Arabic name, and number of ayahs.
                            int number = surah.getInt("number");
                            String englishName = surah.getString("englishName");
                            String arabicName = surah.getString("name");
                            int ayahCount = surah.getInt("numberOfAyahs");

                            // Add English display name to the English list.
                            surahEnglish.add(number + ". " + englishName);

                            // Add Arabic display name to the Arabic list using Arabic digits.
                            surahArabic.add(toArabicNumber(number) + ". " + arabicName);

                            // Store the ayah count for validation later.
                            surahAyahCounts.add(ayahCount);

                            // Store the first global ayah number of this Surah.
                            surahStartGlobalAyah.add(globalStartAyah);

                            // Prepare the start number for the next Surah.
                            globalStartAyah += ayahCount;
                        }

                        // After the API data is ready, connect it to the searchable dropdown.
                        setupSurahSearch();

                        // Tell the user the app is ready.
                        tvStatus.setText(isArabic ? "جاهز للبدء" : "Ready to start");

                    } catch (Exception e) {
                        // This happens if the API response is received but cannot be read correctly.
                        Toast.makeText(
                                this,
                                isArabic ? "حدث خطأ في قراءة بيانات السور" : "Error reading Surah data",
                                Toast.LENGTH_SHORT
                        ).show();

                        tvStatus.setText(isArabic ? "خطأ في تحميل السور" : "Error loading Surahs");
                    }
                },
                error -> {
                    // This happens if there is no internet or the API request fails.
                    Toast.makeText(
                            this,
                            isArabic ? "تعذر تحميل السور من الإنترنت" : "Could not load Surahs from API",
                            Toast.LENGTH_SHORT
                    ).show();

                    tvStatus.setText(isArabic ? "تأكدي من الاتصال بالإنترنت" : "Check your internet connection");
                }
        );

        // Add the request to the queue so Volley actually runs it.
        queue.add(request);
    }

    private void setupSurahSearch() {
        // Choose which list should appear based on the selected language.
        // Arabic mode = Arabic Surah names.
        // English mode = English Surah names.
        ArrayList<String> currentList = isArabic ? surahArabic : surahEnglish;

        // Create an adapter that connects the Surah list to the AutoCompleteTextView.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                currentList
        );

        // Attach the adapter to the searchable Surah picker.
        actvSurah.setAdapter(adapter);

        // This makes the Surah picker start showing suggestions as soon as possible.
        // Important: the XML also supports Arabic letters, so Arabic typing can work.
        actvSurah.setThreshold(0);

        // When the user taps the Surah field, show the dropdown list.
        // This helps because the user can choose without typing.
        actvSurah.setOnClickListener(v -> actvSurah.showDropDown());

        // When the Surah field becomes active, also show the dropdown list.
        // This makes the picker easier to use in both Arabic and English.
        actvSurah.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                actvSurah.showDropDown();
            }
        });

        // If the API list is not empty, show the first Surah by default.
        if (!currentList.isEmpty()) {
            actvSurah.setText(currentList.get(0), false);
            selectedSurahIndex = 0;
        }

        // This runs when the user chooses a Surah from the dropdown.
        actvSurah.setOnItemClickListener((parent, view, position, id) -> {
            // Save the selected Surah index.
            selectedSurahIndex = position;

            // Clear old ayah input because each Surah has a different ayah count.
            etFromAyah.setText("");
            etToAyah.setText("");

            // Show a confirmation message.
            tvStatus.setText(isArabic ? "تم اختيار السورة" : "Surah selected");
        });
    }

    private void setupRepeatPicker() {
        // Minimum repeat count is 1.
        npRepeat.setMinValue(1);

        // Maximum repeat count is 20.
        npRepeat.setMaxValue(20);

        // Default repeat count is 3.
        npRepeat.setValue(3);
    }

    private void updateLanguage() {
        // Arabic font used when Arabic mode is selected.
        Typeface arabicFont = getResources().getFont(R.font.estedad_regular);

        // English font used when English mode is selected.
        Typeface englishFont = getResources().getFont(R.font.dynapuff_regular);

        // Pick the correct font depending on the saved language.
        Typeface selectedFont = isArabic ? arabicFont : englishFont;

        // Apply the selected font to buttons.
        btnBack.setTypeface(selectedFont);
        btnPlay.setTypeface(selectedFont);

        // Apply the selected font to labels and text.
        tvStatus.setTypeface(selectedFont);
        tvSecondTitle.setTypeface(selectedFont);
        tvSecondSubtitle.setTypeface(selectedFont);
        tvRepeatLabel.setTypeface(selectedFont);
        tvSurahLabel.setTypeface(selectedFont);

        // Apply the selected font to input fields.
        etFromAyah.setTypeface(selectedFont);
        etToAyah.setTypeface(selectedFont);
        actvSurah.setTypeface(selectedFont);

        // Change all text to Arabic if Arabic mode is selected.
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
            // Change all text to English if English mode is selected.
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
        // If the API did not load yet, the Surah lists will still be empty.
        if (surahEnglish.isEmpty() || surahArabic.isEmpty() || surahAyahCounts.isEmpty()) {
            Toast.makeText(
                    this,
                    isArabic ? "انتظري حتى يتم تحميل السور" : "Please wait until Surahs finish loading",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        // Get the Surah text currently written in the picker.
        String selectedSurahText = actvSurah.getText().toString().trim();

        // Choose the list that matches the current language.
        ArrayList<String> currentList = isArabic ? surahArabic : surahEnglish;

        // Reset the selected index before searching.
        selectedSurahIndex = -1;

        // Search for the selected Surah text inside the current language list.
        // This makes sure the user selected a real Surah from the API list.
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).equals(selectedSurahText)) {
                selectedSurahIndex = i;
                break;
            }
        }

        // If no matching Surah was found, show an error.
        if (selectedSurahIndex == -1) {
            Toast.makeText(
                    this,
                    isArabic ? "اختاري سورة صحيحة من القائمة" : "Please choose a valid Surah from the list",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        // Read the ayah range from the input fields.
        String fromText = etFromAyah.getText().toString().trim();
        String toText = etToAyah.getText().toString().trim();

        // Make sure the user typed both numbers.
        if (fromText.isEmpty() || toText.isEmpty()) {
            Toast.makeText(
                    this,
                    isArabic ? "أدخلي من الآية وإلى الآية" : "Please enter From Ayah and To Ayah",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        try {
            // The XML now allows Arabic digits, such as ١ ٢ ٣.
            // Java's Integer.parseInt() only understands English digits by default.
            // So before converting the input into integers, we convert Arabic digits to English digits.
            fromText = convertArabicDigitsToEnglish(fromText);
            toText = convertArabicDigitsToEnglish(toText);

            // Convert the cleaned text input into integer numbers.
            fromAyah = Integer.parseInt(fromText);
            toAyah = Integer.parseInt(toText);

        } catch (NumberFormatException e) {
            // This catches invalid input, such as letters instead of numbers.
            Toast.makeText(
                    this,
                    isArabic ? "أدخلي أرقامًا صحيحة للآيات" : "Please enter valid ayah numbers",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        // Get the maximum ayah number for the selected Surah from the API data.
        int maxAyah = surahAyahCounts.get(selectedSurahIndex);

        // Make sure the entered ayahs are inside the selected Surah range.
        if (fromAyah < 1 || toAyah < 1 || fromAyah > maxAyah || toAyah > maxAyah) {
            Toast.makeText(
                    this,
                    isArabic ? "رقم الآية خارج نطاق السورة" : "Ayah number is outside this Surah range",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        // Make sure the start ayah is not after the end ayah.
        if (fromAyah > toAyah) {
            Toast.makeText(
                    this,
                    isArabic ? "رقم البداية يجب أن يكون أصغر من النهاية" : "From Ayah must be smaller than To Ayah",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
        }

        // true means all inputs are valid.
        return true;
    }

    private void startPlaying() {
        // Validate all user inputs before moving to ThirdActivity.
        if (!readInputs()) return;

        // Convert the Surah-local ayah number into a global Quran ayah number.
        // ThirdActivity expects global ayah numbers, so this conversion is required.
        int globalFromAyah = surahStartGlobalAyah.get(selectedSurahIndex) + fromAyah - 1;
        int globalToAyah = surahStartGlobalAyah.get(selectedSurahIndex) + toAyah - 1;

        // Create an Intent to open ThirdActivity.
        Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);

        // Send selected Surah number to ThirdActivity.
        // selectedSurahIndex starts at 0, so we add 1 to get the real Surah number.
        intent.putExtra("SURAH_NUMBER", String.valueOf(selectedSurahIndex + 1));

        // Send the global ayah range to ThirdActivity.
        intent.putExtra("FROM_AYAH", String.valueOf(globalFromAyah));
        intent.putExtra("TO_AYAH", String.valueOf(globalToAyah));

        // Send the repeat count chosen by the user.
        intent.putExtra("REPEAT_LIMIT", String.valueOf(npRepeat.getValue()));

        // Open ThirdActivity.
        startActivity(intent);
    }

    private String convertArabicDigitsToEnglish(String input) {
        /*
            This method converts Arabic digits into English digits.

            Example:
            ١٢٣ becomes 123

            Why we need this:
            The user may type Arabic numbers in Arabic mode.
            However, Integer.parseInt() expects normal English digits.
            So we convert the digits first, then parse them safely.
        */

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
        // Convert the number to English digits first.
        String englishNumber = String.valueOf(number);

        // Arabic digit symbols from 0 to 9.
        String[] arabicDigits = {"٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩"};

        // StringBuilder is used to build the converted Arabic number efficiently.
        StringBuilder result = new StringBuilder();

        // Loop through every digit in the English number.
        for (int i = 0; i < englishNumber.length(); i++) {
            // Convert the current character into an integer digit.
            int digit = Character.getNumericValue(englishNumber.charAt(i));

            // Add the matching Arabic digit to the result.
            result.append(arabicDigits[digit]);
        }

        // Return the final Arabic number as text.
        return result.toString();
    }
}