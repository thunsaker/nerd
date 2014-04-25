package com.thunsaker.nerd.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.thunsaker.nerd.R;

public class PreferencesHelper {
    public final static String PREFS_NAME = "NerdPrefs";

    // TwitterClient Prefs
    public static boolean getTwitterEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_twitter_enabled),
                false);
    }

    public static void setTwitterEnabled(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_twitter_enabled),
                newValue);
        prefsEditor.commit();
    }

    public static boolean getTwitterConnected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(
                context.getString(R.string.prefs_twitter_connected),
                false);
    }

    public static void setTwitterConnected(Context context, boolean newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(
                context.getString(R.string.prefs_twitter_connected),
                newValue);
        prefsEditor.commit();
    }

    public static String getTwitterToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_twitter_token),
                null);
    }

    public static void setTwitterToken(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_twitter_token),
                newValue);
        prefsEditor.commit();
    }

    public static String getTwitterSecret(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(
                context.getString(R.string.prefs_twitter_secret),
                null);
    }

    public static void setTwitterSecret(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString(
                context.getString(R.string.prefs_twitter_secret),
                newValue);
        prefsEditor.commit();
    }
}