package com.avivz_gavriels_elyaha.dailymealrecipes.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class Scheduler {

    public static void scheduleDailyService(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RecipeGenerationService.class);
        SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (!sp.getBoolean("notification", false))
            return;
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int hour = sp.getInt("timerHour", 9);
        int minute = sp.getInt("timerMinute", 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);  // Set the desired hour
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long startTime = calendar.getTimeInMillis();
        if (startTime < System.currentTimeMillis()) {
            startTime += AlarmManager.INTERVAL_DAY;
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
