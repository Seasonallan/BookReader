package com.lectek.android.lereader.binding.model;

import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;

/**
 * 加载网络数据的后台线程 Model基类
 * @author linyiwei
 *
 * @param <Result>
 */
public abstract class BaseLoadNetDataBackgroundModel<Result> extends BaseLoadNetDataModel<Result>{

    @Override
    protected ITerminableThread newThread(Runnable runnable){
        return ThreadFactory.createTerminableThreadInPool(runnable, ThreadPoolFactory.getBackgroundThreadPool());
    }

}
