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
import com.gloomy.shreddingrobot.Utility.Constants;
import com.gloomy.shreddingrobot.Widget.Logger;
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

import java.text.DecimalFormat;

public class TrackingFragment extends BaseFragment
        implements LocationFragment.LocationCallbacks,
                MotionFragment.MotionCallbacks {

    private static final String TAG = "TrackingFragment";

    private Resources resources;
    private int switchBtnAnimLength;

    private TypefaceTextView tvCurSpeed, tvAirTime, tvMaxAirTime, tvDuration;
    private TypefaceTextView tvCurSpeedUnit, tvAirTimeUnit, tvMaxAirTimeUnit;
    private FrameLayout switchButton;
    private TextView switchButtonIcon;

    private double maxAirTime;

    private int speedUnitToggle, timeUnitToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        initView(rootView);
        bindEvent();

        return rootView;
    }

    private void initView(View rootView) {
        tvCurSpeed = (TypefaceTextView) rootView.findViewById(R.id.tv_max_speed);
        tvAirTime = (TypefaceTextView) rootView.findViewById(R.id.tv_air_time);
        tvMaxAirTime = (TypefaceTextView) rootView.findViewById(R.id.tv_max_air_time);
        tvDuration = (TypefaceTextView) rootView.findViewById(R.id.tv_duration);

        tvCurSpeedUnit = (TypefaceTextView) rootView.findViewById(R.id.tv_max_speed_unit);
        tvAirTimeUnit = (TypefaceTextView) rootView.findViewById(R.id.tv_air_time_unit);
        tvMaxAirTimeUnit = (TypefaceTextView) rootView.findViewById(R.id.tv_max_air_time_unit);

        switchButton = (FrameLayout) rootView.findViewById(R.id.btn_start);
        switchButtonIcon = (TextView) rootView.findViewById(R.id.tv_morph);

        resources = getResources();
        switchBtnAnimLength = resources.getInteger(R.integer.switch_anim_length);
    }

    public void resetBtn() {

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
                } else {
                    Log.e(TAG, "stopTracking");
                    resetBtn();
                    parentActivity.stopTracking();
                }
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        speedUnitToggle = sp.getInt(Constants.SP_SPEED_UNIT, 0);
        timeUnitToggle = sp.getInt(Constants.SP_TIME_UNIT, 0);

        if (parentActivity.isTracking()){
            updateSpeed(parentActivity.getCurSpeed(), 0.0);
            maxAirTime = 0.0;
            if (parentActivity.getMaxAirTime()>1.0){timeUnitToggle = 0;}
            updateAirTime(parentActivity.getMaxAirTime());
            updateAirTime(parentActivity.getAirTime());
            updateDuration(parentActivity.getDuration());

            switchButtonIcon.setBackground(resources.getDrawable(R.drawable.switch_btn_stop));
            switchButton.setBackground(resources.getDrawable(R.drawable.switch_btn_bg_red));
        }else{
            updateSpeed(0.0, 0.0);
            updateAirTime(0.0);
            updateDuration(0);
            
            resetBtn();
        }
    }

    @Override
    public void updateSpeed(double curSpeed, double accuracy) {
        DecimalFormat speedDF = new DecimalFormat("@@@");
        double displaySpeed = curSpeed;
        switch (speedUnitToggle){
            case 1:
                tvCurSpeedUnit.setText("m/s");
                break;
            case 2:
                displaySpeed *= 2.2366;
                tvCurSpeedUnit.setText("mi/h");
                break;
            default:
                displaySpeed *= 3.6;
                tvCurSpeedUnit.setText("km/h");
                break;
        }
        tvCurSpeed.setText(speedDF.format(displaySpeed));
    }

    @Override
    public void updateAltitude(double altitude) {
        // TODO: May not be necessary anymore
    }

    @Override
    public void updateAirTime(double airTime) {
        if (timeUnitToggle==0){
            DecimalFormat dff = new DecimalFormat("0.00");
            tvAirTime.setText(dff.format(airTime));

            if (airTime == 0.0 || airTime > maxAirTime) {
                maxAirTime = airTime;
                tvMaxAirTime.setText(dff.format(maxAirTime));
            }
            tvAirTimeUnit.setText("s");
            tvMaxAirTimeUnit.setText("s");
        }else{
            int disPlayAirTime = Math.round((float)(airTime*1000.0));
            tvAirTime.setText(disPlayAirTime+"");

            if (airTime == 0.0 || airTime > maxAirTime) {
                maxAirTime = airTime;
                tvMaxAirTime.setText(disPlayAirTime+"");
            }
            tvAirTimeUnit.setText("ms");
            tvMaxAirTimeUnit.setText("ms");
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

    public void autoOff() {
        if (switchButton==null){
            Logger.e(TAG, "autoOff: view is null");
        }else{
            switchButton.postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetBtn();
                }
            }, 500);
        }
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