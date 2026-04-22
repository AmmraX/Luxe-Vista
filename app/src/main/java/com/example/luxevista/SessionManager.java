package com.example.luxevista;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;

    Editor editor;

    Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LuxeVistaSession";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_USER_ID = "user_id";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
    /**
     * Create login session
     * */
    public void createLoginSession(String email, String username, int userId) {
        editor.putBoolean(IS_LOGIN, true);
        
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putInt(KEY_USER_ID, userId);
        
        editor.commit();
    }
    
    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
    
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        
        user.put(KEY_USER_ID, String.valueOf(pref.getInt(KEY_USER_ID, -1)));
        
        return user;
    }
    
    /**
     * Get user ID from session
     * @return user ID or -1 if not found
     */
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }
    
    /**
     * Clear session details
     * */
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
} 