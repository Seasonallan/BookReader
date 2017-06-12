/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.lectek.android.LYReader.pay.alipay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.recharge.alipay.AlixDefine;
import com.lectek.android.lereader.lib.recharge.alipay.AlixId;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;

public class MobileSecurePayHelper {
	static final String TAG = "MobileSecurePayHelper";
	public static final String ALIPAY_PLUGIN_NAME = "alipay_msp.apk";
	private ProgressDialog mProgress = null;
	Activity mContext = null;

	public MobileSecurePayHelper(Activity context) {
		this.mContext = context;
	}

	public boolean detectMobile_sp() {
		boolean isMobile_spExist = isMobile_spExist();
		if (!isMobile_spExist) {
			// get the cacheDir.
			File cacheDir = mContext.getCacheDir();
			final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
			LogUtil.v(TAG, "cachePath: " + cachePath);

			File file = new File(cachePath);
			if (file.exists()) {
				file.delete();
			}
//			 捆绑安装
			retrieveApkFromAssets(mContext, ALIPAY_PLUGIN_NAME,cachePath);

			mProgress = BaseHelper.showProgress(mContext, null, "正在检测安全支付服务版本", false, true);

			new Thread(new Runnable() {

				@Override
				public void run() {
					// 检测是否有新的版本。
					PackageInfo apkInfo = getApkInfo(mContext, cachePath);
					String newApkdlUrl = null;
					try {
						newApkdlUrl = checkNewUpdate(apkInfo);
						LogUtil.e("----newApkdlUrl = "+newApkdlUrl);
						// 动态下载
						if (newApkdlUrl != null) {
							retrieveApkFromNet(mContext, newApkdlUrl, cachePath);
							Message msg = new Message();
							msg.what = AlixId.RQF_INSTALL_CHECK;
							msg.obj = cachePath;
							mHandler.sendMessage(msg);
						} else{
							//安装本地
							Message msg = mHandler.obtainMessage(AlixId.RQF_INSTALL_WITHOUT_DOWNLOAD, cachePath);
							mHandler.sendMessage(msg);
						}
					} catch (JSONException e) {
						LogUtil.v(TAG, "detectMobile_sp: " + e.getMessage());
						e.printStackTrace();
						Message msg = mHandler.obtainMessage(AlixId.RQF_INSTALL_WITHOUT_DOWNLOAD, cachePath);
						mHandler.sendMessage(msg);
					}
					// send the result back to caller.
				}
			}).start();
		}
		// else ok.

		return isMobile_spExist;
	}

	public void showInstallConfirmDialog(final Activity context, final String cachePath) {
		ConfirmListener confirmListener = new ConfirmListener() {
			
			@Override
			public void onClick(View v) {
				// 修改apk权限
				BaseHelper.chmod("777", cachePath);
				try {
                   Thread.sleep(500);
                } catch (InterruptedException e) {
                   e.printStackTrace();
                }

                LogUtil.v(TAG, "showInstallConfirmDialog cachePath: " + cachePath);
				// install the apk.
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.parse("file://" + cachePath), "application/vnd.android.package-archive");
				context.startActivity(intent);
			}
		};
		DialogUtil.commonConfirmDialog(context, null, R.string.alipay_confirm_install, confirmListener);
//		final AlertDialog.Builder tDialog = new AlertDialog.Builder(context);
//		tDialog.setIcon(R.drawable.alipay_info);
//		tDialog.setTitle(context.getResources().getString(R.string.confirm_install_hint));
//		tDialog.setMessage(context.getResources().getString(R.string.alipay_confirm_install));
//
//		tDialog.setPositiveButton(R.string.ensure_recharge,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						// 修改apk权限
//						BaseHelper.chmod("777", cachePath);
//						try {
//                           Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                           e.printStackTrace();
//                        }
//
//                        LogUtil.v(Tag, "showInstallConfirmDialog cachePath: " + cachePath);
//						// install the apk.
//						Intent intent = new Intent(Intent.ACTION_VIEW);
//						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						intent.setDataAndType(Uri.parse("file://" + cachePath), "application/vnd.android.package-archive");
//						context.startActivity(intent);
//					}
//				});
//
//		tDialog.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//			}
//		});
//
//		tDialog.show();
	}

	public boolean isMobile_spExist() {
		PackageManager manager = mContext.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.alipay.android.app"))
				return true;
		}

		return false;
	}

	// 捆绑安装
	public boolean retrieveApkFromAssets(Context context, String fileName, String path) {
		boolean bRet = false;

		try {
			InputStream is = context.getAssets().open(fileName);

			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	}

	/**
	 * 获取未安装的APK信息
	 * 
	 * @param context
	 * @param archiveFilePath
	 *            APK文件的路径。如：/sdcard/download/XX.apk
	 */
	public static PackageInfo getApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_META_DATA);
		return apkInfo;
	}

	// 检查是否有新的版本，如果有，返回apk的下载地址。
	public String checkNewUpdate(PackageInfo packageInfo) throws JSONException {
		String url = null;

		JSONObject resp = null;
		if (null != packageInfo) {
			LogUtil.i(TAG, "packageInfo versionName: " + packageInfo.versionName);
			LogUtil.i(TAG, "packageInfo packageName: " + packageInfo.packageName);
			if (null != packageInfo.applicationInfo) {
				LogUtil.i(TAG, "packageInfo " + packageInfo.applicationInfo.packageName);
			}
			LogUtil.v(TAG, "packageInfo string: " + packageInfo.toString());
			resp = sendCheckNewUpdate(packageInfo.versionName);
		} else {
			LogUtil.v(TAG, "packageInfo default version: 2.0.0");
			resp = sendCheckNewUpdate("2.0.0");
		}
		if (null != resp) {
			LogUtil.v(TAG, "checkNewUpdate resp: " + resp.toString());
			if (resp.getString("needUpdate").equalsIgnoreCase("true")) {
				url = resp.getString("updateUrl");
				LogUtil.v(TAG, "alipay update url: " + url);
			}
		}

		return url;
	}

	public JSONObject sendCheckNewUpdate(String versionName) throws JSONException {
		JSONObject objResp = null;
		JSONObject req = new JSONObject();
		req.put(AlixDefine.action, AlixDefine.actionUpdate);

		JSONObject data = new JSONObject();
		data.put(AlixDefine.platform, "android");
		data.put(AlixDefine.VERSION, versionName);
		data.put(AlixDefine.partner, "");
		LogUtil.i(TAG, "sendCheckNewUpdate data: " + data.toString());

		req.put(AlixDefine.data, data);
		LogUtil.i(TAG, "sendCheckNewUpdate req: " + req.toString());

		objResp = sendRequest(req.toString());

		return objResp;
	}

	public JSONObject sendRequest(final String content) throws JSONException {
		NetworkManager nM = new NetworkManager(this.mContext);

		LogUtil.i(TAG, "sendRequest content: " + content);
		//
		JSONObject jsonResponse = null;
		String response = null;

		synchronized (nM) {
			response = nM.SendAndWaitResponse(content, Constant.server_url);
		}

		LogUtil.i(TAG, "sendRequest wait Response");
		String jsonData = null;
		if (!TextUtils.isEmpty(response)) {
			LogUtil.i(TAG, "sendRequest Response: " + response);
			int startIndex = response.indexOf("{");
			if (startIndex > 0) {
				int endIndex = response.indexOf("}");
				if (endIndex > startIndex) {
					jsonData = response.substring(startIndex, endIndex + 1);
				}
			} else {
				jsonData = response;
			}
		}
		if (!TextUtils.isEmpty(jsonData)) {
			LogUtil.i(TAG, "sendRequest jsonData: " + jsonData);
			jsonResponse = new JSONObject(jsonData);
		}

		if (jsonResponse != null) {
			LogUtil.i(TAG, "sendRequest jsonResponse: " + jsonResponse.toString());
		}

		return jsonResponse;
	}

	// 动态下载
	public boolean retrieveApkFromNet(Context context, String strurl, String filename) {
		boolean bRet = false;

		try {
			NetworkManager nM = new NetworkManager(this.mContext);
			bRet = nM.urlDownloadToFile(context, strurl, filename);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bRet;
	}

	// close the progress bar
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// the handler use to receive the install check result.
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case AlixId.RQF_INSTALL_CHECK: 
				case AlixId.RQF_INSTALL_WITHOUT_DOWNLOAD:
					closeProgress();
					String cachePath = (String) msg.obj;
					LogUtil.e("----msg.what = "+msg.what);
					LogUtil.v(TAG, "Alipay cachePath: " + cachePath);
					showInstallConfirmDialog(mContext, cachePath);
//					if (isDownLoadSuccuseed) {
//					} else {
//						BaseHelper.showDialog(mContext, null, "下载失败，请稍后再试！", R.drawable.alipay_info);
//					}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
