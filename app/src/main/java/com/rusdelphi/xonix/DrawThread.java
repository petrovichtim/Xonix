package com.rusdelphi.xonix;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by volodya on 22.06.2015.
 */
public class DrawThread extends Thread {
    private final QuadrateItem[][] matrixField;
    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;
    private Bitmap picture;
    private Matrix matrix;
    private long prevTime;
    int lives = 3;
    int level = 1;
    int complete = 0;

    public DrawThread(SurfaceHolder surfaceHolder, Resources resources, QuadrateItem[][] matrixField) {
        this.surfaceHolder = surfaceHolder;
        this.matrixField = matrixField;


        // ��������� ��������, ������� ����� ������������
        picture = BitmapFactory.decodeResource(resources, R.drawable.stoimost);

        // ��������� ������� �������������� ��� ��������
        matrix = new Matrix();
        matrix.postScale(3.0f, 3.0f);
        matrix.postTranslate(100.0f, 100.0f);

        // ��������� ������� �����
        prevTime = System.currentTimeMillis();
    }

    public void setRunning(boolean run) {
        runFlag = run;
    }

    public void drawRect(Canvas canvas, QuadrateItem item) {
        Rect myRect = new Rect();
        myRect.set(item.x1, item.y1, item.x2, item.y2);
        // �����
        Paint greenPaint = new Paint();
        // ���� �����
        greenPaint.setColor(item.color);
        // ��� - �������
        greenPaint.setStyle(Paint.Style.FILL);
        // ����������� ������ ��������������� ������ �������� ������
        canvas.drawRect(myRect, greenPaint);
    }

    @Override
    public void run() {
        Canvas canvas;
        Paint textPaint = new Paint();
        textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40);
        textPaint.setColor(Color.GREEN);

        int playerX = 0;
        int playerY = 0;
        QuadrateItem player = new QuadrateItem(matrixField[playerX][playerY]);
        player.color = Color.GREEN;
        String playerDirection = "down";

        while (runFlag) {
            // �������� ������� ����� � ��������� ������� � ����������
            // ����������� �������� �������
            long now = System.currentTimeMillis();
            long elapsedTime = now - prevTime;
            if (elapsedTime > 100) {

                prevTime = now;
                //matrix.preRotate(2.0f, picture.getWidth() / 2, picture.getHeight() / 2);
                if (playerDirection.equals("right"))
                    if (playerX >= 0 && playerX < 39) {
                        playerX += 1;
                    }
                if (playerDirection.equals("left"))
                    if (playerX >= 0 && playerX < 39)
                        playerX -= 1;
                if (playerDirection.equals("down"))
                    if (playerY >= 0 && playerY < 19)
                        playerY += 1;
                if (playerDirection.equals("up"))
                    if (playerY >= 0 && playerY < 19)
                        playerY -= 1;

                Log.d("run", "playerX=" + playerX + " playerY=" + playerY);
                player = new QuadrateItem(matrixField[playerX][playerY]);
                player.color = Color.GREEN;

            }
            canvas = null;
            try {
                // получаем объект Canvas и выполняем отрисовку
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    if (canvas != null) {
                        int i, j;
                        for (i = 0; i < 40; i++)
                            for (j = 0; j < 20; j++) { // ������ ���
                                drawRect(canvas, matrixField[i][j]);
                            }
                        String info = "Lives:" + lives + " Level:" + level + " (" + complete + "/80)";// ������ ������� ����� ������
                        canvas.drawText(info, 5, 40, textPaint);
                        // ������ ������
                        drawRect(canvas, player);

                        //canvas.drawColor(Color.BLACK);
                        // canvas.drawBitmap(picture, matrix, null);
                    }
                }
            } finally {
                if (canvas != null) {
                    // отрисовка выполнена. выводим результат на экран
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
