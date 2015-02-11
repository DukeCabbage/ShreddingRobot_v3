package com.gloomy.shreddingrobot.Utility;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.gloomy.shreddingrobot.MainActivity;

public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    protected MainActivity parentActivity;
    protected SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (MainActivity) getActivity();
        sp = getActivity().getSharedPreferences("ShreddingPref", Context.MODE_PRIVATE);
    }
}
