package com.avivz_gavriels_elyaha.dailymealrecipes.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "received a notification");
        Intent recipeGenerationServiceIntent = new Intent(context, RecipeGenerationService.class);
        context.startService(recipeGenerationServiceIntent);
    }
}
