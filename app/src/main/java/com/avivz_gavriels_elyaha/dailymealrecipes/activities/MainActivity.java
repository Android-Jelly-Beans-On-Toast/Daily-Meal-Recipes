package com.avivz_gavriels_elyaha.dailymealrecipes.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avivz_gavriels_elyaha.dailymealrecipes.ConnectivityReceiver;
import com.avivz_gavriels_elyaha.dailymealrecipes.R;
import com.avivz_gavriels_elyaha.dailymealrecipes.RecipeAdapter;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.DatabaseHelper;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;
import com.avivz_gavriels_elyaha.dailymealrecipes.notification.NotificationScheduler;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnItemClickListener {

    private SharedPreferences sp;
    ArrayList<Recipe> previousMealsRecipeList;
    ArrayList<Recipe> criteriaMealsRecipeList;

    private final ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    private BroadcastReceiver noInternetReceiver;
    private BroadcastReceiver internetRestoredReceiver;
    private AlertDialog noInternetDialog;
    private boolean isSearchSubmitted = false;

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

        // LocalBroadcastManager receiver
        noInternetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                showNoInternetDialog();
            }
        };

        internetRestoredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dismissNoInternetDialog();
            }
        };

        // notification scheduler
        NotificationScheduler.updateNotificationScheduler(this);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query != null && !isSearchSubmitted) {
                    isSearchSubmitted = true;  // prevent multiple submissions


                    // launch RecipeActivity and send the search query there
                    Intent intent = new Intent(MainActivity.this, RecipeActivity.class);
                    intent.putExtra("iWantToEatText", query);
                    startActivity(intent);

                    // hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                    searchView.postDelayed(() -> isSearchSubmitted = false, 500);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // take care of horizontal scroll view
        updateRecyclerViews();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        try (DatabaseHelper database = new DatabaseHelper(this)) {
            // update the adapters with new data if settings were changed
            updateRecipes(database);
        }
    }

    private int getNumRecentRecipes() {
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        String recentRecipesSp = sp.getString("recentHistory", "5");
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

        NotificationScheduler.updateNotificationScheduler(this);

        // register connectivity receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);

        // register no internet receiver to show dialog when there is no internet connection
        LocalBroadcastManager.getInstance(this).registerReceiver(noInternetReceiver, new IntentFilter("NO_INTERNET_CONNECTION"));
        LocalBroadcastManager.getInstance(this).registerReceiver(internetRestoredReceiver, new IntentFilter("INTERNET_CONNECTION_RESTORED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister connectivity receiver
        unregisterReceiver(connectivityReceiver);
        // unregister no internet receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(noInternetReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(internetRestoredReceiver);
    }

    private void updateRecyclerViews() {
        RecyclerView previousMealsRecyclerView = findViewById(R.id.previousMealsRecyclerView);
        RecyclerView criteriaMealsRecyclerView = findViewById(R.id.criteriaMealsRecyclerView);
        previousMealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        criteriaMealsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        previousMealsRecyclerView.setNestedScrollingEnabled(false);
        criteriaMealsRecyclerView.setNestedScrollingEnabled(false);

        // get recipe lists with data from database and add to adapters
        try (DatabaseHelper database = new DatabaseHelper(this)) {
            // get info from settings in shared preferences
            updateRecipes(database);

            // create and set adapters
            RecipeAdapter previousMealsRecipeAdapter = new RecipeAdapter(this, previousMealsRecipeList, this);
            RecipeAdapter criteriaMealsRecipeAdapter = new RecipeAdapter(this, criteriaMealsRecipeList, this);
            previousMealsRecyclerView.setAdapter(previousMealsRecipeAdapter);
            criteriaMealsRecyclerView.setAdapter(criteriaMealsRecipeAdapter);
        }
    }

    private void updateRecipes(DatabaseHelper database) {
        sp = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isKosher = sp.getBoolean("kosher", false);
        boolean isQuick = sp.getBoolean("quick", false);
        boolean isLowCalories = sp.getBoolean("lowCalories", false);
        int numRecentRecipes = getNumRecentRecipes();

        // get recipes from database
        previousMealsRecipeList = database.getRecipes(numRecentRecipes, false, false, false);
        criteriaMealsRecipeList = database.getRecipes(numRecentRecipes, isKosher, isQuick, isLowCalories);

        // update recentMealsEmpty TextView's text
        TextView recentMealsEmpty = findViewById(R.id.recentMealsEmpty);
        if (previousMealsRecipeList.isEmpty()) {
            recentMealsEmpty.setVisibility(View.VISIBLE);
        } else {
            recentMealsEmpty.setVisibility(View.INVISIBLE);
        }

        // update criteriaMealsEmpty TextView's text
        TextView criteriaMealsEmpty = findViewById(R.id.criteriaMealsEmpty);
        if (criteriaMealsRecipeList.isEmpty()) {
            criteriaMealsEmpty.setVisibility(View.VISIBLE);
        } else {
            criteriaMealsEmpty.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem settingsMenuItem = menu.add("Settings");
        MenuItem exitMenuItem = menu.add("Exit");
        MainActivity that = this;
        settingsMenuItem.setOnMenuItemClickListener(item -> {
            Log.d("hello", "setting clicked!");
            Intent intent = new Intent(that, SettingsActivity.class);
            startActivity(intent);
            return true;
        });
        exitMenuItem.setOnMenuItemClickListener(item -> {
            finish();
            System.exit(0);
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

    private void showNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }

        noInternetDialog = new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("The app needs internet connection to work properly.\n\nWould you like to go to the settings to enable internet?")
                .setPositiveButton("Settings", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)))
                .setNegativeButton("No", null)
                .setCancelable(false)
                .show();
    }

    private void dismissNoInternetDialog() {
        if (noInternetDialog != null && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }
}
