package com.example.quranmemorizationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "quran.db";

    // Version 3 is used because the progress table was changed and global_ayah_number was added.
    private static final int DB_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Local cache for ayah text and Mushaf page details.
        db.execSQL(
                "CREATE TABLE verses (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "global_ayah_number INTEGER UNIQUE," +
                        "surah_number INTEGER," +
                        "ayah_number INTEGER," +
                        "page_number INTEGER," +
                        "text_ar TEXT)"
        );

        // Stores only one row: the last memorization position saved by the user.
        db.execSQL(
                "CREATE TABLE user_progress (" +
                        "id INTEGER PRIMARY KEY CHECK (id = 1)," +
                        "surah_number INTEGER," +
                        "ayah_number INTEGER," +
                        "global_ayah_number INTEGER," +
                        "repeat_limit INTEGER," +
                        "saved_at TEXT DEFAULT CURRENT_TIMESTAMP)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For this student project, recreating the tables is the clearest and safest option.
        db.execSQL("DROP TABLE IF EXISTS verses");
        db.execSQL("DROP TABLE IF EXISTS user_progress");
        onCreate(db);
    }

    public static class Progress {
        public final int surahNumber;
        public final int ayahNumber;
        public final int globalAyahNumber;
        public final int repeatLimit;

        public Progress(int surahNumber, int ayahNumber, int globalAyahNumber, int repeatLimit) {
            this.surahNumber = surahNumber;
            this.ayahNumber = ayahNumber;
            this.globalAyahNumber = globalAyahNumber;
            this.repeatLimit = repeatLimit;
        }
    }

    public void insertOrUpdateVerse(int globalAyahNumber, int surahNumber, int ayahNumber, int pageNumber, String textAr) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("global_ayah_number", globalAyahNumber);
        values.put("surah_number", surahNumber);
        values.put("ayah_number", ayahNumber);
        values.put("page_number", pageNumber);
        values.put("text_ar", textAr);

        db.insertWithOnConflict("verses", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void insertVerse(Verse verse) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("surah_number", verse.surahNumber);
        values.put("ayah_number", verse.ayahNumber);
        values.put("page_number", verse.pageNumber);
        values.put("text_ar", verse.textAr);

        db.insert("verses", null, values);
        db.close();
    }

    public ArrayList<Verse> getAllVerses() {
        ArrayList<Verse> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT surah_number, ayah_number, page_number, text_ar FROM verses ORDER BY global_ayah_number ASC",
                null
        );

        if (c.moveToFirst()) {
            do {
                list.add(new Verse(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3)));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    public ArrayList<Verse> getVersesInRange(int startGlobalAyah, int endGlobalAyah) {
        ArrayList<Verse> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT surah_number, ayah_number, page_number, text_ar " +
                        "FROM verses WHERE global_ayah_number >= ? AND global_ayah_number <= ? " +
                        "ORDER BY global_ayah_number ASC",
                new String[]{String.valueOf(startGlobalAyah), String.valueOf(endGlobalAyah)}
        );

        if (c.moveToFirst()) {
            do {
                list.add(new Verse(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3)));
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return list;
    }

    public Verse getVerseByGlobalAyahNumber(int globalAyahNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT surah_number, ayah_number, page_number, text_ar " +
                        "FROM verses WHERE global_ayah_number = ? LIMIT 1",
                new String[]{String.valueOf(globalAyahNumber)}
        );

        Verse verse = null;

        if (c.moveToFirst()) {
            verse = new Verse(c.getInt(0), c.getInt(1), c.getInt(2), c.getString(3));
        }

        c.close();
        db.close();
        return verse;
    }

    public void saveProgress(int surahNumber, int ayahNumber, int globalAyahNumber, int repeatLimit) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("surah_number", surahNumber);
        values.put("ayah_number", ayahNumber);
        values.put("global_ayah_number", globalAyahNumber);
        values.put("repeat_limit", repeatLimit);
        values.put("saved_at", "CURRENT_TIMESTAMP");

        db.insertWithOnConflict("user_progress", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Progress getLastProgress() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT surah_number, ayah_number, global_ayah_number, repeat_limit " +
                        "FROM user_progress WHERE id = 1 LIMIT 1",
                null
        );

        Progress progress = null;

        if (c.moveToFirst()) {
            progress = new Progress(
                    c.getInt(0),
                    c.getInt(1),
                    c.getInt(2),
                    c.getInt(3)
            );
        }

        c.close();
        db.close();
        return progress;
    }

    public String getLastProgressText() {
        Progress progress = getLastProgress();

        if (progress == null) {
            return "No Progress";
        }

        return "Last Read: Surah " + progress.surahNumber + " Ayah " + progress.ayahNumber;
    }
}
