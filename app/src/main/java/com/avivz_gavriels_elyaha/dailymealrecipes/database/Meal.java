package com.avivz_gavriels_elyaha.dailymealrecipes.database;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class Meal {

    private final int id;
    private final String title;
    private final Bitmap foodImage;
    private final String calories;
    private final String[] ingredients;
    private final String[] instructions;
    private final Date dateOfCreation;
    private final boolean kosher;
    private final boolean quick;
    private final boolean lowCalories;

    // Constructor to initialize the class fields without id
    public Meal(JSONObject jsonObject, Bitmap foodImage) {
        this(0, jsonObject, foodImage); // default id to 0 for new entries
    }

    // Constructor to initialize the class fields with id
    public Meal(int id, JSONObject jsonObject, Bitmap foodImage) {
        this.id = id;
        this.foodImage = foodImage;
        String tempTitle = "";
        String tempCalories = "";
        String[] tempIngredients = null;
        String[] tempInstructions = null;
        Date tempDateOfCreation = new Date(); // default to current date
        boolean tempKosher = false;
        boolean tempQuick = false;
        boolean tempLowCalories = false;

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

            // Extract the kosher flag (if present)
            if (jsonObject.has("kosher")) {
                tempKosher = jsonObject.getBoolean("kosher");
            }

            // Extract the quick flag (if present)
            if (jsonObject.has("quick")) {
                tempQuick = jsonObject.getBoolean("quick");
            }

            // Extract the lowCalories flag (if present)
            if (jsonObject.has("lowCalories")) {
                tempLowCalories = jsonObject.getBoolean("lowCalories");
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
        this.dateOfCreation = tempDateOfCreation;
        this.kosher = tempKosher;
        this.quick = tempQuick;
        this.lowCalories = tempLowCalories;
    }

    public Meal(int id, String title, Bitmap foodImage, String calories, String[] ingredients, String[] instructions,
                Date dateOfCreation, boolean kosher, boolean quick, boolean lowCalories) {
        this.id = id;
        this.title = title;
        this.foodImage = foodImage;
        this.calories = calories;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.dateOfCreation = dateOfCreation;
        this.kosher = kosher;
        this.quick = quick;
        this.lowCalories = lowCalories;
    }

    // Getters to access the private fields
    public int getId() {
        return id;
    }

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
