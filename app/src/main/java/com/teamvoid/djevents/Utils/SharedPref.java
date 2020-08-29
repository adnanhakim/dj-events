package com.teamvoid.djevents.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SharedPref {
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREFS = "SHARED_PREFS";

    public SharedPref(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void saveUserData(String name, String email, String year, String department, List<String> topics) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.EMAIL, email);
        editor.putString(Constants.NAME, name);
        editor.putString(Constants.YEAR, year);
        editor.putStringSet(Constants.TOPICS, new HashSet<>(topics));
        editor.putString(Constants.DEPARTMENT, department);
        editor.putBoolean(Constants.IS_COMMITTEE, false);
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

    public List<String> getTopics() {
        return new ArrayList<>(sharedPreferences.getStringSet(Constants.TOPICS, new HashSet<>()));
    }

    public void saveCommitteeData(String name, String email, String dpUrl, List<String> topics) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.EMAIL, email);
        editor.putString(Constants.NAME, name);
        editor.putString(Constants.DP_URL, dpUrl);
        editor.putStringSet(Constants.TOPICS, new HashSet<>(topics));
        editor.putBoolean(Constants.IS_COMMITTEE, true);
        editor.apply();
    }

    public boolean isCommittee() {
        return sharedPreferences.getBoolean(Constants.IS_COMMITTEE, false);
    }

    public String getCommitteeEmail() {
        return sharedPreferences.getString(Constants.EMAIL, null);
    }

    public String getCommitteeName() {
        return sharedPreferences.getString(Constants.NAME, null);
    }

    public String getCommitteeDpUrl() {
        return sharedPreferences.getString(Constants.DP_URL, null);
    }

    public void removeData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
