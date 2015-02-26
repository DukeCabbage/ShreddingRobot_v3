package com.gloomy.shreddingrobot.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.gloomy.shreddingrobot.Dao.DBTrack;
import com.gloomy.shreddingrobot.Dao.DBTrackDao;
import com.gloomy.shreddingrobot.Dao.DaoManager;
import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Widget.ExpandingListView;
import com.gloomy.shreddingrobot.Widget.Logger;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryArrayAdapter extends BaseAdapter {
    private static final String TAG = "HistoryArrayAdapter";
    private static final Long ENTRY_ANIM_DELAY = 130l;

    private Context _context;
    private SharedPreferences _pref;

    private LayoutInflater inflater;
    private ArrayList<DBTrack> objects;
    private ExpandingListView listView;

    LongSparseArray<Boolean> mStaMap = new LongSparseArray<>();
    SparseBooleanArray mIniMap = new SparseBooleanArray();
    SparseBooleanArray mFirstTrackMap = new SparseBooleanArray();

    private boolean firstWave = true;
    private int entryAnimQueue = 0;

    SimpleDateFormat dateF = new SimpleDateFormat("MMM d", Locale.US);
    DecimalFormat sig3 = new DecimalFormat("@@@");
    DecimalFormat sig2 = new DecimalFormat("@@");
    DecimalFormat dff = new DecimalFormat("0.00");

    public HistoryArrayAdapter(Context context, SharedPreferences pref) {
        objects = new ArrayList<>();
        _context = context;
        _pref = pref;
        inflater = LayoutInflater.from (context);
    }

    public void setListView(ExpandingListView listView) {
        this.listView = listView;
    }

    public void updateData(final ArrayList<DBTrack> tracks) {
//        Logger.e(objects.size() + " " +tracks.size());
        listView.post(new Runnable() {
            @Override
            public void run() {
                if (getCount()!=tracks.size()){
                    objects = tracks;
                    for (int i = 0; i < objects.size(); ++i) {
                        mStaMap.put(objects.get(i).getId(), false);
                        mIniMap.put(i, false);
                        mFirstTrackMap.put(i, false);
                    }
                    notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public int getCount() {
        int count = 0;
        if (objects != null) {
            count = objects.size();
        }
        return count;
    }

    @Override
    public DBTrack getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return objects.get(i).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
//        Logger.e(TAG, "getView()" + position + " " + (convertView == null) + " " + mIniMap.get(position));
        final HistoryViewHolder viewHolder;

        DBTrack mTrack = objects.get(position);
        long trackId = mTrack.getId();
        Date mDate = mTrack.getDate();
        String mLocation = mTrack.getLocationName();

        int duration = mTrack.getDuration();
        int distance = mTrack.getDistance();
        double maxSpeed = mTrack.getMaxSpeed();
        double avgSpeed = mTrack.getAvgSpeed();
        double maxAirTime = mTrack.getMaxAirTime();
        double jumpDist = mTrack.getMaxJumpDistance();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_history, parent, false);
            viewHolder = new HistoryViewHolder();
            viewHolder.findView(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (HistoryViewHolder) convertView.getTag();
            if (!mStaMap.get(trackId)) {
                viewHolder.expandingLayout.setVisibility(View.GONE);
                convertView.setBackgroundColor(_context.getResources().getColor(R.color.history_background));
                viewHolder.arrowUpDown.setText(Constants.ICON_ARROW_DOWN);
            } else {
                viewHolder.expandingLayout.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(_context.getResources().getColor(R.color.history_open_BG));
                viewHolder.arrowUpDown.setText(Constants.ICON_ARROW_UP);

            }

        }


        // Entry animation
        // flag firstWave determines whether this view is recycled or not
        // not recycled views are rendered simultaneously, so delay time is set according to position
        // recycled views are rendered sequentially, so delay time is based on how many animations are underway
        if (!mIniMap.get(position)){
            Animation entryAnim = AnimationUtils.loadAnimation(_context, R.anim.histo_item_enter_left);

            if (firstWave){
                entryAnim.setStartOffset(ENTRY_ANIM_DELAY * position);
            }else{
                entryAnim.setStartOffset(ENTRY_ANIM_DELAY*(entryAnimQueue+1));
            }
//            Logger.e(TAG, entryAnim.getStartOffset() + " " + position);

            entryAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mIniMap.put(position, true);
                        entryAnimQueue++;
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (entryAnimQueue>0) {
                        entryAnimQueue--;
                    }
                    if(entryAnimQueue<=1){
                        firstWave = false;
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            convertView.startAnimation(entryAnim);
        }


        if (position==0){
            mFirstTrackMap.put(position, true);
        }else{
            Date previousDate = getItem(position-1).getDate();
            if (previousDate.getMonth()!=mDate.getMonth() ||
                    previousDate.getDate()!=mDate.getDate()){
                mFirstTrackMap.put(position, true);
            }else{
                mFirstTrackMap.put(position, false);
            }
        }

        if (mFirstTrackMap.get(position)){
            viewHolder.categoryLayout.setVisibility(View.VISIBLE);
            viewHolder.trackDate.setText(dateF.format(mDate));
            viewHolder.trackLocation.setText(mLocation);
            if (mLocation.length()>8){
                viewHolder.trackLocation.setTextSize(14);
            }
        }else{
            viewHolder.categoryLayout.setVisibility(View.GONE);
        }

        if (distance<1000){
            viewHolder.trackDistance.setText(distance + " m");
        } else {
            viewHolder.trackDistance.setText(sig3.format(distance/Constants.UC_KM_TO_M) + " km");
        }

        int hours = duration / Constants.UC_SECONDS_IN_HOUR;
        int minutes = duration % Constants.UC_SECONDS_IN_HOUR / Constants.UC_SECONDS_IN_MINUTE;
        int seconds = duration % Constants.UC_SECONDS_IN_HOUR % Constants.UC_SECONDS_IN_MINUTE;
        String hoursStr, minutesStr, secondsStr;

        hoursStr = hours<10 ? "0"+hours : ""+hours;
        minutesStr = minutes<10 ? "0"+minutes : ""+minutes;
        secondsStr = seconds<10 ? "0"+seconds : ""+seconds;

        viewHolder.trackDuration.setText(hoursStr+":"+minutesStr+":"+secondsStr);

        int speedUnit = _pref.getInt(Constants.SP_SPEED_UNIT, 0);
        // Displaying max speed
        double displayMaxSpeed = maxSpeed;
        switch (speedUnit) {
            case 0:
                displayMaxSpeed *= Constants.UC_MS_TO_KMH;
                viewHolder.maxSpeedUnit.setText("km/h");
                break;
            case 2:
                displayMaxSpeed *= Constants.UC_MS_TO_MIH;
                viewHolder.maxSpeedUnit.setText("mi/h");
                break;
            default:
                viewHolder.maxSpeedUnit.setText("m/s");
                break;
        }

        if (displayMaxSpeed > 10.0) {
            viewHolder.maxSpeed.setText(sig3.format(displayMaxSpeed));
        } else {
            viewHolder.maxSpeed.setText(sig2.format(displayMaxSpeed));
        }

        // Displaying max speed
        double displayAvgSpeed = avgSpeed;
        switch (speedUnit) {
            case 0:
                displayAvgSpeed *= Constants.UC_MS_TO_KMH;
                viewHolder.avgSpeedUnit.setText("km/h");
                break;
            case 2:
                displayAvgSpeed *= Constants.UC_MS_TO_MIH;
                viewHolder.avgSpeedUnit.setText("mi/h");
                break;
            default:
                viewHolder.avgSpeedUnit.setText("m/s");
                break;
        }

        if (displayAvgSpeed > 10.0) {
            viewHolder.avgSpeed.setText(sig3.format(displayAvgSpeed));
        } else {
            viewHolder.avgSpeed.setText(sig2.format(displayAvgSpeed));
        }

        // Displaying air time
        int timeUnit = _pref.getInt(Constants.SP_TIME_UNIT, 0);
        switch (timeUnit){
            default:
                viewHolder.airTime.setText(dff.format(maxAirTime));
                viewHolder.airTimeUnit.setText("s");
                break;
            case 1:
                int time = Math.round((float)(maxAirTime*Constants.UC_MILLISECONDS_IN_SECOND));
                viewHolder.airTime.setText(time+"");
                viewHolder.airTimeUnit.setText("ms");
        }

        // Displaying max jump distance
        double displayJumpDist = jumpDist;
        switch (speedUnit) {
            case 2:
                displayJumpDist *= Constants.UC_M_TO_FT;
                viewHolder.jumpDistUnit.setText("ft");
                break;
            default:
                viewHolder.jumpDistUnit.setText("m");
                break;
        }

        viewHolder.jumpDist.setText(sig2.format(displayJumpDist));

        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(_context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                builder.setTitle(_context.getString(R.string.delete_track_title));
                builder.setMessage(_context.getString(R.string.delete_track_message))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteTrack(position);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return convertView;
    }

    // Calls when data in "objects" changes
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public boolean getStat(int pos) {
        return mStaMap.get(getTrackId(pos));
    }

    public long getTrackId(int pos) {
        return objects.get(pos).getId();
    }

    public void setStat(int pos, boolean newStat) {
        mStaMap.put(objects.get(pos).getId(), newStat);
    }

    public boolean isFirstTrackOfTheDay(int position){
        return mFirstTrackMap.get(position);
    }

    public void printAllStat() {
        for (int i = 0; i < mStaMap.size(); i++) {
            long id = mStaMap.keyAt(i);
            Log.e(TAG, id+": "+mIniMap.get(i));
        }
    }

    public void deleteTrack(int position) {
        if (position<0||position>objects.size()) {
            return;
        }
        DaoManager daoManager = DaoManager.getInstance(_context);
        DBTrackDao trackDao = daoManager.getDBTrackDao(DaoManager.TYPE_READ);
        trackDao.delete(objects.get(position));
        objects.remove(position);
        notifyDataSetChanged();
    }

    public void resetEntryAnim(){
        for (int i = 0; i < objects.size(); ++i) {
            mIniMap.put(i, false);
        }
    }
}