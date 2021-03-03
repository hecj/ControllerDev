package dev.controller.hecj.cn.controllerdev.util;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hecj on 16/10/18.
 */
public class SharedPreferenceUtil {

    private static int MODE_PRIVATE = 0;
    private static String CONFIG_FILE = "cosmop_cache";

    public static void setValue(Context context, String key, String value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(key,value);
        editor.commit();
    }

    public static void setValue(Context context, String key, Long value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putLong(key,value);
        editor.commit();
    }

    public static void setValue(Context context, String key, Integer value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(key,value);
        editor.commit();
    }

    public static void setValue(Context context, String key, Float value){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putFloat(key,value);
        editor.commit();
    }

    public static String getStr(Context context, String key){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    public static Long getLong(Context context, String key){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getLong(key,-1l);
    }

    public static Integer getInt(Context context, String key){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(key,-1);
    }

    public static Float getFloat(Context context, String key){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getFloat(key,-1f);
    }

    private static SharedPreferences.Editor getEditor(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIG_FILE, MODE_PRIVATE);
        return sharedPreferences.edit();
    }

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getApplicationContext().getSharedPreferences(CONFIG_FILE, MODE_PRIVATE);
    }
}