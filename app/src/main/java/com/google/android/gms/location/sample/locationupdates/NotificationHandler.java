package com.google.android.gms.location.sample.locationupdates;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Grishma on 19/11/15.
 */
public class NotificationHandler extends BroadcastReceiver {
    private Context mContxt;
    private static final String TAG = NotificationHandler.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        mContxt = context;

        String action = intent.getAction(); //Get the button actions

        if (!TextUtils.isEmpty(action)) {
            int notifId = intent.getIntExtra(HomeActivity.EXTRA_NOTIFICATION_ID, 0);
            Log.v(TAG + "=======", "Notification id==" + notifId);
            Intent mintent = new Intent(context, LocationUpdateService.class);
            if (action.equalsIgnoreCase(HomeActivity.ACTION_STOP)) {
                Log.v(TAG + "=======", "Pressed YES");

                if (LocationUpdateService.isEnded) {
                    Log.v(TAG + "=======", "Service stopped.");
                    context.stopService(mintent);
                    Toast.makeText(context, "Service stopped.", Toast.LENGTH_SHORT).show();
                    cancelNotification(context, notifId);
                }
            }
//            else {
//                        long endDuration = mEvent.getEnd_time() - System.currentTimeMillis();
//                        Log.v(TAG + "=======", "End duration===" + endDuration);
//                        if (endDuration > 0) {
//                Log.v(TAG + "=======", "Event not expired");
//                context.startService(mintent);
//                        } else {
//                            context.stopService(mintent);
//                            Toast.makeText(context, R.string.lbl_event_expired, Toast.LENGTH_SHORT).show();
//                            Log.v(TAG + " Else=======", "  Event expired");
//                            cancelNotification(context, notifId);
//                        }
//            }
//                        }
//            }
//            else if (action.equalsIgnoreCase(Const.ACTION_DISMISS)) {
//                Log.v(TAG + "=======", "Pressed Dismissed");
//                context.stopService(mintent);
//                cancelNotification(context, notifId);
//            }
//                } else {
//                    Toast.makeText(context, R.string.event_has_been_deleted, Toast.LENGTH_SHORT).show();
//                    context.stopService(new Intent(context, BubblesService.class));
//                    cancelNotification(context, notifId);
//                }
//        }
        }

    }

    /**
     * Cancel the notification
     *
     * @param mContext
     * @param mnotinotifId
     */
    private void cancelNotification(Context mContext, int mnotinotifId) {
        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(mnotinotifId);
    }

}
