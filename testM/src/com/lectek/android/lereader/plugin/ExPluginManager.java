package com.lectek.android.lereader.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.LYReader.dialog.LeYueDialog;
import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.account.thirdPartApi.net.Callback;
import com.lectek.android.lereader.lib.net.http.AbsConnect;
import com.lectek.android.lereader.lib.storage.dbase.JsonArrayList;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.Plugin;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.ToastUtil;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * 插件管理
 * @author ljp
 */
public class ExPluginManager {

    public static abstract class PluginInstallCallback implements Callback {

        @Override
        public void onFail(int paramInt, String paramString) {
            ToastUtil.showToast(BaseApplication.getInstance(), "插件安装失败！");
        }

        @Override
        public void onCancel(int paramInt) {
            ToastUtil.showToast(BaseApplication.getInstance(), "操作取消！");
        }
    }

    private Callback mCallback;
    private BaseExPlugin mPlugin;
    private Activity mActivity;
    public ExPluginManager(Activity activity, int type, Callback callback){
        this.mActivity = activity;
        this.mPlugin = getPlugin(type);
        this.mCallback = callback;
        if (mPlugin ==null){
            throw new RuntimeException("error plugin type!");
        }
    }
    private Context getContext(){
        return BaseApplication.getInstance();
    }

    private BaseExPlugin getPlugin(int type){
        switch (type){
            case ExPluginType.PDF_SO:
                return new ExPluginPdfSo(BaseApplication.getInstance());
        }
        return null;
    }

    /**
     * 检测插件是否已经安装
     * @return
     */
    public boolean checkPlugin(){
        return mPlugin.check();
    }

    /**
     * 安装插件
     */
    public void install(){
        showInstallDialog();
    }

    private boolean isStop = false;
    private ProgressBar mProgressBar;
    private LeYueDialog mDialog;
    private TextView mDescTextView;
    private void showInstallDialog(){
        mDialog = new LeYueDialog(mActivity);
        View view = View.inflate(mActivity, R.layout.plugin_dialog_install, null);
        mDescTextView  = (TextView) view.findViewById(R.id.plugin_textview_des);
        mProgressBar = (ProgressBar) view.findViewById(R.id.plugin_progress_percent);
        mDescTextView.setText(mPlugin.getInstallDesc());
        mProgressBar.setVisibility(View.GONE);
        mDialog.setContentLay(view);
        mDialog.dealDialogBtn(R.string.plugin_install, new DialogUtil.ConfirmListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.setNoButton();
                        setDialogText("正在检测路径，请稍后！");
                        mProgressBar.setVisibility(View.VISIBLE);
                        mProgressBar.setIndeterminate(true);
                        ThreadFactory.createTerminableThread(new Runnable() {
                            @Override
                            public void run() {
                                JsonArrayList<Plugin> plugins = new JsonArrayList<Plugin>(Plugin.class);
                                String pluginStr = PreferencesUtil.getInstance(mActivity).getPlugin();
                                try {
                                    if (TextUtils.isEmpty(pluginStr)){
                                        plugins =  ApiProcess4Leyue.getInstance(getContext()).getPlugins();
                                    }else{
                                        plugins.fromJsonArray(new JSONArray(pluginStr));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (plugins != null && plugins.size() > 0){
                                    PreferencesUtil.getInstance(mActivity).setPluginStr(plugins.toJsonArray().toString());
                                    final Plugin plugin = mPlugin.getPlugin(plugins);
                                    if (plugin != null){
                                        BaseApplication.getHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgressBar.setIndeterminate(false);
                                                mProgressBar.setProgress(0);
                                            }
                                        });
                                        setDialogText("正在下载安装，请稍后！");
                                        startDownload(plugin.pluginFileUrl, mPlugin.getPath() , mPlugin.getName());
                                        return;
                                    }
                                } 
                            	onDownloadFail();
                            }
                        }).start();
                    }
                }, R.string.plugin_cancel, new DialogUtil.CancelListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                }, false
        );
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                mDialog.dismiss();
                mCallback.onCancel(-1);
                isStop = true;
            }
        });
        mDialog.setCancelable(true);
        mDialog.show();
    }

    private void setDialogText(final String text){
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mDescTextView.setText(text);
            }
        });
    }

    private void onDownloadSuccess(final String filePath) {
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess(filePath);
                mDialog.dismiss();
            }
        });
    }


    private void onDownloadCancel() {
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mCallback.onCancel(0);
                mDialog.dismiss();
            }
        });
    }

    private void onDownloadFail() {
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                mCallback.onFail(0, "");
                mDialog.dismiss();
            }
        });
    }

    protected void onDownloadProgressChange(final long currentBytes,final long totalBytes){
        mProgressBar.setProgress((int) (currentBytes * 100 / totalBytes));
    }

    public void startDownload(String url, String filePath, String fileName) {
        isStop = false;
        InputStream is = null;
        File file = null;
        file = new File(filePath+ "/"+ fileName.hashCode()+".temp");

        final DefaultHttpClient client = AbsConnect.getDefaultHttpClient(getContext());
        final HttpGet get = new HttpGet(url);
        try {
            int mCurrentBytes = 0;
            int mTotalBytes = 0;

            HttpResponse rp = client.execute(get);

            int statusCode = rp.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK
                    || statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
                Header h = rp.getFirstHeader("Content-Length");
                if (h != null) {
                    mTotalBytes = Integer.valueOf(h.getValue());
                }
                if(mTotalBytes < 0){
                    mTotalBytes = 0;
                }
                is = rp.getEntity().getContent();
                BufferedInputStream bis = new BufferedInputStream(is);

                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                if (file.isFile()) {
                    RandomAccessFile oSavedFile = new RandomAccessFile(file,
                            "rw");
                    oSavedFile.seek(0);
                    int bufferSize = 4096;
                    byte[] b = new byte[bufferSize];
                    int nRead;
                    long bytesNotified = mCurrentBytes;
                    long timeLastNotification = 0;
                    for (;;) {
                        if(isStop){
                            onDownloadCancel();
                            return;
                        }
                        nRead = bis.read(b, 0, bufferSize);

                        if (nRead == -1) {
                            break;
                        }
                        mCurrentBytes += nRead;
                        oSavedFile.write(b, 0, nRead);
                        long now = System.currentTimeMillis();
                        if (mCurrentBytes - bytesNotified > 4096
                                && now - timeLastNotification > 800) {
                            onDownloadProgressChange(mCurrentBytes , mTotalBytes);
                            bytesNotified = mCurrentBytes;
                            timeLastNotification = now;
                        }
                        Thread.sleep(10L);
                    }
                    oSavedFile.close();
                    if (( mTotalBytes != 0 && mCurrentBytes == mTotalBytes) || ( mTotalBytes == 0 && nRead == -1 ) ) {// 下载完成
                        file.renameTo(new File(filePath + "/"+ fileName));
                        onDownloadSuccess(filePath + "/"+fileName);
                    }else{// 下载没完全，即下载失败
                        onDownloadFail();
                    }
                }
            } else {// 没找到下载文件
                onDownloadFail();
            }
        } catch (Exception e) {
            onDownloadFail();
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                }
            }
        }
    }
}
