package com.gloomy.shreddingrobot;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.gloomy.shreddingrobot.SensorFragment.LocationFragment;
import com.gloomy.shreddingrobot.SensorFragment.MotionFragment;
import com.gloomy.shreddingrobot.UIFragment.DrawerFragment;
import com.gloomy.shreddingrobot.UIFragment.HistoryFragment;
import com.gloomy.shreddingrobot.UIFragment.ResultFragment;
import com.gloomy.shreddingrobot.UIFragment.SettingFragment;
import com.gloomy.shreddingrobot.UIFragment.TrackingFragment;
import com.gloomy.shreddingrobot.Widget.Logger;
import com.nineoldandroids.animation.Animator;


public class MainActivity extends ActionBarActivity
        implements DrawerFragment.NavigationDrawerCallbacks,
                LocationFragment.LocationCallbacks,
                MotionFragment.MotionCallbacks {

    private static final String TAG = "MainActivity";

    private Context _context;
    private MaterialMenuDrawable materialMenu;
    private FragmentManager mFragManager;
    private Menu menu;
    private Toolbar toolbar;

    private DrawerFragment mDrawerFragment;
    private TrackingFragment mTrackingFragment;
    private HistoryFragment mHistoryFragment;
    private SettingFragment mSettingFragment;

    private LocationFragment mLocationFragment;
    private MotionFragment mMotionFragment;

    private CharSequence mTitle;

    private boolean tracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.d(TAG,"onCreate");

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            Log.i(TAG, "setting toolbar");
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
        mFragManager = getFragmentManager();
        _context = this;
        initUI();
        initSensor();
    }

    @Override
    public void onResume(){
        Logger.d(TAG,"onResume");

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Logger.d(TAG,"onCreateOptionsMenu");
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    private void hideOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }


    private void initUI() {
        mDrawerFragment = (DrawerFragment) mFragManager.findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setUp((DrawerLayout) findViewById(R.id.main_drawer_layout), toolbar);

        if(null==mTrackingFragment)
            mTrackingFragment = new TrackingFragment();
        if(null==mHistoryFragment)
            mHistoryFragment = new HistoryFragment();
        if(null==mSettingFragment)
            mSettingFragment = new SettingFragment();
    }

    private void initSensor() {
        tracking = false;
        mLocationFragment = new LocationFragment();
        mLocationFragment.setUpDataCallback(this);
        mFragManager.beginTransaction().add(mLocationFragment, "locationFrag").commit();

        mMotionFragment = new MotionFragment();
        mMotionFragment.setUpDataCallback(this);
        mFragManager.beginTransaction().add(mMotionFragment, "motionFrag").commit();
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
                mMotionFragment.setUpUICallback(mTrackingFragment);
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
        // TODO: May not be necessary anymore
    }

    @Override
    public void updateAirTime(double airTime) {
        // TODO: Implement updating displaced air time
    }

    @Override
    public void updateDuration(int duration) {
        // TODO: Implement updating total track time
    }

    public void startTracking() {
        tracking = true;
        mLocationFragment.startTracking();
        mMotionFragment.startTracking();
    }

    public void stopTraking() {
        tracking = false;
        mLocationFragment.stopTracking();
        mMotionFragment.stopTracking();
        // Create a new Fragment to be placed in the activity layout
        ResultFragment resultFragment = new ResultFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        resultFragment.setArguments(getIntent().getExtras());

        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        mFragTransaction.setCustomAnimations(R.anim.enter_from_top, 0);
        mFragTransaction.replace(R.id.container, resultFragment, "resultFrag").commit();
        setUpResultActionBar();
    }

    private void setUpResultActionBar(){

        getSupportActionBar().setTitle("Cypress Mountain Run 12");
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                transitFromResultToTrack();
            }
        });
        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.X, false);
        toolbar.setNavigationIcon(materialMenu);
        showOption(R.id.action_share);

        mDrawerFragment.disableDrawer();
    }

    public void transitFromResultToTrack(){
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        mFragTransaction.setCustomAnimations(0, R.anim.leave_from_top);
        mFragTransaction.replace(R.id.container, mTrackingFragment, "trackingFrag").commit();
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER, false);
        materialMenu.setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                mLocationFragment.setUpUICallback(mTrackingFragment);
                mMotionFragment.setUpUICallback(mTrackingFragment);
                initUI();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        mTitle = getString(R.string.title_section1);
        restoreActionBar();
        hideOption(R.id.action_share);
        mDrawerFragment.enableDrawer();
    }

    public boolean isTracking() { return tracking; }
}
