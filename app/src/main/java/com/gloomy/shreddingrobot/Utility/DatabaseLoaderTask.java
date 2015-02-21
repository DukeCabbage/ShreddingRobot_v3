package com.gloomy.shreddingrobot.Utility;

import android.content.Context;
import android.os.AsyncTask;

import com.gloomy.shreddingrobot.Dao.DBTrack;
import com.gloomy.shreddingrobot.Dao.DBTrackDao;
import com.gloomy.shreddingrobot.Dao.DaoManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DatabaseLoaderTask extends AsyncTask<Integer, Void , ArrayList<DBTrack>> {

    private ArrayList<DBTrack> dataList;
    private final WeakReference<HistoryArrayAdapter> mHistoAdapterReference;
    private final Context _context;

    public DatabaseLoaderTask(Context context, HistoryArrayAdapter adapter) {
        mHistoAdapterReference = new WeakReference<>(adapter);
        _context = context;
        dataList = new ArrayList<>();
    }

    @Override
    protected ArrayList<DBTrack> doInBackground(Integer... params) {
        DaoManager daoManager = DaoManager.getInstance(_context);
        DBTrackDao trackDao = daoManager.getDBTrackDao(DaoManager.TYPE_READ);
        dataList = (ArrayList<DBTrack>) trackDao.queryBuilder().orderDesc(DBTrackDao.Properties.Date).list();
        return dataList;
    }

    @Override
    protected void onPostExecute(ArrayList<DBTrack> dataList) {
        if (mHistoAdapterReference != null && dataList != null) {
            final HistoryArrayAdapter mHistoAdapter = mHistoAdapterReference.get();
            if (mHistoAdapter != null) {
                mHistoAdapter.updateData(dataList);
            }
        }
    }

}
