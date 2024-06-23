package com.avivz_gavriels_elyaha.dailymealrecipes.gemini;

public interface GeminiCallback {
    void onSuccess(String result);

    void onFailure(Throwable throwable);
}
