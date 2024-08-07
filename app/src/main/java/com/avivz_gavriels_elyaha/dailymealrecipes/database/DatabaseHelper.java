package com.avivz_gavriels_elyaha.dailymealrecipes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "GeminiResponse";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_FOOD_IMAGE = "foodImage";
    private static final String COLUMN_CALORIES = "calories";
    private static final String COLUMN_INGREDIENTS = "ingredients";
    private static final String COLUMN_INSTRUCTIONS = "instructions";
    private static final String COLUMN_DATE_OF_CREATION = "dateOfCreation";
    private static final String COLUMN_KOSHER = "kosher";
    private static final String COLUMN_QUICK = "quick";
    private static final String COLUMN_LOW_CALORIES = "lowCalories";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_FOOD_IMAGE + " TEXT, " +
                COLUMN_CALORIES + " TEXT, " +
                COLUMN_INGREDIENTS + " TEXT, " +
                COLUMN_INSTRUCTIONS + " TEXT, " +
                COLUMN_DATE_OF_CREATION + " INTEGER, " +
                COLUMN_KOSHER + " INTEGER, " +
                COLUMN_QUICK + " INTEGER, " +
                COLUMN_LOW_CALORIES + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertRecipe(Recipe response) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, response.getTitle());
        values.put(COLUMN_FOOD_IMAGE, response.getFoodImageUri());
        values.put(COLUMN_CALORIES, response.getCalories());
        values.put(COLUMN_INGREDIENTS, arrayToString(response.getIngredients()));
        values.put(COLUMN_INSTRUCTIONS, arrayToString(response.getInstructions()));
        values.put(COLUMN_DATE_OF_CREATION, response.getDateOfCreation().getTime());
        values.put(COLUMN_KOSHER, response.isKosher() ? 1 : 0);
        values.put(COLUMN_QUICK, response.isQuick() ? 1 : 0);
        values.put(COLUMN_LOW_CALORIES, response.isLowCalories() ? 1 : 0);

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public void updateRecipeImageUri(long id, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_IMAGE, imageUri);

        db.update(TABLE_NAME, values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_FOOD_IMAGE, COLUMN_CALORIES, COLUMN_INGREDIENTS, COLUMN_INSTRUCTIONS, COLUMN_DATE_OF_CREATION, COLUMN_KOSHER, COLUMN_QUICK, COLUMN_LOW_CALORIES},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        Recipe response = new Recipe(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CALORIES)),
                stringToArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS))),
                stringToArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS))),
                new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_OF_CREATION))),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KOSHER)) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUICK)) == 1,
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOW_CALORIES)) == 1
        );
        cursor.close();
        return response;
    }

    private String arrayToString(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s).append(",");
        }
        return sb.toString();
    }

    private String[] stringToArray(String str) {
        return str.split(",");
    }


    public ArrayList<Recipe> getRecipes(int numOfRecipes, boolean kosher, boolean quick, boolean lowCalories) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Recipe> recipes = new ArrayList<>();

        String selection = "1=1";
        List<String> selectionArgsList = new ArrayList<>();

        if (kosher) {
            selection += " AND " + COLUMN_KOSHER + "=?";
            selectionArgsList.add("1");
        }

        if (quick) {
            selection += " AND " + COLUMN_QUICK + "=?";
            selectionArgsList.add("1");
        }

        if (lowCalories) {
            selection += " AND " + COLUMN_LOW_CALORIES + "=?";
            selectionArgsList.add("1");
        }

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);

        Cursor cursor = db.query(TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                COLUMN_DATE_OF_CREATION + " DESC",
                String.valueOf(numOfRecipes));

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CALORIES)),
                        stringToArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS))),
                        stringToArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS))),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_OF_CREATION))),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KOSHER)) == 1,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUICK)) == 1,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOW_CALORIES)) == 1
                );
                recipes.add(recipe);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return recipes;
    }
}
