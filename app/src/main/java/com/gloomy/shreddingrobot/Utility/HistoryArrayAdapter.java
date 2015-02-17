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

    private int entryAnimQueue = 0;

    SimpleDateFormat dateF = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
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

    public void updateData(ArrayList<DBTrack> tracks) {
//        Logger.e(objects.size() + " " +tracks.size());
        if (getCount()!=tracks.size()){
            objects = tracks;
            for (int i = 0; i < objects.size(); ++i) {
                mStaMap.put(objects.get(i).getId(), false);
                mIniMap.put(i, false);
            }
            notifyDataSetChanged();
        }
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
        int duration = mTrack.getDuration();
        int distance = mTrack.getDistance();
        double maxSpeed = mTrack.getMaxSpeed();
        double avgSpeed = mTrack.getAvgSpeed();
        double maxAirTime = mTrack.getMaxAirTime();
        double jumpDist = mTrack.getMaxJumpDistance();
        String mLocation = mTrack.getLocationName();
        Date mDate = mTrack.getDate();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_history, null);
            viewHolder = new HistoryViewHolder();
            viewHolder.findView(convertView);
            convertView.setTag(viewHolder);

//            // Entry Animations with delay between each view
            if (!mIniMap.get(position)) {
                Animation entryAnim = AnimationUtils.loadAnimation(_context, R.anim.histo_item_enter_left);
                if (entryAnimQueue == 1) {
                    entryAnim.setStartOffset(ENTRY_ANIM_DELAY);
                } else {
                    entryAnim.setStartOffset(ENTRY_ANIM_DELAY * position);
                }

                entryAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        entryAnimQueue++;
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (entryAnimQueue > 1) {
                            entryAnimQueue--;
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                mIniMap.put(position, true);
                convertView.startAnimation(entryAnim);
            }
        } else {
            viewHolder = (HistoryViewHolder) convertView.getTag();
            if (!mStaMap.get(trackId)) {
                viewHolder.expanding_layout.setVisibility(View.GONE);
                convertView.setBackgroundColor(_context.getResources().getColor(R.color.history_BG));
                viewHolder.arrowUpDown.setText(Constants.ICON_ARROW_DOWN);
            } else {
                viewHolder.expanding_layout.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(_context.getResources().getColor(R.color.history_open_BG));
                viewHolder.arrowUpDown.setText(Constants.ICON_ARROW_UP);
            }

            //Entry animation when new views appear from bottom
            if (!mIniMap.get(position)) {
                Animation entryAnim = AnimationUtils.loadAnimation(_context, R.anim.histo_item_enter_left);
                entryAnim.setStartOffset(ENTRY_ANIM_DELAY*(entryAnimQueue+1));
                entryAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        entryAnimQueue++;
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (entryAnimQueue > 0){
                            entryAnimQueue--;
                        }
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                convertView.startAnimation(entryAnim);
                mIniMap.put(position, true);
            }
        }

        if (distance<1000){
            viewHolder.trackDistance.setText(distance + " m");
        } else {
            viewHolder.trackDistance.setText(sig3.format(distance/1000.0) + " km");
        }

        int hours = duration/60;
        int minutes = duration%60;
        String hoursStr, minutesStr;

        minutesStr = minutes<10 ? "0"+minutes : ""+minutes;
        hoursStr = hours<10 ? "0"+hours : ""+hours;

        viewHolder.trackDuration.setText(hoursStr+":"+minutesStr);

        int veloUnit = _pref.getInt("VELOCITY_UNIT", 0);
        // Displaying max speed
        double displayMaxSpeed = maxSpeed;
        switch (veloUnit) {
            case 0:
                displayMaxSpeed *= 3.6;
                viewHolder.maxSpeedUnit.setText("km/h");
                break;
            case 2:
                displayMaxSpeed *= 2.2366;
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
        switch (veloUnit) {
            case 0:
                displayAvgSpeed *= 3.6;
                viewHolder.avgSpeedUnit.setText("km/h");
                break;
            case 2:
                displayAvgSpeed *= 2.2366;
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
        int timeUnit = _pref.getInt("TIME_UNIT", 0);
        switch (timeUnit){
            default:
                viewHolder.airTime.setText(dff.format(maxAirTime));
                viewHolder.airTimeUnit.setText("s");
                break;
            case 1:
                viewHolder.airTime.setText(Math.round((float)(maxAirTime*1000.0)));
                viewHolder.airTimeUnit.setText("ms");
        }

        // Displaying max jump distance
        double displayJumpDist = jumpDist;
        switch (veloUnit) {
            case 2:
                displayJumpDist *= 3.28084;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(_context);
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