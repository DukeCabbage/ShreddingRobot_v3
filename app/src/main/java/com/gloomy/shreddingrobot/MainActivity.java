package com.gloomy.shreddingrobot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.gloomy.shreddingrobot.Utility.Constants;
import com.gloomy.shreddingrobot.Widget.Logger;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import com.facebook.Session;
import com.nineoldandroids.animation.Animator;

public class MainActivity extends ActionBarActivity
        implements DrawerFragment.NavigationDrawerCallbacks,
                LocationFragment.LocationCallbacks,
                MotionFragment.MotionCallbacks {

    private static final String TAG = "MainActivity";

    private static final int AVERAGE_SPEED_UPDATE_INTERVAL_IN_SECONDS = 10;
    private static final int ALTITUDE_AVERAGING_QUEUE_SIZE = 20;
    private static final int AUTO_OFF_ALTITUDE_THRESHOLD = 150;

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
    private boolean showingResult = false;

    private LocationFragment mLocationFragment;
    private MotionFragment mMotionFragment;

    private boolean gpsEnabled;
    private double curAltitude, altitude_min;
    private ArrayBlockingQueue<Double> rawAltData;

    private CharSequence mTitle;

    private DBTrackDao trackDao;
    private DBTrack curTrack;

    private boolean tracking = false;
    private Date trackDate;

    private String trackLocation, lastLocation;
    private int trackDuration;
    private double curSpeed, maxSpeed, avgSpeed;
    private double airTime,  maxAirTime;
    private double jumpDistance, maxJumpDistance;
    private int trackLevel;

    private boolean liftOff;
    private int sleepTime;

    protected SharedPreferences sp;

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

        sp = getSharedPreferences("ShreddingPref", Context.MODE_PRIVATE);
        lastLocation = sp.getString(Constants.SP_LAST_LOCATION, "unknown");
        settingUpdated();
        initUI();
        initSensor();
    }

    @Override
    public void onResume(){
        super.onResume();
        // Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(_context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS for speed measuring");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    gpsEnabled = false;
                    dialogInterface.cancel();
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }else{
            gpsEnabled = true;
        }
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

    public void setTrackLocation(String trackLocation) {
        this.trackLocation = trackLocation;
        if (showingResult){
            getSupportActionBar().setTitle(trackLocation);
        }
        Toast.makeText(_context, "geo-fencing: " + trackLocation, Toast.LENGTH_SHORT).show();
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
        if(null==mResultFragment) {
            mResultFragment = new ResultFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            mResultFragment.setArguments(getIntent().getExtras());
        }
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
    public void updateAltitude(double newAltitude) {
        if (newAltitude == 0.0) {
            Logger.d(TAG, "no altitude reading");
        } else {
            curAltitude *= ALTITUDE_AVERAGING_QUEUE_SIZE;
            boolean stabilizedAlt = rawAltData.size() == ALTITUDE_AVERAGING_QUEUE_SIZE;
            if (stabilizedAlt) {
                curAltitude -= rawAltData.poll();
                curAltitude += newAltitude;
                rawAltData.add(newAltitude);

            } else {
                curAltitude += newAltitude;
                rawAltData.add(newAltitude);
            }
            curAltitude /= ALTITUDE_AVERAGING_QUEUE_SIZE;

            if (stabilizedAlt){
                if (curAltitude < altitude_min) {
                    altitude_min = curAltitude;
                }
                else if (curAltitude > (altitude_min+ AUTO_OFF_ALTITUDE_THRESHOLD)) {
                    if(liftOff){
                        stopTracking();
                        mTrackingFragment.autoOff();
                    }
                }
            }
        }
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


        if (duration%AVERAGE_SPEED_UPDATE_INTERVAL_IN_SECONDS == 0){
            if (duration!=0){
                avgSpeed = (avgSpeed*(duration-AVERAGE_SPEED_UPDATE_INTERVAL_IN_SECONDS) + curSpeed)/(double)duration;
                Logger.d(TAG, "average speed updated: " + avgSpeed);
            }
        }

        if (sleepTime!=0&&duration>=sleepTime){
            stopTracking();
            mTrackingFragment.autoOff();
        }
    }

    public void settingUpdated(){
        sleepTime = sp.getInt(Constants.SP_SLEEP_TIME, 0) * Constants.UC_SECONDS_IN_MINUTE;
        liftOff = sp.getBoolean(Constants.SP_LIFT_OFF, false);
    }

    public void startTracking() {
        Logger.d(TAG, "startTracking");
        tracking = true;
        if (gpsEnabled) {
            mLocationFragment.startTracking();
        }
        mMotionFragment.startTracking();

        rawAltData = new ArrayBlockingQueue<>(ALTITUDE_AVERAGING_QUEUE_SIZE);

        //Initialize DBTrack object
        Random rg = new Random();
        long id = (long) (rg.nextDouble() * 999999);
        curTrack = new DBTrack(id);

        trackDate = new Date();
        trackLocation = null;
        trackDuration = 0;
        curSpeed = 0.0;
        maxSpeed = 0.0;
        avgSpeed = 0.0;
        airTime = 0.0;
        maxAirTime = 0.0;
        jumpDistance = 0.0;
        maxJumpDistance = 0.0;
        trackLevel = 0;
    }

    public void stopTracking() {
        Logger.d(TAG, "stopTracking");
        tracking = false;
        if (gpsEnabled) {
            mLocationFragment.stopTracking();
        }
        mMotionFragment.stopTracking();

        curTrack.setDate(trackDate);
        if (trackLocation==null) {
            curTrack.setLocationName(lastLocation);
        }else{
            curTrack.setLocationName(trackLocation);
            sp.edit().putString(Constants.SP_LAST_LOCATION, trackLocation).apply();
        }
        curTrack.setDuration(trackDuration);
        curTrack.setMaxSpeed(maxSpeed);
        curTrack.setAvgSpeed(avgSpeed);
        curTrack.setMaxAirTime(maxAirTime);
        curTrack.setMaxJumpDistance(maxJumpDistance);

        trackDao.insert(curTrack);
    }

    public void summarizeTrack() {
        double part1 = maxSpeed/30.0;
        double part2 = avgSpeed/10.0;
        double part3 = maxAirTime/5.0;
        double part4 = maxJumpDistance/10.0;

        int level1 = part1>=1 ? 8 : (int)(8*part1);
        int level2 = part2>=1 ? 4 : (int)(4*part2);
        int level3 = part3>=1 ? 8 : (int)(8*part3);
        int level4 = part4>=1 ? 4 : (int)(4*part4);

        trackLevel = level1 + level2 + level3 + level4;

        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        mFragTransaction.setCustomAnimations(R.anim.enter_from_top, 0);
        mFragTransaction.replace(R.id.container, mResultFragment, "resultFrag").commit();
        setUpResultActionBar();
    }

    private void setUpResultActionBar(){
        showingResult = true;
        getSupportActionBar().setTitle(trackLocation);
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
        showingResult = false;
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        mFragTransaction.setCustomAnimations(0, R.anim.leave_from_right);
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
    public boolean isGpsEnabled() { return gpsEnabled; }

    public double getCurSpeed() { return curSpeed; }
    public double getMaxSpeed() {return maxSpeed; }
    public double getAvgSpeed() {return avgSpeed; }
    public double getAirTime() { return airTime; }
    public double getLongestJump() {return maxJumpDistance; }
    public double getMaxAirTime() { return maxAirTime; }
    public int getDuration() { return trackDuration; }
    public int getTrackLevel() {return trackLevel; }
}
