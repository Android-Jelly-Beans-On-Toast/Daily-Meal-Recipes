package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.avivz_gavriels_elyaha.dailymealrecipes.database.Meal;
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

        Meal meal = (Meal) getIntent().getSerializableExtra("meal");

        if (meal != null) {
            displayMeal(meal);
        } else {
            // retrieve captured image from extras as bitmap
            Bitmap capturedImage = getIntent().getParcelableExtra("capturedImage");
            ImageView capturedImageView = findViewById(R.id.generatedImage);
            capturedImageView.setImageBitmap(capturedImage);

            // get a gemini instance
            GeminiUtils geminiUtils = GeminiUtilsFactory.createGeminiUtils(this);

            // show progress bar to the user
            progressBar = findViewById(R.id.loading_layout);
            showProgressBar(progressBar);

            // generate gemini response from captured image
            geminiUtils.generateRecipeFromImage(capturedImage, new GeminiCallback() {
                @Override
                public void onSuccess(Meal result) {
                    hideProgressBar(progressBar);
                    displayMeal(result);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    hideProgressBar(progressBar);
                }
            });
        }
    }

    private void displayMeal(Meal meal) {
        // food image
        ImageView generatedImageView = findViewById(R.id.generatedImage);
        generatedImageView.setImageBitmap(meal.getFoodImage());

        // captured image
        ImageView capturedImageView = findViewById(R.id.capturedImageButton);
        capturedImageView.setImageBitmap(meal.getFoodImage());

        // ingredients
        TextView ingredientsDetailsView = findViewById(R.id.ingredientsDetails);
        ingredientsDetailsView.setText(concatenateArray(meal.getIngredients()));

        // recipe details
        TextView recipeDetailsView = findViewById(R.id.recipeDetails);
        recipeDetailsView.setText(concatenateArray(meal.getInstructions()));

        // recipe title
        setTitle(meal.getTitle());

        // recipe calories
        TextView recipeCaloriesView = findViewById(R.id.recipeCalories);
        recipeCaloriesView.setText(String.format("Estimated Calories: %s", meal.getCalories()));
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
