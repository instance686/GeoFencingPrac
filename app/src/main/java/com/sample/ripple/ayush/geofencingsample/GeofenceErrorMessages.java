package com.sample.ripple.ayush.geofencingsample;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Created by ayush on 15/12/17.
 */

public class GeofenceErrorMessages {
   private GeofenceErrorMessages(){}
    public static String getErrorString(Context context,int errorCode){
        Resources resources=context.getResources();
        switch (errorCode){
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence Service is not available now";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Your App has registered too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "You have provided too many Pending Intents to add geo fence";
            default:
                return "Geo fence Service not available now";
        }
    }
}
