package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.avivz_gavriels_elyaha.dailymealrecipes.notification.NotificationScheduler;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            NotificationScheduler.updateNotificationScheduler(context);
        }
    }
}