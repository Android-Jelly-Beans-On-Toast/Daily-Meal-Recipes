package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {
    // Register the ActivityResultLauncher
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // image captured successfully, process the captured image here
                    if (result.getData() != null && result.getData().getExtras() != null) {
                        Bitmap capturedImage = (Bitmap) result.getData().getExtras().get("data");

                        // launch RecipeActivity and send the captured image there
                        Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                        intent.putExtra("capturedImage", capturedImage);
                        startActivity(intent);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton cameraButton = findViewById(R.id.buttonCamera);
        // Check if the app has permission to access the camera
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
        }

        // Launch the camera activity
        cameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem menuItem1 = menu.add("Settings");
        MenuItem menuItem2 = menu.add("Exit");
        MainActivity that = this;
        menuItem1.setOnMenuItemClickListener(item -> {
            Log.d("hello", "setting clicked!");
            Intent intent = new Intent(that, SettingsActivity.class);
            startActivity(intent);
            return true;
        });
        return true;
    }
}
