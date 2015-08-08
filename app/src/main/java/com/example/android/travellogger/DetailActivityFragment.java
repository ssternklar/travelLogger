package com.example.android.travellogger;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.travellogger.provider.TravelContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView titleTextView = (TextView) rootView.findViewById(R.id.detail_text);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.detail_image_view);
        Intent intent = getActivity().getIntent();

        Uri uri;
        boolean mTwoPane = MainActivity.ismTwoPane();
        if (!mTwoPane) {
            uri = Uri.parse(intent.getStringExtra("uri"));
        } else {
            Bundle bundle=this.getArguments();
            uri = Uri.parse(bundle.getString("Uri"));
        }

        Cursor cursor = getActivity().getContentResolver().query(uri,
                PostsFragment.DB_ROWS,
                TravelContract.EntryEntry.COLUMN_ID +" = ?",
                new String[]{uri.getPathSegments().get(2)},
                null);
        if(cursor.moveToFirst()) {
            String postTitle = cursor.getString(1);

            String text = cursor.getString(2);

            String imageUriString = cursor.getString(5);


            if(imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    imageView.setImageBitmap(bmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            getActivity().setTitle(postTitle);

            titleTextView.setText(text);


        }
        else
        {
            titleTextView.setText("Something went horribly wrong, please contact the developers and let them know you got this!!");
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareIntent());
        } else {
            Log.d(getActivity().getClass().getSimpleName(), "Share Action Provider is null?");
        }
    }
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Test String!");
        return shareIntent;
    }
}
