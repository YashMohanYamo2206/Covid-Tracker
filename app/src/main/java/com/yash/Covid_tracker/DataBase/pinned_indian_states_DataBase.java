package com.yash.Covid_tracker.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class pinned_indian_states_DataBase extends SQLiteOpenHelper {

    public pinned_indian_states_DataBase(@Nullable Context context) {
        super(context, "states", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SCORE_TABLE = "CREATE TABLE " +
                pinned_indian_states_contract.pinned_indian_states_entry.TABLE_NAME + " (" +
                pinned_indian_states_contract.pinned_indian_states_entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_POSITION + " INTEGER NOT NULL, " +
                pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_STATE_NAME + " TEXT NOT NULL, " +
                pinned_indian_states_contract.pinned_indian_states_entry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_SCORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + pinned_indian_states_contract.pinned_indian_states_entry.TABLE_NAME);
        onCreate(db);
    }
}
