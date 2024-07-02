package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
                        String imageUri = result.saveImageToGallery(image, RecipeActivity.this);
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