package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class RecipeErrorActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // image captured successfully, process the captured image here
                    if (result.getData() != null && result.getData().getExtras() != null) {
                        Bitmap capturedImage = (Bitmap) result.getData().getExtras().get("data");

                        // launch RecipeActivity and send the captured image there
                        Intent intent = new Intent(this, RecipeActivity.class);
                        intent.putExtra("capturedImage", capturedImage);
                        startActivity(intent);
                        finish();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_error);

        AppCompatButton tryAgainButton = findViewById(R.id.recipe_error_try_again_button);
        AppCompatButton mainMenuButton = findViewById(R.id.recipe_error_main_menu_button);
        tryAgainButton.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intent);
        });
        mainMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecipeErrorActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}