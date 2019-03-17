package com.easyremainder;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHalper {
    private  static  PreferenceHalper instance;
    private Context context;
    private SharedPreferences preferences;
    public  static final String SPLAH_IS_INVISIBLE="splash_is_invisible";
    private PreferenceHalper(){

    }
    public static PreferenceHalper getInstance() {
        if (instance == null) {
instance=new PreferenceHalper();
        }
        return instance;

    }
    public void init(Context context){
        this.context=context;
        preferences=context.getSharedPreferences("preferences",Context.MODE_PRIVATE);

    }
    public void putBoolean(String key,boolean value){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public boolean getBoolean(String key){
        return  preferences.getBoolean(key,false);
    }


}
