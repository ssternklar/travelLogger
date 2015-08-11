package com.example.android.travellogger;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new DetailActivityFragment())
                    .commit();
        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_detail, menu);
//        return true;
//    }

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
//        if(id==R.id.Google) {
////            Intent intent = new Intent(DetailActivity.this, PlusActivity.class);
////            startActivity(intent);
//
//            Intent shareIntent = new PlusShare.Builder(this)
//                    .setType("text/plain")
//                    .setText("Welcome to the Google+ platform.")
//                    .setContentUrl(Uri.parse("https://developers.google.com/+/"))
//                    .getIntent();
//
//            startActivityForResult(shareIntent, 0);
//        }

        /*if(id == R.id.Google)
        {

        AccountManager manager = AccountManager.get(this);
        Account[] list = manager.getAccounts();
        final ArrayList<String> accounts = new ArrayList<String>();
        final String messageString = "test";
        for(Account account : list)
        {
            if(account.type.equalsIgnoreCase("com.google"))
            {
                accounts.add(account.name);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Choose account:")
                .setItems((String[])accounts.toArray(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new PostGPlusTask().execute(
                                messageString,
                                accounts.get(which)
                        );
                    }
                });

        }*/

        return super.onOptionsItemSelected(item);
    }

}
