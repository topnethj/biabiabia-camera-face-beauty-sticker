package com.douyaim.qsapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDexApplication;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class LibApp extends MultiDexApplication {

    public static final String TAG = "LibApp";
    private static Context appContext;
    protected static Handler uiHandler = new Handler(Looper.getMainLooper());
    protected static ExecutorService threadPoolExecutor;
    private ThreadFactory mThreadFactory = new IFThreadFactory();

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        threadPoolExecutor = Executors.newCachedThreadPool(mThreadFactory);
    }

    public static void runOnUiThread(Runnable r) {
        uiHandler.post(r);
    }

    public static void poolExecute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }

    private static class IFThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        IFThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "iF_pool-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement() + r.getClass().getSimpleName(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
