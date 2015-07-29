package com.example.android.travellogger.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.travellogger.provider.TravelContract.EntryEntry;
import com.example.android.travellogger.provider.TravelContract.JournalEntry;
/**
 * Created by Sam on 7/22/2015.
 */
public class TravelDbHelper extends SQLiteOpenHelper {

    //The version of our database. Do not change unless the database schema changes
    private static final int DB_VER = 1;

    static final String DB_NAME = "travel.db";

    public TravelDbHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VER);
    }

    public void onCreate(SQLiteDatabase db)
    {
        final String SQL_CREATE_JOURNAL_TABLE =
                "CREATE TABLE " + JournalEntry.TABLE_NAME + " (" +
                        JournalEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                        JournalEntry.COLUMN_NAME + " TEXT NOT NULL );";

        final String SQL_CREATE_ENTRY_TABLE =
                "CREATE TABLE " + EntryEntry.TABLE_NAME + " (" +
                        EntryEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        EntryEntry.COLUMN_JOURNAL_ID + " INTEGER NOT NULL, " +
                        EntryEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                        EntryEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        EntryEntry.COLUMN_TEXT + " TEXT NOT NULL, " +
                        EntryEntry.COLUMN_IMAGE_CONTENT_PATH + " TEXT, " +
                        //Set up our foreign key stuff in case we need it
                        " FOREIGN KEY (" + EntryEntry.COLUMN_JOURNAL_ID + ") REFERENCES " +
                        JournalEntry.TABLE_NAME + " (" + JournalEntry.COLUMN_ID + "));";

        db.execSQL(SQL_CREATE_JOURNAL_TABLE);
        db.execSQL(SQL_CREATE_ENTRY_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer)
    {
        //We don't have any new form of database schema or reason to update yet,
        //so for not it just drops both of our tables. This will change later,
        //so if we have a reason to upgrade, remove these lines later
        db.execSQL("DROP TABLE IF EXISTS " + JournalEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EntryEntry.TABLE_NAME);
        onCreate(db);
    }

}
