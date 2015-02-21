package com.gloomy.shreddingrobot.UIFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;

public class SettingFragment extends BaseFragment {

    private static final String TAG = "SettingFragment";
    static final int REQUEST_TAKE_PHOTO  = 1;

    private int veloUnitToggle;
    public TextView[] veloUnit = new TextView[3];





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }
}
