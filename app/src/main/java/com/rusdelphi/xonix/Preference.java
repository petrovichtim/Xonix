package com.rusdelphi.xonix;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by volodya on 30.06.2015.
 */
public class Preference {
    private SharedPreferences prefs;
    private static final String PREFS = "PREFS";
    public static final String NUMBER_OF_LIFES = "NUMBER_OF_LIFES";
    public static final String GAME_SPEED = "GAME_SPEED";


    Preference(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void setData(String param, int value) {
        Editor editor = prefs.edit();
        editor.putInt(param, value);
        editor.apply();

    }

    public int getData(String param) {
        if (param.equals(NUMBER_OF_LIFES))
            return prefs.getInt(NUMBER_OF_LIFES, 1);
        if (param.equals(GAME_SPEED))
            return prefs.getInt(GAME_SPEED, 1);
        else return 0;

    }


}
