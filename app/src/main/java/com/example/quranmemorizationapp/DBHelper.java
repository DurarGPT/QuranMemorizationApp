package com.example.quranmemorizationapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "quran.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // VERSES TABLE
        db.execSQL(
                "CREATE TABLE verses (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "surah_number INTEGER," +
                        "ayah_number INTEGER," +
                        "page_number INTEGER," +
                        "text_ar TEXT)"
        );

        // USER PROGRESS TABLE
        db.execSQL(
                "CREATE TABLE user_progress (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "last_surah INTEGER," +
                        "last_ayah INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS verses");

        db.execSQL("DROP TABLE IF EXISTS user_progress");

        onCreate(db);
    }

    // INSERT VERSE
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

    // GET ALL VERSES
    public ArrayList<Verse> getAllVerses() {

        ArrayList<Verse> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM verses",
                null
        );

        if (c.moveToFirst()) {

            do {

                Verse verse = new Verse(

                        c.getInt(1),
                        c.getInt(2),
                        c.getInt(3),

                        c.getString(4)

                );

                list.add(verse);

            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return list;
    }

    // GET VERSES IN RANGE
    public ArrayList<Verse> getVersesInRange(int startAyah,
                                             int endAyah) {

        ArrayList<Verse> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM verses WHERE ayah_number >= ? AND ayah_number <= ?",
                new String[]{
                        String.valueOf(startAyah),
                        String.valueOf(endAyah)
                }
        );

        if (c.moveToFirst()) {

            do {

                Verse verse = new Verse(

                        c.getInt(1),
                        c.getInt(2),
                        c.getInt(3),

                        c.getString(4)
                );

                list.add(verse);

            } while (c.moveToNext());
        }

        c.close();
        db.close();

        return list;
    }

    // GIRL 4: Get one verse from SQLite for the display screen.
    public Verse getVerseByAyahNumber(int ayahNumber) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT surah_number, ayah_number, page_number, text_ar " +
                        "FROM verses WHERE ayah_number = ? LIMIT 1",
                new String[]{String.valueOf(ayahNumber)}
        );

        Verse verse = null;

        if (c.moveToFirst()) {

            verse = new Verse(
                    c.getInt(0),
                    c.getInt(1),
                    c.getInt(2),
                    c.getString(3)
            );
        }

        c.close();
        db.close();

        return verse;
    }

    // SAVE USER PROGRESS
    public void saveProgress(int surah,
                             int ayah) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        db.execSQL("DELETE FROM user_progress");

        ContentValues values =
                new ContentValues();

        values.put("last_surah", surah);

        values.put("last_ayah", ayah);

        db.insert("user_progress",
                null,
                values);

        db.close();
    }

    // GET LAST PROGRESS
    public String getLastProgress() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM user_progress LIMIT 1",
                null
        );

        String result = "No Progress";

        if (c.moveToFirst()) {

            int surah = c.getInt(1);

            int ayah = c.getInt(2);

            result = "Last Read: Surah " +
                    surah +
                    " Ayah " +
                    ayah;
        }

        c.close();

        db.close();

        return result;
    }
}