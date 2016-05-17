package com.google.android.gms.location.sample.locationupdates;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.sample.locationupdates.Utils.Const;

/**
 * Created by Grishma on 17/5/16.
 */
public class HomeActivity extends Activity {

    protected Button mStartUpdatesButton, mStopUpdatesButton;
    private boolean mIsServiceStarted = false;
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String ACTION_STOP = "STOP_ACTION";
    public static final String ACTION_FROM_NOTIFICATION = "isFromNotification";
    //    public static final String ACTION_SNOOZE = "SNOOZE_ACTION";
//    public static final String ACTION_DISMISS = "DISMISS_ACTION";
    private String action;
    private int notifID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // Locate the UI widgets.
//        mIsServiceStarted = false;
        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);

    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mIsServiceStarted) {
            mIsServiceStarted = true;
            setButtonsEnabledState();
            OnGoingLocationNotification(this);
            startService(new Intent(this, LocationUpdateService.class));
        }
    }

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */
    private void setButtonsEnabledState() {
        if (mIsServiceStarted) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    public void stopUpdatesButtonHandler(View view) {
        if (mIsServiceStarted) {
            mIsServiceStarted = false;
            setButtonsEnabledState();
            cancelNotification(this, notifID);
            stopService(new Intent(this, LocationUpdateService.class));
        }
    }

    public void exportDatabaseToSdCard(View view) {
        Const.ExportDatabase(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getAction() != null) {
            action = getIntent().getAction();
            notifID = getIntent().getIntExtra(EXTRA_NOTIFICATION_ID, 0);
            if (action.equalsIgnoreCase(ACTION_FROM_NOTIFICATION)) {
                mIsServiceStarted = true;
                setButtonsEnabledState();

            }
        }
    }

    /**
     * Method to generate OnGoingLocationNotification
     *
     * @param mcontext
     */
    public static void OnGoingLocationNotification(Context mcontext) {
        int mNotificationId;

        mNotificationId = (int) System.currentTimeMillis();

        //Broadcast receiver to handle the stop action
        Intent mstopReceive = new Intent(mcontext, NotificationHandler.class);
        mstopReceive.putExtra(EXTRA_NOTIFICATION_ID, mNotificationId);
        mstopReceive.setAction(ACTION_STOP);
        PendingIntent pendingIntentStopService = PendingIntent.getBroadcast(mcontext, (int) System.currentTimeMillis(), mstopReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                  new NotificationCompat.Builder(mcontext)
                            .setSound(alarmSound)
                            .setSmallIcon(R.drawable.ic_cast_off_light)
                            .setContentTitle("Location Service")
                            .addAction(R.drawable.ic_cancel, "Stop Service", pendingIntentStopService)
                            .setOngoing(true).setContentText("Running...");
        mBuilder.setAutoCancel(false);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mcontext, HomeActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        resultIntent.setAction(ACTION_FROM_NOTIFICATION);
        resultIntent.putExtra(EXTRA_NOTIFICATION_ID, mNotificationId);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mcontext, (int) System.currentTimeMillis(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                  (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(mNotificationId);

        Notification mNotification = mBuilder.build();
        mNotification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify(mNotificationId, mNotification);

    }

    private void cancelNotification(Context mContext, int mnotinotifId) {
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(mnotinotifId);
    }
}
