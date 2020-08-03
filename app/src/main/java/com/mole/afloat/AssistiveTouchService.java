package com.mole.afloat;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.LogUtil;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;


public class AssistiveTouchService extends Service {

    private static final long DELAY_MILLIS = 2000;

    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams mParams;
    private Button mMouseView;

    private boolean isMoving;

    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {

        public void run() {
            setMouseColor(false);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("NYDBG 2222222222222222");
        createFloatView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createFloatView() {
        LogUtil.d("NUDBG------------");
        mMouseView = new Button(getApplicationContext());

        mHandler = new Handler();

        mWindowManager = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }

        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.width = 200;
        mParams.height = 200;

        mMouseView.setOnTouchListener(new OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = mParams.x;
                        paramY = mParams.y;
                        isMoving = false;
                        mMouseView
                                .setBackgroundResource(R.drawable.assistive_button_darker);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isMoving = true;
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        mParams.x = paramX + dx;
                        mParams.y = paramY + dy;
                        // ����������λ��
                        mWindowManager.updateViewLayout(mMouseView, mParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        setMouseColor(true);
                        break;
                }
                return isMoving;
            }
        });

        mMouseView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isMoving) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    setMouseColor(true);
                }
            }
        });

        mWindowManager.addView(mMouseView, mParams);
        setMouseColor(true);
    }

    private void setMouseColor(boolean touch) {
        if (touch) {
            mMouseView.setBackgroundResource(R.drawable.assistive_button_darker);
            mHandler.postDelayed(mRunnable, DELAY_MILLIS);
        } else {
            mMouseView.setBackgroundResource(R.drawable.assistive_button_lighter);
        }
    }

}
