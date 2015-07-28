package com.example.android.travellogger.provider;

import android.content.ContentResolver;
import android.net.Uri;

import org.apache.http.auth.AUTH;

/**
 * Created by Sam on 7/22/2015.
 */
public class TravelContract {

    //The authority that this contract works under
    public static final String AUTHORITY = "com.example.android.travellogger.provider";

    //The base Uri for all of our content stuff
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    //The path to our journal and entry tables from the query
    public static final String PATH_JOURNAL = "/*";
    public static final String PATH_ENTRY = PATH_JOURNAL + "/*";

    //Forces dates to be at the beginning of the day so we can query based on it easier
    public static long normalizeDate(long date)
    {
        //While I much dislike the use of the magic number
        //this will force the dates to the correct time
        return date - (date % 86400);
    }

    //Gets the time of day from a UNIX-style timestamp
    public static long getTimeOfDay(long date)
    {
        //The magic number here is the same as above
        return date % 86400;
    }

    public static final class JournalEntry {

        //Content URI to get to a journal entry
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_JOURNAL).build();

        //Strings representing the mime types of the content
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_JOURNAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_JOURNAL;

        //The table name
        public static final String TABLE_NAME = "journal";
        //The ID of this journal
        public static final String COLUMN_ID = "_id";
        //The name of this journal
        public static final String COLUMN_NAME = "name";
    }

    //This sounds super weird
    public static final class EntryEntry{

        //Content URI to get to a journal entry
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(PATH_ENTRY).build();

        //Strings representing the mime types of the content
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ENTRY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_ENTRY;

        //The table name
        public static final String TABLE_NAME = "entry";
        //The entry id
        public static final String COLUMN_ID = "_id";
        //The journal id that this entry belongs to
        public static final String COLUMN_JOURNAL_ID = "journal_id";
        //The title of this entry
        public static final String COLUMN_TITLE = "title";
        //The text of this entry
        public static final String COLUMN_TEXT = "text";
        //The date of this entry
        public static final String COLUMN_DATE = "date";
        //An image associated with this entry, stored as URI string to the location
        public static final String COLUMN_IMAGE_CONTENT_PATH = "image_content_path";
        //The latitude that this event occurred at
        public static final String COLUMN_GEO_INTENT = "geo_intent";
    }
}
