package com.example.android.travellogger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.travellogger.provider.TravelContentProvider;
import com.example.android.travellogger.provider.TravelContract.JournalEntry;
import com.example.android.travellogger.provider.TravelContract.EntryEntry;

/**
 * Created by Sam on 7/29/2015.
 */
public class ContentProviderTest extends AndroidTestCase {
    public void setUp() throws Exception
    {
        getContext().deleteDatabase("travel.db");
        super.setUp();
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


        //Get the Uri associated with a given Journal to use to insert entries into said journal
        values = new ContentValues();
        values.put(JournalEntry.COLUMN_NAME, "My Journal");
        Uri uri = resolver.insert(JournalEntry.CONTENT_URI, values);

        values = new ContentValues();
        values.put(EntryEntry.COLUMN_TITLE, "Testing Title");
        values.put(EntryEntry.COLUMN_TEXT, "This is the text of the thingy. It exists. Woo. Yay!");
        values.put(EntryEntry.COLUMN_DATE, 1);


        //INSERT
        resolver.insert(uri, values);
        //QUERY
        Cursor eCursor = resolver.query(uri, null, EntryEntry.COLUMN_DATE + " > 0", null, null);
        assertTrue("We have not found our data!", eCursor.moveToFirst());
        assertTrue("We have an incorrect number of columns!", eCursor.getColumnCount() == 6);
        //UPDATE
        values.put(EntryEntry.COLUMN_DATE, 5);
        int changed = resolver.update(uri, values, EntryEntry.COLUMN_DATE + " > 0", null);
        assertTrue("We failed to update the entry!", changed > 0);
        //DELETE
        resolver.delete(uri, EntryEntry.COLUMN_ID + " = 1", null);
        eCursor.requery();
        assertFalse("We failed to delete our data!", eCursor.moveToFirst());

        resolver.insert(uri, values);
        resolver.insert(uri, values);

        //SAFE DELETE TEST
        assertTrue("Failed to safely delete things!", TravelContentProvider.SafeDeleteJournal(resolver, "My Journal") >= 2);
        eCursor.close();
    }
}
