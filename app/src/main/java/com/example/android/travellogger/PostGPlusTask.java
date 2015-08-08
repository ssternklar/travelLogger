package com.example.android.travellogger;

import android.accounts.AccountManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sam on 8/8/2015.
 */
public class PostGPlusTask extends AsyncTask<String, Void, Void> {


    protected Void doInBackground(String... params)
    {

        if(params.length == 0)
        {
            return null;
        }
        HttpURLConnection urlConnection = null;
        String json = "{\n" +
                "  \"object\": {\n" +
                "    \"originalContent\": \"" + params[0] + "\",\n" +
                "  },\n" +
                "  \"access\": {\n" +
                "    \"items\": [{\n" +
                "        \"type\": \"domain\"\n" +
                "    }],\n" +
                "    \"domainRestricted\": true\n" +
                "  }\n" +
                "}";

        String format = "json";

        try {

            final String base = "https://www.googleapis.com/plusDomains/v1/people/";
            final String extension = "/activities";

            Uri built = Uri.parse(base).buildUpon().appendPath(params[1]).appendPath(extension).build();

            URL url = new URL(built.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "AIzaSyAO2sN_dEu_XTGBWVnhCRvNzAlRkKpHwNo");
            urlConnection.connect();

            OutputStream stream = urlConnection.getOutputStream();

            byte[] bytes = json.getBytes();

            stream.write(bytes);
            stream.flush();
            stream.close();

        }
        catch (Exception e)
        {
            Log.e("PostGPlusTask", e.getMessage(), e);
        }
        finally {
            if(urlConnection != null)
            {
                urlConnection.disconnect();
            }


        }



        return null;
    }

    protected void onPostExecute(Void result)
    {
    }
}
