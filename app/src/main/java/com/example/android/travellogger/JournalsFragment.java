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
public class JournalsFragment extends Fragment {
    private ArrayAdapter<String> mJournalsAdapter;

    public JournalsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_main, container, false);
        String[] data = {
                "Journal 1 Title",
                "Journal 2 Title",
                "Journal 3 Title",
                "Journal 4 Title",
                "Journal 5 Title",
                "Journal 6 Title",
                "Journal 7 Title"
        };
        List<String> titlesList = new ArrayList<String>(Arrays.asList(data));

        mJournalsAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_journal,
                R.id.list_item_journal_textview,
                titlesList);
        View rootView = inflater.inflate(R.layout.journals_fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_journal);
        listView.setAdapter(mJournalsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent(getActivity(), DisplayPostsActivity.class);
                startActivity(intent);
            }
        });

        return rootView;

    }
}
