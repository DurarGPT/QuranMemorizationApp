package com.example.quranmemorizationapp;
public class Verse {

    public int surahNumber;
    public int ayahNumber;
    public int pageNumber;

    public String textAr;
    public String audioUrl;
    public String imageUrl;

    public Verse(int surahNumber,
                 int ayahNumber,
                 int pageNumber,
                 String textAr,
                 String audioUrl,
                 String imageUrl) {

        this.surahNumber = surahNumber;
        this.ayahNumber = ayahNumber;
        this.pageNumber = pageNumber;

        this.textAr = textAr;
        this.audioUrl = audioUrl;
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Surah " + surahNumber +
                " Ayah " + ayahNumber +
                "\n" + textAr;
    }
}