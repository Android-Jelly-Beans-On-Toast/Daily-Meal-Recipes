package com.avivz_gavriels_elyaha.dailymealrecipes.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.avivz_gavriels_elyaha.dailymealrecipes.R;

public class ErrorActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_error);

        setTitle("Error");

        // get error message and set it to the error message the user sees
        String errorMessage = getIntent().getStringExtra("errorMessage");
        if (errorMessage != null && !errorMessage.isEmpty()) {
            TextView errorMessageTextView = findViewById(R.id.errorContent);
            errorMessageTextView.setText(errorMessage);
        }

        AppCompatButton tryAgainButton = findViewById(R.id.recipe_error_try_again_button);
        AppCompatButton mainMenuButton = findViewById(R.id.recipe_error_main_menu_button);
        tryAgainButton.setOnClickListener(v -> {
            if (errorMessage != null && errorMessage.equals(getString(R.string.error_message_bad_image))) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
            } else {
                Intent intent = new Intent(ErrorActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
        mainMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(ErrorActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}