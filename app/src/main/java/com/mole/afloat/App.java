package com.mole.afloat;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.LogUtil;

public class App extends Application {
    public static boolean isResumed;
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtil.d("Lifecycle", activity.getLocalClassName() + " was Created");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtil.d("Lifecycle", activity.getLocalClassName() + " was Started");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                isResumed = true;
                LogUtil.d("Lifecycle", activity.getLocalClassName() + " was oResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                isResumed = false;
                LogUtil.d("Lifecycle", activity.getLocalClassName() + " was Paused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtil.d("Lifecycle", activity.getLocalClassName() + " was Stopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtil.d("Lifecycle", activity.getLocalClassName() + " was SaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtil.d("Lifecycle", activity.getLocalClassName() + " was Destroyed");
            }
        });
        TTAdManagerHolder.init(this);

    }
}
