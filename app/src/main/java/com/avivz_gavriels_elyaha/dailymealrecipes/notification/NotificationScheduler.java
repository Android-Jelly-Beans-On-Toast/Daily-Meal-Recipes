package com.avivz_gavriels_elyaha.dailymealrecipes.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.avivz_gavriels_elyaha.dailymealrecipes.activities.MainActivity;
import com.avivz_gavriels_elyaha.dailymealrecipes.activities.RecipeActivity;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;

import java.util.Calendar;

public class NotificationScheduler {

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private final Context context;
    private static final String CHANNEL_ID = "TIMED_RECIPE_CHANNEL";
    private static final String CHANNEL_NAME = "Recipe Notifications";
    private static final int NOTIFICATION_ID = 694201337;

    public NotificationScheduler(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    public void updateNotificationScheduler() {
        SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (sp.getBoolean("notification", false)) {
            this.scheduleDailyNotification();
        } else {
            this.cancelDailyNotification();
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    public void createNotification(String title, String message, Recipe recipe) {
        // create an Intent for MainActivity
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // create an Intent for RecipeActivity
        Intent recipeIntent = new Intent(context, RecipeActivity.class);
        // add additional info to the RecipeActivity intent
        recipeIntent.putExtra("notificationRecipe", recipe);

        // use TaskStackBuilder to build the back stack and get the PendingIntent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(mainIntent);
        stackBuilder.addNextIntent(recipeIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).
                setContentIntent(pendingIntent).
                setAutoCancel(true);

        // show the notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }

        // update the notification scheduler
        NotificationScheduler notificationScheduler = new NotificationScheduler(context);
        notificationScheduler.updateNotificationScheduler();
    }

    public void scheduleDailyNotification() {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("timerHour", 12);
        int minute = sharedPreferences.getInt("timerMinute", 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // If the time is in the past, set it for the next day
        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Log.d("NotificationScheduler", "current time: " + Calendar.getInstance().getTime());
        Log.d("NotificationScheduler", "set time: " + calendar.getTime());

        Intent intent = new Intent(this.context, NotificationReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void cancelDailyNotification() {
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
