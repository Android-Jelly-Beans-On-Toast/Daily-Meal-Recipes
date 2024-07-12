package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.avivz_gavriels_elyaha.dailymealrecipes.BuildConfig;
import com.avivz_gavriels_elyaha.dailymealrecipes.R;
import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.InputStream;
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


    private String generatePromptForImage() {
        SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String options = "";
        String kosher = this.context.getResources().getString(R.string.kosher);
        String quick = this.context.getResources().getString(R.string.quick);
        String lowCalories = this.context.getResources().getString(R.string.low_calories);
        String geminiPrompt = this.context.getResources().getString(R.string.promptForGeminiImage);
        if (sp.getBoolean("kosher", false))
            options += kosher + ", ";
        if (sp.getBoolean("quick", false))
            options += quick + " ";
        if (sp.getBoolean("lowCalories", false))
            options += "and " + lowCalories + " ";
        return String.format(geminiPrompt, options);
    }

    private String generatePromptForText(String iWantToEatText) {
        SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String options = "";
        String kosher = this.context.getResources().getString(R.string.kosher);
        String quick = this.context.getResources().getString(R.string.quick);
        String lowCalories = this.context.getResources().getString(R.string.low_calories);
        String geminiPrompt = this.context.getResources().getString(R.string.promptForGeminiText);
        if (sp.getBoolean("kosher", false))
            options += kosher + ", ";
        if (sp.getBoolean("quick", false))
            options += quick + " ";
        if (sp.getBoolean("lowCalories", false))
            options += lowCalories + " ";
        return String.format(geminiPrompt, options, iWantToEatText);
    }

    private String generatePromptForNotification() {
        SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        int hour = sp.getInt("timerHour", 12);
        int minute = sp.getInt("timerMinute", 0);
        String options = "";
        String kosher = this.context.getResources().getString(R.string.kosher);
        String quick = this.context.getResources().getString(R.string.quick);
        String lowCalories = this.context.getResources().getString(R.string.low_calories);
        String time = hour + ":" + minute;
        String geminiPrompt = this.context.getResources().getString(R.string.promptForGeminiNotification);
        if (sp.getBoolean("kosher", false))
            options += kosher + ", ";
        if (sp.getBoolean("quick", false))
            options += quick + " ";
        if (sp.getBoolean("lowCalories", false))
            options += lowCalories + " ";
        return String.format(geminiPrompt, options, time);
    }

    public void generateRecipeFromImage(Bitmap image, GeminiCallback callback) {
        Content content = new Content.Builder()
                .addText(generatePromptForImage())
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
                    try {
                        // Parse the JSON response string
                        JSONObject responseJson = new JSONObject(resultText.substring(8, resultText.length() - 4));

                        searchImage(responseJson.getString("title"), new searchImageCallback() {
                            @Override
                            public void onImageFetched(Bitmap bitmap) {
                                // generate response
                                Recipe recipe = new Recipe(responseJson, context);
                                callback.onSuccess(recipe, bitmap);
                            }

                            @Override
                            public void onError(Exception e) {
                                callback.onFailure(e);
                            }
                        });
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                Log.e("GeminiResponse", "Failed to generate content", throwable);
                callback.onFailure(throwable);
            }
        }, executor);
    }

    public void generateRecipeFromText(String iWantToEatText, GeminiCallback callback) {
        Content content = new Content.Builder()
                .addText(generatePromptForText(iWantToEatText))
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
                    try {
                        // Parse the JSON response string
                        JSONObject responseJson = new JSONObject(resultText.substring(8, resultText.length() - 4));

                        searchImage(responseJson.getString("title"), new searchImageCallback() {
                            @Override
                            public void onImageFetched(Bitmap bitmap) {
                                // generate response
                                Recipe recipe = new Recipe(responseJson, context);
                                callback.onSuccess(recipe, bitmap);
                            }

                            @Override
                            public void onError(Exception e) {
                                callback.onFailure(e);
                            }
                        });
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                Log.e("GeminiResponse", "Failed to generate content", throwable);
                callback.onFailure(throwable);
            }
        }, executor);
    }

    public void generateRecipeForNotification(GeminiCallback callback) {
        Log.d("GeminiUtils", "Starting generateRecipeForNotification");

        Content content = new Content.Builder()
                .addText(generatePromptForNotification())
                .build();

        GenerativeModelFutures model = GenerativeModelFutures.from(this.geminiModel);
        Executor executor = Executors.newSingleThreadExecutor();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                Log.d("GeminiUtils", "onSuccess!");
                String resultText = result.getText();
                if (resultText != null) {
                    Log.d("GeminiUtils", resultText);
                    try {
                        // Parse the JSON response string
                        JSONObject responseJson = new JSONObject(resultText.substring(8, resultText.length() - 4));

                        searchImage(responseJson.getString("title"), new searchImageCallback() {
                            @Override
                            public void onImageFetched(Bitmap bitmap) {
                                // generate response
                                Recipe recipe = new Recipe(responseJson, context);
                                callback.onSuccess(recipe, bitmap);
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("GeminiUtils", "Image fetch error", e);
                                callback.onFailure(e);
                            }
                        });
                    } catch (Exception e) {
                        Log.e("GeminiUtils", "JSON parse error", e);
                        callback.onFailure(e);
                    }
                } else {
                    Log.e("GeminiUtils", "Result text is null");
                    callback.onFailure(new Exception("Result text is null"));
                }
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                Log.e("GeminiUtils", "Failed to generate content", throwable);
                callback.onFailure(throwable);
            }
        }, executor);
    }


    public interface searchImageCallback {
        void onImageFetched(Bitmap bitmap);

        void onError(Exception e);
    }

    private void searchImage(String query, searchImageCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String apiKey = BuildConfig.GOOGLE_API_KEY;
            String cx = BuildConfig.GOOGLE_CX;

            String urlString = "https://www.googleapis.com/customsearch/v1?q=" + query + "&cx=" + cx + "&key=" + apiKey + "&searchType=image&num=1";
            Bitmap bitmap = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder result = new StringBuilder();
                int read;
                byte[] buffer = new byte[1024];
                while ((read = inputStream.read(buffer)) != -1) {
                    result.append(new String(buffer, 0, read));
                }

                JsonObject jsonObject = JsonParser.parseString(result.toString()).getAsJsonObject();
                JsonArray items = jsonObject.getAsJsonArray("items");
                String imageUrl = "";
                if (!items.isEmpty()) {
                    JsonObject firstItem = items.get(0).getAsJsonObject();
                    imageUrl = firstItem.get("link").getAsString();
                }
                if (!imageUrl.isEmpty()) {
                    InputStream imageStream = new URL(imageUrl).openStream();
                    bitmap = BitmapFactory.decodeStream(imageStream);
                }

                Bitmap finalBitmap = bitmap;
                // run callback on the main thread
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    if (finalBitmap != null) {
                        callback.onImageFetched(finalBitmap);
                    } else {
                        callback.onError(new Exception("Unable to fetch image"));
                    }
                });
            } catch (Exception e) {
                // run callback on the main thread
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> callback.onError(e));
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        });
    }

}
