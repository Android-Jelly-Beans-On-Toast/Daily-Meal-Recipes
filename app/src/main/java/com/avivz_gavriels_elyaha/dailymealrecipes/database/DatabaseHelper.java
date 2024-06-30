package com.avivz_gavriels_elyaha.dailymealrecipes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
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

    public long insertMeal(Meal response) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, response.getTitle());
        values.put(COLUMN_FOOD_IMAGE, bitmapToBase64(response.getFoodImage()));
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

    public Meal getMeal(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_FOOD_IMAGE, COLUMN_CALORIES, COLUMN_INGREDIENTS, COLUMN_INSTRUCTIONS, COLUMN_DATE_OF_CREATION, COLUMN_KOSHER, COLUMN_QUICK, COLUMN_LOW_CALORIES},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        Meal response = new Meal(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                base64ToBitmap(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE))),
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

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public Meal[] getMeals(int numberOfMeals, boolean kosher, boolean quick, boolean lowCalories) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Meal> meals = new ArrayList<>();

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
                String.valueOf(numberOfMeals));

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Meal meal = new Meal(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        base64ToBitmap(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_IMAGE))),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CALORIES)),
                        stringToArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENTS))),
                        stringToArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSTRUCTIONS))),
                        new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_OF_CREATION))),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_KOSHER)) == 1,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUICK)) == 1,
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOW_CALORIES)) == 1
                );
                meals.add(meal);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return meals.toArray(new Meal[0]);
    }

}
