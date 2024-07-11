package com.avivz_gavriels_elyaha.dailymealrecipes.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.avivz_gavriels_elyaha.dailymealrecipes.R;
import com.avivz_gavriels_elyaha.dailymealrecipes.NonScrollableListView;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.DatabaseHelper;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiCallback;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtils;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtilsFactory;

import java.io.File;
import java.io.OutputStream;
import java.net.UnknownHostException;

public class RecipeActivity extends AppCompatActivity {

    private LinearLayout progressBar;

    private String errorMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        setTitle("Loading Recipe...");

        // show progress bar to the user
        progressBar = findViewById(R.id.loading_layout);
        showProgressBar(progressBar);

        // retrieve captured image from extras as bitmap
        Bitmap capturedImage = getIntent().getParcelableExtra("capturedImage");

        // retrieve recipe if it came from a notification or from scrolling menu in main activity
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        // retrieve "i want to eat..." text from extras
        String iWantToEatText = getIntent().getStringExtra("iWantToEatText");

        // get a gemini instance
        GeminiUtils geminiUtils = GeminiUtilsFactory.createGeminiUtils(this);

        // notification recipe and image was sent
        Recipe notificationRecipe = (Recipe) getIntent().getSerializableExtra("notificationRecipe");

        if (notificationRecipe != null) {
            // update UI with recipe details
            updateRecipeDetails(notificationRecipe);
            hideProgressBar(progressBar);
            return;
        }

        // "i want to eat" text was sent from MainActivity
        if (iWantToEatText != null && !iWantToEatText.isEmpty()) {

            // generate recipe
            geminiUtils.generateRecipeFromText(iWantToEatText, new GeminiCallback() {
                @Override
                public void onSuccess(Recipe result, Bitmap image) {
                    // insert this recipe into the database
                    try (DatabaseHelper databaseHelper = new DatabaseHelper(RecipeActivity.this)) {
                        long id = databaseHelper.insertRecipe(result);
                        result.setId(id);
                        String imageUri = saveImageToGallery(image, RecipeActivity.this, result.getId());
                        result.setFoodImageUri(imageUri);
                        databaseHelper.updateRecipeImageUri(id, imageUri);

                    }
                    // update UI with recipe details
                    updateRecipeDetails(result);
                    hideProgressBar(progressBar);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    if (throwable.getCause() instanceof UnknownHostException) {
                        errorMessage = getString(R.string.error_message_no_internet);
                    } else {
                        errorMessage = getString(R.string.error_message_bad_query);
                    }
                    Intent intent = new Intent(RecipeActivity.this, ErrorActivity.class);
                    intent.putExtra("errorMessage", errorMessage);
                    startActivity(intent);
                    hideProgressBar(progressBar);
                    finish();
                }
            });
            return;
        }

        // captured image was sent from MainActivity
        if (capturedImage != null) {

            geminiUtils.generateRecipeFromImage(capturedImage, new GeminiCallback() {
                @Override
                public void onSuccess(Recipe result, Bitmap image) {
                    // insert this recipe into the database
                    try (DatabaseHelper databaseHelper = new DatabaseHelper(RecipeActivity.this)) {
                        long id = databaseHelper.insertRecipe(result);
                        result.setId(id);
                        String imageUri = saveImageToGallery(image, RecipeActivity.this, result.getId());
                        result.setFoodImageUri(imageUri);
                        databaseHelper.updateRecipeImageUri(id, imageUri);

                    }
                    // update UI with recipe details
                    updateRecipeDetails(result);
                    hideProgressBar(progressBar);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    if (throwable.getCause() instanceof UnknownHostException) {
                        errorMessage = getString(R.string.error_message_no_internet);
                    } else {
                        errorMessage = getString(R.string.error_message_bad_image);
                    }
                    Intent intent = new Intent(RecipeActivity.this, ErrorActivity.class);
                    intent.putExtra("errorMessage", errorMessage);
                    startActivity(intent);
                    hideProgressBar(progressBar);
                    finish();
                }
            });
            return;
        }

        // recipe was sent from MainActivity
        if (recipe != null) {
            updateRecipeDetails(recipe);
            hideProgressBar(progressBar);
            return;
        }

        // if this code is reached, an unexpected error occurred
        Intent intent = new Intent(RecipeActivity.this, ErrorActivity.class);
        errorMessage = getString(R.string.error_message_default);
        intent.putExtra("errorMessage", errorMessage);
        startActivity(intent);
        hideProgressBar(progressBar);
        finish();
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

    private void updateRecipeDetails(Recipe recipe) {
        // food image
        ImageView generatedImageView = findViewById(R.id.generatedImage);
        generatedImageView.setImageBitmap(recipe.getFoodImage(RecipeActivity.this));

        // ingredients
        NonScrollableListView ingredientsDetailsView = findViewById(R.id.ingredientsDetails);
        ArrayAdapter<String> ingredientsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipe.getIngredients());
        ingredientsDetailsView.setAdapter(ingredientsAdapter);

        // recipe details
        NonScrollableListView recipeDetailsView = findViewById(R.id.recipeDetails);
        ArrayAdapter<String> recipeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipe.getInstructions());
        recipeDetailsView.setAdapter(recipeAdapter);

        // recipe title
        TextView recipeTitle = findViewById(R.id.recipeTitle);
        recipeTitle.setText(recipe.getTitle());
        setTitle("Recipe");

        // recipe calories
        TextView recipeCaloriesView = findViewById(R.id.recipeCalories);
        recipeCaloriesView.setText(String.format("Estimated Calories: %s", recipe.getCalories()));
    }

    // function to show progress bar to the user
    private void showProgressBar(LinearLayout progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(LinearLayout progressBar) {
        progressBar.setVisibility(View.INVISIBLE);
    }

}