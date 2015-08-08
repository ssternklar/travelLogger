package com.example.android.travellogger.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.travellogger.provider.TravelContract.EntryEntry;
import com.example.android.travellogger.provider.TravelContract.JournalEntry;

import java.util.Calendar;

/**
 * Created by Sam on 7/28/2015.
 */
public class TravelContentProvider extends ContentProvider {
    private static final UriMatcher matcher = buildUriMatcher();

    static final int JOURNAL = 10;
    static final int ENTRY = 20;
    static final int ENTRY_BY_JOURNAL = 15;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String auth = TravelContract.AUTHORITY;

        matcher.addURI(auth, TravelContract.PATH_JOURNAL, JOURNAL);
        matcher.addURI(auth, TravelContract.PATH_ENTRY + "/#", ENTRY);
        matcher.addURI(auth, TravelContract.PATH_ENTRY + "/*", ENTRY_BY_JOURNAL);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        return true;
    }


    @Override
    public String getType(Uri uri){
        final int match = matcher.match(uri);

        switch (match){
            case JOURNAL:
                return JournalEntry.CONTENT_TYPE;
            case ENTRY:
                return EntryEntry.CONTENT_ITEM_TYPE;
            case ENTRY_BY_JOURNAL:
                return JournalEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI tested: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        final SQLiteDatabase db = new TravelDbHelper(getContext()).getReadableDatabase();
        Cursor ret;
        switch (matcher.match(uri))
        {
            case JOURNAL:
                ret = db.query(
                        JournalEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ENTRY:
                ret = db.query(
                        EntryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ENTRY_BY_JOURNAL:
                if(selection == (null) || selection.equals(""))
                    selection = "1";
                Cursor cursor = db.query(JournalEntry.TABLE_NAME, new String[]{JournalEntry.COLUMN_ID}, JournalEntry.COLUMN_NAME + " = ?", new String[]{uri.getPathSegments().get(1)}, null, null, null);
                if(cursor.moveToFirst()) {
                    long journalID = cursor.getLong(0);
                    ret = db.query(EntryEntry.TABLE_NAME, projection, selection + " AND " + EntryEntry.COLUMN_JOURNAL_ID + " = " + journalID, selectionArgs, null, null, sortOrder);
                }
                else throw new UnsupportedOperationException("There is no journal with the name: " + uri.getPathSegments().get(1));
                cursor.close();
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI detected: " + uri);
        }

        ret.setNotificationUri(getContext().getContentResolver(), uri);
        return ret;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        final SQLiteDatabase db = new TravelDbHelper(getContext()).getWritableDatabase();
        Uri ret = null;
        long dbID;
        switch (matcher.match(uri))
        {
            case JOURNAL:
                dbID = db.insertOrThrow(JournalEntry.TABLE_NAME, null, values);
                if (dbID > 0)
                    ret = EntryEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(dbID)).build();
                else throw new android.database.SQLException("Failed to insert row into uri: " + uri);
                break;
            case ENTRY:
                int value = Integer.parseInt(uri.getPathSegments().get(1));
                values.put(EntryEntry.COLUMN_JOURNAL_ID, value);
                values.put(EntryEntry.COLUMN_DATE, Calendar.getInstance().getTime().getTime());
                dbID = db.insert(EntryEntry.TABLE_NAME, null, values);
                if(dbID > 0)
                {
                    ret = EntryEntry.CONTENT_URI.buildUpon().appendPath(Long.toString(dbID)).build();
                }
                else throw new android.database.SQLException("Failed to insert row into uri: " + uri);
                break;
            case ENTRY_BY_JOURNAL:
                Cursor cursor = db.query(JournalEntry.TABLE_NAME, new String[]{JournalEntry.COLUMN_ID}, JournalEntry.COLUMN_NAME + " = ?", new String[]{uri.getPathSegments().get(1)}, null, null, null);
                if(cursor.moveToFirst()) {
                    values.put(EntryEntry.COLUMN_JOURNAL_ID, cursor.getLong(0));
                    values.put(EntryEntry.COLUMN_DATE, Calendar.getInstance().getTime().getTime());
                    dbID = db.insert(EntryEntry.TABLE_NAME, null, values);
                    if (dbID > 0) {
                        String journalName = uri.getPathSegments().get(1);
                        ret = EntryEntry.CONTENT_URI.buildUpon().appendPath(journalName).build();
                    }
                    else throw new android.database.SQLException("Failed to insert row into uri: " + uri);
                    cursor.close();
                }
                else{
                    throw new UnsupportedOperationException("Entry attempted to access an uncreated journal: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI detected: " + uri);
        }
        return ret;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = new TravelDbHelper(getContext()).getWritableDatabase();

        int rows = 0;

        if(selection == null) selection = "1";

        switch (matcher.match(uri))
        {
            case JOURNAL:
                rows = db.delete(JournalEntry.TABLE_NAME, selection, selectionArgs);
                Log.w("TravelLogger", "Journal deleted without using TravelContentProvider.SafeDelete()! Possible journal entries left undeleted!");
                break;
            case ENTRY:
                rows = db.delete(EntryEntry.TABLE_NAME, selection + " AND " + EntryEntry.COLUMN_JOURNAL_ID + " = " + uri.getPathSegments().get(1), selectionArgs);
                break;
            case ENTRY_BY_JOURNAL:
                Cursor cursor = db.query(JournalEntry.TABLE_NAME, new String[]{JournalEntry.COLUMN_ID}, JournalEntry.COLUMN_NAME + " = ?", new String[]{uri.getPathSegments().get(1)}, null, null, null);
                if(cursor.moveToFirst()) {
                    long journalID = cursor.getLong(0);
                    rows = db.delete(EntryEntry.TABLE_NAME, selection + " AND " + EntryEntry.COLUMN_JOURNAL_ID + " = " + journalID, selectionArgs);
                }
                else throw new UnsupportedOperationException("No journal with name: " + uri.getPathSegments().get(1));
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri detected: " + uri);
        }

        if(rows > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = new TravelDbHelper(getContext()).getWritableDatabase();

        int rows = 0;

        if(selection == (null) || selection.equals(""))
        {
            selection = "1";
        }

        switch (matcher.match(uri))
        {
            case JOURNAL:
                rows = db.update(JournalEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ENTRY:
                values.put("journal_id", Integer.parseInt(uri.getPathSegments().get(1)));
                rows = db.update(EntryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ENTRY_BY_JOURNAL:
                Cursor c = db.query(JournalEntry.TABLE_NAME, new String[]{JournalEntry.COLUMN_ID}, JournalEntry.COLUMN_NAME + " = ?", new String[]{uri.getPathSegments().get(1)}, null,null,null);
                if(!c.moveToFirst())
                {
                    throw new UnsupportedOperationException("There is no journal with the following name: " + uri.getPathSegments().get(1));
                }
                long dbIndex = c.getLong(0);
                rows = db.update(EntryEntry.TABLE_NAME, values, selection + " AND " + EntryEntry.COLUMN_JOURNAL_ID + " = " + dbIndex, selectionArgs);
                c.close();
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri detected: " + uri);
        }

        if(rows > 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    public static int SafeDeleteJournal(ContentResolver resolver, String journalName)
    {
        int ret = 0;
        Cursor c = resolver.query(JournalEntry.CONTENT_URI, new String[]{JournalEntry.COLUMN_ID}, JournalEntry.COLUMN_NAME + " = ?", new String[]{journalName}, null);
        if(c.moveToFirst()) {
            int journalID = c.getInt(0);
            String journalIDString = Integer.toString(journalID);
            c.close();
            ret = resolver.delete(EntryEntry.CONTENT_URI.buildUpon().appendPath(journalIDString).build(), EntryEntry.COLUMN_JOURNAL_ID + " = ?", new String[]{journalIDString});
            ret += resolver.delete(JournalEntry.CONTENT_URI, JournalEntry.COLUMN_ID + " = ?", new String[]{journalIDString});
        }
        return ret;
    }

}
