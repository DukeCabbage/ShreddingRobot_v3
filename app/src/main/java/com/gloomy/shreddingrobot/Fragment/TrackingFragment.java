package com.gloomy.shreddingrobot.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;


public class TrackingFragment extends BaseFragment {

    public static TrackingFragment newInstance() {
        return new TrackingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking, container, false);
    }
}
