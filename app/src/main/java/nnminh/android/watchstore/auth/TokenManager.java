package nnminh.android.watchstore.auth;

import nnminh.android.watchstore.models.User;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class TokenManager {
    private static final String PREF_NAME = "prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER = "user";
    private static final Gson gson = new Gson();
    private static TokenManager instance;
    private SharedPreferences prefs;

    private TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void deleteToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    // Save user info
    public void saveUser(User user) {
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply();
    }

    // Get user info (returns null if not found)
    public User getUser() {
        String json = prefs.getString(KEY_USER, null);
        if (json != null) {
            return gson.fromJson(json, User.class);
        }
        return null;
    }

    // Optional: clear user data on logout
    public void deleteUser() {
        prefs.edit().remove(KEY_USER).apply();
    }

    // Helper to check login status
    public boolean isLoggedIn() {
        String token = getToken();
        if (token == null || token.isEmpty()) {
            return false;
        }
        if (isTokenExpired(token)) {
            deleteToken();
            deleteUser();
            return false;
        }
        return true;
    }

    private boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return true; // malformed token

            String payloadJson = new String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE | android.util.Base64.NO_PADDING | android.util.Base64.NO_WRAP));
            com.google.gson.JsonObject payload = gson.fromJson(payloadJson, com.google.gson.JsonObject.class);

            if (!payload.has("exp")) return true;

            long exp = payload.get("exp").getAsLong();
            long now = System.currentTimeMillis() / 1000;

            return now >= exp;
        } catch (Exception e) {
            return true; // On any error, treat as expired
        }
    }
}