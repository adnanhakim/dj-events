package com.teamvoid.djevents.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREFS = "SHARED_PREFS";

    public SharedPref(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void saveLoginData(String name, String email, String year, String department) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.EMAIL, email);
        editor.putString(Constants.NAME, name);
        editor.putString(Constants.YEAR, year);
        editor.putString(Constants.DEPARTMENT, department);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(Constants.EMAIL, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(Constants.NAME, null);
    }

    public String getUserYear() {
        return sharedPreferences.getString(Constants.YEAR, null);
    }

    public String getUserDepartment() {
        return sharedPreferences.getString(Constants.DEPARTMENT, null);
    }

    public void removeLoginData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.EMAIL);
        editor.remove(Constants.NAME);
        editor.remove(Constants.YEAR);
        editor.remove(Constants.DEPARTMENT);
        editor.apply();
    }
}
