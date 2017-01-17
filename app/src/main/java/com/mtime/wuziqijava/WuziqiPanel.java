package com.mtime.wuziqijava;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by zhaoning on 2017/1/11.
 * 自定义view练习
 */

public class WuziqiPanel extends View {
    //画布大小 行高 行数
    private int mPanelWidth;
    private float lineHeight;
    private int MAX_LINES = 17;
    //五子棋 连接为5游戏结束
    private int MAX_COUNT_IN_LINE = 5;

    //棋盘的画笔
    private Paint mPaint = new Paint();

    //黑白棋子
    private Bitmap whiteStone;
    private Bitmap blackStone;

    //比例 用于调整棋子的大小
    private float ratio = 3.0f / 4;

    //轮到黑子
    private boolean isBlack = true;
    private ArrayList<Point> whiteStoneArray = new ArrayList<Point>();
    private ArrayList<Point> blackStoneArray = new ArrayList<Point>();

    //是否游戏结束
    private boolean isGameOver;

    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.parseColor("#ffffff"));
        init();
    }

    /**
     * 重新开始
     */
    public void restart() {
        whiteStoneArray.clear();
        blackStoneArray.clear();
        isGameOver = false;
        isBlack = true;
        invalidate();
    }

    /**
     * 悔棋
     */
    public void regret() {
        if (blackStoneArray.size() > 0 || whiteStoneArray.size() > 0) {
            if (isBlack) {
                whiteStoneArray.remove(whiteStoneArray.size() - 1);
                isBlack = !isBlack;
            } else {
                blackStoneArray.remove(blackStoneArray.size() - 1);
                isBlack = !isBlack;
            }
            invalidate();
        }
    }

    private void init() {
        //设置棋盘画笔的颜色抗锯齿和类型
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        //初始化棋子的bitmap
        whiteStone = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_w2);
        blackStone = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_b1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (isGameOver) {
            return false;
        }

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point point = getValiedPoint(x, y);
            //如果该位置已经有棋子了
            if (whiteStoneArray.contains(point) || blackStoneArray.contains(point)) {
                return false;
            }

            if (isBlack) {
                blackStoneArray.add(point);
            } else {
                whiteStoneArray.add(point);
            }
            //将棋子添加到集合中后 要换手
            isBlack = !isBlack;

            //添加完棋子后重绘棋盘
            invalidate();
        }
        return true;
    }

    //确定棋子的位置
    private Point getValiedPoint(int x, int y) {
        return new Point((int) (x / lineHeight), (int) (y / lineHeight));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        lineHeight = mPanelWidth * 1.0f / MAX_LINES;

        //画布确定后 根据比例调整棋子的大小
        int stoneWidth = (int) (lineHeight * ratio);
        whiteStone = Bitmap.createScaledBitmap(whiteStone, stoneWidth, stoneWidth, false);
        blackStone = Bitmap.createScaledBitmap(blackStone, stoneWidth, stoneWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawStone(canvas);
        checkGameOver();
    }

    /**
     * 检测游戏是否结束
     */
    private void checkGameOver() {

        checkFiveInline(whiteStoneArray);
        checkFiveInline(blackStoneArray);


        if (isGameOver) {
            Toast.makeText(getContext(), "游戏结束", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否五子相连
     *
     * @param stoneArray
     */
    private boolean checkFiveInline(ArrayList<Point> stoneArray) {
        for (Point point : stoneArray) {
            boolean checkHor = checkHorizontal(point, stoneArray);
            boolean checkVer = checkVertical(point, stoneArray);
            boolean checkleft = checkLeft(point, stoneArray);
            boolean checkRight = checkRight(point, stoneArray);
            if (checkHor || checkVer || checkleft || checkRight) {
                isGameOver = true;
                return true;
            }
        }
        return false;
    }

    /**
     * 检查右斜方是否五子相连
     *
     * @param point
     * @param stoneArray
     * @return
     */
    private boolean checkRight(Point point, ArrayList<Point> stoneArray) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (stoneArray.contains(new Point(point.x + i, point.y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count >= MAX_COUNT_IN_LINE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查左斜方是否五子相连
     *
     * @param point
     * @param stoneArray
     * @return
     */
    private boolean checkLeft(Point point, ArrayList<Point> stoneArray) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (stoneArray.contains(new Point(point.x + i, point.y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count >= MAX_COUNT_IN_LINE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查纵向是否五子相连
     *
     * @param point
     * @param stoneArray
     * @return
     */
    private boolean checkVertical(Point point, ArrayList<Point> stoneArray) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (stoneArray.contains(new Point(point.x, point.y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count >= MAX_COUNT_IN_LINE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查横向是否五子相连
     *
     * @param point
     * @param stoneArray
     */
    private boolean checkHorizontal(Point point, ArrayList<Point> stoneArray) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (stoneArray.contains(new Point(point.x + i, point.y))) {
                count++;
            } else {
                break;
            }
        }
        if (count >= MAX_COUNT_IN_LINE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 绘制棋子
     *
     * @param canvas
     */
    private void drawStone(Canvas canvas) {
        for (int i = 0; i < whiteStoneArray.size(); i++) {
            Point white = whiteStoneArray.get(i);
            canvas.drawBitmap(whiteStone, lineHeight * (white.x + (1 - ratio) / 2), lineHeight * (white.y + (1 - ratio) / 2), null);
        }

        for (int i = 0; i < blackStoneArray.size(); i++) {
            Point black = blackStoneArray.get(i);
            canvas.drawBitmap(blackStone, lineHeight * (black.x + (1 - ratio) / 2), lineHeight * (black.y + (1 - ratio) / 2), null);
        }
    }

    /**
     * 绘制棋盘
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int width = mPanelWidth;
        float lineHeight = this.lineHeight;

        for (int i = 0; i < MAX_LINES; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (width - lineHeight / 2);
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }
}