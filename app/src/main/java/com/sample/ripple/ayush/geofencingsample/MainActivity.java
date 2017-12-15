package com.sample.ripple.ayush.geofencingsample;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener
    ,ResultCallback<Status>{
        GoogleApiClient mGoogleApiClient;
        private static final String TAG="FromActivity";
        ArrayList<Geofence> geofences=new ArrayList<>();



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mGoogleApiClient=new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).
                        addConnectionCallbacks(this).
                        addOnConnectionFailedListener(this)
                .build();
        Log.v(TAG,"onCreate ");

    }
public void addGeoFenceButtonHandler(View view){
    if (!mGoogleApiClient.isConnected()) {
        Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
        return;
    }

    try {
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                // The GeofenceRequest object.
                getGeofencingRequest(),
                // A pending intent that that is reused when calling removeGeofences(). This
                // pending intent is used to generate an intent when a matched geofence
                // transition is observed.
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().
    } catch (SecurityException securityException) {
        // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
    }
}
public void populateGeofenceList() {
    for (Map.Entry<String, LatLng> entry : Constants.BAY_AREA_LANDMARKS.entrySet()) {
        geofences.add(new Geofence.Builder()
                .setRequestId(entry.getKey())
                .setCircularRegion(
                        entry.getValue().latitude,
                        entry.getValue().longitude,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }  }

    private GeofencingRequest getGeofencingRequest(){
    GeofencingRequest.Builder builder=new GeofencingRequest.Builder();
    builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
    builder.addGeofences(geofences);
    return builder.build();
    }

        private PendingIntent getGeofencePendingIntent() {
            Intent intent = new Intent(this, GeoFenceTransitionsIntentService.class);
            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling addgeoFences()
            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }



    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG,"onStart ");

        mGoogleApiClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG,"onStop ");

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG,"onConnected ");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "Connection suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.v(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());

    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Toast.makeText(
                    this,
                    "Geofences Added",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
        }
    }
}

