package com.gloomy.shreddingrobot.UIFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.SensorFragment.LocationFragment;
import com.gloomy.shreddingrobot.Utility.BaseFragment;

import java.text.DecimalFormat;


public class TrackingFragment extends BaseFragment
        implements LocationFragment.LocationCallbacks {

    private static final String TAG = "TrackingFrag";

    private TextView tvCurSpeed, tvAirTime, tvMaxAirTime, tvTrackLength;
    private Button switchButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        tvCurSpeed = (TextView) rootView.findViewById(R.id.tv_speed);
    }

    @Override
    public void updateSpeed(double curSpeed, double accuracy) {
        double displaySpeed = curSpeed * 3.6;

        DecimalFormat speedDF = new DecimalFormat("@@@");

        tvCurSpeed.setText(speedDF.format(displaySpeed) + " km/h");
    }

    @Override
    public void updateAltitude(double altitude) {

    }
}
