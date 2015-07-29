package com.example.android.travellogger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.travellogger.provider.TravelContract.JournalEntry;

/**
 * Created by Sam on 7/29/2015.
 */
public class ContentProviderTest extends AndroidTestCase {
    public void setUp()
    {
        getContext().deleteDatabase("travel.db");
    }

    public void testJournalQueries() throws Throwable
    {
        ContentResolver resolver = getContext().getContentResolver();

        //insert and query
        ContentValues values = new ContentValues();
        values.put(JournalEntry.COLUMN_NAME, "My Journal");
        resolver.insert(JournalEntry.CONTENT_URI, values);
        Cursor cursor = resolver.query(JournalEntry.CONTENT_URI, new String[]{JournalEntry.COLUMN_ID, JournalEntry.COLUMN_NAME}, JournalEntry.COLUMN_NAME + " = ?", new String[]{"My Journal"}, null);
        cursor.moveToFirst();
        int cursorValue = cursor.getInt(0);
        assertTrue("Failed to get index of journal: " + cursorValue, cursorValue > 0);

        //update
        values.put(JournalEntry.COLUMN_NAME, "Newly Renamed");
        resolver.update(JournalEntry.CONTENT_URI, values, JournalEntry.COLUMN_ID + " = ?", new String[]{Integer.toString(cursorValue)});
        Cursor cursor2 = resolver.query(JournalEntry.CONTENT_URI, new String[]{JournalEntry.COLUMN_ID, JournalEntry.COLUMN_NAME}, JournalEntry.COLUMN_ID + " = ?", new String[]{Integer.toString(cursorValue)}, null);
        cursor2.moveToFirst();
        assertTrue("Failed to update name: " + cursor2.getString(1), cursor2.getString(1).equals("Newly Renamed"));
        cursor.requery();
        assertFalse("Old entry still exists!", cursor.moveToFirst());
        assertTrue("Old and new are not the same!", cursorValue == cursor2.getInt(0));

        //delete
        resolver.delete(JournalEntry.CONTENT_URI, JournalEntry.COLUMN_ID + " = ?", new String[]{Integer.toString(cursorValue)});
        cursor2.requery();
        assertFalse("Query did not delete correctly!", cursor2.moveToFirst());
        cursor.close();
        cursor2.close();
    }

    public void testEntryQueries() throws Throwable
    {

    }
}
