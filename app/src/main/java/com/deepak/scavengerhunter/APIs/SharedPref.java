package com.deepak.scavengerhunter.APIs;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref {

    public static String DB_NAME = "pokerdb";



    public static void saveUserInfo(Context context,String userInfo){
        SharedPreferences sharedPreferences
                = context.getSharedPreferences(DB_NAME,
                MODE_PRIVATE);

        // Creating an Editor object
        // to edit(write to the file)
        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();

        myEdit.putString("userInfo",userInfo);

        myEdit.commit();
        myEdit.apply();


    }

    public static JSONObject getUserInfo(Context context){
        JSONObject jsonObject = null;
        // Retrieving the value using its keys
        // the file name must be same in both saving
        // and retrieving the data
        SharedPreferences sh
                = context.getSharedPreferences(DB_NAME,
                MODE_PRIVATE);

        // The value will be default as empty string
        // because for the very first time
        // when the app is opened,
        // there is nothing to show
        String Json = sh.getString("userInfo", "");
        try {
            jsonObject = new JSONObject(Json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    public static Boolean checkLoginStatus(Context context){

        // Retrieving the value using its keys
        // the file name must be same in both saving
        // and retrieving the data
        SharedPreferences sh
                = context.getSharedPreferences(DB_NAME,
                MODE_PRIVATE);

        // The value will be default as empty string
        // because for the very first time
        // when the app is opened,
        // there is nothing to show
        String Json = sh.getString("userInfo", "");
        if(Json == null || Json == ""){
            return false;
        }else{
            return true;
        }
    }

    public static Boolean logout(Context context){
        SharedPreferences sh
                = context.getSharedPreferences(DB_NAME,
                MODE_PRIVATE);
        sh.edit().clear().commit();
        if(checkLoginStatus(context)){
            return false;
        }else{
            return true;
        }

    }

    public static String getUserId(Context context){
        SharedPreferences sh
                = context.getSharedPreferences(DB_NAME,
                MODE_PRIVATE);

        JSONObject Json;
        String id = null;
        try {
            Json = new JSONObject(sh.getString("userInfo", ""));
            id = Json.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }
}
