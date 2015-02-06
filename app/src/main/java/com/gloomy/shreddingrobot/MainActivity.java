package com.gloomy.shreddingrobot;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.gloomy.shreddingrobot.SensorFragment.LocationFragment;
import com.gloomy.shreddingrobot.UIFragment.DrawerFragment;
import com.gloomy.shreddingrobot.UIFragment.HistoryFragment;
import com.gloomy.shreddingrobot.UIFragment.SettingFragment;
import com.gloomy.shreddingrobot.UIFragment.TrackingFragment;


public class MainActivity extends ActionBarActivity
        implements DrawerFragment.NavigationDrawerCallbacks, LocationFragment.LocationCallbacks {

    private static final String TAG = "MainActivity";

    private Context _context;
    private FragmentManager mFragManager;

    private DrawerFragment mDrawerFragment;
    private TrackingFragment mTrackingFragment;
    private HistoryFragment mHistoryFragment;
    private SettingFragment mSettingFragment;

    private LocationFragment mLocationFragment;

    private CharSequence mTitle;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            Log.i(TAG, "setting toolbar");
            setSupportActionBar(toolbar);
        }

        _context = this;
        mFragManager = getFragmentManager();
        initUI();
        initSensor();
    }

    private void initUI() {
        mDrawerFragment = (DrawerFragment) mFragManager.findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setUp((DrawerLayout) findViewById(R.id.main_drawer_layout), toolbar);

        mTrackingFragment = new TrackingFragment();
        mHistoryFragment = new HistoryFragment();
        mSettingFragment = new SettingFragment();


    }

    private void initSensor() {
        mLocationFragment = new LocationFragment();
        mLocationFragment.setUpDataCallback(this);
        mFragManager.beginTransaction().add(mLocationFragment, "locationFrag").commit();
    }

    // Drawer Fragment Callbacks
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        switch(position) {
            case 0:
                mFragTransaction.replace(R.id.container, mTrackingFragment, "trackingFrag").commit();
                mLocationFragment.setUpUICallback(mTrackingFragment);
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mFragTransaction.replace(R.id.container, mHistoryFragment, "historyFrag").commit();
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mFragTransaction.replace(R.id.container, mSettingFragment, "settingFrag").commit();
                mTitle = getString(R.string.title_section3);
                break;
        }
        restoreActionBar();
    }

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // Location Fragment callbacks
    @Override
    public void updateSpeed(double curSpeed, double accuracy) {

    }

    @Override
    public void updateAltitude(double altitude) {

    }

    // Timer Fragment callbacks
}
