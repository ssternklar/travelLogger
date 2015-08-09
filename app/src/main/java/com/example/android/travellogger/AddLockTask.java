package com.example.android.travellogger;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.travellogger.provider.TravelContract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sam on 8/9/2015.
 */
public class AddLockTask extends AsyncTask<Integer, Void, String>
    {
        int id;

        Context myContext;

        public void Setup(Context context) {
            myContext = context;
        }

        @Override
        protected String doInBackground(Integer... params) {

            if(myContext == null) {
                Log.e("AddLockTask", "Setup was not called on this Task!");
            }

            id = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String randomNumberString = null;

            try {
                URL url = new URL("https://www.random.org/integers/?num=1&min=1000&max=9999&col=1&base=10&format=plain&rnd=new");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + " ");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                randomNumberString = buffer.toString();
            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MainActivityFragment", "Error closing stream", e);
                    }
                }
            }
            if(randomNumberString != null) {
                randomNumberString = randomNumberString.trim();
                ContentValues values = new ContentValues();
                values.put(TravelContract.JournalEntry.COLUMN_LOCK, randomNumberString);
                myContext.getContentResolver().update(
                        TravelContract.JournalEntry.CONTENT_URI,
                        values,
                        TravelContract.JournalEntry.COLUMN_ID + " = ?",
                        new String[]{Integer.toString(id)});

                return randomNumberString;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String ret)
        {
            new AlertDialog.Builder(myContext).setTitle("Journal ID:")
                    .setMessage("The password for this journal is: " + ret)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

