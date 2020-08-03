package android.util;

import android.os.Build;
import android.text.TextUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtil {

    private static final int MAX_LOG_LINE_LENGTH = 4068;

    private static String sTag = "LogUtil";
    private static boolean DBG = Build.TYPE.equalsIgnoreCase("eng") || Build.TYPE.equalsIgnoreCase("userdebug");
    private static long sTimestamp = 0;

    public static void setTag(String tag) {
        sTag = tag;
    }

    public static void i(String msg) {
        if (DBG) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        android.util.Log.i(sTag, msg.substring(start, len));
                        break;
                    } else {
                        android.util.Log.i(sTag, msg.substring(start, end));
                    }
                }
            } else {
                android.util.Log.i(sTag, msg);
            }
        }
    }

    public static void v(String msg) {
        if (DBG) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        android.util.Log.v(sTag, msg.substring(start, len));
                        break;
                    } else {
                        android.util.Log.v(sTag, msg.substring(start, end));
                    }
                }
            } else {
                android.util.Log.v(sTag, msg);
            }
        }
    }

    public static void d(String msg) {
        if (DBG) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        android.util.Log.d(sTag, msg.substring(start, len));
                        break;
                    } else {
                        android.util.Log.d(sTag, msg.substring(start, end));
                    }
                }
            } else {
                android.util.Log.d(sTag, msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (DBG) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        android.util.Log.d(sTag, tag + " " + msg.substring(start, len));
                        break;
                    } else {
                        android.util.Log.d(sTag, tag + " " + msg.substring(start, end));
                    }
                }
            } else {
                android.util.Log.d(sTag, tag + msg);
            }
        }
    }

    public static void w(String msg) {
        if (DBG) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        android.util.Log.w(sTag, msg.substring(start, len));
                        break;
                    } else {
                        android.util.Log.w(sTag, msg.substring(start, end));
                    }
                }
            } else {
                android.util.Log.w(sTag, msg);
            }
        }
    }

    public static void w(Throwable tr) {
        if (DBG) {
            android.util.Log.w(sTag, "", tr);
        }
    }

    public static void w(String msg, Throwable tr) {
        if (DBG && null != msg) {
            android.util.Log.w(sTag, msg, tr);
        }
    }

    public static void e(String msg) {
        if (DBG) {
            if (null != msg && msg.length() > 0) {
                int start = 0;
                int end = 0;
                int len = msg.length();
                while (true) {
                    start = end;
                    end = start + MAX_LOG_LINE_LENGTH;
                    if (end >= len) {
                        android.util.Log.e(sTag, msg.substring(start, len));
                        break;
                    } else {
                        android.util.Log.e(sTag, msg.substring(start, end));
                    }
                }
            } else {
                android.util.Log.e(sTag, msg);
            }
        }
    }

    public static void e(Throwable tr) {
        if (DBG) {
            android.util.Log.e(sTag, "!!!error!!!", tr);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (DBG) {
            android.util.Log.e(sTag, msg, tr);
        }
    }

    public static void markStart(String msg) {
        sTimestamp = System.currentTimeMillis();
        if (!TextUtils.isEmpty(msg)) {
            e("[Started|" + sTimestamp + "]" + msg);
        }
    }

    public static void elapsed(String msg) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - sTimestamp;
        sTimestamp = currentTime;
        e("[Elapsed|" + elapsedTime + "]" + msg);
    }

    public static boolean isDebugable() {
        return DBG;
    }

    public static void setDebugable(boolean debugable) {
        DBG = debugable;
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

}
