package com.brcxzam.sedapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Token {

    private SharedPreferences sharedPref;

    public Token(Context ctx) {
        String PREFERENCE_FILE_KEY = "com.brcxzam.sedapp.preference_file_key";
        sharedPref = ctx.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    public void setStatus(boolean status){
        sharedPref.edit().putBoolean("status",status).apply();
    }

    public boolean getStatus(){
        return sharedPref.getBoolean("status",false);
    }

    public void setCargo(String cargo){
        sharedPref.edit().putString("cargo",cargo).apply();
    }

    public String getCargo(){
        return sharedPref.getString("cargo","");
    }

    public void setToken(String token){
        sharedPref.edit().putString("token",token).apply();
    }

    public String getToken() {
        return sharedPref.getString("token", "");
    }

    public void clearToken() {
        sharedPref.edit().clear().apply();
    }
}
