package com.gloomy.shreddingrobot;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.gloomy.shreddingrobot.Dao.DBTrack;
import com.gloomy.shreddingrobot.Dao.DBTrackDao;
import com.gloomy.shreddingrobot.Dao.DaoManager;
import com.gloomy.shreddingrobot.SensorFragment.LocationFragment;
import com.gloomy.shreddingrobot.SensorFragment.MotionFragment;
import com.gloomy.shreddingrobot.UIFragment.DrawerFragment;
import com.gloomy.shreddingrobot.UIFragment.HistoryFragment;
import com.gloomy.shreddingrobot.UIFragment.ResultFragment;
import com.gloomy.shreddingrobot.UIFragment.SettingFragment;
import com.gloomy.shreddingrobot.UIFragment.TrackingFragment;

import java.util.Date;
import java.util.Random;

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
    private ResultFragment mResultFragment;

    private LocationFragment mLocationFragment;
    private MotionFragment mMotionFragment;

    private CharSequence mTitle;

    private DBTrackDao trackDao;
    private DBTrack curTrack;

    private boolean tracking;
    private Date trackDate;
    private String trackLocation;
    private double trackDuration;
    private double curSpeed, maxSpeed, avgSpeed;
    private double airTime,  maxAirTime;
    private double jumpDistance, maxJumpDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
        mFragManager = getFragmentManager();
        _context = this;
        DaoManager daoManager = DaoManager.getInstance(_context);
        trackDao = daoManager.getDBTrackDao(DaoManager.TYPE_WRITE);
        initUI();
        initSensor();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
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
        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);

        if(null==mTrackingFragment)
            mTrackingFragment = new TrackingFragment();
        if(null==mHistoryFragment)
            mHistoryFragment = new HistoryFragment();
        if(null==mSettingFragment)
            mSettingFragment = new SettingFragment();
        if(null==mResultFragment)
            mResultFragment = new ResultFragment();
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        mResultFragment.setArguments(getIntent().getExtras());
    }

    private void initSensor() {
        tracking = false;
        mLocationFragment = new LocationFragment();
        mLocationFragment.setUpDataCallback(this);
        mFragManager.beginTransaction().add(mLocationFragment, "locationFrag").commit();

        mMotionFragment = new MotionFragment();
        mMotionFragment.setUpDataCallback(this);
        mFragManager.beginTransaction().add(mMotionFragment, "motionFrag").commit();
        mLocationFragment.setUpUICallback(mTrackingFragment);
        mMotionFragment.setUpUICallback(mTrackingFragment);
    }

    // Drawer Fragment Callbacks
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        mFragTransaction.setCustomAnimations(0, R.anim.leave_from_right);
        switch(position) {
            case 0:
                mFragTransaction.replace(R.id.container, mTrackingFragment, "trackingFrag").commit();
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
        if (accuracy <= 10){
            this.curSpeed = curSpeed;
            if (this.curSpeed > this.maxSpeed){
                this.maxSpeed = this.curSpeed;
            }
        }
    }

    @Override
    public void updateAltitude(double altitude) {
        // TODO: May not be necessary anymore
    }

    @Override
    public void updateAirTime(double airTime) {
        this.airTime = airTime;
        if (this.airTime > this.maxAirTime) {
            this.maxAirTime = this.airTime;
        }
        if (curSpeed!=0){
            jumpDistance = curSpeed*airTime;
            if (jumpDistance > maxJumpDistance){
                maxJumpDistance = jumpDistance;
            }
        }
    }

    @Override
    public void updateDuration(int duration) {
        trackDuration = duration;
        if (duration!=0){
            avgSpeed = (avgSpeed*(duration-1) + curSpeed)/(double)duration;
        }
    }

    public void startTracking() {
        tracking = true;
        mLocationFragment.startTracking();
        mMotionFragment.startTracking();

        //Initialize DBTrack object
        Random rg = new Random();
        long id = (long) (rg.nextDouble() * 999999);
        curTrack = new DBTrack(id);

        trackDate = new Date();
        trackLocation = "Over the mountain";
        trackDuration = 0.0;
        curSpeed = 0.0;
        maxSpeed = 0.0;
        avgSpeed = 0.0;
        airTime = 0.0;
        maxAirTime = 0.0;
        jumpDistance = 0.0;
        maxJumpDistance = 0.0;
    }

    public void stopTracking() {
        tracking = false;
        mLocationFragment.stopTracking();
        mMotionFragment.stopTracking();

        curTrack.setDate(trackDate);
        curTrack.setLocationName(trackLocation);
        curTrack.setMaxSpeed(maxSpeed);
        curTrack.setAvgSpeed(avgSpeed);
        curTrack.setMaxAirTime(maxAirTime);
        curTrack.setMaxJumpDistance(maxJumpDistance);

        trackDao.insert(curTrack);

//        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
//        mFragTransaction.setCustomAnimations(R.anim.enter_from_right, 0);
//        mFragTransaction.replace(R.id.container, mResultFragment, "resultFrag").commit();
//        setUpResultActionBar();
    }

    private void setUpResultActionBar(){
        getSupportActionBar().setTitle("Cypress Mountain Run 12");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                backFromResultPage();
            }
        });
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.X, false);
        toolbar.setNavigationIcon(materialMenu);
        showOption(R.id.action_share);

        mDrawerFragment.disableDrawer();
    }

    public void backFromResultPage(){
        materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER, false);
        onNavigationDrawerItemSelected(mDrawerFragment.getSelected());

        hideOption(R.id.action_share);
        mDrawerFragment.setUp((DrawerLayout) findViewById(R.id.main_drawer_layout), toolbar);
        mDrawerFragment.enableDrawer();
    }

    public boolean isTracking() { return tracking; }
}
