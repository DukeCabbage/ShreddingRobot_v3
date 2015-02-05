package com.gloomy.shreddingrobot.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.ResultActivity;
import com.gloomy.shreddingrobot.Utility.BaseFragment;


public class TrackingFragment extends BaseFragment {
private Button btn;
    public static TrackingFragment newInstance() {
        return new TrackingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tracking, container, false);
    }
    @Override
    public void onResume(){
        findView();
        bindEvent();
        super.onResume();
    }

    private void bindEvent() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ResultActivity.class);
                startActivity(intent);
            }
        });
    }

    private void findView() {
        btn = (Button) getView().findViewById(R.id.button);
    }


}
