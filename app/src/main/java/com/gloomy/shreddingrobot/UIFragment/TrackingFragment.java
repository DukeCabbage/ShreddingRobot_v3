package com.gloomy.shreddingrobot.UIFragment;

import android.content.res.Resources;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.SensorFragment.LocationFragment;
import com.gloomy.shreddingrobot.SensorFragment.MotionFragment;
import com.gloomy.shreddingrobot.Utility.BaseFragment;

import java.text.DecimalFormat;

public class TrackingFragment extends BaseFragment
        implements LocationFragment.LocationCallbacks,
                MotionFragment.MotionCallbacks {

    private static final String TAG = "TrackingFrag";

    private Resources resources;

    public TextView tvCurSpeed, tvAirTime, tvMaxAirTime, tvDuration;
    private FrameLayout switchButton;
    private TextView switchButtonIcon;

    private int switchBtnAnimLength;

    private double maxAirTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        initView(rootView);
        setUp();
        bindEvent();
        return rootView;
    }

    private void initView(View rootView) {
        tvCurSpeed = (TextView) rootView.findViewById(R.id.tv_max_speed);
        tvAirTime = (TextView) rootView.findViewById(R.id.tv_air_time);
        tvMaxAirTime = (TextView) rootView.findViewById(R.id.tv_max_air_time);
        tvDuration = (TextView) rootView.findViewById(R.id.tv_duration);

        switchButton = (FrameLayout) rootView.findViewById(R.id.btn_start);
        switchButtonIcon = (TextView) rootView.findViewById(R.id.tv_morph);
    }

    private void setUp() {
        resources = getResources();
        switchBtnAnimLength = resources.getInteger(R.integer.switch_anim_length);
        switchButton.setBackground(resources.getDrawable(R.drawable.switch_btn_bg_transition));
        switchButtonIcon.setBackground(resources.getDrawable(R.drawable.switch_btn_play));
    }

    private void bindEvent() {
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!parentActivity.isTracking()) {
                    TransitionDrawable transition = (TransitionDrawable) v.getBackground();
                    transition.startTransition(switchBtnAnimLength);

                    switchButtonIcon.setBackground(resources.getDrawable(R.drawable.switch_btn_vector));
                    animateDrawables(switchButtonIcon);

                    v.setEnabled(false);
                    parentActivity.startTracking();
                    maxAirTime = 0.0;
                } else {
                    Log.e(TAG, "stopTracking");
                    setUp();
                    parentActivity.stopTracking();
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void updateSpeed(double curSpeed, double accuracy) {
        double displaySpeed = curSpeed * 3.6;

        DecimalFormat speedDF = new DecimalFormat("@@@");

        tvCurSpeed.setText(speedDF.format(displaySpeed));
    }

    @Override
    public void updateAltitude(double altitude) {
        // TODO: May not be necessary anymore
    }

    @Override
    public void updateAirTime(double airTime) {
        DecimalFormat dff = new DecimalFormat("0.00");
        tvAirTime.setText(dff.format(airTime));

        if (airTime > maxAirTime) {
            maxAirTime = airTime;
            tvMaxAirTime.setText(dff.format(maxAirTime));
        }
    }

    @Override
    public void updateDuration(int duration) {
        if (duration<0) return;
        int hours = duration/60;
        int minutes = duration%60;
        String hoursStr, minutesStr;

        minutesStr = minutes<10 ? "0"+minutes : ""+minutes;
        hoursStr = hours<10 ? "0"+hours : ""+hours;

        tvDuration.setText(hoursStr+":"+minutesStr);

    }

    private void animateDrawables(View view) {
        if (!(view instanceof TextView)) {
            return;
        }
        final TextView textView = (TextView) view;

        if (textView.getBackground() instanceof Animatable) {
            ((Animatable) textView.getBackground()).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setBackground(resources.getDrawable(R.drawable.switch_btn_stop));
                    switchButton.setBackground(resources.getDrawable(R.drawable.switch_btn_bg_red));
                    switchButton.setEnabled(true);
                }
            }, switchBtnAnimLength);
        }

    }
}