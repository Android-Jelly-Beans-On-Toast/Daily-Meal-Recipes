package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;


import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;

public interface GeminiCallback {
    void onSuccess(Recipe result);

    void onFailure(Throwable throwable);
}
