package com.avivz_gavriels_elyaha.dailymealrecipes.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.avivz_gavriels_elyaha.dailymealrecipes.R;
import com.avivz_gavriels_elyaha.dailymealrecipes.RecipeActivity;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiCallback;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtils;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtilsFactory;

import java.util.Calendar;
import java.util.Date;

public class RecipeGenerationService extends Service {
    private static final String CHANNEL_ID = "RecipeNotificationChannel";
    private static final int NOTIFICATION_ID = 1;

    public String getMealTypeByHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 6 && hour < 12)
            return "breakfast";
        if (hour >= 12 && hour < 18)
            return "lunch";
        return "dinner";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GeminiUtils geminiUtils = GeminiUtilsFactory.createGeminiUtils(this);
        geminiUtils.generateRecipeFromText(this.getMealTypeByHour(new Date()), new GeminiCallback() {
            @Override
            public void onSuccess(Recipe result, Bitmap image) {
                notifyUser(result);
                // TODO: maybe save image here?
                stopSelf();
            }

            @Override
            public void onFailure(Throwable throwable) {
                stopSelf();
            }
        });
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyUser(Recipe result) {
        createNotificationChannel();

        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("meal", (CharSequence) result);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap foodImage = result.getFoodImage(this); // Assuming getFoodImage returns a Bitmap

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo) // Your small icon resource
                .setContentTitle(result.getTitle())
                .setContentText("Click to see the recipe details")
                .setLargeIcon(foodImage)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(foodImage))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        CharSequence name = "Recipe Notifications";
        String description = "Channel for recipe notifications";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}

