package com.gloomy.shreddingrobot;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ResultActivity extends ActionBarActivity {
    private static final String TAG = ResultActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            Log.i(TAG, "setting toolbar");
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Cypress 25th Dec 2015 Run 12");
            //toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_cancel);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));


    }
}
