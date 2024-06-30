package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;


import com.avivz_gavriels_elyaha.dailymealrecipes.database.Meal;

public interface GeminiCallback {
    void onSuccess(Meal result);

    void onFailure(Throwable throwable);
}
