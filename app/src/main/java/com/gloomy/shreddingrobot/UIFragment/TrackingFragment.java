package com.gloomy.shreddingrobot.UIFragment;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.ResultActivity;
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
        bindEvent(rootView);
        return rootView;
    }

    private void bindEvent(final View rootView) {
        FrameLayout button = (FrameLayout) rootView.findViewById(R.id.btn_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ResultActivity.class);
//                startActivity(intent);
                TransitionDrawable transition = (TransitionDrawable) v.getBackground();
                transition.startTransition(1000);

                TextView morph = (TextView) rootView.findViewById(R.id.tv_morph);
                animateDrawables(morph);

            }
        });
    }

    private void initView(View rootView) {
        tvCurSpeed = (TextView) rootView.findViewById(R.id.tv_speed);
    }

    @Override
    public void onResume(){

        super.onResume();

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

    private void animateDrawables(View view) {
        if (!(view instanceof TextView)) {
            return;
        }
        TextView textView = (TextView) view;

        if (textView.getBackground() instanceof Animatable) {
            ((Animatable) textView.getBackground()).start();
        }

    }
}
