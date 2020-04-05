package becker.andy.userapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import becker.andy.userapp.models.User;

public class PrefConfig {

    private  SharedPreferences sharedPreferences;
    private Context context;

    public PrefConfig(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences("pref_file", Context.MODE_PRIVATE);
    }

    public PrefConfig(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void writeLoginStatus(boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("login_status", status);
        editor.apply();
    }

    public void writeUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", user.getName());
        editor.putString("designation", user.getDesignation());
        editor.putString("district", user.getDistrict());
        editor.putString("email", user.getEmail());
        editor.putString("mobile", user.getMobile());
        editor.putString("access_token", user.getAccess_token());
        editor.apply();
    }

     public User readUser() {
        User user = new User();
        user.setName(sharedPreferences.getString("name", "none"));
        user.setDesignation(sharedPreferences.getString("designation", "none"));
        user.setDistrict(sharedPreferences.getString("district", "none"));
        user.setEmail(sharedPreferences.getString("email", "none"));
        user.setMobile(sharedPreferences.getString("mobile", "none"));
        user.setAccess_token(sharedPreferences.getString("access_token", "none"));
        return user;
    }

    public void writeToggleState(boolean clicked){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("toggle_state", clicked);
        editor.apply();
    }

    public boolean readToggleState(){
        return sharedPreferences.getBoolean("toggle_state", false);
    }



    public boolean readLoginStatus() {
        return sharedPreferences.getBoolean("login_status", false);
    }

    public void writeUserId(int userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.commit();
    }

    public int readUserId() {
        return sharedPreferences.getInt("userId", 0);
    }

    public void writeInsti(String insti) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("insti", insti);
        editor.commit();
    }

    public String readInsti() {
        return sharedPreferences.getString("insti", "none");
    }

    public void writeEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.commit();
    }

    public String readEmail() {
        return sharedPreferences.getString("email", "none");
    }
}
