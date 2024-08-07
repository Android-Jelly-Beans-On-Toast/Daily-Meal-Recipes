package com.avivz_gavriels_elyaha.dailymealrecipes.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class Recipe implements Serializable {

    private long id;
    private final String title;
    private String foodImageUri;
    private final String calories;
    private final String[] ingredients;
    private final String[] instructions;
    private final Date dateOfCreation;
    private final boolean kosher;
    private final boolean quick;
    private final boolean lowCalories;

    // Constructor to initialize the class fields without id
    public Recipe(JSONObject jsonObject, Context context) {
        this(0, jsonObject, context); // default id to 0 for new entries
    }


    // Constructor to initialize the class fields with id
    public Recipe(long id, JSONObject jsonObject, Context context) {
        SharedPreferences sp = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        this.id = id;
        this.foodImageUri = "";
        String tempTitle = "";
        String tempCalories = "";
        String[] tempIngredients = null;
        String[] tempInstructions = null;
        Date tempDateOfCreation = new Date(); // default to current date
        boolean tempKosher = sp.getBoolean("kosher", false);
        boolean tempQuick = sp.getBoolean("quick", false);
        boolean tempLowCalories = sp.getBoolean("lowCalories", false);

        try {
            // Extract the title (if present)
            if (jsonObject.has("title")) {
                tempTitle = jsonObject.getString("title");
            }

            // Extract the calories (if present)
            if (jsonObject.has("calories")) {
                tempCalories = jsonObject.getString("calories");
            }

            // Extract the ingredients array (if present) and convert to String[]
            if (jsonObject.has("ingredients")) {
                JSONArray jsonArrayIngredients = jsonObject.getJSONArray("ingredients");
                tempIngredients = new String[jsonArrayIngredients.length()];
                for (int i = 0; i < jsonArrayIngredients.length(); i++) {
                    tempIngredients[i] = jsonArrayIngredients.getString(i);
                }
            }

            // Extract the instructions array (if present) and convert to String[]
            if (jsonObject.has("steps")) {
                JSONArray jsonArrayInstructions = jsonObject.getJSONArray("steps");
                tempInstructions = new String[jsonArrayInstructions.length()];
                for (int i = 0; i < jsonArrayInstructions.length(); i++) {
                    tempInstructions[i] = jsonArrayInstructions.getString(i);
                }
            }

            // Extract the date of creation (if present)
            if (jsonObject.has("dateOfCreation")) {
                tempDateOfCreation = new Date(jsonObject.getLong("dateOfCreation"));
            }

        } catch (Exception e) {
            // Handle any exceptions that may occur during the parsing process
            Log.d("Recipe", "Error parsing JSON");
        }

        // Assign the extracted values to the class fields
        this.title = tempTitle;
        this.calories = tempCalories;
        this.ingredients = tempIngredients;
        this.instructions = tempInstructions;
        this.dateOfCreation = tempDateOfCreation;
        this.kosher = tempKosher;
        this.quick = tempQuick;
        this.lowCalories = tempLowCalories;
    }

    public Recipe(long id, String title, String foodImageUri, String calories, String[] ingredients, String[] instructions,
                  Date dateOfCreation, boolean kosher, boolean quick, boolean lowCalories) {
        this.id = id;
        this.title = title;
        this.foodImageUri = foodImageUri;
        this.calories = calories;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.dateOfCreation = dateOfCreation;
        this.kosher = kosher;
        this.quick = quick;
        this.lowCalories = lowCalories;
    }

    // Getters to access the private fields
    public long getId() {
        return id;
    }

    public void setId(long newId) {
        this.id = newId;
    }

    public String getTitle() {
        return title;
    }

    public String getFoodImageUri() {
        return this.foodImageUri;
    }

    public void setFoodImageUri(String newValue) {
        this.foodImageUri = newValue;
    }

    public Bitmap getFoodImage(Context context) {
        return getBitmapFromUri(this.foodImageUri, context);
    }

    private Bitmap getBitmapFromUri(String imageUriString, Context context) {
        try {
            Uri imageUri = Uri.parse(imageUriString);
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            return null;
        }
    }

    public String getCalories() {
        return calories;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String[] getInstructions() {
        return instructions;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public boolean isKosher() {
        return kosher;
    }

    public boolean isQuick() {
        return quick;
    }

    public boolean isLowCalories() {
        return lowCalories;
    }

}
