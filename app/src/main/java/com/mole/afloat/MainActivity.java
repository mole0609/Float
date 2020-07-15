package com.mole.afloat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

public class MainActivity extends Activity {
    private SharedPreferences sp;
    private Context mContext;
    private Button mBtAnchor;
    private Button mBtVibrate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        sp = mContext.getSharedPreferences(Constant.SAVED_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        findViewById(R.id.btn_show_or_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatWindowManager.getInstance().applyOrShowFloatWindow(MainActivity.this);
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
