package com.example.quranmemorizationapp;
public class Verse {

    public int surahNumber;
    public int ayahNumber;
    public int pageNumber;

    public String textAr;

    public Verse(int surahNumber,
                 int ayahNumber,
                 int pageNumber,
                 String textAr) {

        this.surahNumber = surahNumber;
        this.ayahNumber = ayahNumber;
        this.pageNumber = pageNumber;

        this.textAr = textAr;
    }

    @Override
    public String toString() {

        return "Surah " + surahNumber +
                " | Ayah " + ayahNumber +
                "\n" + textAr;
    }
}