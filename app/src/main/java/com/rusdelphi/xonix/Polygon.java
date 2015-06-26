package com.rusdelphi.xonix;

/**
 * Created by volodya on 26.06.2015.
 */
public class Polygon {

    // Polygon coodinates.
    private int[] polyY, polyX;

    // Number of sides in the polygon.
    private int polySides;

    /**
     * Default constructor.
     *
     * @param px Polygon y coods.
     * @param py Polygon x coods.
     * @param ps Polygon sides count.
     */
    public Polygon(int[] px, int[] py, int ps) {
        polyX = px;
        polyY = py;
        polySides = ps;
    }

    /**
     * Checks if the Polygon contains a point.
     *
     * @param x Point horizontal pos.
     * @param y Point vertical pos.
     * @return Point is in Poly flag.
     * @see "http://alienryderflex.com/polygon/"
     */
    public boolean contains(int x, int y) {
        boolean c = false;
        int i, j = 0;
        for (i = 0, j = polySides - 1; i < polySides; j = i++) {
            if (((polyY[i] > y) != (polyY[j] > y))
                    && (x < (polyX[j] - polyX[i]) * (y - polyY[i]) / (polyY[j] - polyY[i]) + polyX[i]))
                c = !c;
        }
        return c;
    }

}

