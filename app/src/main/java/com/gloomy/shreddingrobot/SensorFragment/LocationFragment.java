package com.gloomy.shreddingrobot.SensorFragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.gloomy.shreddingrobot.Utility.GetPlaceTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationFrag";
    public static final int UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private static final int FASTEST_INTERVAL_IN_MILLISECONDS = 500;

    private Context _context;
    private LocationCallbacks mUICallback;
    private LocationCallbacks mDataCallback;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean firstConnected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = getActivity();
        init();
    }

    public void setUpDataCallback(LocationCallbacks callback){
        mDataCallback = callback;
    }

    public void setUpUICallback(LocationCallbacks callback) {
        mUICallback = callback;
    }

    private void init() {
        firstConnected = true;
        mGoogleApiClient = new GoogleApiClient.Builder(_context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS); // Update location every second
    }

    public void startTracking() {
        mGoogleApiClient.connect();
        Log.e(TAG, "mGoogleApiClient connect");
    }

    public void stopTracking() {
        mGoogleApiClient.disconnect();
//        mUICallback.updateSpeed(0.0, 0.0);
        Log.e(TAG, "mLocationClient disconnect");
    }

    @Override
    public void onConnected(Bundle dataBundle) {
//        Toast.makeText(_context, "Connected", Toast.LENGTH_SHORT).show();
        // Send request for location update
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        //cypress' lat long
//        String lat = "49.393155";
//        String lon = "-123.212721";
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());

        new GetPlaceTask(getActivity()).execute(lat, lon);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        if (firstConnected){
            Toast.makeText(_context, "Got location feedback", Toast.LENGTH_SHORT).show();
            firstConnected = false;
        }

        double speed = location.getSpeed();
        double accuracy = location.getAccuracy();
        double altitude = location.getAltitude();

//        Log.e(TAG, "Location received: " + location.toString());
//        Log.e(TAG, "Speed: " + speed);
//        Log.e(TAG, "Accuracy: " + accuracy);
//        Log.e(TAG, "Altitude: " + altitude);

        if (mDataCallback != null) {
            mDataCallback.updateSpeed(speed, accuracy);
            mDataCallback.updateAltitude(altitude);
        }

        if (mUICallback != null) {
            mUICallback.updateSpeed(speed, accuracy);
            mUICallback.updateAltitude(altitude);
        }
    }

    public static interface LocationCallbacks {
        void updateSpeed(double curSpeed, double accuracy);
        void updateAltitude(double altitude);
    }
}
