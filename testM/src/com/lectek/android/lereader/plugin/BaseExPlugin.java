package com.lectek.android.lereader.plugin;

import android.content.Context;

import com.lectek.android.lereader.net.response.Plugin;
import com.umeng.socialize.net.ab;

import java.util.List;

/**
 * 插件基类
 */
public abstract class BaseExPlugin {

    protected Context mContext;
    public BaseExPlugin(Context context){
        this.mContext = context;
    }

    /**
     * 获取插件信息
     * @param plugins
     * @return
     */
    public abstract Plugin getPlugin(List<Plugin> plugins);

    /**
     * 检测插件是否已经安装
     * @return
     */
    public abstract boolean check();

    /**
     * 获取安装提示
     * @return
     */
    public abstract String getInstallDesc();

    /**
     * 获取插件文件名
     * @return
     */
    public abstract String getName();

    /**
     * 获取插件安装路径
     * @return
     */
    public abstract String getPath();
}
