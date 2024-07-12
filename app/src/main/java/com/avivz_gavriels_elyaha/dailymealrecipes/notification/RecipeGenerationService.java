package com.avivz_gavriels_elyaha.dailymealrecipes.notification;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.avivz_gavriels_elyaha.dailymealrecipes.R;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.DatabaseHelper;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiCallback;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtils;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtilsFactory;

import java.io.File;
import java.io.OutputStream;

public class RecipeGenerationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Perform the gemini prompt calculation in a background thread
        Log.d("MyRecipeGenerationService", "Starting recipe generation");
        new Thread(() -> {
            try {
                GeminiUtils geminiUtils = GeminiUtilsFactory.createGeminiUtils(this);

                geminiUtils.generateRecipeForNotification(new GeminiCallback() {
                    @Override
                    public void onSuccess(Recipe result, Bitmap image) {
                        Log.d("MyRecipeGenerationService", "Recipe generation success");
                        sendNotification("Here's your recipe for today!", result.getTitle(), result, image);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MyRecipeGenerationService", "Recipe generation failed", throwable);
                        sendNotification("Recipe generation error", "Sorry, we couldn't generate a recipe for you today.", null, null);
                    }
                });
            } catch (Exception e) {
                Log.e("MyRecipeGenerationService", "Error performing calculation", e);
            } finally {
                stopSelf();
            }
        }).start();

        return START_STICKY;
    }


    private void sendNotification(String title, String message, Recipe recipe, Bitmap image) {
        // insert this recipe into the database
        if (recipe != null && image != null) {
            try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
                long id = databaseHelper.insertRecipe(recipe);
                recipe.setId(id);
                String imageUri = saveImageToGallery(image, this, recipe.getId());
                recipe.setFoodImageUri(imageUri);
                databaseHelper.updateRecipeImageUri(id, imageUri);
            }
        }

        // send notification
        NotificationScheduler notificationScheduler = new NotificationScheduler(this);
        notificationScheduler.createNotification(title, message, recipe);
    }

    private String saveImageToGallery(Bitmap bitmap, Context context, long id) {
        String imageFileName = "food_image_" + id + ".jpg";
        String appName = context.getResources().getString(R.string.app_name_no_spaces);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + appName);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DailyMealRecipes");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            String savedImagePath = imageFile.getAbsolutePath();
            try {
                assert uri != null;
                OutputStream fOut = getContentResolver().openOutputStream(uri);
                assert fOut != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                return null;
            }

            // Add the image to the system gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(savedImagePath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);

            return contentUri.toString();
        }
        return null;
    }
}

