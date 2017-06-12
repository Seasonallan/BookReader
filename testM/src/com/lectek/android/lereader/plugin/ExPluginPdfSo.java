package com.lectek.android.lereader.plugin;

import java.io.File;
import java.util.List;

import android.content.Context;

import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.net.response.Plugin;

/**
 * PDF插件
 */
public class ExPluginPdfSo extends BaseExPlugin {

    public ExPluginPdfSo(Context context) {
        super(context);
    }

    @Override
    public Plugin getPlugin(List<Plugin> plugins) {
        for (Plugin plugin: plugins){
            if (plugin.pluginName.equalsIgnoreCase("PDF")){
                return plugin;
            }
        }
        return null;
    }

    @Override
    public boolean check() {
        File cacheFile = mContext.getCacheDir();
        File parent = cacheFile.getParentFile();
        File libSo = new File(parent + "/lib", getName());
        return FileUtil.isFileExists(getPath()+"/"+getName())
                || FileUtil.isFileExists(libSo.getAbsolutePath());
    }

    @Override
    public String getInstallDesc() {
        return "是否安装PDF插件？";
    }

    @Override
    public String getName() {
        return "libpdf.so";
    }

    @Override
    public String getPath() {
        return mContext.getCacheDir().getAbsolutePath();
    }


}
