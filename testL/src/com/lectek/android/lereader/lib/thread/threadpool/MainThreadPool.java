package com.lectek.android.lereader.lib.thread.threadpool;

import com.lectek.android.lereader.lib.thread.internal.AbsThreadPool;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.thread.internal.IThreadPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 主线程池，包含主要运行线程和后台线程。优先级区分
 * @deprecated
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 *
 */
public class MainThreadPool extends AbsThreadPool {

    /* 单例 */
    private static IThreadPool instance = new MainThreadPool();

    public static synchronized IThreadPool getInstance() {
        if (instance == null){
            instance = new MainThreadPool();
        }
        return instance;
    }

    @Override
    public int getCorePoolSize() {
		return 2;
	}

	@Override
    public int getMaximumPoolSize() {
		return 4;
	}

	@Override
    public long getKeepAliveTime() {
		return 500;
	}

	@Override
    public TimeUnit getTimeUnit() {
		return TimeUnit.MILLISECONDS;
	}

	@Override
    public BlockingQueue<Runnable> newQueue() {
		return new MainThreadQueue<Runnable>();
	}

    @Override
    public synchronized void destroy() {
        super.destroy();
        instance = null;
    }

    public class MainThreadQueue<Runnable> implements BlockingQueue<Runnable> {
        private ArrayList<Runnable> mainThreadQueues = new ArrayList<Runnable>();
        private ArrayList<Runnable> childThreadQueues = new ArrayList<Runnable>();
        @Override
        public boolean add(Runnable Runnable) {
            if (Runnable instanceof ITerminableThread){
               /* if (((ITerminableThread)Runnable).isMainThread()){
                    mainThreadQueues.add(Runnable);
                }else{
                    childThreadQueues.add(Runnable);
                }*/
            }else{
                mainThreadQueues.add(Runnable);
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends Runnable> Runnables) {
            for(Runnable runnable: Runnables){
                add(runnable);
            }
            return true;
        }

        @Override
        public void clear() {
            mainThreadQueues.clear();
            childThreadQueues.clear();
        }

        @Override
        public boolean offer(Runnable Runnable) {
            return add(Runnable);
        }

        @Override
        public Runnable remove() {
            if (mainThreadQueues.size() > 0){
                return mainThreadQueues.remove(0);
            }else if (childThreadQueues.size() > 0){
                return childThreadQueues.remove(0);
            }
            throw new NoSuchElementException();
        }

        @Override
        public Runnable poll() {
            if (mainThreadQueues.size() > 0){
                return mainThreadQueues.remove(0);
            }else if (childThreadQueues.size() > 0){
                return childThreadQueues.remove(0);
            }
            return null;
        }

        @Override
        public Runnable element() {
            if (mainThreadQueues.size() > 0){
                return mainThreadQueues.get(0);
            }else if (childThreadQueues.size() > 0){
                return childThreadQueues.get(0);
            }
            throw new NoSuchElementException();
        }

        @Override
        public Runnable peek() {
            if (mainThreadQueues.size() > 0){
                return mainThreadQueues.get(0);
            }else if (childThreadQueues.size() > 0){
                return childThreadQueues.get(0);
            }
            return null;
        }

        @Override
        public void put(Runnable Runnable) throws InterruptedException {
            add(Runnable);
        }

        @Override
        public boolean offer(Runnable Runnable, long l, TimeUnit timeUnit) throws InterruptedException {
            return add(Runnable);
        }

        @Override
        public Runnable take() throws InterruptedException {
            return poll();
        }

        @Override
        public Runnable poll(long l, TimeUnit timeUnit) throws InterruptedException {
            return poll();
        }

        @Override
        public int remainingCapacity() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean remove(Object o) {
            if (mainThreadQueues.remove(o)){
                return true;
            }
            if (childThreadQueues.remove(o)){
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> objects) {
            if (mainThreadQueues.removeAll(objects)){
                return true;
            }
            if (childThreadQueues.removeAll(objects)){
                return true;
            }
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> objects) {
            if (mainThreadQueues.retainAll(objects)){
                return true;
            }
            if (childThreadQueues.retainAll(objects)){
                return true;
            }
            return false;
        }

        @Override
        public int size() {
            return mainThreadQueues.size() + childThreadQueues.size();
        }

        @Override
        public Object[] toArray() {
            Object[] obj = new Object[size()];
            System.arraycopy(mainThreadQueues.toArray(), 0, obj, 0, mainThreadQueues.size());
            System.arraycopy(childThreadQueues.toArray(), 0, obj, mainThreadQueues.size(), childThreadQueues.size());
            return obj;
        }

        @Override
        public <T> T[] toArray(T[] ts) {
            return null;
        }

        @Override
        public boolean contains(Object o) {
            if (mainThreadQueues.contains(o)){
                return true;
            }
            if (childThreadQueues.contains(o)){
                return true;
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> objects) {
            if (mainThreadQueues.containsAll(objects)){
                return true;
            }
            if (childThreadQueues.containsAll(objects)){
                return true;
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return mainThreadQueues.size() == 0 && childThreadQueues.size() == 0;
        }

        @Override
        public Iterator<Runnable> iterator() {
            return mainThreadQueues.iterator();
        }

        @Override
        public int drainTo(Collection<? super Runnable> objects) {
            return size();
        }

        @Override
        public int drainTo(Collection<? super Runnable> objects, int i) {
            return size();
        }
    }


}
