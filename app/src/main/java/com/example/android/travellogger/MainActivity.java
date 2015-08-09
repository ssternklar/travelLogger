package com.example.android.travellogger;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.android.travellogger.provider.TravelContract;


public class MainActivity extends ActionBarActivity {

    private String m_Text;
    private static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.detail_container, new DetailActivityFragment(), "DF")
//                        .commit();
//            }
            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

                @Override
                public void onBackStackChanged() {
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment);
                    if (f != null){
                        String fragClassName = f.getClass().getName();

                        if (fragClassName.equals(JournalsFragment.class.getName())){
                            setTitle ("Journals");
                            //set selected item position, etc
//                        } else if (fragClassName.equals(PostsFragment.class.getName())) {
//                            setTitle("Posts");
                        }
                    }

                }
            });
        } else {
            mTwoPane = false;
        }
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
            final Context context = this;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Journal Title:");
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(TravelContract.JournalEntry.COLUMN_NAME, m_Text);
                    Uri uri = getContentResolver().insert(TravelContract.JournalEntry.CONTENT_URI, values);
                    getContentResolver().notifyChange(TravelContract.JournalEntry.CONTENT_URI, null);

                    /*Intent intent = new Intent(MainActivity.this, DisplayPostsActivity.class);
                    intent.putExtra("journal name", m_Text);
                    intent.putExtra("uri", uri.toString());

                    startActivity(intent);*/
                }
            });
            builder.setNeutralButton("Lock", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(TravelContract.JournalEntry.COLUMN_NAME, m_Text);
                    Uri uri = getContentResolver().insert(TravelContract.JournalEntry.CONTENT_URI, values);

                    AddLockTask task = new AddLockTask();
                    task.Setup(context);
                    task.execute(Integer.parseInt(uri.getPathSegments().get(1)));
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
    public static boolean ismTwoPane() {
        return mTwoPane;
    }


}
