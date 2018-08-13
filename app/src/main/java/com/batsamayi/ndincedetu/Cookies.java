package com.batsamayi.ndincedetu;

import android.content.Context;
import android.content.SharedPreferences;

public class Cookies {
    private static Cookies mInstance = null;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    private static final String SHARED_PREF_NAME = "ndincede_tu_pref";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_NAME_FIRST = "user_name_first";
    private static final String KEY_USER_NAME_LAST = "user_name_last";
    private static final String KEY_USER_IS_VERIFIED = "user_is_verified";

    private Cookies(Context context) {
        prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized Cookies getInstance(Context context) {
        if (mInstance == null)
            mInstance = new Cookies(context);
        return mInstance;
    }

    public void userLogin(Users user) {
        editor = prefs.edit();
        editor.putInt(KEY_USER_ID, user.Id);
        editor.putString(KEY_USERNAME, user.Username);
        editor.putString(KEY_USER_NAME_FIRST, user.FirstName);
        editor.putString(KEY_USER_NAME_LAST, user.LastName);
        editor.putBoolean(KEY_USER_IS_VERIFIED, user.IsApproved);
        editor.apply();
    }

    public void userVerify(Users user) {
        editor = prefs.edit();
        editor.putBoolean(KEY_USER_IS_VERIFIED, user.IsApproved);
        editor.apply();
    }

    public Users userGetCurrent() {
        Users user = new Users();
        user.Id = prefs.getInt(KEY_USER_ID, -1);
        user.Username = prefs.getString(KEY_USERNAME, "");
        user.FirstName = prefs.getString(KEY_USER_NAME_FIRST, "");
        user.LastName = prefs.getString(KEY_USER_NAME_LAST, "");
        user.IsApproved = prefs.getBoolean(KEY_USER_IS_VERIFIED, false);
        return user;
    }

    public void logout() {
        new ServerConnector().userLogout(userGetCurrent());
        editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}