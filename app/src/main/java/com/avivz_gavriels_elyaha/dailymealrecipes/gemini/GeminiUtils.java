package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.avivz_gavriels_elyaha.dailymealrecipes.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiUtils {
    private final GenerativeModel geminiModel;
    private final Context context;

    public GeminiUtils(Context context) {
        this.geminiModel = new GenerativeModel("gemini-1.5-flash", BuildConfig.API_KEY);
        this.context = context;
    }

    public void generateRecipeFromImage(Bitmap image, GeminiCallback callback) {
        SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        Content content = new Content.Builder()
                .addText("generate a recipe for the food in the image, add also a link for image from google image for this food, estimate the calories in your recipe. format your response like this: {<name of the food># <link to the image># <calories estimation># <ingredients># <steps>}")
                .addImage(image)
                .build();

        GenerativeModelFutures model = GenerativeModelFutures.from(this.geminiModel);
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                Log.d("GeminiResponse", "onSuccess!");
                String resultText = result.getText();
                if (resultText != null) {
                    Log.d("GeminiResponse", resultText);
                    callback.onSuccess(resultText);
                }
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                Log.e("GeminiResponse", "Failed to generate content", throwable);
                callback.onFailure(throwable);
            }
        }, executor);
    }

    public Bitmap searchImage(String query) {
        String accessKey = BuildConfig.UNSPLASH_KEY;
        String urlString = "https://api.unsplash.com/search/photos?query=" + query + "&client_id=" + accessKey;
        Bitmap bitmap = null;
        // send http request to unsplash api
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        Log.d("GeminiResponse", "searchImageUrl: " + url);
        return bitmap;
    }

}
