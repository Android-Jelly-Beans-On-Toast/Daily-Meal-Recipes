package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiResponse {
    private final String title;
    private final Bitmap foodImage;
    private final String calories;
    private final String[] ingredients;
    private final String[] instructions;

    // Constructor to initialize the class fields
    public GeminiResponse(JSONObject jsonObject, Bitmap foodImage) {
        this.foodImage = foodImage;
        String tempTitle = "";
        String tempCalories = "";
        String[] tempIngredients = null;
        String[] tempInstructions = null;

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
        } catch (Exception e) {
            // Handle any parsing exceptions
            // TODO: add error message to user
        }

        // Assign the extracted values to the class fields
        this.title = tempTitle;
        this.calories = tempCalories;
        this.ingredients = tempIngredients;
        this.instructions = tempInstructions;
    }

    // Getters to access the private fields
    public String getTitle() {
        return title;
    }

    public Bitmap getFoodImage() {
        return foodImage;
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
}
