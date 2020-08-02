package android.util;

import android.os.Build;

public class LogUtil {
    private static final String LOG_TAG = "LogUtil";
    private static final boolean DBG = Build.TYPE.equalsIgnoreCase("eng") || Build.TYPE.equalsIgnoreCase("userdebug");
    private static final boolean FORCE_LOGGING = (0 == 0);

    public static void d(String className, String msg) {
        if (DBG || FORCE_LOGGING) {
            Log.d(LOG_TAG, "[" + className + "] " + msg);
        }
    }

    public static void i(String className, String msg) {
        Log.i(LOG_TAG, "[" + className+ "] " + msg);
    }

    public static void w(String className, String msg) {
        Log.w(LOG_TAG, "[" + className+ "] " + msg);
    }

    public static void e(String className, String msg) {
        Log.e(LOG_TAG, "[" + className+ "] " + msg);
    }
}
