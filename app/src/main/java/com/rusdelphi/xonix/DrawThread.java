package com.rusdelphi.xonix;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by volodya on 22.06.2015.
 */
public class DrawThread extends Thread {
    private QuadrateItem[][] matrixField;
    private int gameSpeed;
    private int numberLifes;
    private boolean runFlag = false;
    private SurfaceHolder surfaceHolder;
    private Bitmap picture;
    private Matrix matrix;
    private long prevTime;
    private static Activity parent;
    int level = 1;
    int complete = 0;
    static String playerDirection = "up";
    Queue<String> zone1 = new ConcurrentLinkedQueue<>(); // В ОЧЕРЕДЬ!!!
    Queue<String> zone2 = new ConcurrentLinkedQueue<>();


    public DrawThread(SurfaceHolder surfaceHolder, Resources resources, QuadrateItem[][] matrixField, int gameSpeed, int numberLifes, Activity parent) {
        this.surfaceHolder = surfaceHolder;
        this.matrixField = matrixField;

        this.gameSpeed = gameSpeed;
        this.numberLifes = numberLifes;
        this.parent = parent;
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
        Paint itemPaint = new Paint();
        itemPaint.setColor(item.color);
        itemPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(myRect, itemPaint);
    }

    static int[] addElement(int[] a, int e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    public void clearMatrixfield() {
        int i, j;
        for (i = 0; i < 40; i++)
            for (j = 0; j < 20; j++) {

                if (i == 0 || i == 39 || j == 0 || j == 19)
                    matrixField[i][j].color = Color.BLUE;
                else
                    matrixField[i][j].color = Color.TRANSPARENT;
            }
    }


    public static boolean isInList(
            final List<int[]> list, final int[] candidate) {

        for (final int[] item : list) {
            if (Arrays.equals(item, candidate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        Canvas canvas;
        // загрузили свой шрифт
        Typeface tf = Typeface.createFromAsset(parent.getAssets(),
                "Dots.ttf");
        Paint textPaint = new Paint();
        //textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Tools.spToPixels(15));
        textPaint.setColor(Color.GREEN);
        textPaint.setTypeface(tf);
        // позиция игрока
        int playerX = 0;
        int playerY = 0;

        String[] testFillTime = {"right", "right", "right", "right", "down", "down", "down", "down", "left", "left", "left", "left"};
        int testIndex = 0;

        // позиция  монстра
        int monsterX = Tools.randInt(2, 37);
        int monsterY = Tools.randInt(2, 17);
        // шаг смещения монстра
        int deltaX = 1;
        int deltaY = 1;
        // траектория  игрока
        List<int[]> playerPath = new ArrayList<>();


        QuadrateItem player = new QuadrateItem(matrixField[playerX][playerY]);
        player.color = Color.GREEN;

        QuadrateItem monster = new QuadrateItem(matrixField[monsterX][monsterY]);
        monster.color = Color.RED;


        while (runFlag) {
            // �������� ������� ����� � ��������� ������� � ����������
            // ����������� �������� �������
            long now = System.currentTimeMillis();
            long elapsedTime = now - prevTime;
            if (elapsedTime > 1000 / gameSpeed) {

                prevTime = now;
                // тест
//                if (testIndex < 11)
//                    playerDirection = testFillTime[testIndex];
//                testIndex++;

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
                // если пересекается собственный путь, то уменьшаем число жизней
                if (isInList(playerPath, new int[]{playerX, playerY})) {
                    numberLifes -= 1;
                    playerX = 0;
                    playerY = 0;
                    playerPath.clear();
                    if (numberLifes < 0) {
                        runFlag = false;
                        parent.runOnUiThread(new Runnable() {
                            public void run() {
                                GameActivity.showGameOver();
                            }
                        });
                    } else continue;
                }


                // если следующий шаг по пустому полю, то начинаем запись

                if (matrixField[playerX][playerY].color != Color.BLUE)
                    playerPath.add(new int[]{playerX, playerY});
                // int[] polyX = new int[0], polyY = new int[0];
                // если  путь больше двух, то заполняем путь

                if (playerPath.size() > 1) {
                    if (matrixField[playerX][playerY].color == Color.BLUE) {
                        // закрашиваем  путь
                        for (int[] p : playerPath)
                            matrixField[p[0]][p[1]].color = Color.BLUE;

                        playerPath.clear();
                        //тут надо найти белые фигуры и закрасить меньшую или меньшие
                        //long fillTime = System.currentTimeMillis();
                        zone1.clear();
                        zone2.clear();
                        int i, j;
                        for (i = 1; i < 39; i++)
                            for (j = 1; j < 19; j++) {
                                if (matrixField[i][j].color == Color.TRANSPARENT)
                                    findNeighbours(i, j);
                            }
                        //  Log.d("DrawThread", "zone1=" + zone1);
                        // Log.d("DrawThread", "zone2=" + zone2);
                        if (zone1.contains(monsterX + ";" + monsterY))
                            for (String s : zone2) {
                                String[] p = s.split(";");
                                matrixField[Integer.parseInt(p[0])][Integer.parseInt(p[1])].color = Color.BLUE;
                            }
                        else
                            for (String s : zone1) {
                                String[] p = s.split(";");
                                matrixField[Integer.parseInt(p[0])][Integer.parseInt(p[1])].color = Color.BLUE;
                            }
                        // Log.d("DrawThread", "fillTime=" + (System.currentTimeMillis() - fillTime));
                    }

                }


                player = new QuadrateItem(matrixField[playerX][playerY]);
                player.color = Color.GREEN;

                int monsterPos[] = moveMonster(monsterX, monsterY, deltaX, deltaY);
                monsterX = monsterPos[0];
                monsterY = monsterPos[1];
                deltaX = monsterPos[2];
                deltaY = monsterPos[3];

                if (isInList(playerPath, new int[]{monsterX, monsterY})) {
                    numberLifes -= 1;
                    playerX = 0;
                    playerY = 0;
                    playerPath.clear();
                    if (numberLifes < 0) {
                        runFlag = false;
                        parent.runOnUiThread(new Runnable() {
                            public void run() {
                                GameActivity.showGameOver();
                            }
                        });
                    } else continue;
                }
                // Log.d("DrawThread", "monsterX=" + monsterX + " monsterY=" + monsterY);

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
                        long completeFields = Math.round(((double) (complete - 116) / 684) * 100);


                        // рисуем текст
                        String info = "Количество жизней:" + numberLifes + " Уровень:" + level + " (" + completeFields + "/80%)";// ������ ������� ����� ������

                        canvas.drawText(info, Tools.dpToPx(5), Tools.spToPixels(18), textPaint);
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

                        // проверка на окончание уровня
                        if (completeFields > 80) {
                            level++; // увеличили уровень
                            gameSpeed++; //увеличили скорость
                            playerPath.clear();
                            playerX = 0;
                            playerY = 0;
                            complete = 0;
                            clearMatrixfield();

                        }

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


    private void findNeighbours(int i, int j) {
        String current = new StringBuilder().append(i).append(";").append(j).toString();
        String left = new StringBuilder().append(i - 1).append(";").append(j).toString();
        String right = new StringBuilder().append(i + 1).append(";").append(j).toString();
        String top = new StringBuilder().append(i).append(";").append(j - 1).toString();
        String down = new StringBuilder().append(i).append(";").append(j + 1).toString();


        if (zone1.size() == 0 && zone2.size() == 0) {
            zone1.add(current);//  i + ";" + j);
            if (matrixField[i - 1][j].color == Color.TRANSPARENT) //слева
                zone1.add(left);
            if (matrixField[i + 1][j].color == Color.TRANSPARENT)  //справа
                zone1.add(right);
            if (matrixField[i][j + 1].color == Color.TRANSPARENT)  //снизу
                zone1.add(down);
            if (matrixField[i][j - 1].color == Color.TRANSPARENT) // сверху
                zone1.add(top);

            for (String s : zone1) {
                String[] p = s.split(";");
                // Log.d("zone1 loop", s);
                findNeighbours(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
            }
        }
        if (zone1.contains(current)) {
            if (matrixField[i - 1][j].color == Color.TRANSPARENT && !zone1.contains(left))
                zone1.add(left); // слева
            if (matrixField[i + 1][j].color == Color.TRANSPARENT && !zone1.contains(right))
                zone1.add(right); //справа
            if (matrixField[i][j + 1].color == Color.TRANSPARENT && !zone1.contains(down))
                zone1.add(down);
            if (matrixField[i][j - 1].color == Color.TRANSPARENT && !zone1.contains(top))
                zone1.add(top);
        }

        if (!zone1.contains(current)) {
            if (!zone2.contains(current)) {
                zone2.add(current);

                for (String s : zone2) {
                    String[] p = s.split(";");
                    // Log.d("zone2 loop", s);
                    findNeighbours(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
                }
            }
            if (matrixField[i - 1][j].color == Color.TRANSPARENT && !zone2.contains(left))
                zone2.add(left);//слева
            if (matrixField[i + 1][j].color == Color.TRANSPARENT && !zone2.contains(right))
                zone2.add(right);//справа
            if (matrixField[i][j + 1].color == Color.TRANSPARENT && !zone2.contains(down))
                zone2.add(down);
            if (matrixField[i][j - 1].color == Color.TRANSPARENT && !zone2.contains(top))
                zone2.add(top);
        }

    }


    public boolean contains(int[] array, int key) {
        Arrays.sort(array);
        return Arrays.binarySearch(array, key) >= 0;
    }

    private int[] moveMonster(int monsterX, int monsterY, int deltaX, int deltaY) {
        // проверка на удары об край экрана и об заполненные блоки
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


        int[] pos = {monsterX, monsterY, deltaX, deltaY};

        return pos;
    }
}
