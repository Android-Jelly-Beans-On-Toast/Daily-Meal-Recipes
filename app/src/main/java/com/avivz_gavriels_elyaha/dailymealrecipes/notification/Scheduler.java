package com.avivz_gavriels_elyaha.dailymealrecipes.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

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
        Log.d("mylog", "in Scheduler!");

        try {
            Log.d("mylog", "in try block!");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(context, "Exact alarms are not allowed. Please grant the permission in settings.", Toast.LENGTH_LONG).show();
                    Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    context.startActivity(settingsIntent);
                    return;
                }
            }
            Log.d("mylog", "end try block! start time:" + hour + ":" + minute);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
        } catch (SecurityException e) {
            Log.d("mylog", "Permission to schedule exact alarms is required.");
            e.printStackTrace();
            Toast.makeText(context, "Permission to schedule exact alarms is required.", Toast.LENGTH_LONG).show();
        }
    }
}
