package com.douyaim.qsapp.utils;

import android.text.TextUtils;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * APP中统一的日志工具类
 *
 * @author zhangwanxin
 */
public class L {
    private static String TAG = "IF";
    private static boolean OPEN_LOG = true;//默认打开
    private static String DEFAULT_MSG = "No msg for this report";
    private static boolean isInitLogger = false;

    public static void setTAG(String TAG) {
        L.TAG = TAG;
        Logger.init(TAG);
    }

    public static void setOpenLog(boolean openLog) {
        OPEN_LOG = openLog;
    }

    private static boolean isOpenLog() {
        return OPEN_LOG;
    }


    private static void init() {
        if (!isInitLogger) {
            Logger.init(TAG)                 // default PRETTYLOGGER or use just init()
                    .methodCount(1)                 // default 2
                    .hideThreadInfo()              // default shown
                    .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                    .methodOffset(2);            // default 0
//                .logAdapter(new AndroidLogAdapter()); //default AndroidLogAdapter
            isInitLogger = true;
        }
    }

    public static void dObj(Object obj) {
        if (isOpenLog()) {
            init();
            if (obj != null) {
                d(JSON.toJSON(obj).toString());
            } else {
                d("obj is null!!!");
            }
        }
    }


    public static void d(String msg) {
        d(null, msg);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void d(String tag, String msg, Object args) {
        if (isOpenLog()) {
            if (TextUtils.isEmpty(tag)) {
                Log.d(TAG, checkMsg(msg, args));
            } else {
                Log.d(TAG + "_" + tag, checkMsg(msg, args));
            }
        }

    }

    public static void v(String msg) {
        v(null, msg);
    }

    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }

    public static void v(String tag, String msg, Object args) {
        if (isOpenLog()) {
            if (TextUtils.isEmpty(tag)) {
                Log.v(TAG, checkMsg(msg, args));
            } else {
                Log.v(TAG + "_" + tag, checkMsg(msg, args));
            }
        }

    }

    public static void i(String msg) {
        i(null, msg);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static void i(String tag, String msg, Object args) {
        if (isOpenLog()) {
            if (TextUtils.isEmpty(tag)) {
                Log.i(TAG, checkMsg(msg, args));
            } else {
                Log.i(TAG + "_" + tag, checkMsg(msg, args));
            }
        }

    }

    public static void w(String msg) {
        w(null, msg);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, String msg, Object args) {
        if (isOpenLog()) {
            if (TextUtils.isEmpty(tag)) {
                Log.i(TAG, checkMsg(msg, args));
            } else {
                Log.i(TAG + "_" + tag, checkMsg(msg, args));
            }
        }

    }

    public static void e(String msg) {
        e(null, msg);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, Exception e) {
        e(tag, checkMsg("Error", e), null);
    }

    public static void e(String tag, String msg, Object args) {
        if (isOpenLog()) {
            if (TextUtils.isEmpty(tag)) {
                Log.e(TAG, checkMsg(msg, args));
            } else {
                Log.e(TAG + "_" + tag, checkMsg(msg, args));
            }
        }

    }

    public static void wtf(String msg) {
        wtf(null, msg);
    }

    public static void wtf(String tag, String msg) {
        wtf(tag, msg, null);
    }

    public static void wtf(String tag, String msg, Object args) {
        if (isOpenLog()) {
            if (TextUtils.isEmpty(tag)) {
                Log.wtf(TAG, checkMsg(msg, args));
            } else {
                Log.wtf(TAG + "_" + tag, checkMsg(msg, args));
            }
        }

    }


    public static void json(String json) {
        json(null, json);
    }

    public static void json(String tag, String json) {
        if (isOpenLog()) {
            init();
            if (!TextUtils.isEmpty(tag)) {
                Logger.t(tag);
            }
            if (TextUtils.isEmpty(json)) {
                Logger.json(json);
            } else {
                Logger.e("json is null!!!");
            }
        }
    }

    public static void xml(String xml) {
        xml(null, xml);
    }

    public static void xml(String tag, String xml) {
        if (isOpenLog()) {
            init();
            if (!TextUtils.isEmpty(tag)) {
                Logger.t(tag);
            }
            if (TextUtils.isEmpty(xml)) {
                Logger.xml(xml);
            } else {
                Logger.e("xml is null!!!");
            }

        }
    }


    private static String checkMsg(String msg, Object arg) {
        if (TextUtils.isEmpty(msg)) {
            msg = DEFAULT_MSG;
        }
        if (arg != null) {
            msg = msg + "-->" + arg.toString();
        }
        return msg;
    }

    private L() {
    }
}