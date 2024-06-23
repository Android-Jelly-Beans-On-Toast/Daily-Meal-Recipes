package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;

import android.content.Context;

public class GeminiUtilsFactory {
    public static GeminiUtils createGeminiUtils(Context context) {
        return new GeminiUtils(context);
    }
}
