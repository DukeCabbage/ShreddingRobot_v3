package com.gloomy.shreddingrobot;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import com.gloomy.shreddingrobot.Fragment.DrawerFragment;
import com.gloomy.shreddingrobot.Fragment.HistoryFragment;
import com.gloomy.shreddingrobot.Fragment.SettingFragment;
import com.gloomy.shreddingrobot.Fragment.TrackingFragment;


public class MainActivity extends ActionBarActivity
        implements DrawerFragment.NavigationDrawerCallbacks {

    private static final String TAG = "MainActivity";

    private FragmentManager mFragManager;

    private DrawerFragment mDrawerFragment;
    private TrackingFragment mTrackingFragment;
    private HistoryFragment mHistoryFragment;
    private SettingFragment mSettingFragment;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragManager = getFragmentManager();

        mDrawerFragment = (DrawerFragment) mFragManager.findFragmentById(R.id.navigation_drawer);
        mDrawerFragment.setUp((DrawerLayout) findViewById(R.id.main_drawer_layout));

        mTrackingFragment = TrackingFragment.newInstance();
        mHistoryFragment = HistoryFragment.newInstance();
        mSettingFragment = SettingFragment.newInstance();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentTransaction mFragTransaction = mFragManager.beginTransaction();
        switch(position) {
            case 0:
                mFragTransaction.replace(R.id.container, mTrackingFragment).commit();
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mFragTransaction.replace(R.id.container, mHistoryFragment).commit();
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mFragTransaction.replace(R.id.container, mSettingFragment).commit();
                mTitle = getString(R.string.title_section3);
                break;
        }
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
