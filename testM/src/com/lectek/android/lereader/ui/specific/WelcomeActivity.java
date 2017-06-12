package com.lectek.android.lereader.ui.specific;

import gueei.binding.observables.IntegerObservable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.lectek.android.LYReader.R;
import com.lectek.android.app.AbsContextActivity;
import com.lectek.android.app.BaseApplication;
import com.lectek.android.lereader.account.AccountManager;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.model.bookShelf.BroadcastAdvertisementModel;
import com.lectek.android.lereader.binding.model.contentinfo.ScoreUploadModel;
import com.lectek.android.lereader.binding.model.search.SearchKeyWordModel;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.thread.internal.ITerminableThread;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.net.response.UpdateInfo;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.presenter.DownloadPresenterLeyue;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.dbase.util.UserInfoLeYueDB;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.utils.AES;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.lereader.utils.WelcomeImageLoader4Wo;
import com.lectek.android.update.UpdateManager;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;

/**
 * @description
	启动页面
 * @author chendt
 * @date 2013-12-6
 * @Version 1.0
 */
public class WelcomeActivity extends AbsContextActivity{
	public static final String  TAG = WelcomeActivity.class.getSimpleName();
	public final IntegerObservable bProgress = new IntegerObservable(0);
	public final IntegerObservable bProgressMax = new IntegerObservable(100);
    public static final String GOTO_CONTENTINFO = "book";
    public static final String GOTO_SUBJECTINFO = "project";
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

        if (interceptComeFromXinGe()){
            return;
        }

        setAndBindRootView(R.layout.welcome_layout, this);
        Drawable loginBg = WelcomeImageLoader4Wo.getWelcomeImageDrawable(true);
        if(loginBg != null){
            findViewById(R.id.login_logo_bg).setBackgroundDrawable(loginBg);
        }
        
        addNativeBookLeyue();
        MyAndroidApplication.addBackgroundTask(new Runnable() {
			
			@Override
			public void run() {
				checkClientUpdate(MyAndroidApplication.getInstance());
			}
		}, true);
        
        UserInfoLeYueDB.getInstance(WelcomeActivity.this);
        //待添加微信调用业务判断
        autoLogin();

		new ScoreUploadModel().start(UserScoreInfo.ANDROID_LOGIN,CommonUtil.getMyUUID(this));
        new BroadcastAdvertisementModel().start();
		new SearchKeyWordModel(){
            @Override
            protected ITerminableThread newThread(Runnable runnable){
                return ThreadFactory.createTerminableThreadInPool(runnable, ThreadPoolFactory.getBackgroundThreadPool());
            }
        }.start();
        startLeyue();
	} 

    /**
     * 拦截来自信鸽的通知跳转
     */
    private boolean interceptComeFromXinGe(){
        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
        if (click != null) {
            String customContent = click.getCustomContent();
            if (customContent != null && customContent.length() != 0) {
                try {
                    JSONObject json = new JSONObject(customContent);
                    if(json.has(GOTO_CONTENTINFO)){
                        String bookId = json.getString(GOTO_CONTENTINFO);
                        ContentInfoActivityLeyue.openActivity(this, bookId, true);
                        finish();
                        return true;
                    }else if(json.has(GOTO_SUBJECTINFO)){
                        String bookId = json.getString(GOTO_SUBJECTINFO);
                        if(bookId.contains("url")){
                            String targetUrl = bookId.substring(bookId.lastIndexOf("url=")+"url=".length());
                            Intent mIntent = new Intent(this,ThirdUrlActivity.class);
                            mIntent.putExtra(LeyueConst.GOTO_THIRD_PARTY_URL_TAG, targetUrl);
                            startActivity(mIntent);
                        }else{
                            Intent intent = new Intent(this,SubjectDetailActivity.class);
                            intent.putExtra(LeyueConst.GOTO_SUBJECT_DETAIL_TAG,
                                    LeyueConst.ASSERT_FOLDER_PATH.concat(LeyueConst.BOOK_CITY_SUBJECT_DETAIL_HTML)
                                            +"?url="+bookId+LeyueConst.GET_TITLE_TAG+"");
                            intent.putExtra(SubjectDetailActivity.NOTI_TAG, true);
                            startActivity(intent);
                        }
                        finish();
                        return true;
                    }
                    startActivity(new Intent(this, MyMessagesActivity.class));
                    finish();
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        XGPushManager.onActivityStoped(this);
        super.onDestroy();
    }
	
	/**乐阅onCreate操作*/
//	private void leyueOnCreate(){
//		new Thread(new Runnable() {
//					
//					@Override
//					public void run() {
//                        
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                
//                            }
//                        });
//						
//					}
//			}).start();
//	}
	
	private void addNativeBookLeyue(){
		boolean isFristEnter = PreferencesUtil.getInstance(getApplicationContext()).getIsFirstEnterApp(true);
		if(!isFristEnter)
			return;
		PreferencesUtil.getInstance(getApplicationContext()).setIsFirstEnterApp(false);
		DownloadInfo infoA = getDownloadInfo("download_info_jjl.txt");
		DownloadInfo infoB = getDownloadInfo("download_info_bbqner.txt");
		if (CommonUtil.isBookExist(infoA.contentID)) {
			infoA.state = 3;
		}else {
			DownloadPresenterLeyue.deleteDB(infoA);
		}
		if (CommonUtil.isBookExist(infoB.contentID)) {
			infoB.state = 3;
		}else {
			DownloadPresenterLeyue.deleteDB(infoB);
		}
		DownloadPresenterLeyue.addBookDownloadedInfoIfNotExit(infoA);
		DownloadPresenterLeyue.addBookDownloadedInfoIfNotExit(infoB);
	}
		
	/**
	 * 获取本地文件中的DownloadInfo json数据
	 * @param jsonFileName
	 * @return
	 */
	public static DownloadInfo getDownloadInfo(String jsonFileName){
		InputStream fis = null;
		DownloadInfo downloadInfo = new DownloadInfo();
		byte[] buf;
		try {
			fis = MyAndroidApplication.getInstance().getAssets().open(jsonFileName);
			buf = new byte[fis.available()];
			fis.read(buf);
			downloadInfo.fromJsonObject(new JSONObject(new String(buf,LeyueConst.UTF8)));
		} catch (Exception e) {
			try {
				if (fis!=null) {
					fis.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			try {
				if (fis!=null) {
					fis.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (downloadInfo!=null) {
			downloadInfo.timestamp = System.currentTimeMillis();
			downloadInfo.filePathLocation = Constants.BOOKS_DOWNLOAD + downloadInfo.contentID+".epub";
            downloadInfo.state = DownloadAPI.STATE_ONLINE;
			downloadInfo.secret_key = AES.encrypt(downloadInfo.secret_key);
//			DBS_ID = downloadInfo.contentID;
		}
		return downloadInfo;
	}
	
	/**针对用户退出应用后，自动登陆*/
	private void autoLogin() {
		//20140504新隐式登录注释原来逻辑 --wuwq
//		if (PreferencesUtil.getInstance(this).getIsLogin()) {
//			UserInfoLeyue info = new UserInfoLeyue();
//			info.setUserId(PreferencesUtil.getInstance(this).getUserId());
//			info.setAccount(PreferencesUtil.getInstance(this).getUserName());
//			info.setSource(PreferencesUtil.getInstance(this).getUserSource());
//			info.setNickName(PreferencesUtil.getInstance(this).getUserNickName());
//			info.setMobile(PreferencesUtil.getInstance(this).getBindPhoneNum());
//			info.setEmail(PreferencesUtil.getInstance(this).getBindEmail());
//			AccountManage.getInstence().setmUserInfo(info);
//		}
		//判断是否wap页面调用启动程序 
		Uri data = getIntent().getData();
        if (data != null){
            String scheme = data.getScheme();
//            String host = data.getHost();
            List<String> params = data.getPathSegments();
            if (scheme.equals("com.lectek.leyue.webchat") && params != null && params.size() ==2){
            	AccountManager.getInstance().requestUserInfo(params.get(0));
    			return;
            }
        }else{
        	AccountManager.getInstance().autoLogin();
        }
	}

	
	private void startLeyue(){
		new Thread(){
			int i = 1;
			@Override
			public void run() {
				for(;i <= 101;i++){
					if(isFinishing()){
						return;
					}
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(i <= 100){
								bProgress.set(i);
							}
						}	
					});
					try{
						if(i > 50){
							Thread.sleep(20);
						}else if(i <= 50){
							Thread.sleep(10);
						}
					}catch (Exception e) {}
				}
				gotoMainLeyue();
			}
		}.start();
	}

	
	/**
	 * 客户端版本检测
	 */
	public static void checkClientUpdate(Context context){
		UpdateInfo updateInfo = null;
			try {
				updateInfo = ApiProcess4Leyue.getInstance(context).checkUpdate();
			} catch (GsonResultException e) {
				e.printStackTrace();
			}
			if(updateInfo!=null){
				updateInfo.setUpdateMessage(filterBrString(updateInfo.getUpdateMessage()));
				LogUtil.v(TAG, "updateInfo:" + updateInfo.toString());
			}
		UpdateManager.setUpdateInfo(updateInfo);
	}
	
	/**
	 * 过滤带“< b r / >”的字符串
	 * @param targetStr
	 */
	private static String filterBrString(String targetStr){
		if(TextUtils.isEmpty(targetStr))
			return "";
		
		LogUtil.i("pinotao", targetStr);
		return targetStr.replaceAll("<br/>", "\n");
	}
	
	private void gotoMainLeyue(){
        if (PreferencesUtil.getInstance(WelcomeActivity.this).getIsFirstGuide()) {
            startActivity(new Intent(this, GuiderActivity.class));
        }else {
            int saveVersion = PreferencesUtil.getInstance(WelcomeActivity.this).getAppVersion();
            int currentVersion = CommonUtil.getVersionCode(WelcomeActivity.this);
            if (currentVersion > saveVersion){
                startActivity(new Intent(this, GuiderActivity.class));
            }else{
//                startActivity(new Intent(this, CoverActivity.class));
                startActivity(new Intent(this, SlideActivityGroup.class));
            }
        }
		finish();
	}	
	
}
