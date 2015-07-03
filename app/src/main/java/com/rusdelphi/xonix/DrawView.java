package com.rusdelphi.xonix;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private QuadrateItem[][] matrixField = new QuadrateItem[40][20];
    private float x1, x2;
    private float y1, y2;


    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            //определяем координаты первого касания
            case MotionEvent.ACTION_DOWN: {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                float dx = x2 - x1;
                float dy = y2 - y1;
                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0)
                        DrawThread.playerDirection = "right";
                    if (dx < 0)
                        DrawThread.playerDirection = "left";
                } else {
                    if (dy > 0)
                        DrawThread.playerDirection = "down";
                    if (dy < 0)
                        DrawThread.playerDirection = "up";
                }
            }
            break;
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Preference prefs = new Preference(getContext()); // получили настройки
        int indent = Tools.dpToPx(5); // отступ между элементами
        int side = (getWidth() / 40) - indent; // размер квадрата
        int startY = (getHeight() - (side + indent) * 20) / 2;
        int startX = 5;
        int i, j;
        for (i = 0; i < 40; i++)
            for (j = 0; j < 20; j++) {
                int x1 = startX + side * i + indent * i;
                int y1 = startY + side * j + indent * j;
                int x2 = startX + side * i + side + indent * i;
                int y2 = startY + side * j + side + indent * j;
                if (i == 0 || i == 39 || j == 0 || j == 19)
                    matrixField[i][j] = new QuadrateItem(x1, y1, x2, y2, Color.BLUE);
                else
                    matrixField[i][j] = new QuadrateItem(x1, y1, x2, y2, Color.TRANSPARENT);
            }

        Activity activity = (Activity) getContext();
        drawThread = new DrawThread(getHolder(), getResources(), matrixField,prefs.getData(Preference.GAME_SPEED),prefs.getData(Preference.NUMBER_OF_LIFES),activity);
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}