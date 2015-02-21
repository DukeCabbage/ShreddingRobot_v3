package com.gloomy.shreddingrobot.Utility;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

public class HistoryViewHolder {
    public RelativeLayout categoryLayout;
    public TypefaceTextView trackDate;
    public TypefaceTextView trackLocation;

    public RelativeLayout indexLayout;
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

    public TextView shareBtn;
    public TextView deleteBtn;

    public void findView(View rootView){
        categoryLayout = (RelativeLayout) rootView.findViewById(R.id.histo_category_layout);
        trackDate = (TypefaceTextView) categoryLayout.findViewById(R.id.tv_track_date);
        trackLocation = (TypefaceTextView) categoryLayout.findViewById(R.id.tv_track_location);

        indexLayout = (RelativeLayout) rootView.findViewById(R.id.histo_index_layout);
        trackDuration = (TypefaceTextView) indexLayout.findViewById(R.id.tv_track_duration);
        trackDistance = (TypefaceTextView) indexLayout.findViewById(R.id.tv_track_distance);
        arrowUpDown = (TypefaceTextView) indexLayout.findViewById(R.id.arrow_up_down);
        arrowUpDown.setText(Constants.ICON_ARROW_DOWN);

        expandingLayout = (LinearLayout) rootView.findViewById(R.id.expanding_layout);
        maxSpeed = (TypefaceTextView) expandingLayout.findViewById(R.id.max_speed);
        maxSpeedUnit = (TypefaceTextView) expandingLayout.findViewById(R.id.max_speed_unit);
        avgSpeed = (TypefaceTextView) expandingLayout.findViewById(R.id.avg_speed);
        avgSpeedUnit = (TypefaceTextView) expandingLayout.findViewById(R.id.avg_speed_unit);
        airTime = (TypefaceTextView) expandingLayout.findViewById(R.id.air_time);
        airTimeUnit = (TypefaceTextView) expandingLayout.findViewById(R.id.air_time_unit);
        jumpDist = (TypefaceTextView) expandingLayout.findViewById(R.id.jump_dist);
        jumpDistUnit = (TypefaceTextView) expandingLayout.findViewById(R.id.jump_dist_unit);

        shareBtn = (TextView) expandingLayout.findViewById(R.id.share_btn);
        deleteBtn = (TextView) expandingLayout.findViewById(R.id.delete_btn);
        shareBtn.setText(Constants.ICON_SHARE);
        deleteBtn.setText(Constants.ICON_DELETE);
    }
}