/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.mole.afloat.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Vibrator;
import android.util.LogUtil;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mole.afloat.App;
import com.mole.afloat.Constant;
import com.mole.afloat.FloatWindowManager;
import com.mole.afloat.R;


public class FloatView extends FrameLayout {
    private static final String TAG = "FloatView";
    //移动的阈值
    private static final int TOUCH_SLOP = 20;
    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;
    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;
    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;
    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;
    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;
    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;
    /**
     * 记录手指按下的时间
     */
    private long downTime;
    /**
     * 记录手指抬起的时间
     */
    private long upTime;

    private boolean isAnchoring = false;
    private boolean isShowing = false;
    private WindowManager windowManager = null;
    private WindowManager.LayoutParams mParams = null;
    private ClockView mClockView;
    private Vibrator mVibrator;
    private long[] pattern = {100, 400, 100, 400}; // 停止 开启 停止 开启
    private Runnable mLongPressRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.d(TAG, "LongPressRunnable");
            if (!App.isResumed) {
                Intent intent = new Intent();
                intent.setClassName("com.mole.afloat", "com.mole.afloat.activitis.MainActivity");
                getContext().startActivity(intent);
                LogUtil.d(TAG, "startActivity");
            }
        }
    };


    private final GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent event) {
            LogUtil.d(TAG, "onDown-----" + getActionName(event.getAction()));
            downTime = System.currentTimeMillis();
            xInView = event.getX();
            yInView = event.getY();
            xDownInScreen = event.getRawX();
            yDownInScreen = event.getRawY();
            xInScreen = event.getRawX();
            yInScreen = event.getRawY();
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            LogUtil.d(TAG, "onShowPress-----" + getActionName(e.getAction()));
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            LogUtil.d(TAG, "onSingleTapConfirmed-----" + getActionName(e.getAction()));
            Toast.makeText(getContext(), "开始计时", Toast.LENGTH_SHORT).show();
            mClockView.start(3000, 3000);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            LogUtil.d(TAG, "onDoubleTap-----" + getActionName(e.getAction()));
            mClockView.cancel();
            Toast.makeText(getContext(), "重置计时", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtil.d(TAG, "onScroll-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                    + e2.getX() + "," + e2.getY() + ")" + " ,(" + distanceX + "," + distanceY + ")");
            xInScreen = e2.getRawX();
            yInScreen = e2.getRawY();
            updateViewPosition();
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            LogUtil.d(TAG, "onLongPress-----" + getActionName(e.getAction()));
            post(mLongPressRunnable);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogUtil.d(TAG, "onFling-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                    + e2.getX() + "," + e2.getY() + ")" + " ,(" + velocityX + "," + velocityY + ")");
            anchorToSide();
            if (Math.abs(velocityY) > 10000 && e2.getY() < 50) {
                LogUtil.d(TAG, "onFling-----" + velocityY);
                FloatWindowManager.getInstance().dismissWindow();
            }
            return false;
        }
    });

    public FloatView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View floatView = inflater.inflate(R.layout.float_window_layout, null);
        mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        mClockView = floatView.findViewById(R.id.clockView);
        mClockView.setFinishListener(new ClockView.onFinishListener() {
            @Override
            public void onFinish() {
                boolean isVibrate = getContext().getSharedPreferences(Constant.SAVED_SHARED_PREFERENCES, Context.MODE_PRIVATE).getBoolean("isVibrate", false);
                LogUtil.d("isVibrate = " + isVibrate);
                if (isVibrate) {
                    mVibrator.vibrate(pattern, -1);
                }
                LogUtil.d("finish");
            }
        });

        addView(floatView);
        floatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                return true; // 注：返回true才能完整接收触摸事件
            }
        });
    }

    private String getActionName(int action) {
        String name = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                name = "ACTION_DOWN";
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                name = "ACTION_MOVE";
                break;
            }
            case MotionEvent.ACTION_UP: {
                name = "ACTION_UP";
                break;
            }
            default:
                break;
        }
        return name;
    }

    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    public void setIsShowing(boolean isShowing) {
        this.isShowing = isShowing;
    }

    //已丢弃,touch判断点击滑动长按
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (true || isAnchoring) {
            return true;
        }
        LogUtil.d("NYDBG", " getAction " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                // 手指移动的时候更新小悬浮窗的位置
                removeCallbacks(mLongPressRunnable);
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                upTime = System.currentTimeMillis();
                if (Math.abs(xDownInScreen - xInScreen) <= ViewConfiguration.get(getContext()).getScaledTouchSlop()
                        && Math.abs(yDownInScreen - yInScreen) <= ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    // 点击效果
                    if (upTime - downTime > ViewConfiguration.getLongPressTimeout()) {
                        Toast.makeText(getContext(), "this float window is long", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "this float window is clicked", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //吸附效果
                    anchorToSide();
                }
                removeCallbacks(mLongPressRunnable);
                break;
            default:
                break;
        }
        return true;
    }

    private void anchorToSide() {
        boolean isAnchor = getContext().getSharedPreferences(Constant.SAVED_SHARED_PREFERENCES, Context.MODE_PRIVATE).getBoolean("isAnchor", false);
        LogUtil.d("isAnchor = " + isAnchor);
        if (!isAnchor) {
            return;
        }
        isAnchoring = true;
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int middleX = mParams.x + getWidth() / 2;

        int animTime = 0;
        int xDistance = 0;
        int yDistance = 0;

        int dp_25 = dp2px(15);

        //1
        if (middleX <= dp_25 + getWidth() / 2) {
            xDistance = dp_25 - mParams.x;
        }
        //2
        else if (middleX <= screenWidth / 2) {
            xDistance = dp_25 - mParams.x;
        }
        //3
        else if (middleX >= screenWidth - getWidth() / 2 - dp_25) {
            xDistance = screenWidth - mParams.x - getWidth() - dp_25;
        }
        //4
        else {
            xDistance = screenWidth - mParams.x - getWidth() - dp_25;
        }

        //1
        if (mParams.y < dp_25) {
            yDistance = dp_25 - mParams.y;
        }
        //2
        else if (mParams.y + getHeight() + dp_25 >= screenHeight) {
            yDistance = screenHeight - dp_25 - mParams.y - getHeight();
        }
        LogUtil.e("xDistance  " + xDistance + "   yDistance" + yDistance);

        animTime = Math.abs(xDistance) > Math.abs(yDistance) ? (int) (((float) xDistance / (float) screenWidth) * 600f)
                : (int) (((float) yDistance / (float) screenHeight) * 900f);
        this.post(new AnchorAnimRunnable(Math.abs(animTime), xDistance, yDistance, System.currentTimeMillis()));
    }

    public int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void updateViewPosition() {
        //增加移动误差
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        LogUtil.d(TAG, "x  " + mParams.x + "   y  " + mParams.y);
        windowManager.updateViewLayout(this, mParams);
    }

    private class AnchorAnimRunnable implements Runnable {

        private int animTime;
        private long currentStartTime;
        private Interpolator interpolator;
        private int xDistance;
        private int yDistance;
        private int startX;
        private int startY;

        public AnchorAnimRunnable(int animTime, int xDistance, int yDistance, long currentStartTime) {
            this.animTime = animTime;
            this.currentStartTime = currentStartTime;
            interpolator = new AccelerateDecelerateInterpolator();
            this.xDistance = xDistance;
            this.yDistance = yDistance;
            startX = mParams.x;
            startY = mParams.y;
        }

        @Override
        public void run() {
            if (System.currentTimeMillis() >= currentStartTime + animTime) {
                if (mParams.x != (startX + xDistance) || mParams.y != (startY + yDistance)) {
                    mParams.x = startX + xDistance;
                    mParams.y = startY + yDistance;
                    windowManager.updateViewLayout(FloatView.this, mParams);
                }
                isAnchoring = false;
                return;
            }
            float delta = interpolator.getInterpolation((System.currentTimeMillis() - currentStartTime) / (float) animTime);
            int xMoveDistance = (int) (xDistance * delta);
            int yMoveDistance = (int) (yDistance * delta);
            LogUtil.e("delta:  " + delta + "  xMoveDistance  " + xMoveDistance + "   yMoveDistance  " + yMoveDistance);
            mParams.x = startX + xMoveDistance;
            mParams.y = startY + yMoveDistance;
            if (!isShowing) {
                return;
            }
            windowManager.updateViewLayout(FloatView.this, mParams);
            FloatView.this.postDelayed(this, 16);
        }
    }
}
