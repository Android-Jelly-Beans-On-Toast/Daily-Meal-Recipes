package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;

import android.graphics.Bitmap;

public class GeminiResponse {
    private final String title;
    private final Bitmap foodImage;
    private final int calories;
    private final String ingredients;
    private final String instructions;

    public GeminiResponse(String bruteRespnse, Bitmap foodImage) {
        String[] parts = bruteRespnse.split("#");
        this.title = parts[0];
        this.foodImage = foodImage;
        this.calories = Integer.parseInt(parts[1]);
        this.ingredients = parts[2];
        this.instructions = parts[3];
    }

    public int getCalories() {
        return calories;
    }

    public String getTitle() {
        return this.title;
    }

    public Bitmap getFoodImage() {
        return this.foodImage;
    }

    public String getIngredients() {
        return this.ingredients;
    }

    public String getInstructions() {
        return instructions;
    }
}
