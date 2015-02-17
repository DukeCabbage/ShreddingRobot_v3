package com.gloomy.shreddingrobot.Utility;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

public class HistoryViewHolder {
    public RelativeLayout histoIndexLayout;
    public TypefaceTextView trackDistance;
    public TypefaceTextView trackDuration;
    public TypefaceTextView arrowUpDown;

    public LinearLayout expandingLayout;
    public TypefaceTextView maxSpeed;
    public TypefaceTextView maxSpeedUnit;
    public TypefaceTextView avgSpeed;
    public TypefaceTextView avgSpeedUnit;
    public TypefaceTextView airTime;
    public TypefaceTextView airTimeUnit;
    public TypefaceTextView jumpDist;
    public TypefaceTextView jumpDistUnit;

    public LinearLayout expanding_layout;
    public TextView shareBtn;
    public TextView deleteBtn;

    public void findView(View rootView){
        histoIndexLayout = (RelativeLayout) rootView.findViewById(R.id.histo_index_layout);
        trackDuration = (TypefaceTextView) rootView.findViewById(R.id.tv_track_duration);
        trackDistance = (TypefaceTextView) rootView.findViewById(R.id.tv_track_distance);
        arrowUpDown = (TypefaceTextView) rootView.findViewById(R.id.arrow_up_down);
        arrowUpDown.setText(Constants.ICON_ARROW_DOWN);

        expanding_layout = (LinearLayout) rootView.findViewById(R.id.expanding_layout);
        maxSpeed = (TypefaceTextView) rootView.findViewById(R.id.max_speed);
        maxSpeedUnit = (TypefaceTextView) rootView.findViewById(R.id.max_speed_unit);
        avgSpeed = (TypefaceTextView) rootView.findViewById(R.id.avg_speed);
        avgSpeedUnit = (TypefaceTextView) rootView.findViewById(R.id.avg_speed_unit);
        airTime = (TypefaceTextView) rootView.findViewById(R.id.air_time);
        airTimeUnit = (TypefaceTextView) rootView.findViewById(R.id.air_time_unit);
        jumpDist = (TypefaceTextView) rootView.findViewById(R.id.jump_dist);
        jumpDistUnit = (TypefaceTextView) rootView.findViewById(R.id.jump_dist_unit);

        shareBtn = (TextView) rootView.findViewById(R.id.share_btn);
        deleteBtn = (TextView) rootView.findViewById(R.id.delete_btn);
        shareBtn.setText(Constants.ICON_SHARE);
        deleteBtn.setText(Constants.ICON_DELETE);
    }
}