package com.asyncstorage;

import java.util.ArrayDeque;
import java.util.concurrent.Executor;

/**
 * Ported from upstream @react-native-async-storage/async-storage SerialExecutor.java.
 */
public class SerialExecutor implements Executor {
    private final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
    private Runnable mActive;
    private final Executor executor;

    public SerialExecutor(Executor executor) {
        this.executor = executor;
    }

    public synchronized void execute(final Runnable r) {
        mTasks.offer(new Runnable() {
            public void run() {
                try {
                    r.run();
                } finally {
                    scheduleNext();
                }
            }
        });
        if (mActive == null) {
            scheduleNext();
        }
    }

    synchronized void scheduleNext() {
        if ((mActive = mTasks.poll()) != null) {
            executor.execute(mActive);
        }
    }
}
