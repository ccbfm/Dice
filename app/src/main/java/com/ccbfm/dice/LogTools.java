package com.ccbfm.dice;

import android.util.Log;

public class LogTools {
    private static final String TAG = "Dice";
    private static final boolean DISABLE = true;
    private static final int LEVEL = 3;

    public static int d(String tag, String msg) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.d(TAG, "[" + tag + "]>>>" + msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.d(TAG, "[" + tag + "]>>>" + msg, tr);
    }

    public static int i(String tag, String msg) {
        if (DISABLE && LEVEL > Log.INFO) {
            return 0;
        }
        return Log.i(TAG, "[" + tag + "]>>>" + msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (DISABLE && LEVEL > Log.INFO) {
            return 0;
        }
        return Log.i(TAG, "[" + tag + "]>>>" + msg, tr);
    }

    public static int w(String tag, String msg) {
        if (DISABLE && LEVEL > Log.WARN) {
            return 0;
        }
        return Log.w(TAG, "[" + tag + "]>>>" + msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (DISABLE && LEVEL > Log.WARN) {
            return 0;
        }
        return Log.w(TAG, "[" + tag + "]>>>" + msg, tr);
    }

    public static int e(String tag, String msg) {
        if (DISABLE && LEVEL > Log.ERROR) {
            return 0;
        }
        return Log.e(TAG, "[" + tag + "]>>>" + msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (DISABLE && LEVEL > Log.ERROR) {
            return 0;
        }
        return Log.e(TAG, "[" + tag + "]>>>" + msg, tr);
    }

    public static int d(String tag, String method, String msg) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.d(TAG, "[" + tag + "]>(" + method + ")>" + msg);
    }

    public static int i(String tag, String method, String msg) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.i(TAG, "[" + tag + "]>(" + method + ")>" + msg);
    }

    public static int w(String tag, String method, String msg) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.w(TAG, "[" + tag + "]>(" + method + ")>" + msg);
    }

    public static int e(String tag, String method, String msg) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.e(TAG, "[" + tag + "]>(" + method + ")>" + msg);
    }

    public static int d(String tag, String method, String msg, Throwable tr) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.d(TAG, "[" + tag + "]>(" + method + ")>" + msg, tr);
    }

    public static int i(String tag, String method, String msg, Throwable tr) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.i(TAG, "[" + tag + "]>(" + method + ")>" + msg, tr);
    }

    public static int w(String tag, String method, String msg, Throwable tr) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.w(TAG, "[" + tag + "]>(" + method + ")>" + msg, tr);
    }

    public static int e(String tag, String method, String msg, Throwable tr) {
        if (DISABLE && LEVEL > Log.DEBUG) {
            return 0;
        }
        return Log.e(TAG, "[" + tag + "]>(" + method + ")>" + msg, tr);
    }
}
