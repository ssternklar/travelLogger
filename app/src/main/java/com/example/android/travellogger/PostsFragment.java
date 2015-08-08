package com.example.android.travellogger;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_display_posts, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public static String[] DB_ROWS = {
        TravelContract.EntryEntry.COLUMN_ID,
        TravelContract.EntryEntry.COLUMN_TITLE,
        TravelContract.EntryEntry.COLUMN_TEXT,
        TravelContract.EntryEntry.COLUMN_DATE,
        TravelContract.EntryEntry.COLUMN_JOURNAL_ID,
        TravelContract.EntryEntry.COLUMN_IMAGE_CONTENT_PATH,
        TravelContract.EntryEntry.COLUMN_ONLINE_ID
    };

    public static int COL_TITLE = 1;

    private Uri uri;

    public PostsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(MainActivity.ismTwoPane()) {
            setHasOptionsMenu(true);
        }
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
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        String uriString = getActivity().getIntent().getStringExtra("uri");
        if(uriString == null)
        {
            if(savedInstanceState != null) {
                uriString = savedInstanceState.getString("uri", null);
            }
            if(uriString == null)
            {
                throw new UnsupportedOperationException("Activity was started with no uri in the intent extras!");
            }
        }

        uri = Uri.parse(uriString);

        if(uri.getPathSegments().get(0) == null || !uri.getPathSegments().get(0).equals(TravelContract.PATH_ENTRY))
        {
            throw new UnsupportedOperationException("Uri string from intent failed to parse!");
        }

        getLoaderManager().initLoader(1, null, this);
        super.onActivityCreated(savedInstanceState);
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
}