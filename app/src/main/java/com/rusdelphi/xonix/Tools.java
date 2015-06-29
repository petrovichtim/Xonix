package com.rusdelphi.xonix;

import android.content.res.Resources;

import java.util.Random;

/**
 * Created by volodya on 29.06.2015.
 */
public class Tools {
    public static float spToPixels(float px) {
        float scaledDensity = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return px * scaledDensity;
    }

    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * density);
    }
    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
