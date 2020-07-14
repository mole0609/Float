package com.mole.afloat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class Clock extends View {
    public Clock(Context context) {
        super(context);
        initView();
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {

    }
}
