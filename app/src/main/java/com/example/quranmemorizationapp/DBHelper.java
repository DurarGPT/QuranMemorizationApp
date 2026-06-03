package com.example.quranmemorizationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    // Database name used by the app.
    private static final String DB_NAME = "quran.db";

    /*
        Database version.

        IMPORTANT:
        We changed this from 2 to 3 because we updated the user_progress table.
        When Android sees a higher DB_VERSION, it will call onUpgrade().
    */
    private static final int DB_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ================= BALQEES PART =================
    // This class manages the SQLite database.

    @Override
    public void onCreate(SQLiteDatabase db) {

        /*
            VERSES TABLE

            This table stores Quran verse information locally.

            Columns:
            - id: unique row ID
            - surah_number: Surah number
            - ayah_number: Ayah number
            - page_number: Mushaf page number
            - text_ar: Arabic verse text
        */
        db.execSQL(
                "CREATE TABLE verses (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "surah_number INTEGER," +
                        "ayah_number INTEGER," +
                        "page_number INTEGER," +
                        "text_ar TEXT)"
        );

        /*
            USER PROGRESS TABLE

            This table stores the last memorization progress.

            We keep only ONE row in this table.
            Every time the user moves to a new ayah, the old progress is deleted
            and the new progress is inserted.

            Columns:
            - last_surah: last selected/current Surah number
            - last_ayah: last reached ayah number
            - repeat_limit: the repeat count selected by the user
            - current_repeat: optional current repeat number
            - page_number: current Mushaf page number if available
            - updated_at: the time when progress was saved
        */
        db.execSQL(
                "CREATE TABLE user_progress (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "last_surah INTEGER," +
                        "last_ayah INTEGER," +
                        "repeat_limit INTEGER," +
                        "current_repeat INTEGER," +
                        "page_number INTEGER," +
                        "updated_at TEXT)"
        );
    }

    // ========== BALQEES PART ==========
    // This method updates the database when the app version changes.
    // Since this is a university project, we safely delete old tables and recreate them.
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        // Delete old verses table if it exists.
        db.execSQL("DROP TABLE IF EXISTS verses");

        // Delete old progress table if it exists.
        db.execSQL("DROP TABLE IF EXISTS user_progress");

        // Recreate the database with the new table structure.
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

    // GET ONE VERSE FROM SQLITE FOR THE DISPLAY SCREEN
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

    /*
        SAVE USER PROGRESS - SIMPLE VERSION

        This keeps your old method working.
        If another Activity already calls saveProgress(surah, ayah),
        it will not break.

        It simply calls the full saveProgress method with default values.
    */
    public void saveProgress(int surah,
                             int ayah) {

        saveProgress(
                surah,
                ayah,
                1,
                1,
                0
        );
    }

    /*
        SAVE USER PROGRESS - FULL VERSION

        Use this method from ThirdActivity when you know:
        - current Surah
        - current Ayah
        - repeat limit
        - current repeat count
        - page number

        We delete the previous row first because we only need the latest progress.
    */
    public void saveProgress(int surah,
                             int ayah,
                             int repeatLimit,
                             int currentRepeat,
                             int pageNumber) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Keep only one saved progress row.
        db.execSQL("DELETE FROM user_progress");

        ContentValues values = new ContentValues();

        values.put("last_surah", surah);
        values.put("last_ayah", ayah);
        values.put("repeat_limit", repeatLimit);
        values.put("current_repeat", currentRepeat);
        values.put("page_number", pageNumber);
        values.put("updated_at", String.valueOf(System.currentTimeMillis()));

        db.insert("user_progress", null, values);

        db.close();
    }

    /*
        GET LAST PROGRESS AS CURSOR

        Use this in SecondActivity for the Continue Last Progress button.

        IMPORTANT:
        Close the Cursor in the Activity after reading it.
    */
    public Cursor getLastProgressCursor() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT last_surah, last_ayah, repeat_limit, current_repeat, page_number, updated_at " +
                        "FROM user_progress ORDER BY id DESC LIMIT 1",
                null
        );
    }

    /*
        CHECK IF PROGRESS EXISTS

        This helps SecondActivity decide whether to open ThirdActivity
        or show No saved progress found.
    */
    public boolean hasSavedProgress() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id FROM user_progress LIMIT 1",
                null
        );

        boolean exists = c.moveToFirst();

        c.close();
        db.close();

        return exists;
    }

    /*
        GET LAST PROGRESS AS TEXT

        This keeps your old method working.
        It is useful for testing/debugging, but for opening ThirdActivity,
        use getLastProgressCursor() instead.
    */
    public String getLastProgress() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT last_surah, last_ayah FROM user_progress LIMIT 1",
                null
        );

        String result = "No Progress";

        if (c.moveToFirst()) {

            int surah = c.getInt(0);
            int ayah = c.getInt(1);

            result = "Last Read: Surah " +
                    surah +
                    " Ayah " +
                    ayah;
        }

        c.close();
        db.close();

        return result;
    }

    /*
        CLEAR USER PROGRESS

        Optional helper method.
        Use it only if you want to reset saved progress.
    */
    public void clearProgress() {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM user_progress");

        db.close();
    }
}
