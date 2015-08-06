package com.example.android.travellogger.provider;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.travellogger.PostsFragment;
import com.example.android.travellogger.R;

public class PostsAdapter extends CursorAdapter {

    public PostsAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_post, parent, false);
        TextView textView = (TextView)view.findViewById(R.id.list_item_post_textview);
        textView.setText(cursor.getString(PostsFragment.COL_TITLE));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView textView = (TextView)view.findViewById(R.id.list_item_post_textview);
        textView.setText(cursor.getString(PostsFragment.COL_TITLE));
    }

}
