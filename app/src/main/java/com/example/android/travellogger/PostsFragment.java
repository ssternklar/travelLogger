package com.example.android.travellogger;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.android.travellogger.provider.PostsAdapter;
import com.example.android.travellogger.provider.TravelContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class PostsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private PostsAdapter mPostsAdapter;

    private ListView listView;
    private int mPosition = ListView.INVALID_POSITION;
    private String m_Text;
    private String mUri;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_display_posts, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public static String[] DB_ROWS = {
        TravelContract.EntryEntry.COLUMN_ID,
        TravelContract.EntryEntry.COLUMN_TITLE,
        TravelContract.EntryEntry.COLUMN_TEXT,
        TravelContract.EntryEntry.COLUMN_DATE,
        TravelContract.EntryEntry.COLUMN_JOURNAL_ID,
        TravelContract.EntryEntry.COLUMN_IMAGE_CONTENT_PATH
    };

    public static int COL_TITLE = 1;
    public static int COL_DATE = 3;

    private Uri uri;

    public PostsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(MainActivity.ismTwoPane()) {
            getActivity().setTitle("Posts");
        }
        setHasOptionsMenu(true);
        //return inflater.inflate(R.layout.fragment_main, container, false);
        /*String[] data = {
                "Post 1 Title",
                "Post 2 Title",
                "Post 3 Title",
                "Post 4 Title",
                "Post 5 Title",
                "Post 6 Title",
                "Post 7 Title"
        };
        List<String> titlesList = new ArrayList<String>(Arrays.asList(data));*/

        mPostsAdapter = new PostsAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_post);
        listView.setAdapter(mPostsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                mPosition = position;
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String data = null;
                if (cursor != null) {
                    data = cursor.getString(COL_TITLE);
                }

                //If we got here without a valid id, I want this to throw an exception
                //It should never happen!!!
                String idString = Long.toString(cursor.getLong(0));
                Uri newUri = uri.buildUpon().appendPath(idString).build();

                boolean mTwoPane = MainActivity.ismTwoPane();

                if (!mTwoPane) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("title", data);
                    intent.putExtra("uri", newUri.toString());
                    startActivity(intent);
                } else {
                    DetailActivityFragment detail = new DetailActivityFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", data);
                    bundle.putString("uri", newUri.toString());
                    detail.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.detail_container, detail)
                            .commit();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            final String[] options = {
                    "Delete",
                    "Change Name",
            };

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final long _id = id;
                new AlertDialog.Builder(getActivity()).setTitle("Extra Options")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which)
                                {
                                    case 0:
                                        getActivity().getContentResolver().delete(uri, TravelContract.EntryEntry.COLUMN_ID + " = " + _id, null);
                                        break;
                                    case 1:
                                        final EditText input = new EditText(getActivity());
                                        new AlertDialog.Builder(getActivity()).setTitle("Rename to:")
                                                .setView(input)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ContentValues values = new ContentValues();
                                                        values.put(TravelContract.EntryEntry.COLUMN_TITLE, input.getText().toString());
                                                        getActivity().getContentResolver().update(uri, values, TravelContract.EntryEntry.COLUMN_ID + " = " + _id, null);
                                                    }
                                                }).setNegativeButton("Cancel", null)
                                                .show();

                                        break;
                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
                return true;
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey("mPosition"))
        {
            mPosition = savedInstanceState.getInt("mPosition");
        }

        return rootView;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add_new_post) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("New Post Title:");
            final EditText input = new EditText(getActivity());
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(TravelContract.EntryEntry.COLUMN_TITLE, m_Text);
                    Uri uri;
                    if (!MainActivity.ismTwoPane()) {
                        uri = Uri.parse(getActivity().getIntent().getStringExtra("uri"));
                    } else {
                        uri = Uri.parse(mUri);
                    }

                    Uri newUri = getActivity().getContentResolver().insert(uri, values);
                    getActivity().getContentResolver().notifyChange(uri, null);

                    Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                    intent.putExtra("post name", m_Text);
                    intent.putExtra("uri", newUri.toString());
                    startActivity(intent);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if(savedInstanceState != null)
        {
            mUri = savedInstanceState.getString("uri");
        }

        StartLoader();

        super.onActivityCreated(savedInstanceState);
    }

    public void StartLoader()
    {

        String uriString;
        if(mUri == null) {
            uriString = getActivity().getIntent().getStringExtra("uri");
            if (uriString == null) {
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    Log.d("TEST", "test2");
                    uriString = bundle.getString("uri", null);
                    mUri = uriString;
                }
                if (uriString == null) {
                    throw new UnsupportedOperationException("Activity was started with no uri in the intent extras!");
                }
            } else {
                mUri = uriString;
            }
        }
        else
        {
            uriString = mUri;
        }

        uri = Uri.parse(uriString);

        if(uri.getPathSegments().get(0) == null || !uri.getPathSegments().get(0).equals(TravelContract.PATH_ENTRY))
        {
            throw new UnsupportedOperationException("Uri string from intent failed to parse!");
        }

        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle b)
    {
        String sortOrder = TravelContract.JournalEntry.COLUMN_ID + " DESC";
        String[] selectionArgs = { uri.getPathSegments().get(1) };
        return new CursorLoader(getActivity(),
                uri,
                DB_ROWS,
                TravelContract.EntryEntry.COLUMN_JOURNAL_ID + " = ?",
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPostsAdapter.swapCursor(data);
        if (listView != null && mPosition != ListView.INVALID_POSITION) {
            listView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPostsAdapter.swapCursor(null);
    }


    public void onSaveInstanceState(Bundle outBundle)
    {
        outBundle.putString("uri", mUri);
        if(mPosition != ListView.INVALID_POSITION) {
            outBundle.putInt("mPosition", mPosition);
        }
        super.onSaveInstanceState(outBundle);
    }
}