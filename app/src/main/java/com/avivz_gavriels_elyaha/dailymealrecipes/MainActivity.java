package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avivz_gavriels_elyaha.dailymealrecipes.database.DatabaseHelper;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;

import java.util.ArrayList;

import com.avivz_gavriels_elyaha.dailymealrecipes.notification.Scheduler;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnItemClickListener {

    private SharedPreferences sp;
    // await the camera result and if its successful open the Recipe Activity
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
        Scheduler.scheduleDailyService(this);
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

        // simulate a click on the SearchView when the the search bar image is clicked to open the search view
        ImageView backgroundImage = findViewById(R.id.searchBarImage);
        SearchView searchView = findViewById(R.id.searchBarView);

        backgroundImage.setOnClickListener(v -> {
            // Simulate click on the SearchView
            searchView.requestFocus();
            searchView.setIconified(false);
        });

        // take care of horizontal scroll view
        updateRecyclerViews();
    }

    private int getNumRecentRecipes() {
        String recentRecipesSp = sp.getString("recipeHistory", "5");
        if (recentRecipesSp.equals("All")) {
            return -1;
        }
        return Integer.parseInt(recentRecipesSp);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // update the adapters with new data
        updateRecyclerViews();
    }

    private void updateRecyclerViews() {
        RecyclerView previousMealsRecyclerView = findViewById(R.id.previousMealsRecyclerView);
        RecyclerView criteriaMealsRecyclerView = findViewById(R.id.criteriaMealsRecyclerView);
        previousMealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        criteriaMealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // get recipe lists with data from database and add to adapters
        try (DatabaseHelper database = new DatabaseHelper(this)) {
            // get info from settings in shared preferences
            sp = getSharedPreferences("settings", MODE_PRIVATE);
            boolean isKosher = sp.getBoolean("kosher", false);
            boolean isQuick = sp.getBoolean("quick", false);
            boolean isLowCalories = sp.getBoolean("lowCalories", false);
            int numRecentRecipes = getNumRecentRecipes();

            // get recipes from database
            ArrayList<Recipe> previousMealsRecipeList = database.getRecipes(numRecentRecipes, false, false, false);
            ArrayList<Recipe> criteriaMealsRecipeList = database.getRecipes(numRecentRecipes, isKosher, isQuick, isLowCalories);

            // create and set adapters
            RecipeAdapter previousMealsRecipeAdapter = new RecipeAdapter(this, previousMealsRecipeList, this);
            RecipeAdapter criteriaMealsRecipeAdapter = new RecipeAdapter(this, criteriaMealsRecipeList, this);
            previousMealsRecyclerView.setAdapter(previousMealsRecipeAdapter);
            criteriaMealsRecyclerView.setAdapter(criteriaMealsRecipeAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem menuItem1 = menu.add("Settings");
        // TODO: do something with menuItem2
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

    @Override
    public void onItemClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra("recipe", recipe);
        startActivity(intent);
    }
}
