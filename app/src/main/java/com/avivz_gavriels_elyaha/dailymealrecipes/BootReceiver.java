package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.avivz_gavriels_elyaha.dailymealrecipes.activities.SettingsActivity;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Set the alarm here
            SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            if (sp.getBoolean("notification", false)) {
                SettingsActivity settingsActivity = new SettingsActivity();
                settingsActivity.setDailyAlarm();
            }
        }
    }
}