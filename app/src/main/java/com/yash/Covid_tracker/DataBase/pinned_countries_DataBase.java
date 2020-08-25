package com.yash.Covid_tracker.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class pinned_countries_DataBase extends SQLiteOpenHelper {

    public pinned_countries_DataBase(@Nullable Context context) {
        super(context, "countries", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SCORE_TABLE = "CREATE TABLE " +
                pinned_countries_contract.pinned_countries_entry.TABLE_NAME + " (" +
                pinned_countries_contract.pinned_countries_entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                pinned_countries_contract.pinned_countries_entry.COLUMN_COUNTRY_NAME + " TEXT NOT NULL, " +
                pinned_countries_contract.pinned_countries_entry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                pinned_countries_contract.pinned_countries_entry.COLUMN_SLUG + " TEXT NOT NULL, " +
                pinned_countries_contract.pinned_countries_entry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_SCORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + pinned_countries_contract.pinned_countries_entry.TABLE_NAME);
        onCreate(db);
    }
}
