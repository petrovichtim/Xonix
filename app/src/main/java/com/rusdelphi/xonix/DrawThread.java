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
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
    static String playerDirection = "up";


    public DrawThread(SurfaceHolder surfaceHolder, Resources resources, QuadrateItem[][] matrixField) {
        this.surfaceHolder = surfaceHolder;
        this.matrixField = matrixField;

        // загружаем картинку, которую будем отрисовывать
        picture = BitmapFactory.decodeResource(resources, R.drawable.stoimost);

        // формируем матрицу преобразований для картинки
        matrix = new Matrix();
        matrix.postScale(3.0f, 3.0f);
        matrix.postTranslate(100.0f, 100.0f);

        // сохраняем текущее время
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

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Override
    public void run() {
        Canvas canvas;
        Paint textPaint = new Paint();
        textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40);
        textPaint.setColor(Color.GREEN);
        // позиция игрока
        int playerX = 0;
        int playerY = 0;

        // позиция  монстра
        int monsterX = randInt(0, 39);
        int monsterY = randInt(0, 19);
        // шаг смещения монстра
        int deltaX = 1;
        int deltaY = 1;
        // траектория  игрока
        ArrayList<int[]> playerPath = new ArrayList<>();
        //playerPath.add(new int[]{playerX, playerY});

        QuadrateItem player = new QuadrateItem(matrixField[playerX][playerY]);
        player.color = Color.GREEN;

        QuadrateItem monster = new QuadrateItem(matrixField[monsterX][monsterY]);
        monster.color = Color.RED;

        while (runFlag) {
            // �������� ������� ����� � ��������� ������� � ����������
            // ����������� �������� �������
            long now = System.currentTimeMillis();
            long elapsedTime = now - prevTime;
            if (elapsedTime > 100) {

                prevTime = now;
                //matrix.preRotate(2.0f, picture.getWidth() / 2, picture.getHeight() / 2);
                if (playerDirection.equals("right")) {
                    playerX += 1;
                    if (playerX > 39)
                        playerX = 39;
                }
                if (playerDirection.equals("left")) {
                    playerX -= 1;
                    if (playerX < 0)
                        playerX = 0;
                }
                if (playerDirection.equals("down")) {
                    playerY += 1;
                    if (playerY > 19)
                        playerY = 19;

                }
                if (playerDirection.equals("up")) {
                    playerY -= 1;
                    if (playerY < 0)
                        playerY = 0;
                }
                // если следующий шаг по пустому полю, то начинаем запись

                if (matrixField[playerX][playerY].color != Color.BLUE)
                    playerPath.add(new int[]{playerX, playerY});

                // если  путь больше двух, то заполняем путь
                if (playerPath.size() > 1) {
                    if (matrixField[playerX][playerY].color == Color.BLUE) {
                        // закрашиваем  путь
                        for (int[] p : playerPath)
                            matrixField[p[0]][p[1]].color = Color.BLUE;
                        playerPath.clear();
                    }
                }
                Log.d("run", "playerX=" + playerX + " playerY=" + playerY);
                Log.d("run", "playerPath=" + TextUtils.join(";", playerPath));

                player = new QuadrateItem(matrixField[playerX][playerY]);
                player.color = Color.GREEN;

                int monsterPos[] = moveMonster(monsterX, monsterY, deltaX, deltaY);
                monsterX = monsterPos[0];
                monsterY = monsterPos[1];
                deltaX = monsterPos[2];
                deltaY = monsterPos[3];
                Log.d("run", "monsterX=" + monsterX + " monsterY=" + monsterY);
                monster = new QuadrateItem(matrixField[monsterX][monsterY]);
                monster.color = Color.RED;
            }
            canvas = null;
            try {
                // получаем объект Canvas и выполняем отрисовку
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    if (canvas != null) {
                        canvas.drawColor(Color.BLACK); // очистка холста
                        complete = 0;

                        int i, j;
                        for (i = 0; i < 40; i++) // рисуем рамку
                            for (j = 0; j < 20; j++) {
                                // выводим квадраты
                                drawRect(canvas, matrixField[i][j]);
                                // считаем процент закрашенных
                                if (matrixField[i][j].color == Color.BLUE)
                                    complete++;

                            }

                        String info = "Lives:" + lives + " Level:" + level + " (" + Math.round(((double) (complete - 116) / 784) * 100) + "/80)";// ������ ������� ����� ������
                        canvas.drawText(info, 5, 40, textPaint);
                        // рисуем  путь
                        for (int[] p : playerPath) {
                            QuadrateItem path = new QuadrateItem(matrixField[p[0]][p[1]]);
                            path.color = Color.YELLOW;
                            drawRect(canvas, path);
                        }
                        //рисуем игрока
                        drawRect(canvas, player);
                        //рисуем монстра
                        drawRect(canvas, monster);

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


    public boolean contains(int[] array, int key) {
        Arrays.sort(array);
        return Arrays.binarySearch(array, key) >= 0;
    }

    private int[] moveMonster(int monsterX, int monsterY, int deltaX, int deltaY) {
        // проверка на удары об край экрана
        monsterX += deltaX;
        if (deltaX > 0) {
            if //(monsterX >= 38) ||
                    (matrixField[monsterX + 1][monsterY].color == Color.BLUE) {
                deltaX *= -1;
            }
        } else {
            if (matrixField[monsterX - 1][monsterY].color == Color.BLUE) {//(monsterX <= 1) {
                deltaX *= -1;
            }
        }
        monsterY += deltaY;
        if (deltaY > 0) {
            if (matrixField[monsterX][monsterY + 1].color == Color.BLUE) {//(monsterY >= 18) {
                deltaY *= -1;
            }
        } else {
            if (matrixField[monsterX][monsterY - 1].color == Color.BLUE) {//(monsterY <= 1) {
                deltaY *= -1;
            }
        }
        // проверка на удары об заполненные блоки


        int[] pos = {monsterX, monsterY, deltaX, deltaY};

        return pos;
    }
}
