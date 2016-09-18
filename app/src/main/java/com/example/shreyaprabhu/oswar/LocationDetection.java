package com.example.shreyaprabhu.oswar;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Shreya Prabhu on 9/18/2016.
 */
public class LocationDetection implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mgoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private static final String TAG = "MainActivity";
    Context context;
    Activity activity;
    MainActivity mainActivity;

    /*
     *  Initialise the required variables through MainActivity
     */
    public void Intialise(Context context, Activity activity) {

        mgoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.context = context;
        this.activity = activity;
        mainActivity = ((MainActivity) context);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }


    public Location getLocation() {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (mLocation != null) {
            Toast.makeText(context, "Latitude:" + mLocation.getLatitude() + "\n" + "Longitude:" + mLocation.getLongitude(),Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Location not Detected",Toast.LENGTH_SHORT).show();
        }
        return mLocation;
    }

    /*
     * tasks to be performed once permission is granted(for Marshmallow and above)
     */
    public void onRequestResult() {
        // permission was granted, yay! Do the

        // contacts-related task you need to do.
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mgoogleApiClient);
        if (mLocation != null) {
            Toast.makeText(context, "Latitude:" + mLocation.getLatitude() + "\n" + "Longitude:" + mLocation.getLongitude(),Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Location not Detected",Toast.LENGTH_SHORT).show();
        }


    }

    public void startLocationDetection() {
        mgoogleApiClient.connect();

    }

    public void stopLocationDetection() {
        if (mgoogleApiClient.isConnected()) {
            mgoogleApiClient.disconnect();
        }

    }

    /*
     * Function to get Location updates after every given interval of time
     */

    public void getLocationUpdates(int interval, int fastestInterval) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleApiClient, mLocationRequest, this);
    }

    /*
     * Function called when location is changed.
     */



    @Override
    public void onLocationChanged(Location location) {
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Toast.makeText(context, "Latitude:" + mLocation.getLatitude() + "\n" + "Longitude:" + mLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Updated: " + mLastUpdateTime, Toast.LENGTH_SHORT).show();

        for (int i = 0; i <= mainActivity.eventsDbHelper.getProfilesCount(); i++) {
            Cursor phonedetailcursor = mainActivity.eventsDbHelper.getEvent(i);
            if (phonedetailcursor != null && phonedetailcursor.moveToNext()) {
                String phoneNo = phonedetailcursor.getString(2);
                String sms = "Help1" + location.getLatitude() + location.getLongitude();
                mainActivity.sendsms(phoneNo,sms);
            }

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mgoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());

    }
}