package com.vectorcoder.androidecommerce.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vectorcoder.androidecommerce.activities.MainActivity;
import com.vectorcoder.androidecommerce.activities.SplashScreen;
import com.vectorcoder.androidecommerce.app.MyAppPrefsManager;
import com.vectorcoder.androidecommerce.utils.NotificationHelper;
import com.vectorcoder.androidecommerce.utils.NotificationScheduler;
import com.vectorcoder.androidecommerce.utils.Utilities;


/**
 * AlarmReceiver receives the Broadcast Intent
 */

public class AlarmReceiver extends BroadcastReceiver {
    
    
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        
                NotificationScheduler.setReminder(context, AlarmReceiver.class);
            }
        }


        Intent notificationIntent = new Intent(context, SplashScreen.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(context);


        //Trigger the notification
        NotificationHelper.showNewNotification
                (
                    context,
                    notificationIntent,
                    myAppPrefsManager.getLocalNotificationsTitle(),
                    myAppPrefsManager.getLocalNotificationsDescription(),
                    null
                );
        
    }
    
}

