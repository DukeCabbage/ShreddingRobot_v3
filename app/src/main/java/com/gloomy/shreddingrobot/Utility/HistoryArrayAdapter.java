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
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloomy.shreddingrobot.Dao.DBTrack;
import com.gloomy.shreddingrobot.Dao.DBTrackDao;
import com.gloomy.shreddingrobot.Dao.DaoManager;
import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Widget.ExpandingListView;
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryArrayAdapter extends ArrayAdapter<DBTrack> {
    private static final String TAG = "HistoryArrayAdapter";
    private static final Long ENTRY_ANIM_DELAY = 130l;

    private Context _context;
    private SharedPreferences _pref;

    private LayoutInflater inflater;
    private List<DBTrack> objects;
    private ExpandingListView listView;

    LongSparseArray<Boolean> mStaMap = new LongSparseArray<>();
    SparseBooleanArray mIniMap = new SparseBooleanArray();

    private int entryAnimQueue = 0;

    SimpleDateFormat dateF = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
    DecimalFormat sig3 = new DecimalFormat("@@@");
    DecimalFormat sig2 = new DecimalFormat("@@");
    private DecimalFormat dff = new DecimalFormat("0.00");

    public HistoryArrayAdapter(Context context, List<DBTrack> objects, SharedPreferences pref) {
        super(context, R.layout.history_item_layout, objects);
        this.objects = objects;
        this._context = context;
        this._pref = pref;

        for (int i = 0; i < objects.size(); ++i) {
            mStaMap.put(objects.get(i).getId(), false);
            mIniMap.put(i, false);
        }
        inflater = LayoutInflater.from (context);
    }

    public void setListView(ExpandingListView listView) {
        this.listView = listView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final ViewHolderItem viewHolder;

        DBTrack mTrack = objects.get(position);
        long trackId = mTrack.getId();
        double maxSpeed = mTrack.getMaxSpeed();
        double maxAirTime = mTrack.getMaxAirTime();
        String mLocation = mTrack.getLocationName();
        Date mDate = mTrack.getDate();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.history_item_layout, parent, false);
            viewHolder = new ViewHolderItem();

            viewHolder.maxAirTime = (TypefaceTextView) convertView.findViewById(R.id.trackMaxAirTime);
            viewHolder.maxSpeed = (TypefaceTextView) convertView.findViewById(R.id.trackMaxSpeed);
            viewHolder.maxSpeedUnit = (TypefaceTextView) convertView.findViewById(R.id.trackMaxSpeedUnit);

            viewHolder.trackLocation = (TypefaceTextView) convertView.findViewById(R.id.trackLocation);
            viewHolder.arrowUpDown = (TypefaceTextView) convertView.findViewById(R.id.arrow_up_down);
            viewHolder.trackDate = (TypefaceTextView) convertView.findViewById(R.id.trackDate);
            viewHolder.arrowUpDown.setText(Constants.ICON_ARROW_DOWN);

            viewHolder.expanding_layout = (RelativeLayout)convertView.findViewById(R.id.expanding_layout);
            viewHolder.shareBtn = (TextView) convertView.findViewById(R.id.share_btn);
            viewHolder.deleteBtn = (TextView) convertView.findViewById(R.id.delete_btn);
            viewHolder.shareBtn.setText(Constants.ICON_SHARE);
            viewHolder.deleteBtn.setText(Constants.ICON_DELETE);

            convertView.setTag(viewHolder);

//            // Entry Animations with delay between each view
            Animation entryAnim = AnimationUtils.loadAnimation(_context, R.anim.histo_item_enter_left);
            if (entryAnimQueue==1){
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
                    if (entryAnimQueue > 1){
                        entryAnimQueue--;
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            mIniMap.put(position, true);
            convertView.startAnimation(entryAnim);

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
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

        viewHolder.maxAirTime.setText(dff.format(maxAirTime));
        int veloUnit = _pref.getInt("VELOCITY_UNIT", 0);
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

        viewHolder.trackDate.setText(dateF.format(mDate));
        viewHolder.trackLocation.setText(mLocation);

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
            Log.e(TAG, id+": "+mStaMap.get(id));
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

    public static class ViewHolderItem {
        private TypefaceTextView maxAirTime;
        private TypefaceTextView maxSpeed;
        private TypefaceTextView maxSpeedUnit;

        private TypefaceTextView arrowUpDown;
        private TypefaceTextView trackDate;
        private TypefaceTextView trackLocation;

        private RelativeLayout expanding_layout;
        private TextView shareBtn;
        private TextView deleteBtn;
    }

    public void resetEntryAnim(){
        for (int i = 0; i < objects.size(); ++i) {
            mIniMap.put(i, false);
        }
    }
}