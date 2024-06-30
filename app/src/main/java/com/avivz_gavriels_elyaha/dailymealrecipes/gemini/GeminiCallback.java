package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;


import android.graphics.Bitmap;

import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;

public interface GeminiCallback {
    void onSuccess(Recipe result, Bitmap image);

    void onFailure(Throwable throwable);
}
