package com.nammashasane.utils;

public class Constants {
    // OpenRouter API
    public static final String OPENROUTER_API_KEY = "YOUR_OPENROUTER_API_KEY";
    public static final String OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1/chat/completions";
    public static final String OPENROUTER_MODEL = "mistralai/mistral-7b-instruct:free";

    // Firebase collections
    public static final String COLLECTION_INSCRIPTIONS = "inscriptions";
    public static final String COLLECTION_ALERTS = "preservation_alerts";

    // SharedPreferences
    public static final String PREFS_NAME = "namma_shasane_prefs";
    public static final String KEY_ONBOARDING_DONE = "onboarding_done";

    // Intent extras
    public static final String EXTRA_INSCRIPTION_ID = "inscription_id";
    public static final String EXTRA_INSCRIPTION_NAME = "inscription_name";
    public static final String EXTRA_INSCRIPTION_LAT = "inscription_lat";
    public static final String EXTRA_INSCRIPTION_LNG = "inscription_lng";
}
