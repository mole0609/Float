package com.mole.afloat.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.mole.afloat.R;

public class PanelClockView extends View {
    private Paint ringPaint;//圆环的画笔
    private Paint textPaint;//字体画笔
    private int width;//空间的宽度
    private int height;//空间的高度
    private int ring_color;//圆环颜色
    private int back_color;//背景颜色
    private int ring_width;//圆环宽度
    private int start_angle;//起始角度
    private int sweep_angle;//结束角度
    private int current_angle;//当前的角度
    private int text_color;//字体颜色
    private int text_size;//字体大小
    private int path_color;//路径颜色
    private int precent;//进度
    private ValueAnimator animator;

    public PanelClockView(Context context) throws Exception {
        this(context, null);
    }

    public PanelClockView(Context context, @Nullable AttributeSet attrs) throws Exception {
        this(context, attrs, 0);
    }

    public PanelClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) throws Exception {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PanelView);
        ring_color = ta.getColor(R.styleable.PanelView_ring_color, Color.GREEN);
        back_color = ta.getColor(R.styleable.PanelView_back_color, Color.WHITE);
        ring_width = (int) ta.getDimension(R.styleable.PanelView_ring_width, getResources().getDimension(R.dimen.width));
        start_angle = ta.getInteger(R.styleable.PanelView_start_angle, -225);
        sweep_angle = ta.getInteger(R.styleable.PanelView_sweep_angle, 270);
        text_color = ta.getColor(R.styleable.PanelView_text_color, Color.RED);
        text_size = (int) ta.getDimension(R.styleable.PanelView_text_size, getResources().getDimension(R.dimen.textSize));
        path_color = ta.getColor(R.styleable.PanelView_path_color, Color.BLACK);
        ta.recycle();
        if (start_angle < -360 || sweep_angle > 360)
            throw new Exception("angel not allow");
        InitPaint();

    }

    private void InitPaint() {
        ringPaint = new Paint();
        textPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width_pixels = getResources().getDisplayMetrics().widthPixels;
        int height_pixels = getResources().getDisplayMetrics().heightPixels;
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        //获取四个的最小值
        int layout_width = Math.min(width_pixels, width);
        int layout_height = Math.min(height_pixels, height);
        int final_width = Math.min(layout_height, layout_width);
        setMeasuredDimension(final_width, final_width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //先话内部圆环
        int center = getWidth() / 2;
        int radius = center - ring_width;
        drawRing(canvas, radius, center);
        drawText(canvas, radius, center);
    }


    private void drawRing(Canvas canvas, int radius, int center) {
        //画圆环
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);
        ringPaint.setStrokeWidth(0);
        ringPaint.setStyle(Paint.Style.FILL);
        ringPaint.setColor(back_color);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(oval, start_angle * 1.0f, sweep_angle * 1.0f, false, ringPaint);

        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(ring_width);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setColor(ring_color);
        canvas.drawArc(oval, start_angle * 1.0f, sweep_angle * 1.0f, false, ringPaint);

        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(ring_width);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setColor(path_color);
        canvas.drawArc(oval, start_angle * 1.0f, current_angle * 1.0f, false, ringPaint);
    }

    private void drawText(Canvas canvas, int radius, int center) {
        //字体
        String text = precent + "%";
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(text_color);
        textPaint.setTextSize(text_size);
        int baseline = center - (fontMetricsInt.bottom + fontMetricsInt.top) / 2;
        canvas.drawText(text, center, baseline, textPaint);


    }

    public void setDuration(final int duration) {
        if (animator == null) {
            animator = ValueAnimator.ofInt(0, duration);
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int) animation.getAnimatedValue();
                precent = curValue * 100 / duration;
                current_angle = precent * sweep_angle / 100;
                invalidate();
            }
        });
        animator.setDuration(duration);
        animator.start();
    }
}
