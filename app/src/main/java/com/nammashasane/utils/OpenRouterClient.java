package com.nammashasane.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenRouterClient {

    private static final String TAG = "OpenRouterClient";
    private final OkHttpClient client;

    public interface OnTranslationResult {
        void onSuccess(String translation, String dynasty, String period, String giftLaw);
        void onError(String error);
    }

    public OpenRouterClient() {
        client = new OkHttpClient();
    }

    public void translateInscription(String inscriptionNotes, OnTranslationResult callback) {
        String prompt = buildPrompt(inscriptionNotes);

        try {
            JSONObject body = new JSONObject();
            body.put("model", Constants.OPENROUTER_MODEL);

            JSONArray messages = new JSONArray();
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", "You are an expert in ancient Karnataka inscriptions (Shasanas). " +
                    "You help decode and translate ancient Kannada/Sanskrit inscriptions into modern Kannada. " +
                    "Always respond in JSON format with keys: translation_kannada, dynasty, period, gift_or_law.");
            messages.put(systemMsg);

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", prompt);
            messages.put(userMsg);

            body.put("messages", messages);
            body.put("max_tokens", 500);

            Request request = new Request.Builder()
                    .url(Constants.OPENROUTER_BASE_URL)
                    .addHeader("Authorization", "Bearer " + Constants.OPENROUTER_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "https://nammashasane.app")
                    .addHeader("X-Title", "Namma Shasane")
                    .post(RequestBody.create(body.toString(),
                            MediaType.parse("application/json")))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "API call failed", e);
                    callback.onError("Network error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onError("API error: " + response.code());
                        return;
                    }
                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        String content = json.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        // Strip markdown code fences if any
                        content = content.replaceAll("```json", "").replaceAll("```", "").trim();
                        JSONObject result = new JSONObject(content);

                        String translation = result.optString("translation_kannada",
                                "ಈ ಶಾಸನದ ಅನುವಾದ ಲಭ್ಯವಿಲ್ಲ.");
                        String dynasty = result.optString("dynasty", "—");
                        String period = result.optString("period", "—");
                        String giftLaw = result.optString("gift_or_law", "—");

                        callback.onSuccess(translation, dynasty, period, giftLaw);
                    } catch (JSONException e) {
                        callback.onError("Parse error: " + e.getMessage());
                    }
                }
            });

        } catch (JSONException e) {
            callback.onError("Request build error: " + e.getMessage());
        }
    }

    private String buildPrompt(String notes) {
        return "Analyze this Karnataka inscription/Shasane. Based on the description or any visible text: \n\n"
                + "\"" + notes + "\"\n\n"
                + "Provide a JSON response with:\n"
                + "- translation_kannada: Modern Kannada translation or interpretation (2-4 sentences)\n"
                + "- dynasty: The ruling dynasty (e.g., Rashtrakutas, Hoysalas, Gangas, Chalukyas, etc.)\n"
                + "- period: Approximate century or year range\n"
                + "- gift_or_law: What gift, land grant, or law was recorded in this inscription\n\n"
                + "If information is uncertain, provide a historically plausible answer based on Karnataka history. "
                + "Respond ONLY with the JSON object, no other text.";
    }
}
