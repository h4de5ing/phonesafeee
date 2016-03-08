package mo.com.phonesafe.tools;


import android.util.Log;

/**
 * Created by Gh0st on 2015/9/16 14:07
 *
 *
 * log日志工具类(重新封装Log类)
 *
 */


public class Logger {
    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    /**
     * 标记log是否显示
     */

    //内部测试时是使用
    private static boolean isLogEnable = true;
    private static final int LOG_LEVEL = VERBOSE;// 显示所有的


    //发布时使用
    // private static boolean isLogEnable = false;
    // private static final int LOG_LEVEL = ASSERT;// 显示所有的

    public static void v(String TAG, String msg) {
        if (isLogEnable&&LOG_LEVEL<=VERBOSE) {
            Log.v(TAG, msg);
        }
    }
    public static void i(String TAG, String msg) {
        if (isLogEnable&&LOG_LEVEL<=INFO) {
            Log.i(TAG, msg);
        }
    }
    public static void d(String TAG, String msg) {
        if (isLogEnable&&LOG_LEVEL<=DEBUG) {
            Log.d(TAG, msg);
        }
    }
    public static void w(String TAG, String msg) {
        if (isLogEnable&&LOG_LEVEL<=WARN) {
            Log.w(TAG, msg);
        }
    }
    public static void e(String TAG, String msg) {
        if (isLogEnable&&LOG_LEVEL<=ERROR) {
            Log.e(TAG, msg);
        }
    }

}
