package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

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

        // retrieve captured image from extras as bitmap
        Bitmap capturedImage = getIntent().getParcelableExtra("capturedImage");

        ImageView capturedImageView = findViewById(R.id.capturedImage);
        capturedImageView.setImageBitmap(capturedImage);

        // get response from gemini
        GeminiUtils geminiUtils = GeminiUtilsFactory.createGeminiUtils();

        // show progress bar to the user
        progressBar = findViewById(R.id.loading_layout);
        showProgressBar(progressBar);
        geminiUtils.generateRecipeFromImage(capturedImage, new GeminiCallback() {
            @Override
            public void onSuccess(String result) {
                hideProgressBar(progressBar);
            }

            @Override
            public void onFailure(Throwable throwable) {
                hideProgressBar(progressBar);
            }
        });
    }

    // function to show progress bar to the user
    private void showProgressBar(LinearLayout progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(LinearLayout progressBar) {
        progressBar.setVisibility(View.INVISIBLE);
    }

}