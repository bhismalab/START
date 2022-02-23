package com.reading.start.sdk.utils;

import android.support.annotation.NonNull;

import com.reading.start.sdk.general.SdkLog;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProcessExecutor implements Executor {
    private static final String TAG = ProcessExecutor.class.getSimpleName();

    private static final int KEEP_ALIVE = 1;

    private final BlockingQueue<Runnable> mWorkQueue;

    private final ThreadPoolExecutor mThreadPoolExecutor;

    private final ThreadFactory mThreadFactory;

    private static ProcessExecutor sInstance = null;

    public ProcessExecutor(int poolSize) {
        mWorkQueue = new LinkedBlockingQueue<>();
        mThreadFactory = new JobThreadFactory();
        mThreadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize,
                KEEP_ALIVE, TimeUnit.SECONDS, mWorkQueue, mThreadFactory);
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        mThreadPoolExecutor.execute(runnable);
    }

    private static class JobThreadFactory implements ThreadFactory {
        private static final String THREAD_NAME = "start_sdk_";

        private int mCounter = 0;

        @Override
        public Thread newThread(@NonNull Runnable runnable) {
            Thread thread = new Thread(runnable, THREAD_NAME + mCounter++);
            thread.setPriority(Thread.MAX_PRIORITY);
            return thread;
        }
    }

    public static ProcessExecutor getInstance(int poolSize) {
        if (sInstance == null) {
            sInstance = new ProcessExecutor(poolSize);
        }

        return sInstance;
    }

    public static void reset() {
        try {
            if (sInstance != null) {
                sInstance.mWorkQueue.clear();
                sInstance = null;
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        }
    }
}