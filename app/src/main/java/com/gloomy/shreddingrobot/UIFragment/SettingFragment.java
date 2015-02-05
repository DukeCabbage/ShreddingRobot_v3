package com.gloomy.shreddingrobot.UIFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;

public class SettingFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }
}
