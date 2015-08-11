package com.example.android.travellogger;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.travellogger.provider.TravelContract;

import java.io.IOException;


/**
 * A placeholder fragment containing a simple view.
 */
public class CreatePostActivityFragment extends Fragment {

    private static final String[] projection = {
            TravelContract.EntryEntry.COLUMN_TEXT,
            TravelContract.EntryEntry.COLUMN_IMAGE_CONTENT_PATH,
            TravelContract.EntryEntry.COLUMN_DATE
    };

    String uriString;
    private int PICK_IMAGE_REQUEST = 1;
    View rootView;
    String imageLoc = null;
    EditText textBox;
    ImageView imageView;
    String id = null;

    public CreatePostActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_create_post, container, false);
        TextView imgBtn = (TextView) rootView.findViewById(R.id.add_image_button);
        textBox = (EditText)rootView.findViewById(R.id.editText);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }

        });

        Intent intent = getActivity().getIntent();
        uriString = intent.getStringExtra("uri");
        if(uriString == null)
        {
            if(getArguments() != null) {
                uriString = getArguments().getString("uri", null);
            }
            if(uriString == null)
            {
                if(savedInstanceState != null)
                {
                    uriString = savedInstanceState.getString("uri", null);
                }
                if(uriString == null) {
                    throw new UnsupportedOperationException("Activity was started with no uri!");
                }
            }
        }

        Uri uri = Uri.parse(uriString);
        id = uri.getPathSegments().get(2);

        if(savedInstanceState != null)
        {
            if(savedInstanceState.containsKey("imageLoc"))
            {
                LoadImage(savedInstanceState.getString("imageLoc"));
            }
            if(savedInstanceState.containsKey("textBox"))
            {
                textBox.setText(savedInstanceState.getString("textBox"));
            }
        }
        else {

            Cursor cursor = getActivity().getContentResolver().query(
                    uri,
                    projection,
                    TravelContract.EntryEntry.COLUMN_ID + " = ?",
                    new String[]{id},
                    null
            );

            if (cursor.moveToFirst()) {
                String text = cursor.getString(0);
                textBox.setText(text);

                LoadImage(cursor.getString(1));
            }
        }
        return rootView;
    }

    void LoadImage(String path)
    {
        try {

            String imageContentPath = path;
            if(imageContentPath != null) {
                Uri imageUri = Uri.parse(imageContentPath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            }
        }
        catch (Exception e)
        {
            //Just throw it away and forget the operation if we can't get it.
            //It means that there is no image linked to the imageContentPath, or it's been deleted, or something like that
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            imageLoc = data.getDataString();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle)
    {
        outBundle.putString("uri", uriString);
        if(imageLoc != null)
        {
            outBundle.putString("imageLoc", imageLoc);
        }
        if(textBox.getText() != null)
        {
            outBundle.putString("textBox", textBox.getText().toString());
        }
    }

    @Override
    public void onStop()
    {
        ContentValues values = new ContentValues();
        values.put(TravelContract.EntryEntry.COLUMN_TEXT, textBox.getText().toString());
        if(imageLoc != null)
        {
            values.put(TravelContract.EntryEntry.COLUMN_IMAGE_CONTENT_PATH, imageLoc);
        }

        Intent intent = getActivity().getIntent();

        Uri uri = Uri.parse(intent.getStringExtra("uri"));

        getActivity().getContentResolver().update(uri,
                values,
                TravelContract.EntryEntry.COLUMN_ID + " = ?",
                new String[] {id});

        super.onStop();
    }
}
