package com.gloomy.shreddingrobot.Utility;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    protected ActionBarActivity parentActivity;
    protected SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (ActionBarActivity) getActivity();
        sp = getActivity().getSharedPreferences("ShreddingPref", Context.MODE_PRIVATE);
    }


}
