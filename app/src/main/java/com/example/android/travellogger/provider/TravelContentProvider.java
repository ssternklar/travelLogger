package com.example.android.travellogger.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.android.travellogger.provider.TravelContract.EntryEntry;
import com.example.android.travellogger.provider.TravelContract.JournalEntry;

/**
 * Created by Sam on 7/28/2015.
 */
public class TravelContentProvider extends ContentProvider {
    private static final UriMatcher matcher = buildUriMatcher();
    private TravelDbHelper dbHelper;

    static final int JOURNAL = 10;
    static final int ENTRY = 20;
    static final int JOURNAL_AND_ENTRY = 15;

    private static final SQLiteQueryBuilder queryBuilder;

    static {
        queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(
                EntryEntry.TABLE_NAME + " INNER JOIN " +
                        JournalEntry.TABLE_NAME +
                        " ON " + EntryEntry.TABLE_NAME +
                        "." + EntryEntry.COLUMN_JOURNAL_ID +
                        " = " + JournalEntry.TABLE_NAME +
                        "." + JournalEntry.COLUMN_ID);
    }

    private static final String journalSelection = JournalEntry.TABLE_NAME + "." + JournalEntry.COLUMN_NAME + " = ? ";

    private Cursor getEntriesByJournal(Uri uri, String[] projection, String sortOrder) {
        String journalName = uri.getPathSegments().get(0);

        return queryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                journalSelection,
                new String[]{journalName},
                null,
                null,
                sortOrder);
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String auth = TravelContract.AUTHORITY;

        matcher.addURI(auth, TravelContract.PATH_JOURNAL, JOURNAL);
        matcher.addURI(auth, TravelContract.PATH_ENTRY + "/#", ENTRY);
        matcher.addURI(auth, TravelContract.PATH_ENTRY + "/*", JOURNAL_AND_ENTRY);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TravelDbHelper(getContext());
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
            case JOURNAL_AND_ENTRY:
                return JournalEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI tested: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor ret;
        switch (matcher.match(uri))
        {
            case JOURNAL:
                ret = dbHelper.getReadableDatabase().query(
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
                ret = dbHelper.getReadableDatabase().query(
                        EntryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                        );
                break;
            case JOURNAL_AND_ENTRY:
                ret = getEntriesByJournal(uri, projection, selection);
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
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri ret = null;
        long dbID;
        switch (matcher.match(uri))
        {
            case JOURNAL:
                dbID = db.insert(JournalEntry.TABLE_NAME, null, values);
                if (dbID > 0)
                    ret = TravelContract.BASE_URI.buildUpon().appendPath(values.getAsString("name")).build();
                break;
            case ENTRY:
                int value = Integer.parseInt(uri.getPathSegments().get(1));
                values.put("journal_id", value);
                dbID = db.insert(EntryEntry.TABLE_NAME, null, values);
                if(dbID > 0)
                {
                    ret = TravelContract.BASE_URI.buildUpon().appendPath(Integer.toString(value)).appendPath(values.getAsString("name")).build();
                }
                break;
            case JOURNAL_AND_ENTRY:
                Cursor cursor = db.query(JournalEntry.TABLE_NAME, new String[]{JournalEntry.COLUMN_ID}, JournalEntry.COLUMN_NAME + " = ?", new String[]{uri.getPathSegments().get(1)}, null, null, null);
                cursor.moveToFirst();
                values.put("journal_id", cursor.getLong(0));
                dbID = db.insert(EntryEntry.TABLE_NAME, null, values);
                if(dbID > 0)
                {
                    String journalName = uri.getPathSegments().get(1);
                    ret = TravelContract.BASE_URI.buildUpon().appendPath(journalName).appendPath(values.getAsString("name")).build();
                }
                cursor.close();
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI detected: " + uri);
        }

        return ret;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rows = 0;

        if(selection == null) selection = "1";

        switch (matcher.match(uri))
        {
            case JOURNAL:
                rows = db.delete(JournalEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ENTRY:
                rows = db.delete(EntryEntry.TABLE_NAME, selection, selectionArgs);
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
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rows = 0;

        switch (matcher.match(uri))
        {
            case JOURNAL:
                rows = db.update(JournalEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ENTRY:
                values.put("journal_id", Integer.parseInt(uri.getPathSegments().get(1)));
                rows = db.update(EntryEntry.TABLE_NAME, values, selection, selectionArgs);
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

}
