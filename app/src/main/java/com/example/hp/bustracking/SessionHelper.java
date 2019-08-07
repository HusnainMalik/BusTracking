package com.example.hp.bustracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONObject;

public class SessionHelper {


    public static void createDriverLoginSession(Context context, Driver driver) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("is_Driver_logged_in", true)
                .putString("logged_Driver", new Gson().toJson(driver))
                .apply();
    }
    public static void logoutDriver(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove("is_Driver_logged_in")
                .remove("logged_Driver")
                .apply();

    }

    public static void createStudentLoginSession(Context context, JSONObject jobject) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("is_Student_logged_in", true)
                .putString("logged_Student", jobject.toString())
                .apply();
    }
    public static  void logout(Context context){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove("is_Student_logged_in")
                .remove("Student_object")
                .apply();
    }
    public static boolean isStudentLoggedIn(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean Studnet_logged =sharedPreferences.getBoolean("is_Student_logged_in",false);
        return Studnet_logged;
    }



    public static Driver getCurrentDriver(Context context) {
        String driverJson = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("logged_Driver", "");
        return new Gson().fromJson(driverJson, Driver.class);
    }

   /* public  static  void createUserLoginSession(Context context, User user){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean("is_User_logged_in",true)
                .putString("Logged_user",new Gson().toJson(user))
                .apply();

    }
    public static User getCurrentUser(Context context){
        String userJSon =PreferenceManager.getDefaultSharedPreferences(context)
                .getString("Logged_User","");
        return  new Gson().fromJson(userJSon,User.class);
    }*/



}
