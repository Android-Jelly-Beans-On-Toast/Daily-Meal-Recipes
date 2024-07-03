package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.avivz_gavriels_elyaha.dailymealrecipes.database.DatabaseHelper;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiCallback;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtils;
import com.avivz_gavriels_elyaha.dailymealrecipes.gemini.GeminiUtilsFactory;

import java.io.File;
import java.io.OutputStream;

public class RecipeActivity extends AppCompatActivity {

    private LinearLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        setTitle("Recipe from image");

        // show progress bar to the user
        progressBar = findViewById(R.id.loading_layout);
        showProgressBar(progressBar);

        // retrieve captured image from extras as bitmap
        Bitmap capturedImage = getIntent().getParcelableExtra("capturedImage");

        // retrieve recipe if it came from a notification or from scrolling menu in main activity
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        // get a gemini instance
        GeminiUtils geminiUtils = GeminiUtilsFactory.createGeminiUtils(this);


        // update UI based on existing recipe or generate a new recipe
        if (recipe != null) {
            updateRecipeDetails(recipe);
            hideProgressBar(progressBar);
        } else {

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
                    Intent intent = new Intent(RecipeActivity.this, RecipeErrorActivity.class);
                    startActivity(intent);
                    hideProgressBar(progressBar);
                    finish();
                }
            });
        }
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
        TextView ingredientsDetailsView = findViewById(R.id.ingredientsDetails);
        ingredientsDetailsView.setText(concatenateArray(recipe.getIngredients()));

        // recipe details
        TextView recipeDetailsView = findViewById(R.id.recipeDetails);
        recipeDetailsView.setText(concatenateArray(recipe.getInstructions()));

        // recipe title
        setTitle(recipe.getTitle());

        // recipe calories
        TextView recipeCaloriesView = findViewById(R.id.recipeCalories);
        recipeCaloriesView.setText(String.format("Estimated Calories: %s", recipe.getCalories()));
    }

    private String concatenateArray(String[] array) {
        if (array == null) {
            return "";
        }

        StringBuilder concatenated = new StringBuilder();
        for (String ingredient : array) {
            concatenated.append("• ").append(ingredient).append("\n");
        }
        return concatenated.toString().trim();
    }

    // function to show progress bar to the user
    private void showProgressBar(LinearLayout progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(LinearLayout progressBar) {
        progressBar.setVisibility(View.INVISIBLE);
    }

}