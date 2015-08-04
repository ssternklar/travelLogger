package com.example.android.travellogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class PostsFragment extends Fragment {
    private ArrayAdapter<String> mPostsAdapter;


    public PostsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_main, container, false);
        String[] data = {
                "Post 1 Title",
                "Post 2 Title",
                "Post 3 Title",
                "Post 4 Title",
                "Post 5 Title",
                "Post 6 Title",
                "Post 7 Title"
        };
        List<String> titlesList = new ArrayList<String>(Arrays.asList(data));

        mPostsAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_post,
                R.id.list_item_post_textview,
                titlesList);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_post);
        listView.setAdapter(mPostsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                boolean mTwoPane = MainActivity.ismTwoPane();
                if (!mTwoPane) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    String data = (String) parent.getItemAtPosition(position);
                    intent.putExtra("title", data);
                    startActivity(intent);
                } else {
                    DetailActivityFragment detail = new DetailActivityFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", (String) parent.getItemAtPosition(position));
                    detail.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.detail_container, detail)
                            .commit();
                }
            }
        });
        return rootView;
    }
}
