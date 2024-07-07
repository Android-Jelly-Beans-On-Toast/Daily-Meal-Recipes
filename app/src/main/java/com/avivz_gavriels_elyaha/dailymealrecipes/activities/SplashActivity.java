package com.avivz_gavriels_elyaha.dailymealrecipes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.avivz_gavriels_elyaha.dailymealrecipes.R;

//TODO: maybe fix splash screen: https://developer.android.com/develop/ui/views/launch/splash-screen
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DURATION = 3000; // 3 sec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        //The logic of the splash screen - a delay and then the opening of the main activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); //Close the splash activity so it doesn't return if the user clicks the Back button
        }, SPLASH_SCREEN_DURATION);
    }
}
