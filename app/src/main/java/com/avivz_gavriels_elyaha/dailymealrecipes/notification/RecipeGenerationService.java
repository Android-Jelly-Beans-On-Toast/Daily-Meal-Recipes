package com.avivz_gavriels_elyaha.dailymealrecipes.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.avivz_gavriels_elyaha.dailymealrecipes.database.Meal;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiCallback;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtils;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtilsFactory;

import java.util.Calendar;
import java.util.Date;

public class RecipeGenerationService extends Service {
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
        String capturedImage = intent.getStringExtra("capturedImage");
        GeminiUtils geminiUtils = GeminiUtilsFactory.createGeminiUtils(this);
        geminiUtils.generateRecipeFromText(this.getMealTypeByHour(new Date()), new GeminiCallback() {
            @Override
            public void onSuccess(Meal result) {
                notifyUser(result);
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

    private void notifyUser(Meal result) {
        // Notify the user with the result (e.g., using a Notification)
    }
}
