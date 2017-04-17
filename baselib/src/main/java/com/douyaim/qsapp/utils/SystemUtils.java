package com.douyaim.qsapp.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

public class SystemUtils {
    public static final String TAG = "SystemUtils";
    private static String versionName = null;
    private static int versionCode = 0;

    private SystemUtils() {
    }

    public static void printStackTrace() {
        for (StackTraceElement element : new Throwable().getStackTrace()) {
            L.e(TAG, element.toString());
        }
    }

    /**
     * 隐藏系统键盘
     *
     * @param activity
     */
    public static void hideKeyboardFromActivity(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = activity.getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void hideKeyboardFromFragment(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }


    public static boolean aboveApiLevel(int sdkInt) {
        return getApiLevel() >= sdkInt;
    }

    public static int getApiLevel() {
        return Build.VERSION.SDK_INT;
    }

    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static boolean isExternalSDCardMounted() {
        if (Build.VERSION.SDK_INT < 11) {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        } else {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    && !Environment.isExternalStorageEmulated();
        }
    }

    public static String getMacAddress(Context context) {
        WifiManager wifi = null;
        try {
            wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        } catch (Throwable th) {
            // in some special case or rom, it maybe throw exception.
            th.printStackTrace();
        }
        if (wifi == null) {
            return null;
        }
        WifiInfo info = null;
        try {
            // here maybe throw exception in android framework
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (info != null) {
            return info.getMacAddress();
        } else {
            return null;
        }
    }

    public static void testWifiInfo(Context context) {
        WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            String netName = wifiInfo.getSSID(); //获取被连接网络的名称
            String netMac = wifiInfo.getBSSID(); //获取被连接网络的mac地址
            String localMac = wifiInfo.getMacAddress();// 获得本机的MAC地址
            L.d(TAG, "netName:" + netName);   //---netName:HUAWEI MediaPad
            L.d(TAG, "netMac:" + netMac);     //---netMac:78:f5:fd:ae:b9:97
            L.d(TAG, "localMac:" + localMac); //---localMac:BC:76:70:9F:56:BD
        }
    }

    public static String getWifiIPAddress(Context context) {
        try {
            WifiManager mgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            if (mgr == null) {
                return null;
            }

            WifiInfo info = mgr.getConnectionInfo();
            if (info == null) {
                return null;
            }
            // if (info.getSSID() == null) return null;

            int ipAddress = info.getIpAddress();
            if (ipAddress == 0) {
                return null;
            }

            String ip = String.format(Locale.US, "%d.%d.%d.%d", (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));

            return ip;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSecureAndroidID(Context context) {
        return Secure
                .getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    public static String getSdkVersion() {
        try {
            return Build.VERSION.SDK;
        } catch (Exception e) {
            e.printStackTrace();
            return String.valueOf(getSdkVersionInt());
        }
    }

    public static String getSdkReleaseVersion() {
        try {
            return Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
            return getSdkVersion();
        }
    }

    public static int getSdkVersionInt() {
        try {
            return Build.VERSION.SDK_INT;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * if the external storage device which is emulated, that mean the devices
     * does not have real external storage ,result includes that devices.
     *
     * @return
     */
    public static long getAvailableExternalStorage() {
        try {
            File file = Environment.getExternalStorageDirectory();
            if (file != null && file.exists()) {
                StatFs sdFs = new StatFs(file.getPath());
                if (sdFs != null) {
                    long sdBlockSize = sdFs.getBlockSize();
                    long sdAvailCount = sdFs.getAvailableBlocks();
                    return sdAvailCount * sdBlockSize;
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long getTotalExternalMemorySize() {
        try {
            File file = Environment.getExternalStorageDirectory();
            if (file != null && file.exists()) {
                StatFs sdFs = new StatFs(file.getPath());
                long sdBlockSize = sdFs.getBlockSize();
                long sdTotalCount = sdFs.getBlockCount();
                return sdTotalCount * sdBlockSize;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getAvailableInternalStorage() {
        File file = Environment.getDataDirectory();
        if (file.exists()) {
            StatFs sdFs = new StatFs(file.getPath());
            long sdBlockSize = sdFs.getBlockSize();
            long sdAvailCount = sdFs.getAvailableBlocks();
            return sdAvailCount * sdBlockSize;
        }
        return 0;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        if (path != null && path.exists()) {
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        }
        return 0;
    }

    public static boolean checkSdCardStatusOk() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean checkAvailableInternalStorage(long size) {
        long availableStorage = getAvailableInternalStorage();
        // if apkSize is -1 , do not check
        if (size < 0) {
            return true;
        }
        if (availableStorage <= 0) {
            return false;
        }
        return availableStorage >= size;
    }

    public static boolean checkAvailableExternalStorage(long size) {
        long availableStorage = getAvailableExternalStorage();
        // if apkSize is -1 , do not check
        if (size < 0) {
            return true;
        }
        if (availableStorage <= 0) {
            return false;
        }
        return availableStorage >= size;
    }

    public static boolean checkSpaceEnough(String path, InstallOption installOpition) {

        if (TextUtils.isEmpty(path) || installOpition == null) {
            return false;
        }
        if (installOpition == InstallOption.AUTO) {
            return true;
        }
        File file = new File(path);
        if (installOpition == InstallOption.INTERNAL) {
            return checkAvailableInternalStorage(file.length());
        }
        if (installOpition == InstallOption.EXTERNAL) {
            return checkAvailableStorage(file.length());
        }

        return false;
    }

    public static boolean checkAvailableStorage(long size) {
        long availableStorage = getAvailableExternalStorage();
        // if apkSize is -1 , do not check
        if (size < 0) {
            return true;
        }
        if (availableStorage <= 0) {
            return false;
        }
        return availableStorage >= size;
    }

    public static int getVersionCode(Context context) {
        if (versionCode != 0) {
            return versionCode;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getNonNullModel() {
        if (TextUtils.isEmpty(Build.MODEL)) {
            return "";
        } else {
            return Build.MODEL;
        }
    }

    public static IBinder invokeGetService(String name) {
        Method method;
        try {
            method = Class.forName("android.os.ServiceManager").getMethod(
                    "getService", String.class);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        IBinder binder;
        try {
            binder = (IBinder) method.invoke(null, name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
        return binder;
    }

    /**
     * Gets external cache dir.
     *
     * @return cache dir. Can be null is external storage is unmounted
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getDeviceExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return context.getExternalCacheDir();
        } else {
            return new File(Environment.getExternalStorageDirectory() + "/Android/data/"
                    + context.getPackageName() + "/cache/");
        }
    }

    public static String getImei(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            // In some devices, we are not able to get device id, and may cause some exception,
            // so catch it.
            return "";
        }
    }

    public static String getVersionName(Context context) {
        if (versionName == null) {
            PackageInfo packageInfo = getPackageInfo(context, context.getPackageName(), 0);
            if (packageInfo != null) {
                versionName = packageInfo.versionName;
            } else {
                versionName = "";
            }

        }
        return versionName;
    }

    public static PackageInfo getPackageInfo(Context context, String packageName, int flag) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, flag);
        } catch (NameNotFoundException | RuntimeException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * get user config locale, if null, return default locale.
     *
     * @param context
     * @return locale
     */
    public static Locale getLocale(Context context) {
        Locale locale = null;
        try {
            Configuration userConfig = new Configuration();
            Settings.System.getConfiguration(context.getContentResolver(), userConfig);
            locale = userConfig.locale;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }

    /**
     * get android id, use content provider, don't use this method on main thread.
     *
     * @param context
     * @return android id
     */
    public static String getAndroidId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    @TargetApi(9)
    public static boolean isPrimaryExternalStorageRemoveable() {
        if (getApiLevel() >= 9) {
            return Environment.isExternalStorageRemovable();
        } else {
            return true;
        }
    }


    public static int getScreenHeight(WindowManager windowManager) {
        int heightPixels = 0;
        Display defaultDisplay = windowManager.getDefaultDisplay();
        if (aboveApiLevel(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                && !aboveApiLevel(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            try {
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(defaultDisplay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (aboveApiLevel(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            android.graphics.Point realSize = new android.graphics.Point();
            defaultDisplay.getRealSize(realSize);
            heightPixels = realSize.y;
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            defaultDisplay.getMetrics(metrics);
            heightPixels = metrics.heightPixels;
        }
        return heightPixels;
    }

    public static int getScreenWidth(WindowManager windowManager) {
        int widthPixels = 0;
        Display defaultDisplay = windowManager.getDefaultDisplay();
        if (aboveApiLevel(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                && !aboveApiLevel(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(defaultDisplay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (aboveApiLevel(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            android.graphics.Point realSize = new android.graphics.Point();
            defaultDisplay.getRealSize(realSize);
            widthPixels = realSize.x;
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            defaultDisplay.getMetrics(metrics);
            widthPixels = metrics.widthPixels;
        }
        return widthPixels;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static String getScreenResolution(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) (context
                .getSystemService(Context.WINDOW_SERVICE));
        String resolution = "unknown";
        if (wm != null && wm.getDefaultDisplay() != null) {
            wm.getDefaultDisplay().getMetrics(metrics);
            if (aboveApiLevel(Build.VERSION_CODES.FROYO)) {
                switch (wm.getDefaultDisplay().getRotation()) {
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        resolution = String.valueOf(metrics.heightPixels) + "*"
                                + String.valueOf(metrics.widthPixels);
                        break;
                    default:
                        resolution = String.valueOf(metrics.widthPixels) + "*"
                                + String.valueOf(metrics.heightPixels);
                }
            } else {
                resolution = String.valueOf(metrics.widthPixels) + "*"
                        + String.valueOf(metrics.heightPixels);
            }
        }
        return resolution;
    }

    public static boolean hasSoftKeys(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
        return false;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    public enum InstallOption {
        AUTO, EXTERNAL, INTERNAL, ERROR
    }

    /**
     * check if the mobile has been rooted
     *
     * @return the mobile has been rooted
     * @throws IOException
     * @author TQS
     */
    public static boolean isRooted() {
        boolean rooted = false;
        boolean hasSuFile = false;
        String command = "ls -l /%s/su";
        File su = new File("/system/bin/su");
        if (su.exists()) {
            hasSuFile = true;
            command = String.format(command, "system/bin");
        } else {
            su = new File("/system/xbin/su");
            if (su.exists()) {
                hasSuFile = true;
                command = String.format(command, "system/xbin");
            } else {
                su = new File("/data/bin/su");
                if (su.exists()) {
                    hasSuFile = true;
                    command = String.format(command, "data/bin");
                }
            }
        }

        if (hasSuFile) {
            rooted = true;
        }

        return rooted;
    }

    public static String getCPU() {
        String cpuInfo = null;
        FileReader fileReader;
        BufferedReader in = null;
        try {
            fileReader = new FileReader("/proc/cpuinfo");
            try {
                in = new BufferedReader(fileReader, 1024);
                cpuInfo = in.readLine();
            } catch (IOException e) {
                return "unknown";
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    fileReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            return "unknown";
        }

        if (cpuInfo != null) {
            int start = cpuInfo.indexOf(':') + 1;
            cpuInfo = cpuInfo.substring(start);
            return cpuInfo.trim();
        }
        return "unknown";
    }

    public static String getWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return null;
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return null;
        }

        String ssid = wifiInfo.getSSID();
        if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }

        return ssid;
    }

    public static String getSystemDisplayId() {
        if (TextUtils.isEmpty(Build.DISPLAY)) {
            return "";
        } else {
            return Build.DISPLAY;
        }
    }

    public static String getBrand() {
        if (TextUtils.isEmpty(Build.BRAND)) {
            return "";
        } else {
            return Build.BRAND;
        }
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    public static int getMetricsSize(WindowManager windowManager) {
        if (windowManager == null) {
            return 0;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

    @TargetApi(Build.VERSION_CODES.DONUT)
    public static String getDpi(WindowManager windowManager) {
        if (windowManager == null) {
            return "";
        }
        int densityDpi = getMetricsSize(windowManager);
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhpdi";
            case DisplayMetrics.DENSITY_XXHIGH:
            default:
                return "xxhdpi";
        }
    }

    /**
     * @param context
     * @param px
     * @return
     */
    public static float px2dp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    /**
     * @param context
     * @param dp
     * @return
     */
    public static float dp2px(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    /**
     * get current process name
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        int myPid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (process.pid == myPid) {
                return process.processName;
            }
        }
        return "";
    }

    public static boolean isX86() {
        return Build.CPU_ABI.equals("x86");
    }

    public static String getMetaData(Context context, String key) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo app = pm.getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            return bundle.getString(key);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            return packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isActivityAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, 0);
        return !activities.isEmpty();
    }

    public static int getStatusBarHeight(Context context) {
        try {
            int resourceId = context.getResources()
                    .getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
            return -1;
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static long calcPathSize(String path) {
        if (TextUtils.isEmpty(path)) {
            return 0;
        }
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }
        long size = 0;
        if (file.isDirectory()) {
            File[] childList = file.listFiles();
            if (childList == null) {
                return 0;
            }
            for (File childFile : childList) {
                try {
                    size += calcPathSize(childFile.getAbsolutePath());
                } catch (StackOverflowError e) {
                    // too many recursion may cause stack over flow
                    e.printStackTrace();
                    return size;
                } catch (OutOfMemoryError e2) {
                    // too many call filenamesToFiles method
                    e2.printStackTrace();
                    return size;
                }
            }
        } else {
            size = file.length();
        }
        return size;
    }

    public static boolean isAppturboUnlockable(Context context) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo pi : packages) {
            if (pi.packageName.equalsIgnoreCase("com.appturbo.appturboCA2015")
                    || pi.packageName.equalsIgnoreCase("com.appturbo.appoftheday2015")) {
                return true;
            }
        }
        return false;
    }

    public static String getAppVersion(Context c) {
        String appVersion = "";
        PackageInfo pInfo = null;
        try {
            pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            String versionName = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            appVersion = versionName + "." + versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return appVersion;
    }

    public static String getSimName(Context c) {
        try {
            TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimOperatorName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentNetType(Context context) {
        String type = "";
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            type = "null";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = "wifi";
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA
                    || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = "2g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
                    || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = "3g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                type = "4g";
            }
        }
        return type;
    }

    /**
     * 通过域名获取ip地址
     *
     * @param host
     * @return
     */
    public static String getHostAddress(String host) {
        String IPAddress = "";
        InetAddress ReturnStr = null;
        try {
            ReturnStr = java.net.InetAddress.getByName(host);
            IPAddress = ReturnStr.getHostAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return IPAddress;
        }
        return IPAddress;
    }
}
