package com.gloomy.shreddingrobot.UIFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;
import com.gloomy.shreddingrobot.Utility.DatabaseLoaderTask;
import com.gloomy.shreddingrobot.Utility.HistoryArrayAdapter;
import com.gloomy.shreddingrobot.Widget.ExpandingListView;
import com.gloomy.shreddingrobot.Widget.Logger;

public class HistoryFragment extends BaseFragment {
    private final static String TAG = "HistoryFragment";

    HistoryArrayAdapter adapter;
    ExpandingListView listView;

    boolean noEntryAnim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        noEntryAnim = true;

        adapter = new HistoryArrayAdapter(parentActivity, sp);
        listView = (ExpandingListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        new DatabaseLoaderTask(parentActivity, adapter).execute();

        if (listView.getCount()>0) {
            listView.setSelection(0);
        }

//        if (!noEntryAnim) {
//            listView.enableListView(false);
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    listView.enableListView(true);
//                    noEntryAnim = true;
//                }
//            }, 800);
//        }
        listView.enableListView(true);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
