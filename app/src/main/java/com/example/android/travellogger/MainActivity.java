package com.example.android.travellogger;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.android.travellogger.provider.TravelContract;


public class MainActivity extends ActionBarActivity {

    private String m_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if(id == R.id.action_add_new_journal) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Journal Title:");
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    /*Intent intent = new Intent(MainActivity.this, DisplayPostsActivity.class);
                    intent.putExtra("journal name", m_Text);
                    startActivity(intent);*/
                    ContentValues values = new ContentValues();
                    values.put(TravelContract.JournalEntry.COLUMN_NAME, m_Text);
                    getContentResolver().insert(TravelContract.JournalEntry.CONTENT_URI, values);
                    getContentResolver().notifyChange(TravelContract.JournalEntry.CONTENT_URI, null);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }

            return super.onOptionsItemSelected(item);
    }

}
