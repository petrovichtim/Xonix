package com.rusdelphi.xonix;

/**
 * Created by volodya on 23.06.2015.
 */
public class QuadrateItem {
    int x1;
    int y1;
    int x2;
    int y2;
    int color;

    public QuadrateItem(int x1, int y1, int x2, int y2, int color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;

    }

    // Copy constructor
    public QuadrateItem(QuadrateItem aQuadrateItem) {
        this(aQuadrateItem.x1, aQuadrateItem.y1, aQuadrateItem.x2, aQuadrateItem.y2, aQuadrateItem.color);
    }



}
