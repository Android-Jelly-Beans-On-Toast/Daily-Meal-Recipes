package com.avivz_gavriels_elyaha.dailymealrecipes;


import com.google.ai.client.generativeai.GenerativeModel;

public class GeminiUtils {
    GenerativeModel geminiModel;

    public GeminiUtils(String apiKey) {
        geminiModel = new GenerativeModel("gemini", "");
    }
}
