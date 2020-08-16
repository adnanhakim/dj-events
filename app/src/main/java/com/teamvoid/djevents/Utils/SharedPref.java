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

    private String getUserEmail() {
        return sharedPreferences.getString(Constants.EMAIL, null);
    }

    private String getUserName() {
        return sharedPreferences.getString(Constants.NAME, null);
    }

    private String getUserYear() {
        return sharedPreferences.getString(Constants.YEAR, null);
    }

    private String getUserDepartment() {
        return sharedPreferences.getString(Constants.DEPARTMENT, null);
    }
}
