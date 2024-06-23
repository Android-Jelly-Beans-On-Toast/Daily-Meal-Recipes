package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        setTitle("Recipe from image");

        // retrieve captured image from extras as bitmap
        Bitmap capturedImage = getIntent().getParcelableExtra("capturedImage");

        ImageView capturedImageView = findViewById(R.id.capturedImage);
        capturedImageView.setImageBitmap(capturedImage);
        GeminiUtils gm = new GeminiUtils();
        gm.generateRecipeFromImage(capturedImage);
    }

    // take care of parallex effect on scroll down from image

}