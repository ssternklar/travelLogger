package com.example.android.travellogger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.android.travellogger.provider.TravelDbHelper;

import com.example.android.travellogger.provider.TravelContract.EntryEntry;
import com.example.android.travellogger.provider.TravelContract.JournalEntry;

import java.util.Calendar;

/**
 * Created by Sam on 7/22/2015.
 */
public class DbExistTest extends AndroidTestCase {
    @Override
    public void setUp()
    {
        getContext().deleteDatabase("travel.db");
    }

    public void test() throws Throwable
    {
        SQLiteDatabase db = new TravelDbHelper(getContext()).getWritableDatabase();

        ContentValues journalTestValues = new ContentValues();
        journalTestValues.put(JournalEntry.COLUMN_NAME, "TESTING");
        db.insert(JournalEntry.TABLE_NAME, null, journalTestValues);
        Cursor query = db.rawQuery("SELECT * FROM " + JournalEntry.TABLE_NAME +
                " WHERE " + JournalEntry.COLUMN_NAME + "=\"TESTING\"", null);
        query.moveToFirst();
        assertTrue("Insert failed!", query.getPosition() != -1);

        int id = query.getInt(0);

        ContentValues entryTestValues = new ContentValues();
        entryTestValues.put(EntryEntry.COLUMN_JOURNAL_ID, id);
        entryTestValues.put(EntryEntry.COLUMN_DATE, Calendar.getInstance().getTimeInMillis() / 1000L);
        entryTestValues.put(EntryEntry.COLUMN_TITLE, "This is a title test");
        entryTestValues.put(EntryEntry.COLUMN_TEXT, "This is a text test");

        db.insert(EntryEntry.TABLE_NAME, null, entryTestValues);
        query = db.rawQuery("SELECT * FROM " + EntryEntry.TABLE_NAME +
                " WHERE " + EntryEntry.COLUMN_JOURNAL_ID + "=" + id, null);

        query.moveToFirst();
        assertTrue("Insert failed!", query.getPosition() != -1);

        entryTestValues = new ContentValues();
        entryTestValues.put(EntryEntry.COLUMN_JOURNAL_ID, id + 1);
        entryTestValues.put(EntryEntry.COLUMN_DATE, Calendar.getInstance().getTimeInMillis() / 1000L);
        entryTestValues.put(EntryEntry.COLUMN_TITLE, "This is a title test 2");
        entryTestValues.put(EntryEntry.COLUMN_TEXT, "This is a text test 2");

        db.insert(EntryEntry.TABLE_NAME, null, entryTestValues);

        entryTestValues = new ContentValues();
        entryTestValues.put(EntryEntry.COLUMN_JOURNAL_ID, id);
        entryTestValues.put(EntryEntry.COLUMN_DATE, Calendar.getInstance().getTimeInMillis() / 1000L);
        entryTestValues.put(EntryEntry.COLUMN_TITLE, "This is a title test 3");
        entryTestValues.put(EntryEntry.COLUMN_TEXT, "This is a text test 3");

        db.insert(EntryEntry.TABLE_NAME, null, entryTestValues);

        query = db.rawQuery("SELECT * FROM " + EntryEntry.TABLE_NAME +
                " WHERE " + EntryEntry.COLUMN_JOURNAL_ID + "=" + id, null);
        query.moveToFirst();
        assertTrue("We are getting the wrong number of things!", query.getCount() == 2);

        query = db.rawQuery("SELECT * FROM " + EntryEntry.TABLE_NAME +
                " WHERE " + EntryEntry.COLUMN_JOURNAL_ID + "=" + (id + 1), null);
        query.moveToFirst();
        assertTrue("We are getting bad data somehow!",
                query.getString(query.getColumnIndex(EntryEntry.COLUMN_TITLE))
                        .equals("This is a title test 2"));
    }
}
