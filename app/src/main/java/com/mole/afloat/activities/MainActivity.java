package com.mole.afloat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.mole.afloat.AssistiveTouchService;
import com.mole.afloat.Constant;
import com.mole.afloat.FloatWindowManager;
import com.mole.afloat.R;
import com.mole.afloat.permission.PermissionUtil;

public class MainActivity extends Activity {
    private SharedPreferences sp;
    private Context mContext;
    private Button mBtAnchor;
    private Button mBtVibrate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        sp = mContext.getSharedPreferences(Constant.SAVED_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        findViewById(R.id.btn_show_or_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtil.checkPermission(mContext)) {
                    FloatWindowManager.getInstance().showFloatWindow(mContext);
                } else {
                    PermissionUtil.applyPermission(mContext);
                }
            }
        });
        findViewById(R.id.btn_assistive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtil.checkPermission(mContext)) {
                    startService(new Intent(mContext, AssistiveTouchService.class));
                } else {
                    PermissionUtil.applyPermission(mContext);
                }
            }
        });

        findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindowManager.getInstance().dismissWindow();
            }
        });
        mBtAnchor = findViewById(R.id.btn_anchor);
        mBtVibrate = findViewById(R.id.btn_vibrate);
        mBtAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp != null) {
                    final boolean isAnchor = sp.getBoolean("isAnchor", false);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isAnchor", !isAnchor);
                    editor.apply();
                    mBtAnchor.setText(!isAnchor + "");
                }
            }
        });
        mBtVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp != null) {
                    final boolean isVibrate = sp.getBoolean("isVibrate", false);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("isVibrate", !isVibrate);
                    editor.apply();
                    mBtVibrate.setText(!isVibrate + "");
                }
            }
        });


    }

}
