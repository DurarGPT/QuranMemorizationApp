package com.example.quranmemorizationapp;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
//Imports Bundle, which Android uses to pass saved state data into onCreate().
import android.widget.Button;
//for our back button
import android.widget.TextView;
//bcs we have alot of text labels

import androidx.appcompat.app.AppCompatActivity;
// This is the About page class.
public class AboutActivity extends AppCompatActivity {

    // Back button from the XML.
    Button btnBack;

    // TextViews from the XML that show the About page text.
    TextView tvAboutTitle,
            tvAboutDescription,
            tvFeaturesTitle,
            tvFeaturesList,
            tvWhoWeAreTitle,
            tvTeamNames,
            tvWhyTitle,
            tvWhyDescription;

    // This stores/reads app settings like Arabic or English language.
    SharedPreferences sharedPreferences;

    // This method runs when the About page opens.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Runs the original Android setup for the screen.
        super.onCreate(savedInstanceState);

        // Connects this Java file to activity_about.xml.
        setContentView(R.layout.activity_about);

        // Finds the back button from XML by its ID.
        btnBack = findViewById(R.id.btnBack);

        // Finds each TextView from the XML by its ID.
        tvAboutTitle = findViewById(R.id.tvAboutTitle);
        tvAboutDescription = findViewById(R.id.tvAboutDescription);
        tvFeaturesTitle = findViewById(R.id.tvFeaturesTitle);
        tvFeaturesList = findViewById(R.id.tvFeaturesList);

        tvWhoWeAreTitle = findViewById(R.id.tvWhoWeAreTitle);
        tvTeamNames = findViewById(R.id.tvTeamNames);
        tvWhyTitle = findViewById(R.id.tvWhyTitle);
        tvWhyDescription = findViewById(R.id.tvWhyDescription);

        // Opens the saved settings file called QiraatiSettings.
        sharedPreferences =
                getSharedPreferences(
                        "QiraatiSettings",
                        MODE_PRIVATE
                );

        // When the user clicks Back, close this page and return to the previous page.
        btnBack.setOnClickListener(view -> finish());

        // Applies the correct language and font to the page.
        updateLanguage();
    }

    // This method changes the page text and font based on the selected language.
    private void updateLanguage() {

        // Reads if Arabic language is selected.
        // false means English is the default if no setting was saved.
        boolean isArabic =
                sharedPreferences.getBoolean(
                        "arabicLanguage",
                        false
                );

        // Loads the Arabic font from res/font.
        Typeface arabicFont =
                getResources().getFont(R.font.estedad_regular);

        // Loads the English font from res/font.
        Typeface englishFont =
                getResources().getFont(R.font.dynapuff_regular);

        // Chooses Arabic font if Arabic is selected, otherwise English font.
        Typeface selectedFont =
                isArabic ? arabicFont : englishFont;

        // Applies the selected font to the back button.
        btnBack.setTypeface(selectedFont);

        // Applies the selected font to all text on the page.
        tvAboutTitle.setTypeface(selectedFont);
        tvAboutDescription.setTypeface(selectedFont);
        tvFeaturesTitle.setTypeface(selectedFont);
        tvFeaturesList.setTypeface(selectedFont);
        tvWhoWeAreTitle.setTypeface(selectedFont);
        tvTeamNames.setTypeface(selectedFont);
        tvWhyTitle.setTypeface(selectedFont);
        tvWhyDescription.setTypeface(selectedFont);

        // If Arabic is selected, show Arabic text.
        if (isArabic) {

            btnBack.setText("رجوع");

            tvAboutTitle.setText("حول قراءتي");

            tvAboutDescription.setText(
                    "قراءتي هو تطبيق بسيط ومناسب للأطفال يساعدهم على حفظ القرآن بطريقة ممتعة وهادئة ومنظمة."
            );

            tvFeaturesTitle.setText("المميزات");

            tvFeaturesList.setText(
                    "• تكرار الآيات\n" +
                            "• التحكم بسهولة بالحفظ\n" +
                            "• الوضع الليلي\n" +
                            "• دعم العربية والإنجليزية\n" +
                            "• تصميم مناسب للأطفال"
            );

            tvWhoWeAreTitle.setText("من نحن");

            tvTeamNames.setText(
                    "دُرر • بلقيس • ريماس • روان"
            );

            tvWhyTitle.setText("لماذا صنعنا قراءتي؟");

            tvWhyDescription.setText(
                    "صممنا تطبيق قراءتي لمساعدة الأطفال على حفظ القرآن وتعلمه بطريقة سهلة وممتعة وتفاعلية."
            );

        } else {

            // If Arabic is not selected, show English text.

            btnBack.setText("Back");

            tvAboutTitle.setText("About Qiraati");

            tvAboutDescription.setText(
                    "Qiraati is a simple and child-friendly Quran memorization app designed to help children memorize Quran verses in a calming and enjoyable way."
            );

            tvFeaturesTitle.setText("Features");

            tvFeaturesList.setText(
                    "• Ayah repetition\n" +
                            "• Easy memorization controls\n" +
                            "• Dark mode support\n" +
                            "• Arabic and English support\n" +
                            "• Child-friendly design"
            );

            tvWhoWeAreTitle.setText("Who We Are");

            tvTeamNames.setText(
                    "Durar • Balqees • Rimas • Rawan"
            );

            tvWhyTitle.setText("Why We Made Qiraati");

            tvWhyDescription.setText(
                    "We created Qiraati to help children memorize and learn the Quran in a simple, enjoyable, and interactive way."
            );
        }
    }
}