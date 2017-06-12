package com.lectek.android.lereader.lib.thread.internal;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 代表一个线程池
 *   运作机制
 * 1、线程池刚创建时，里面没有一个线程。任务队列是作为参数传进来的。不过，就算队列里面有任务，线程池也不会马上执行它们
 * 2、当调用 execute() 方法添加一个任务时，线程池会做如下判断：
 * a. 如果正在运行的线程数量小于 corePoolSize，那么马上创建线程运行这个任务；
 * b. 如果正在运行的线程数量大于或等于 corePoolSize，那么将这个任务放入队列。
 * c. 如果这时候队列满了，而且正在运行的线程数量小于 maximumPoolSize，那么还是要创建线程运行这个任务；
 * d. 如果队列满了，而且正在运行的线程数量大于或等于 maximumPoolSize，那么线程池会抛出异常，告诉调用者“我不能再接受任务了”。
 * 3、当一个线程完成任务时，它会从队列中取下一个任务来执行。
 * 4、当一个线程无事可做，超过一定的时间（keepAliveTime）时，线程池会判断，如果当前运行的线程数大于 corePoolSize，那么这个线程就被停掉。所以线程池的所有任务完成后，它最终会收缩到 corePoolSize 的大小。
 * 这个过程说明，并不是先加入任务就一定会先执行。假设队列大小为 4，corePoolSize为2，maximumPoolSize为6，那么当加入15个任务时，执行的顺序类似这样：
 * 首先执行任务 1、2，然后任务3~6被放入队列。这时候队列满了，任务7、8、9、10 会被马上执行，而任务 11~15 则会抛出异常。
 * 最终顺序是：1、2、7、8、9、10、3、4、5、6。当然这个过程是针对指定大小的ArrayBlockingQueue<Runnable>来说，
 * 如果是LinkedBlockingQueue<Runnable>，因为该队列无大小限制，所以不存在上述问题。
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 *
 */
public interface IThreadPool { 
    /**
     * 核心线程数，指保留的线程池大小（不超过maximumPoolSize值时，线程池中最多有corePoolSize 个线程工作）。
     */
    public abstract int getCorePoolSize();

    /**
     * 指的是线程池的最大大小（线程池中最大有corePoolSize 个线程可运行）。
     */
    public abstract int getMaximumPoolSize();

    /**
     * 指的是空闲线程结束的超时时间（当一个线程不工作时，过keepAliveTime 长时间将停止该线程）。
     */
    public abstract long getKeepAliveTime();

    /**
     * 是一个枚举，表示 keepAliveTime 的单位（有NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS，7个可选值）。
     */
    public abstract TimeUnit getTimeUnit();

    /**
     * 表示存放任务的队列（存放需要被线程池执行的线程队列）。  如果是LinkedBlockingQueue<Runnable>，队列无大小限制
     */
    public abstract BlockingQueue<Runnable> newQueue();

    /**
     * 添加任务进队列
     * @param newTask
     */
    public abstract void executeTask(Runnable newTask);

    /**
     * 销毁线程池
     */
    public abstract void destroy();

    /**
     * 移除线程
     * @param thread
     */
    public abstract void removeThread(Runnable thread);
}
