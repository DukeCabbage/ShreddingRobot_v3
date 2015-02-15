package com.gloomy.shreddingrobot.UIFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gloomy.shreddingrobot.Dao.DBTrack;
import com.gloomy.shreddingrobot.Dao.DBTrackDao;
import com.gloomy.shreddingrobot.Dao.DaoManager;
import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;
import com.gloomy.shreddingrobot.Utility.HistoryArrayAdapter;
import com.gloomy.shreddingrobot.Widget.ExpandingListView;
import com.gloomy.shreddingrobot.Widget.Logger;

import java.util.ArrayList;

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

        DaoManager daoManager = DaoManager.getInstance(getActivity());
        DBTrackDao trackDao = daoManager.getDBTrackDao(DaoManager.TYPE_READ);
        ArrayList<DBTrack> mTrackList = (ArrayList<DBTrack>) trackDao.queryBuilder().orderDesc(DBTrackDao.Properties.Date).list();

        adapter = new HistoryArrayAdapter(parentActivity, mTrackList, sp);
        listView = (ExpandingListView) rootView.findViewById(R.id.listview);
        listView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
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
