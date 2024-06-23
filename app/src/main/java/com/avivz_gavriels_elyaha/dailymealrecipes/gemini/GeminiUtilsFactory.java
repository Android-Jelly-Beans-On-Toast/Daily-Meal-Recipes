package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;

import android.app.Activity;

public class GeminiUtilsFactory {
    public static GeminiUtils createGeminiUtils(Activity activity) {
        return new GeminiUtils(activity);
    }
}
