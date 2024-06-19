package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // retrieve captured image from extras as bitmap
        Bitmap capturedImage = getIntent().getParcelableExtra("capturedImage");

        // TODO: display captured image in ImageView
//        ImageView capturedImageView = findViewById(R.id.capturedImageView);
//        capturedImageView.setImageBitmap(capturedImage);

    }
}