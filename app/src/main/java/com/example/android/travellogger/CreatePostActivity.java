package com.example.android.travellogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class CreatePostActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String postName = intent.getStringExtra("post name");
        setTitle(postName);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_save_post) {
            CreatePostActivityFragment fragment = (CreatePostActivityFragment)getSupportFragmentManager().findFragmentById(R.id.createpostfragment);
            fragment.saveStuff();
        }

        return super.onOptionsItemSelected(item);
    }

}
