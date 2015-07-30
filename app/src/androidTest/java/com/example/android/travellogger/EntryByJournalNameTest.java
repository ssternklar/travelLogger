package com.example.android.travellogger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.travellogger.provider.TravelContract;

import java.util.Arrays;

/**
 * Created by Sam on 7/30/2015.
 */
public class EntryByJournalNameTest extends AndroidTestCase {
    public void setUp() throws Exception
    {
        getContext().deleteDatabase("travel.db");
        super.setUp();
    }

    public void test() throws Throwable
    {
        ContentResolver resolver = getContext().getContentResolver();

        //insert and query
        ContentValues values = new ContentValues();
        values.put(TravelContract.JournalEntry.COLUMN_NAME, "My Journal");
        Uri oldUri = resolver.insert(TravelContract.JournalEntry.CONTENT_URI, values);

        values = new ContentValues();
        values.put(TravelContract.EntryEntry.COLUMN_TITLE, "Title");
        values.put(TravelContract.EntryEntry.COLUMN_TEXT, "This is the text");
        values.put(TravelContract.EntryEntry.COLUMN_DATE, 10);
        resolver.insert(oldUri, values);


        //Query
        Uri uri = TravelContract.EntryEntry.CONTENT_URI.buildUpon().appendPath("My Journal").build();
        Cursor cursor = resolver.query(uri, null, null, null, null);
        assertTrue("WE GOT NOTHING!", cursor.moveToFirst());

        //Delete
        resolver.delete(uri, TravelContract.EntryEntry.COLUMN_TITLE + " = Title", null);
        cursor.requery();
        assertFalse("WE DIDN'T REMOVE IT!", cursor.moveToFirst());
        //Insert
        resolver.insert(uri, values);
        cursor.requery();
        assertTrue("WE DIDN'T RE-INSERT IT!", cursor.moveToFirst());
        //Update
        values.put(TravelContract.EntryEntry.COLUMN_TITLE, "THINGY!");
        resolver.update(uri, values, null, null);
        cursor.requery();
        cursor.moveToFirst();
        assertTrue("WE DIDN'T UPDATE!", cursor.getString(cursor.getColumnIndex(TravelContract.EntryEntry.COLUMN_TITLE)).equals("THINGY!"));
    }
}
