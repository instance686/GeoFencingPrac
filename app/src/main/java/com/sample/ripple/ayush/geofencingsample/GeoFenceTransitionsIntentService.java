package com.sample.ripple.ayush.geofencingsample;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayush on 15/12/17.
 */

public class GeoFenceTransitionsIntentService extends IntentService {
    private static final String TAG="FromIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public GeoFenceTransitionsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent=GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String errorMessage=GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
            Log.d(TAG,errorMessage);
            return;
        }
        int geoFenceTransition=geofencingEvent.getGeofenceTransition();
        if(geoFenceTransition== Geofence.GEOFENCE_TRANSITION_ENTER||
                geoFenceTransition==Geofence.GEOFENCE_TRANSITION_EXIT){
            List<Geofence> triggeringGeofences=geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails=getGeofenceTransitionDetails(
             this
            ,geoFenceTransition,
             triggeringGeofences );
            sendNotification(geofenceTransitionDetails);
        }


    }
    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private String getGeofenceTransitionDetails(Context context,
                                                int geoFenceTransition,
                                                List<Geofence> triggeringGeofences){
        String geoFenceTransitionString =getTransitionString(geoFenceTransition);
        ArrayList triggerGeofencesIdList=new ArrayList();
        for(Geofence geofence:triggeringGeofences){
            triggerGeofencesIdList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdString= TextUtils.join(", ",triggerGeofencesIdList);
        return geoFenceTransitionString+": "+triggeringGeofencesIdString;
    }
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
