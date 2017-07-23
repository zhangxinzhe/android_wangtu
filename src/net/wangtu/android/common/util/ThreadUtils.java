package net.wangtu.android.common.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 用来执行任务的线程工具，内包含了一个线程池
 * 
 * @author xuan
 * @version $Revision: 25629 $, $Date: 2015-10-15 16:25:34 +0800 (周四, 15 十月 2015) $
 */
public abstract class ThreadUtils {

    /**
     * 执行延迟任务，类似Timer的效果
     */
    private static ScheduledExecutorService scheduleThreadPool;

    private static ScheduledExecutorService getExecutor() {
        if (scheduleThreadPool == null) {
            scheduleThreadPool = Executors.newScheduledThreadPool(20);
        }
        return scheduleThreadPool;
    }

    /**
     * 延后执行任务
     * 
     * @param task
     * @param delay
     */
    public static void schedule(Runnable task, long delay) {
        getExecutor().schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 立即执行任务
     *
     * @param task
     */
    public static void schedule(Runnable task) {
        getExecutor().submit(task);
    }

    public static void resetScheduleThreadPool() {
        if (scheduleThreadPool != null && !scheduleThreadPool.isShutdown()) {
            scheduleThreadPool.shutdown();
        }
        scheduleThreadPool = null;
    }

}
