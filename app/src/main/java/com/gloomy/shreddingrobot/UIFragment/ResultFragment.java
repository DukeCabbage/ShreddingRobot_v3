package com.gloomy.shreddingrobot.UIFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;


public class ResultFragment extends BaseFragment {
    private static final String TAG = ResultFragment.class.getSimpleName();
    private MaterialMenuDrawable materialMenu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_result);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        if (toolbar != null) {
//            Log.i(TAG, "setting toolbar");
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setTitle("Cypress 25th Dec 2015 Run 12");
//            //toolbar.setTitleTextColor(getResources().getColor(R.color.white));
//            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);
//            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                // Handle your drawable state here
//                materialMenu.animateIconState(MaterialMenuDrawable.IconState.X, true);
//            }
//        });
//
//        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
//        toolbar.setNavigationIcon(materialMenu);
//        materialMenu.setNeverDrawTouch(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
//
//
//    }
}
