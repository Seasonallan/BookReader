package com.lectek.android.lereader.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.binding.viewModel.BaseDialog;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.utils.BitmapUtil;
import com.lectek.android.lereader.lib.utils.ClientInfoUtil;
import com.lectek.android.lereader.lib.utils.DateUtil;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.lib.utils.UniqueIdUtils;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.ScoreUploadResponseInfo;
import com.lectek.android.lereader.permanent.ApiConfig;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.permanent.LeyueConst;
import com.lectek.android.lereader.share.util.UmengShareUtils;
import com.lectek.android.lereader.storage.dbase.UserScoreInfo;
import com.lectek.android.lereader.storage.sprefrence.PreferencesUtil;
import com.lectek.android.lereader.ui.specific.ContentInfoActivityLeyue;

/**
 * 一些通用的方法
 * 
 * @author mingkg21
 * @date 2010-4-5
 * @email mingkg21@gmail.com
 */
public class CommonUtil {

	private static final String TAG = CommonUtil.class.getSimpleName();

	/**
	 * 退出程序
	 * 
	 * @param context
	 */
	public static void exitApplication(Context context) {
		context.sendBroadcast(new Intent(AppBroadcast.ACTION_CLOSE_APP));
	}
	
	public static String formatVoiceTotalLen(int len){
		StringBuilder sb = new StringBuilder();
		int hour = len / 3600;
		sb.append(formatTime(hour));
		sb.append(":");
		int min = (len - hour * 3600) / 60;
		sb.append(formatTime(min));
		sb.append(":");
		int m = (len - hour * 3600 - min * 60) % 60;
		sb.append(formatTime(m));
		return sb.toString();
	}
	
	private static StringBuilder formatTime(int time){
		StringBuilder sb = new StringBuilder();
		if(time == 0){
			sb.append("00");
		} else if(time > 9){
			sb.append(time);
		} else {
			sb.append("0");
			sb.append(time);
		}
		return sb;
	}

//	/**
//	 * 判断是否有SDCARD
//	 * 
//	 * @return
//	 */
//	public static boolean isSDcardExist() {
//		if (Environment.getExternalStorageState().equals(
//				Environment.MEDIA_MOUNTED)) {
//			return true;
//		} else {
//			return false;
//		}
//	}

//	/**
//	 * 判断某个文件是否存在
//	 * 
//	 * @param filePath
//	 * @return
//	 */
//	public static boolean isFileExists(String filePath) {
//		if (!isSDcardExist()) {
//			return false;
//		}
//		File file = new File(filePath);
//		if (file != null && file.exists()) {
//			if(file.length() == 0){
//				file.delete();
//				return false;
//			}
//			return true;
//		}
//		return false;
//	}
	
//	/**
//	 * 获取存储卡的可用空间
//	 * 
//	 * @return
//	 */
//	public static long getStorageSize() {
//		if (isSDcardExist()) {
//			StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
//					.getAbsolutePath());
//			long blockSize = stat.getBlockSize();
//			return stat.getAvailableBlocks() * blockSize;
//		}
//		return 0;
//	}

	/**
	 * 判断合法的EMAIL
	 * 
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email) {
		if(TextUtils.isEmpty(email)) {
			return false;
		}
		String check = "\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]{2,3}$";
		try {
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LogUtil.e("checkIsEmail error:" + email);
			return false;
		}
	}
	
	public static String subEndString(final String src, String startStr, String endStr) {
		String newStr = null;
		if (TextUtils.isEmpty(src)) {
			return null;
		}
		int start = src.lastIndexOf(startStr);
		if (start < 0) {
			return null;
		}
		int end = src.lastIndexOf(endStr);
		if (end < 0 || end < start) {
			return null;
		}
		if (start == 0 && end == 0) {
			newStr = src;
		} else {
			newStr = src.substring(start + 1, end);
		}
		
		return newStr; 
	}

	public static Bitmap getImageInSdcard(Context context, String imageName, boolean autoClip) {
		Bitmap result = null;
		String filePath = Constants.BOOKS_TEMP_IMAGE + imageName;
		if (!FileUtil.isFileExists(filePath)) {
			return null;
		}
		
		if(autoClip) {
			
			InputStream is = null;
			try {
				is = new FileInputStream(filePath);
				result = BitmapUtil.clipScreenBoundsBitmap(context.getResources(), is);
			}catch(FileNotFoundException fnf) {
				fnf.printStackTrace();
			}catch(IOException ioe) {
				ioe.printStackTrace();
			}finally {
				if(is != null) {
					try {
						is.close();
					}catch(IOException ioe) {}
				}
			}
		}
		
		if(result == null) {
			result = getImageInSdcard(imageName);
		}
		
		return result;
	}
	
	public static Bitmap getImageInSdcard(String imageName) {
		if (TextUtils.isEmpty(imageName)) {
			return null;
		}
		String filePath = Constants.BOOKS_TEMP_IMAGE + imageName;
		if (!FileUtil.isFileExists(filePath)) {
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		
		//TODO:
		
		
		//-----Test----
//		BitmapFactory.Options opts = new BitmapFactory.Options();  
//		opts.inJustDecodeBounds = true;  
//		BitmapFactory.decodeFile(filePath, opts);  
//		  
//		opts.inSampleSize = computeSampleSize(opts, -1, 124*165);//84-112
//		opts.inJustDecodeBounds = false;  
//		Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts);  
		return bitmap;
	}
	
	
	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);

	    int roundedSize;
	    if (initialSize <= 8 ) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }

	    return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;

	    int lowerBound = (maxNumOfPixels == -1) ? 1 :
	            (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 :
	            (int) Math.min(Math.floor(w / minSideLength),
	            Math.floor(h / minSideLength));

	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }

	    if ((maxNumOfPixels == -1) &&
	            (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}

	

	public static final String newLogFileName() {
		StringBuilder sb = new StringBuilder();
		sb.append(Constants.bookStoredDiretory);
		sb.append(newLogFileName("yyyy_MM_dd_HH_mm"));
		sb.append(".txt");
		return sb.toString();
	}

	public static final String newLogFileName(String format) {
		SimpleDateFormat dd = new SimpleDateFormat(format);
		Date date = new Date();
		return dd.format(date);
	}

	public static void outFileLog(String str) {
		if (!LeyueConst.IS_DEBUG){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(str);
		sb.append("\n");
		StringBuilder filePath = new StringBuilder();
		filePath.append(Constants.bookStoredDiretory);
		filePath.append("out_put_file.txt");
		String logFileName = filePath.toString();
		outPutToFile(sb.toString(), logFileName);
	}

	public static void outPutToFile(String str, String filePath) {
		if (!FileUtil.isSDcardExist()) {
			return;
		}
		if (TextUtils.isEmpty(str)) {
			return;
		}
		try {
			FileWriter fw = new FileWriter(filePath, true);
			fw.write(str);
			fw.flush();
			fw.close();
		} catch (Exception e) {
		}
	}

	private static int logNum = 1;
	private static String logFileName;

	public static final void initLog() {
		logNum = 1;
		logFileName = newLogFileName();
	}

	public static void outLog(String action, int size, long time) {
		return;
	}

	/**
	 * 日志文件输出；序�?外部动作名称 调用接口名称 调用消耗时�? 调用过程流量大小
	 * 
	 * @param action
	 * @param actionName
	 * @param size
	 * @param time
	 */
	public static void outLog(String action, String actionName, int size,
			long time) {
		if (ClientInfoUtil.IS_DEBUG_LOG) {
			if (!FileUtil.isSDcardExist()) {
				return;
			}
			if (TextUtils.isEmpty(action) || TextUtils.isEmpty(actionName)) {
				return;
			}
			StringBuilder sb = new StringBuilder();
			// 序号
			sb.append(logNum);
			sb.append("\t");
			// 外部动作名称
			sb.append(actionName);
			sb.append("\t");
			// 调用接口名称
			sb.append(action);
			sb.append("\t");
			// 调用消耗时�?
			long tempTime = System.currentTimeMillis() - time;
			if (tempTime < 0) {
				tempTime = -tempTime;
			}
			sb.append(tempTime);
			sb.append("\t");
			// 调用过程流量大小
			sb.append(size);
			sb.append("\n");
			// 序号增加
			logNum++;
			LogUtil.v("SurfingReader", "connect time " + sb.toString());
			try {
				if (TextUtils.isEmpty(logFileName)) {
					logFileName = newLogFileName();
					logNum = 1;
				}
				FileWriter fw = new FileWriter(logFileName, true);
				fw.write(sb.toString());
				fw.flush();
				fw.close();
			} catch (Exception e) {
			}
		}
	}

	public static void testDrm(String str) {
		// if(!isSDcardExist()){
		// return;
		// }
		// if(TextUtils.isEmpty(str)){
		// return;
		// }
		// SimpleDateFormat dd = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
		// Date date = new Date();
		// StringBuilder sb = new StringBuilder();
		// sb.append(dd.format(date));
		// sb.append("\t");
		// sb.append(str);
		// sb.append("\n");
		// try {
		// FileWriter fw = new FileWriter(DownloadConstants.bookStoredDiretory +
		// "drm_test.txt", true);
		// fw.write(sb.toString());
		// fw.flush();
		// fw.close();
		// } catch (Exception e) {
		// }
	}

	public static String getOnlineFilePath(String dirName, String fileName) {
		StringBuilder dirPath = new StringBuilder();
		dirPath.append(Constants.BOOKS_ONLINE);
		if (!TextUtils.isEmpty(dirName)) {
			dirPath.append(dirName);
			dirPath.append("/");
		}
		dirPath.append(fileName);
		return dirPath.toString();
	}

    /**
     * 图片流转化为本地文件
     * @param name
     * @param stream
     * @return
     */
    public static String saveImage(String name, InputStream stream) {
        if (!FileUtil.isSDcardExist()) {
            LogUtil.v(TAG, "sdcard is not exist!");
            return null;
        }
        String filePath = null;
        try {
        	FileUtil.createFileDir(Constants.BOOKS_TEMP);
        	FileUtil.createFileDir(Constants.BOOKS_TEMP_IMAGE);
            StringBuilder sb = new StringBuilder();
            sb.append(Constants.BOOKS_TEMP_IMAGE);
            sb.append(name);
            // sb.append(".png");
            filePath = sb.toString();
            sb = null;
            File imgFile = new File(filePath);
            if (!imgFile.exists()) {
                FileOutputStream fos=new FileOutputStream(filePath);
                int data=stream.read();
                while(data!=-1){
                    fos.write(data);
                    data=stream.read();
                }
                fos.close();
            }
        } catch (IOException e) {
            filePath = null;
            LogUtil.e(TAG, "save image err", e);
        }
        return filePath;
    }

//	public static void showUserLoginDialog(Activity context, Runnable runnable, Runnable cancelRunnable) {
//		new UserLoginUtil(context, runnable, cancelRunnable);
//	}
	
//	public static void showUserLoginDialog(final Activity context, final Runnable runnable) {
//		INetWorkAvaliableListener listener = new INetWorkAvaliableListener() {
//			@Override
//			public void execute() {
//				new UserLoginUtil(context, runnable);
//			}
//		};
//		new TryNetWorkUtil(context, listener);
//	}

//	public static void showShareTypeDialog(Activity context, String shareContent) {
//		showShareTypeDialog(context, shareContent, null, null);
//	}

	

	
	/**
	 * 等待对话�?
	 * 
	 * @param context
	 * @return
	 */
	public static Dialog getWaittingDialog(Context context) {
		// final Dialog dialog = new Dialog(context, R.style.TransparentDialog);
		// final Dialog dialog = customDialog(context);
		// dialog.setContentView(R.layout.dialog_waitting);
		final Dialog dialog = getWaittingDialog(context, -1);
		return dialog;
	}

	public static Dialog getNetDialog(Context context) {
		return getWaittingDialog(context, R.string.waitting_dialog_connect);
	}

	public static Dialog getWaittingDialog(Context context, int resId) {
		final Dialog dialog = customDialog(context);
		dialog.setContentView(R.layout.dialog_waitting);
		if (resId > -1) {
			TextView tv = (TextView) dialog.findViewById(R.id.content_tv);
			tv.setText(context.getString(resId));
		}
		dialog.findViewById(R.id.dialog_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		return dialog;
	}

//	public static void commonConfirmDialog(Activity context, int msgId,
//			final ConfirmListener confirmListener) {
//		commonConfirmDialog(context, context.getString(msgId), confirmListener);
//	}
//
//	public static void commonConfirmDialog(Activity context, int msgId,
//			ConfirmListener confirmListener, CancelListener cancelListener) {
//		commonConfirmDialog(context, context.getString(msgId), confirmListener,
//				cancelListener);
//	}
//
//	/**
//	 * 通用的确认对话框
//	 * 
//	 * @param msg
//	 *            对话框的内容
//	 */
//	public static void commonConfirmDialog(Activity context, String msg,
//			final ConfirmListener confirmListener) {
//		commonConfirmDialog(context, msg, confirmListener, null);
//	}
//
//	/**
//	 * 通用的确认对话框
//	 * 
//	 * @param context
//	 * @param msg
//	 * @param confirmListener
//	 *            确定按钮的点击事�?
//	 * @param cancelListener
//	 *            取消按钮的点击事�?
//	 */
//	public static void commonConfirmDialog(Activity context, String msg,
//			final ConfirmListener confirmListener,
//			final CancelListener cancelListener) {
//		final Dialog dialog = customDialog(context);
//		dialog.setContentView(R.layout.dialog_common_confirm);
//		TextView contentTV = (TextView) dialog
//				.findViewById(R.id.dialog_content);
//		contentTV.setText(msg);
//		Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok);
//		OnClickListener confirmOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (confirmListener != null) {
//					confirmListener.onClick(v);
//				}
//				dialog.dismiss();
//			}
//
//		};
//		Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel);
//		OnClickListener cancelOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (cancelListener != null) {
//					cancelListener.onClick(v);
//				}
//				dialog.dismiss();
//			}
//
//		};
//		DialogUtil.dealDialogBtnWithPrimarySecondary(okBtn, R.string.btn_text_confirm, confirmOnClickListener, cancelBtn, R.string.btn_text_cancel, cancelOnClickListener);
//		
//		dialog.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode,
//					KeyEvent event) {
//				if (cancelListener != null) {
//					if (keyCode == KeyEvent.KEYCODE_BACK) {
//						cancelListener.onClick(null);
//						return true;
//					}
//				}
//				return false;
//			}
//		});
//		if (!context.isFinishing()) {
//			dialog.show();
//		}
//	}
//	
//	public static void commonConfirmDialog(Activity context, String msg,
//			final ConfirmListener confirmListener, int confirmStrId,
//			final CancelListener cancelListener, int cancelStrId) {
//		final Dialog dialog = customDialog(context);
//		dialog.setContentView(R.layout.dialog_common_confirm);
//		TextView contentTV = (TextView) dialog
//				.findViewById(R.id.dialog_content);
//		contentTV.setText(msg);
//		Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok);
//		OnClickListener confirmOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (confirmListener != null) {
//					confirmListener.onClick(v);
//				}
//				dialog.dismiss();
//			}
//
//		};
//		Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel);
//		OnClickListener cancelOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (cancelListener != null) {
//					cancelListener.onClick(v);
//				}
//				dialog.dismiss();
//			}
//
//		};
//		DialogUtil.dealDialogBtnWithPrimarySecondary(okBtn, confirmStrId, confirmOnClickListener, cancelBtn, cancelStrId, cancelOnClickListener);
//		
//		dialog.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode,
//					KeyEvent event) {
//				if (cancelListener != null) {
//					if (keyCode == KeyEvent.KEYCODE_BACK) {
//						cancelListener.onClick(null);
//						return true;
//					}
//				}
//				return false;
//			}
//		});
//		if (!context.isFinishing()) {
//			dialog.show();
//		}
//	}

//	public static Dialog confirmDialog(Context context, int msgId, final ConfirmListener confirmListener) {
//		return confirmDialog(context, context.getString(msgId), confirmListener);
//	}
//
//	public static Dialog confirmDialog(Context context, String msg,
//			final ConfirmListener confirmListener) {
//		final Dialog dialog = customDialog(context);
//		dialog.setContentView(R.layout.dialog_common_confirm);
//		TextView contentTV = (TextView) dialog
//				.findViewById(R.id.dialog_content);
//		contentTV.setText(msg);
//		Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok);
//		OnClickListener lOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (confirmListener != null) {
//					confirmListener.onClick(v);
//				}
//				dialog.dismiss();
//			}
//
//		};
//		Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel);
//		OnClickListener rOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//			}
//
//		};
//		DialogUtil.dealDialogBtn(okBtn, R.string.btn_text_confirm, lOnClickListener, cancelBtn, R.string.btn_text_cancel, rOnClickListener);
//		return dialog;
//	}
//	
//	public static Dialog confirmDialog(Context context, int titleId, int msgId, final Runnable confirmRunnable) {
//		return confirmDialog(context, context.getString(titleId), context.getString(msgId),0,0, confirmRunnable,null);
//	}
//	
//	public static Dialog confirmDialog(Context context, int titleId, String msg,int confirmRes , final Runnable confirmRunnable) {
//		return confirmDialog(context, context.getString(titleId), msg,confirmRes,0, confirmRunnable,null);
//	}
//	
//	public static Dialog confirmDialog(Context context, int titleId, int msgId,int confirmRes , final Runnable confirmRunnable) {
//		return confirmDialog(context, context.getString(titleId), context.getString(msgId),confirmRes,0, confirmRunnable,null);
//	}
//	
//	public static Dialog confirmDialog(Context context, int titleId, int msgId,int confirmRes , final Runnable confirmRunnable, final Runnable cancelRunnable) {
//		return confirmDialog(context, context.getString(titleId), context.getString(msgId),confirmRes,0, confirmRunnable,cancelRunnable);
//	}
//	
//	public static Dialog confirmDialog(Context context, String title, String msg
//			, int confirmRes , int cancelRes
//			, final Runnable confirmRunnable, final Runnable cancelRunnable) {
//		final Dialog dialog = getCommonDialog(context);
//		TextView titleTV = (TextView) dialog.findViewById(R.id.dialog_title);
//		titleTV.setText(title);
//		ViewGroup contentView = (ViewGroup) dialog.findViewById(R.id.dialog_content_layout);
//		LayoutInflater.from(context).inflate(R.layout.dialog_common_confirm, contentView);
//		TextView contentTV = (TextView) dialog.findViewById(R.id.dialog_content);
//		contentTV.setText(msg);
//		Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok);
//		if(confirmRes == 0){
//			confirmRes = R.string.btn_text_confirm;
//		}
//		okBtn.setText(confirmRes);
//		OnClickListener lOnClickListener = new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if (confirmRunnable != null) {
//					confirmRunnable.run();
//				}
//				dialog.dismiss();
//			}
//		};
//		Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel);
//		if(cancelRes == 0){
//			cancelRes = R.string.btn_text_cancel;
//		}
//		cancelBtn.setText(confirmRes);
//		OnClickListener rOnClickListener = new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if (cancelRunnable != null) {
//					cancelRunnable.run();
//				}
//				dialog.dismiss();
//			}
//		};
//		DialogUtil.dealDialogBtnWithPrimarySecondary(okBtn,confirmRes, lOnClickListener, cancelBtn, cancelRes, rOnClickListener);
//		return dialog;
//	}

//	/**
//	 * 特殊的确认对话框。取消将退出程序，拦截BACK键�?
//	 * 
//	 * @param context
//	 * @param resId
//	 * @param confirmListener
//	 */
//	public static void specialConfirmDialog(final Context context, int resId,
//			final ConfirmListener confirmListener) {
//		specialConfirmDialog(context, resId, confirmListener, null);
//	}
//
//	public static void specialConfirmDialog(final Context context, int resId,
//			ConfirmListener confirmListener, CancelListener cancelListener) {
//		specialConfirmDialog(context, resId, confirmListener, cancelListener,
//				-1, -1);
//	}
//
//	public static void specialConfirmDialog(final Context context, String msg,
//			ConfirmListener confirmListener, CancelListener cancelListener) {
//		specialConfirmDialog(context, msg, confirmListener, cancelListener, -1,
//				-1);
//	}
//
//	public static void specialConfirmDialog(final Context context, int resId,
//			final ConfirmListener confirmListener,
//			final CancelListener cancelListener, int sureResId, int cancelResId) {
//		specialConfirmDialog(context, context.getString(resId),
//				confirmListener, cancelListener, sureResId, cancelResId);
//	}
//
//	public static void specialConfirmDialog(final Context context, String msg,
//			final ConfirmListener confirmListener,
//			final CancelListener cancelListener, int sureResId, int cancelResId) {
//		final Dialog dialog = customDialog(context);
//		dialog.setContentView(R.layout.dialog_common_confirm);
//		TextView contentTV = (TextView) dialog
//				.findViewById(R.id.dialog_content);
//		contentTV.setText(msg);
//		Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok);
//		if (sureResId != -1) {
////			okBtn.setText(sureResId);
//		} else {
//			sureResId = R.string.btn_text_confirm;
//		}
//		
//		OnClickListener confirmOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//				if (confirmListener != null) {
//					confirmListener.onClick(v);
//				}
//			}
//
//		};
//		Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel);
//		if (cancelResId != -1) {
////			cancelBtn.setText(cancelResId);
//		} else {
//			cancelResId = R.string.btn_text_cancel;
//		}
//		OnClickListener cancelOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//				if (cancelListener != null) {
//					cancelListener.onClick(v);
//				} else {
//					exitApplication(context);
//				}
//			}
//
//		};
//		DialogUtil.dealDialogBtn(okBtn, sureResId, confirmOnClickListener, cancelBtn, cancelResId, cancelOnClickListener);
//				
//		dialog.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode,
//					KeyEvent event) {
//				if (keyCode == KeyEvent.KEYCODE_BACK) {
//					dialog.dismiss();
//					if (cancelListener != null) {
//						cancelListener.onClick(null);
//					} else {
//						exitApplication(context);
//					}
//					return true;
//				} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
//					return true;
//				}
//				return false;
//			}
//		});
//		dialog.show();
//	}

	
	

//	/**
//	 * 提示对话�?
//	 * 
//	 * @param context
//	 * @param resId
//	 */
//	public static void showAlertDialog(Context context, int resId) {
//		showAlertDialog(context, context.getString(resId), null , false);
//	}
//
//	public static void showAlertDialog(Context context, String msg) {
//		showAlertDialog(context, msg, null ,false);
//	}
//
//	public static void showAlertDialog(Context context, int resId,
//			ConfirmListener confirmListener) {
//		showAlertDialog(context, context.getString(resId), confirmListener , false);
//	}
//
//	public static void showAlertDialog(Context context, String msg, boolean isCloseRechargeActivity){
//		showAlertDialog(context, msg, null ,isCloseRechargeActivity);
//	}
//	
//	/**
//	 * @param isCloseRechargeActivity 是否同时关闭activity
//	 * */
//	public static void showAlertDialog(final Context context, String msg,
//			final ConfirmListener confirmListener ,final boolean isCloseRechargeActivity) {
//		final Dialog dialog = customDialog(context);
//		dialog.setContentView(R.layout.dialog_common_confirm);
//		TextView contentTV = (TextView) dialog
//				.findViewById(R.id.dialog_content);
//		contentTV.setText(msg);
//		Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok);
//		okBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (confirmListener != null) {
//					confirmListener.onClick(v);
//				}
//				dialog.dismiss();
//				if(isCloseRechargeActivity){
//					if(!((Activity)context).isFinishing()){
//						((Activity)context).finish();
//					}
//				}
//			}
//
//		});
//		dialog.findViewById(R.id.dialog_cancel).setVisibility(View.GONE);
//		dialog.show();
//	}
	
	public static Dialog showProgressDialog(Context context, int resId, OnClickListener cancelClickListener) {
		final Dialog dialog = customDialog(context);
		dialog.setContentView(R.layout.dialog_waitting);
		if (resId > -1) {
			TextView tv = (TextView) dialog.findViewById(R.id.content_tv);
			tv.setText(context.getString(resId));
		}
		dialog.findViewById(R.id.dialog_cancel).setOnClickListener(cancelClickListener);
		return dialog;
	}

	/**
	 * 删除选择的对话框
	 * 
	 * @param context
	 * @param delCurListener
	 *            删除当前的接�?
	 * @param delAllListener
	 *            删除所有的接口
	 */
	public static void deleteDialog(Context context,
			final DeleteListener delCurListener,
			final DeleteListener delAllListener) {
		final Dialog dialog = customDialog(context);
		dialog.setContentView(R.layout.dialog_delete_data);
		Button curDelBtn = (Button) dialog
				.findViewById(R.id.dialog_delete_current);
		if (delCurListener != null) {
			curDelBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					delCurListener.onClick(v);
					dialog.dismiss();
				}
			});
		} else {
			curDelBtn.setVisibility(View.GONE);
		}
		Button allDelBtn = (Button) dialog.findViewById(R.id.dialog_delete_all);
		if (delAllListener != null) {
			allDelBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					delAllListener.onClick(v);
					dialog.dismiss();
				}
			});
		} else {
			allDelBtn.setVisibility(View.GONE);
		}
		dialog.findViewById(R.id.dialog_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	public static Dialog getCommonDialog(Context context, int contentResId,
			int titleResId) {
		return getCommonDialog(context, contentResId, context
				.getString(titleResId));
	}
	
	public static Dialog getNotitleDialog(Context context, int contentResId){
		Dialog dialog = getCommonDialog(context);
		FrameLayout contentLayout = (FrameLayout) dialog
				.findViewById(R.id.dialog_content_layout);
		View view = LayoutInflater.from(context).inflate(contentResId, null);
		contentLayout.addView(view);
		dialog.findViewById(R.id.dialog_title).setVisibility(View.GONE);
		//dialog.findViewById(R.id.dialog_title_line).setVisibility(View.GONE);
		return dialog;	
	}

	public static Dialog getCommonDialog(Context context, int contentResId,
			String title) {
		Dialog dialog = getCommonDialog(context);
		FrameLayout contentLayout = (FrameLayout) dialog
				.findViewById(R.id.dialog_content_layout);
		View view = LayoutInflater.from(context).inflate(contentResId, null);
		contentLayout.addView(view);
		TextView titleTV = (TextView) dialog.findViewById(R.id.dialog_title);
		titleTV.setText(title);
		return dialog;
	}

	public static Dialog getPersonalEditComfirmDialog(Context context){
		Dialog dialog = CommonUtil.customDialog(context, R.style.CustomNoPaddingDialog);
		dialog.setContentView(R.layout.personal_edit_comfirm_dialog);
		return dialog;
	}
	
	//add by zlq 2013-4-24
	public static Dialog getEditComfirmDialog(Context context){
		Dialog dialog = CommonUtil.customDialog(context, R.style.CustomNoPaddingDialog);
		dialog.setContentView(R.layout.edit_comfirm_dialog);
		return dialog;
	}
	
	//add by zlq 2012-11-12
	public static Dialog getRegisterDialog(Context context){
		Dialog dialog = CommonUtil.customDialog(context, R.style.CustomNoPaddingDialog);
		dialog.setContentView(R.layout.dialog_regist_layout);
		return dialog;
	}
	//end
	
	public static Dialog getTransparentDialog(Context context, View contentView){
		Dialog dialog = CommonUtil.customDialog(context, R.style.TransparentDialog);
		dialog.setContentView(contentView);
		return dialog;
	}
	
	public static Dialog getCommonDialog(Context context) {
		Dialog dialog = CommonUtil.customDialog(context);
		dialog.setContentView(R.layout.dialog_common_layout);
		return dialog;
	}
	
	public static Dialog getCommonRechargeDialog(Context context) {
		Dialog dialog = new BaseDialog(context, R.style.CustomChargeDialog);
		dialog.setContentView(R.layout.dialog_common_recharge_layout);
		return dialog;
	}

	public static final Dialog customDialog(Context context) {
		return customDialog(context, R.style.CustomDialog);
	}
		
	public static final Dialog customDialog(Context context, int theme) {
		Dialog dialog = new BaseDialog(context, theme);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}


//	public static interface ConfirmListener {
//		public void onClick(View v);
//	}
//
//	public static interface CancelListener {
//		public void onClick(View v);
//	}

	public static interface DeleteListener {
		public void onClick(View v);
	}
	

	/**
	 * 如果activity嵌入activityGroup,创建dialog使用。必须使用父级非activityGroup的Activity
	 * 不包括非ActivityGroup和Activity互相嵌套
	 * @param context
	 * @return
	 */
	public static Activity getRealActivity(Activity context) {
		Activity result = context;
		
		while(result.getParent() != null && ((result.getParent() instanceof ActivityGroup) ) && !result.equals(result.getParent())) {
			result = result.getParent();
		}
		
		return result;
	}
	
	/**
	 * 显示通讯录联系人列表
	 * 
	 * @param context
	 * @param editText
	 */
//	public static void showContacts(final Context context, final EditText editText,
//			final Dialog wDialog) {
//		final ArrayList<Contact> contactList = getContacts(context);
//		if (contactList.size() > 0) {
//			final Dialog dialog = customDialog(context);
//			LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
//					android.view.ViewGroup.LayoutParams.FILL_PARENT);
//			LinearLayout layout = new LinearLayout(context);
//			layout.setLayoutParams(lp);
//			ListView listView = new ListView(context);
//			listView.setLayoutParams(lp);
//			listView.setCacheColorHint(Color.TRANSPARENT);
//			listView.setFastScrollEnabled(true);
//			int layoutID = R.layout.contact_item_fashion;
//			ContactAdapter adapter = new ContactAdapter(context, layoutID,
//					contactList);
//			listView.setAdapter(adapter);
//			listView.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> adapterView, View view,
//						int location, long id) {
//					if (location >= contactList.size()) {
//						return;
//					}
//					Contact contact = contactList.get(location);
//					String number = contact.number;
//					if (!TextUtils.isEmpty(number)) {
//						number = number.replace("-", "");
//						number = number.replace(" ", "");
//						int length = number.length(); 
//		 
//						if (length > 11) {
//							int start = length - 11;
//							number = number.substring(start); 
//						}
//					}
//					
//					editText.setText(number);
// 
//					Editable editable = editText.getEditableText();
//					Selection.setSelection(editable, editable.length());
//					dialog.dismiss();
//				}
//			});
//			layout.addView(listView);
//			dialog.setContentView(layout);
//			if (wDialog != null) {
//				wDialog.dismiss();
//			}
//			dialog.show();
//		} else {
//			if (wDialog != null) {
//				wDialog.dismiss();
//			}
//			ToastUtil.showToast(context, R.string.contact_empty);
//		}
//	}

	/**
	 * 获取通讯录的联系�?
	 * 
	 * @param context
	 * @return
	 */
//	private static ArrayList<Contact> getContacts(Context context) {
//		ArrayList<Contact> contactList = new ArrayList<Contact>();
//		Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI,
//				new String[] { Phone.DISPLAY_NAME, Phone.NUMBER }, null, null,
//				Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
//		if (cursor != null) {
//			if (cursor.moveToFirst()) {
//				Contact contact = null;
//				do {
//					contact = new Contact();
//					contact.name = cursor.getString(0);
//					contact.number = cursor.getString(1);
//					contactList.add(contact);
//				} while (cursor.moveToNext());
//			}
//			cursor.close();
//			cursor = null;
//		}
//		return contactList;
//	}

	public static SoundPool soundPool;
	public static HashMap<Integer, Integer> soundPoolIDs;

	private static MediaPlayer mediaPlayer;

	public static void initSoundPool(Context context) {
		// releaseSoundPool();
		// if(ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_R750)
		// ||
		// ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_D530)
		// ||
		// ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_ZTE_V9E)
		// ||
		// ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_ZTE_N600)
		// ||
		// ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_ZTE_N600_PLUS)
		// ||
		// ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_E230A)
		// ||
		// ClientInfoUtil.getOrderModel(context).equals(ClientInfoUtil.ORDER_MODEL_HTC_HERO200)){
		//			
		// }else{
		// soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 5);
		// soundPoolIDs = new HashMap<Integer, Integer>();
		// soundPoolIDs.put(R.raw.on_off, soundPool.load(context, R.raw.on_off,
		// 0));
		// soundPoolIDs.put(R.raw.cube_roate, soundPool.load(context,
		// R.raw.cube_roate, 0));
		// soundPoolIDs.put(R.raw.slide, soundPool.load(context, R.raw.slide,
		// 0));
		// soundPoolIDs.put(R.raw.page_flip, soundPool.load(context,
		// R.raw.page_flip, 0));
		// soundPoolIDs.put(R.raw.reader_fullscreen, soundPool.load(context,
		// R.raw.reader_fullscreen, 0));
		// }
	}

	public static void releaseSoundPool() {
		if (soundPool != null) {
			soundPool.release();
			soundPool = null;
		}
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		if (soundPoolIDs != null) {
			soundPoolIDs.clear();
			soundPoolIDs = null;
		}
	}

	public static void playAudioByResId(Context mContext, int resId) {
		// boolean isVoice = PreferencesUtil.getInstance(mContext).isVocie();
		// if(!isVoice){
		// LogUtil.v(Tag, "voice is set false");
		// return;
		// }
		// if(isSilent(mContext)){
		// LogUtil.v(Tag, "ringer mode is silent");
		// return;
		// }
		// if(ClientInfoUtil.getOrderModel(mContext).equals(ClientInfoUtil.ORDER_MODEL_R750)
		// ||
		// ClientInfoUtil.getOrderModel(mContext).equals(ClientInfoUtil.ORDER_MODEL_D530)
		// ||
		// ClientInfoUtil.getOrderModel(mContext).equals(ClientInfoUtil.ORDER_MODEL_ZTE_V9E)
		// ||
		// ClientInfoUtil.getOrderModel(mContext).equals(ClientInfoUtil.ORDER_MODEL_ZTE_N600)
		// ||
		// ClientInfoUtil.getOrderModel(mContext).equals(ClientInfoUtil.ORDER_MODEL_ZTE_N600_PLUS)
		// ||
		// ClientInfoUtil.getOrderModel(mContext).equals(ClientInfoUtil.ORDER_MODEL_E230A)
		// ||
		// ClientInfoUtil.getOrderModel(mContext).equals(ClientInfoUtil.ORDER_MODEL_HTC_HERO200)){
		// try{
		// if(mediaPlayer != null){
		// mediaPlayer.release();
		// mediaPlayer = null;
		// }
		// mediaPlayer = MediaPlayer.create(mContext, resId);
		// mediaPlayer.start();
		// }catch(Exception e){
		//				
		// }finally{
		// }
		// }else{
		// if(soundPool == null || soundPoolIDs == null){
		// initSoundPool(mContext);
		// }
		// if(soundPool == null || soundPoolIDs == null || soundPoolIDs.size()
		// == 0){
		// LogUtil.v(Tag,
		// "soundPool is null, or sounPoolIDs is null, or soundPoolIDs.size() == 0");
		// return;
		// }
		//			
		// Integer soundID = soundPoolIDs.get(resId);
		// if(soundID != null){
		// soundPool.play(soundID, 1, 1, 0, 0, 1);
		// }
		// }

	}

	public static void playCubeRoateAudio(Context mContext) {
		// playAudioByResId(mContext,R.raw.cube_roate);
	}

	public static void playOnOffAudio(Context mContext) {
		// playAudioByResId(mContext,R.raw.on_off);
	}

	public static void playPageFlipAudio(Context mContext) {
		// playAudioByResId(mContext,R.raw.page_flip);
	}

	public static void playReaderFullscreenAudio(Context mContext) {
		// playAudioByResId(mContext,R.raw.reader_fullscreen);
	}

	public static void playSlideAudio(Context mContext) {
		// playAudioByResId(mContext,R.raw.slide);
	}

	private static boolean isSilent(Context context) {
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int ringerMode = am.getRingerMode();
		return (ringerMode == AudioManager.RINGER_MODE_SILENT || ringerMode == AudioManager.RINGER_MODE_VIBRATE);
	}

	/**
	 * 获取阅读的亮度最小�?
	 * 
	 * @return
	 */
	public static float getReadLightMinValue(Context context) {
		if (ClientInfoUtil.getOrderModel(context).equals(
				ClientInfoUtil.ORDER_MODEL_E230A)) {
			return Constants.READ_LIGHT_MIN_VALUE_E230A;
		} else if (ClientInfoUtil.getOrderModel(context).equals(
				ClientInfoUtil.ORDER_MODEL_D530)) {
			return Constants.READ_LIGHT_MIN_VALUE_D530;
		} else {
			return Constants.READ_LIGHT_MIN_VALUE_COMMON;
		}
	}

	/**
	 * 系统内存查看
	 * 
	 * @param mContext
	 * @return
	 */
	public static void getMemoryInfo(Context mContext) {
		StringBuilder sb = new StringBuilder();
		ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mMemoryInfo = new MemoryInfo();
		mActivityManager.getMemoryInfo(mMemoryInfo);
		// sb.append("\n availMem------------------------->").append(mMemoryInfo.availMem>>10).append("k");
		sb.append("\n availMem---------------->").append(mMemoryInfo.availMem >> 20).append("M");
		sb.append("\n isLowMemory------------->").append(mMemoryInfo.lowMemory);
		LogUtil.i("Mem", sb.toString());
	}

	
	/*begin add by xzz 2012-03-09*/
	private static final int BOOKMARK_START_NO_DAY_INDEX = 11;
	private static final int BOOKMARK_END_NO_DAY_INDEX = 16;
	private static final int BOOKMARK_START_DAY_INDEX = 5;
	private static final int BOOKMARK_END_DAY_INDEX = 16;
	private static final int NOW_DAY = 0;
	private static final int YESTER_DAY = 1;
	private static final int LAST_YEAR = 2;
	private static final int OTHER_DAY = -1;
	private static final int ERROR_DAY = -2;
	 
	/*
	 * 获取新时间
	 * @param strDay  要修改的时间字符串
	 * @return 更改后的时间字符串
	 * */
	public static String getNowDay(String strDay){
		if(TextUtils.isEmpty(strDay)){
			return "之前";
		}
		String strNewDay = "";
		strDay = strDay.replace('/','-');		
		DateUtil dcm = new DateUtil();
		int rc = DateUtil.dateCompare(strDay);
		if(strDay.length() < 15){
			return strDay;
		}
		 
		if(rc == NOW_DAY){
			strNewDay = "今天  " + strDay.substring(BOOKMARK_START_NO_DAY_INDEX, BOOKMARK_END_NO_DAY_INDEX);
		} else if(rc == YESTER_DAY){
			strNewDay = "昨天  "+ strDay.substring(BOOKMARK_START_NO_DAY_INDEX, BOOKMARK_END_NO_DAY_INDEX);
		} else if(rc == LAST_YEAR){
			strNewDay = strDay.substring(0, BOOKMARK_END_DAY_INDEX);
		} else{
			strNewDay = strDay.substring(BOOKMARK_START_DAY_INDEX, strDay.length()-3);
		}
		return strNewDay;
	}
	
	public static String getNowDayUseChinese(String strDay){
		if(TextUtils.isEmpty(strDay)){
			return "";
		}
		String strNewDay = "";
		strDay = strDay.replace('/','-');	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		try {
			Date srcDay = sdf.parse(strDay);
			strDay = srcDay.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int rc = DateUtil.dateCompareWithChinese(strDay);
		if(strDay.length() < 15){
			return strDay;
		}
		 
		if(rc == NOW_DAY){
			strNewDay = "今天  " + strDay.substring(BOOKMARK_START_NO_DAY_INDEX, BOOKMARK_END_NO_DAY_INDEX);
		} else if(rc == YESTER_DAY){
			strNewDay = "昨天  "+ strDay.substring(BOOKMARK_START_NO_DAY_INDEX, BOOKMARK_END_NO_DAY_INDEX);
		} else if(rc == LAST_YEAR){
			strNewDay = strDay.substring(0, BOOKMARK_END_DAY_INDEX);
		} else{
			strNewDay = strDay.substring(BOOKMARK_START_DAY_INDEX, strDay.length()-3);
		}
		return strNewDay;
	}
	
	/*end by xzz 2012-03-09*/
	
	 public static int convertDipOrPx(Context context, int dip) { 
	        float scale = context.getResources().getDisplayMetrics().density; 
	        return (int)(dip*scale + 0.5f*(dip>=0?1:-1)); 
	    }
	 
	 public static String getFormatTime(){
		 String timeStr = formatTime(System.currentTimeMillis());
		 return timeStr;
	 }
	//2010-11-11 17:00:02
	 public static String formatTime(long time){
		 CharSequence timeStr = null;
		 try{
			 timeStr = DateFormat.format("yyyy-MM-dd kk:mm:ss", time);
		 }catch (Exception e){}
		 if(timeStr != null){
			 return timeStr.toString();
		 }else{
			 return "";
		 }
	 }
	 
	 
	 
	 /**
	  *  当这个View被绘制的时候会触发runnable
	  * @param view
	  * @param runnable
	  */
	 public static void setRunInPostDraw(Context context,final View view,final Runnable runnable){
		if(view != null){
			Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
			animation.setDuration(0);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationEnd(Animation animation) {
					view.getHandler().post(new Runnable() {
						
						@Override
						public void run() {
							if(runnable != null){
								runnable.run();
							}
							view.setAnimation(null);
						}
					});
				}
			});
			view.setAnimation(animation);
		} 
	 }
	 /**
	  *  当这个View被绘制的时候会触发runnable,这个方法会主动刷新view
	  * @param view
	  * @param runnable
	  */
	 public static void runInPostDraw(Context context,View view,final Runnable runnable){
		 if(view != null){
			 setRunInPostDraw(context, view, runnable);
			 view.invalidate();
		 }
	}
	 
//	public static void showExitDialog(final Activity activity, final ConfirmListener confirmListener, final CancelListener cancelListener) {
//		final Dialog dialog = customDialog(activity);
//		dialog.setContentView(R.layout.exit_dialog_layout);
//		
//		Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok);
//		OnClickListener confirmOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (confirmListener != null) {
//					confirmListener.onClick(v);
//				}
//				dialog.dismiss();
//			}
//
//		};
//		Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel);
//		OnClickListener cancelOnClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (cancelListener != null) {
//					cancelListener.onClick(v);
//				}
//				dialog.dismiss();
//			}
//
//		};
//		DialogUtil.dealDialogBtnWithPrimarySecondary(okBtn, R.string.btn_text_confirm,
//				confirmOnClickListener, cancelBtn, R.string.btn_text_cancel,
//				cancelOnClickListener);
//
//		dialog.setOnKeyListener(new OnKeyListener() {
//
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode,
//					KeyEvent event) {
//				if (cancelListener != null) {
//					if (keyCode == KeyEvent.KEYCODE_BACK) {
//						cancelListener.onClick(null);
//						return true;
//					}
//				}
//				return false;
//			}
//		});
//		
//		dialog.findViewById(R.id.audio_recommend_ib).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				Uri uri = Uri.parse(AdInfo.DOWNLOAD_URL_TTS);
////				Intent it = new Intent(Intent.ACTION_VIEW, uri);
////				activity.startActivity(it);
//				Runnable runnable = new Runnable() {
//					@Override
//					public void run() {
//						ActivityChannels.gotoFeedbackActivity(activity);
//					}
//				};
//				if (ClientInfoUtil.isGuest()) {	//联网且游客身份，必须先登录
//					//登录处理
//					CommonUtil.showUserLoginDialog(activity, runnable);
//				}else{
//					runnable.run();
//				}
//				dialog.dismiss();
//			}
//		});
//		if (!activity.isFinishing()) {
//			dialog.show();
//		}
//	}
	
	/*begin add by xzz 2012-03-07*/
	/*
	 * 限制中文输入
	 * */
	public static boolean isCN(String str) {
		try {
			byte[] bytes = str.getBytes("UTF-8");
			if (bytes.length == str.length()) {
				return false;
			} else {
				return true;
			}
		} catch (UnsupportedEncodingException e) { 
			e.printStackTrace();
		}
		return false;
	}	
	/*end by xzz 2012-03-07*/
	
	/*begin add by xzz 2012-04-12*/
	static class InternalURLSpan extends ClickableSpan {
		OnClickListener mListener;

		public InternalURLSpan(OnClickListener listener) {
			mListener = listener;
		}

		@Override
		public void onClick(View widget) {
			mListener.onClick(widget);
		}
	}
	/*end by xzz 2012-04-12*/
	
	// 直接拷贝这些代码到你希望的位置，然后在TextView设置了文本之后调用就ok了
	public static void SetLinkClickIntercept(final TextView tv,Context context) {
//	   tv.setMovementMethod(LinkMovementMethod.getInstance());  
	       CharSequence text = tv.getText();  
	       if (text instanceof Spannable) {  
	           int end = text.length();  
	           Spannable sp = (Spannable) tv.getText();  
	           URLSpan[] urls = sp.getSpans(0, end, URLSpan.class); 
	           if (urls.length == 0) {
	        	   return;
	           }
	       
           SpannableStringBuilder spannable = new SpannableStringBuilder(text);  
           // 只拦截 http:// URI
           LinkedList<String> myurls = new LinkedList<String>();
           for (URLSpan uri : urls) {
           	  String uriString = uri.getURL();
           	  if (uriString.indexOf("http://") == 0) {
				 myurls.add(uriString);
           	  }
           }
           //循环把链接发过去            
           for (URLSpan uri : urls) {
           	  String uriString = uri.getURL();
           	  if (uriString.indexOf("http://") == 0) {
           		  CommURLSpan myURLSpan = new CommURLSpan(uriString, myurls,context);  
                      spannable.setSpan(myURLSpan, sp.getSpanStart(uri),  
                           sp.getSpanEnd(uri), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);  
			  }
           }  
           tv.setText(spannable);  
        }  
	}
	
	public static String geturl(String str){
		Pattern pattern = Pattern.compile("http://[\\w\\.\\-/:]+");
        Matcher matcher = pattern.matcher(str);
        StringBuffer buffer = new StringBuffer();
        while(matcher.find()){              
            buffer.append(matcher.group());       
            buffer.append("\r\n");             
        }   
        return buffer.toString();
	}
	
	/**
	 * @param context
	 * @param accoutUserId
	 */
	public static void boundUserId(final Context context, final String accoutUserId){
//		if(!ClientInfoUtil.isGuest()){
//			new Thread() {
//
//				/* (non-Javadoc)
//				 * @see java.lang.Thread#run()
//				 */
//				@Override
//				public void run() {
//					DataSaxParser.getInstance(context).boundUser(accoutUserId);
//				}
//				
//			}.start();
//		}
	}
	
	/**
	 * 获取屏幕宽度，像素值
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}
	
	/**
	 * 获取屏幕高度，像素值
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}
	
	/**
	 * 根据快捷方式的名称判断手机是否已经安装快捷方式
	 * @param title
	 * @return
	 */
	public static boolean hasInstallShortcut(Context context,String title){
		ContentResolver resolver = context.getContentResolver();
		String AUTHORITY ="com.android.launcher2.settings";
		Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
		Cursor c = resolver.query(CONTENT_URI, new String[]{"title","iconResource"}, "title=?", 
				new String[]{title}, null);
		if(c != null && c.getCount()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * 如果分享内容长度大于140，进行截取
	 * @param shareContent
	 * @param shareBookId
	 * @return
	 */
	public static String clipStr(String shareContent, String shareBookId){
		String content = shareContent+shareBookId;
		if(content.length()<=140){
			return content;
		}else{
			int len = shareBookId.length();
			shareContent = shareContent.substring(0, 140-len);
			content = shareContent + shareBookId;
		}
		return content;
	}
	
	/**绘制重复指定布局*/
	public static void getSpecialRepeatLine(View view,Context context){
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.head_bar_bottom_line);
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		drawable.setTileModeXY(TileMode.REPEAT , null );
		drawable.setDither(true);
		view.setBackgroundDrawable(drawable);
	}
	
	/**
	 * 存储 重新连接网络 操作时 的时间戳。
	 */
	public static void saveTimeStampForRetryNetClick(String urlTag){
		if (urlTag == null) {
			return;
		}
		//Calendar c = Calendar.getInstance();
		//WebViewUrlCacheDB.getInstance(BaseApplication.getInstance()).
	//									setUrlInfo(urlTag, 
	//												DateUtil.getNowDayYMDHMS(c.getTime()));
	}
	
	public static String getUrlParamStr(String urlTag,String handleUrl){
		String hrefStr,paraStr,tempStr;
		String[] para;
		int pos;
		hrefStr = handleUrl;
		pos = hrefStr.indexOf("?");
		paraStr = hrefStr.substring(pos+1);
		para = paraStr.split("&");
		tempStr="";
		for(int i=0;i<para.length;i++)
		{
			tempStr = para[i];
			pos = tempStr.indexOf("=");
			if(urlTag.equals(tempStr.substring(0,pos))) {
				return tempStr.substring(pos+1);
			}
		}
		return null;
	}
	
	public static String getBasicStr(String urlTag,String handleUrl){
		String hrefStr,paraStr = null;
		int pos;
		hrefStr = handleUrl;
		pos = hrefStr.indexOf(urlTag);
		if(pos!=-1){
			 paraStr = hrefStr.substring(0,pos-1);
		}else{
			 paraStr = hrefStr;
		}
		return paraStr;
	}
	
	/**
	 * 获取arrays.xml中<integer-array name="id">
	 * 用Context.getResources().getIntArray(R.array.default_smiley_texts)
	 * 获取arrays.xml中<integer-array name="default_smiley_imgs">得到的数组都是为0，
	 * 用这个方法才可以正常获取
	 *
	 * @param intArrayId
	 * @return
	 */
	public static int[] getIntArray(Activity activity, int intArrayId) {
		TypedArray ta = activity.getResources().obtainTypedArray(intArrayId);
		int len = ta.length();
		int[] intArray = new int[len];
		for (int i = 0; i < len; i++) {
			intArray[i] = ta.getResourceId(i, 0);
		}
		ta.recycle();
		return intArray;
	}
	
	/**
	 * 获取设备IMEI号
	 * @return
	 */
	public static String getImei(Context context){
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}
	
	
	/**UUID+设备号序列号 唯一识别码（不可变）*/
	public static String getMyUUID(Context context){
	  final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);   
	  final String tmDevice, tmSerial, androidId;   

	  tmDevice = "" + tm.getDeviceId();  
	  tmSerial = "" + tm.getSimSerialNumber();   
	  androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);   

	  UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());   
	  String uniqueId = deviceUuid.toString();
	  Log.d("debug","uuid="+uniqueId);

	  return uniqueId;
	 }
	
	/**
	 * 处理分享提示
	 * @param context
	 * @param info
	 */
	public static void handleForShareTip(final Context context,ScoreUploadResponseInfo info){
		if (info == null) {
			return;
		}
		switch (info.getRecordStatus()) {
		case ScoreUploadResponseInfo.IS_NOT_REPEAT_RECORD:
			Toast.makeText(context, context.getString(R.string.share_record_ok_tip,info.getThisScore()), Toast.LENGTH_LONG).show();
			Intent updateIntent = new Intent(ContentInfoActivityLeyue.ACTION_SHARE_OK_UPDATE_VIEW);
			updateIntent.putExtra(ContentInfoActivityLeyue.ACTION_SHARE_OK_UPDATE_VIEW, info.getAllAvailableScore());
			context.sendBroadcast(updateIntent);
			break;
		case ScoreUploadResponseInfo.IS_REPEAT_RECORD:
			if (PreferencesUtil.getInstance(context).isShowRepeatTipDialog()) {//是否进行过重复弹出框提示
				//FIXME:这里调用的话，是由WX/YX的Activity调用（但是他们的界面都是不显示的，故弹窗也看不到，所以暂时用广播来处理PS：intent也可以，麻烦点）
				String sourceName ="该途径";
				if (UserScoreInfo.WX_FRIEND.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
					sourceName = "微信好友";
				}else if (UserScoreInfo.WX_ZONE.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
					sourceName = "微信好友圈";
				}else if (UserScoreInfo.YX_FRIEND.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
					sourceName = "易信好友";
				}else if (UserScoreInfo.YX_ZONE.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
					sourceName = "易信好友圈";
				}else if (UserScoreInfo.SINA.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
					sourceName = "新浪微博";
				}else if (UserScoreInfo.QQ_FRIEND.equals(UmengShareUtils.LAST_SHARE_TYPE)) {
					sourceName = "QQ好友";
				}
				Intent intent = new Intent(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE);
				intent.putExtra(ContentInfoActivityLeyue.ACTION_SHOW_TIP_DIALOG_AFTER_SHARE, sourceName);
				context.sendBroadcast(intent);
				PreferencesUtil.getInstance(context).setShowRepeatTipDialog(false);
			}else {
				Toast.makeText(context, context.getString(R.string.share_record_fail_tip), Toast.LENGTH_LONG).show();
			}
			break;
		case ScoreUploadResponseInfo.IS_LIMIT_RECORD:
			if(PreferencesUtil.getInstance(context).isLimitTipToast()){
				Toast.makeText(context, context.getString(R.string.share_record_limit_tip), Toast.LENGTH_LONG).show();
				PreferencesUtil.getInstance(context).setLimitTipToast(false);
			}else{
				Toast.makeText(context, context.getString(R.string.share_record_fail_tip), Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}
	
	public static String getUserId(){
		return PreferencesUtil.getInstance(MyAndroidApplication.getInstance()).getUserId();
	}
	
	/**
	 * 根据 getIsLogin 状态判断
	 * @return
	 */
	public static boolean isGuest(){
		return LeyueConst.TOURIST_USER_ID.equals(PreferencesUtil.getInstance(MyAndroidApplication.getInstance()).getUserId());
	}
	
	/**
	 * drawable --> bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		Bitmap bm = bd.getBitmap();
		return bm;
	}
	
	/**
	 * 从资源中获取Bitmap
	 * @param act
	 * @param resId
	 * @return
	 */
	public static Bitmap getBitmapFromResources(Context act, int resId) {
		Resources res = act.getResources();
		return BitmapFactory.decodeResource(res, resId);
	}
	
	/**
	 * 判断当前界面分享
	 * @param context
	 * @return
	 */
	public static boolean isOnCurrentActivityView(Context context){
		if (UmengShareUtils.shareContext!=null && (UmengShareUtils.shareContext.equals(context) )) {
			LogUtil.e("return true;");
			return true;
		}else {
			LogUtil.e("return false;");
			return false;
		}
	}
	
	/**
	 * 是否是平板
	 * @param context
	 * @return
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	/**
	 * 获取图片的路径
	 * @param context
	 * @param drawable
	 * @return
	 */
	public static String getDrawablePath(Context context,int drawable){
		Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
			    + context.getResources().getResourcePackageName(drawable) + "/"
			    + context.getResources().getResourceTypeName(drawable) + "/"
			    + context.getResources().getResourceEntryName(drawable));
		return uri.toString();
	}
	
	
	public static final String MOBILE_QQ_PKG_NAME = "com.tencent.mobileqq";
	
	/**
	 * 判断指定包名是否已安装
	 * @param context
	 * @param pkgName
	 * @return
	 */
	public static boolean isAppPackageNameInstalled(Context context,String pkgName){
		PackageManager pm = context.getPackageManager();  
	    // 查询所有已经安装的应用程序  
        List<ApplicationInfo> listAppcations = pm  
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);  
        for (ApplicationInfo app : listAppcations) {  
        	//非系统程序  
        	if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {  
        		if (pkgName.equals(app.packageName)) {
					return true;
				}  
        	}   
        	//本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了  
        	else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){  
        		if (pkgName.equals(app.packageName)) {
					return true;
				} 
        	}  
        }  
        return false;
	}
	
	/**解决ScollView嵌套Listview的bug。</br>
	 * 【注意】：子ListView的每个Item必须是LinearLayout，不能是其他的，
	 * 因为其他的Layout(如RelativeLayout)没有重写onMeasure()，所以会在onMeasure()时抛出异常。 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = getListViewHeightBasedOnChildren(listView);
//		 listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
	
	public static int getListViewHeightBasedOnChildren(ListView listView){
		// 获取ListView对应的Adapter
		if (listView == null) {
			return 0;
		}
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return 0;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(listView.getWidth(), MeasureSpec.UNSPECIFIED); // 计算子项View 的宽高
			LogUtil.e("listItem.getMeasuredHeight()  = "
					+ listItem.getMeasuredHeight());
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		totalHeight = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		return totalHeight;
	}

    /**
     * 获取沃阅读信息params
     * @return
     */
    public static String getWoInfoUrlParams(Context context){
        String deviceInfo = UniqueIdUtils.getDeviceInfo(context);
        if(deviceInfo != null && deviceInfo.lastIndexOf(",") == deviceInfo.length()-1){
            deviceInfo = deviceInfo.substring(0, deviceInfo.length()-1);
            deviceInfo = "{"+deviceInfo+"}";
        }
        StringBuilder sb = new StringBuilder();

        String userId = PreferencesUtil.getInstance(context).getUserId();
        String versionName = getApplicationVersionName(context);
        String resolution = DimensionsUtil.getDeviceResolution(context);

        sb.append("userId="+userId);
        sb.append("&versionName="+versionName);
        sb.append("&resolution="+resolution);
        sb.append("&deviceInfo="+ URLEncoder.encode(deviceInfo));
        return sb.toString();
    }

    /**
     * 判断是否有网络可用
     *
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        } else {
            return false;
        }
    }

    /**
     * 获取版本号
     * @param context
     * @return
     */
    public static String getApplicationVersionName(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 获取可用的网络信息
     *
     * @param context
     * @return
     */
    public static NetworkInfo getActiveNetworkInfo(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取设备DeviceModel
     * @return
     */
    public static String getDeviceModel(){
        return android.os.Build.MODEL;
    }
    
    /**
	 * 验证手机号码是否正确
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
    
    /**
	 * 获取省略中间好吗的手机号
	 * @param phone
	 * @return 返回省略后手机好吗。 例如: 189****1234
	 */
	public static String getEllipsisPhone(String phone){
		int phoneLength = phone.length();
		if(phoneLength >= 3){
			int startCount = 3;
			int endCount = 4;
			
			startCount = phoneLength*3/11;
			endCount = phoneLength*4/11;
			
			if(startCount < 1){
				startCount = 1;
			}
			if(endCount < 1){
				endCount = 1;
			}
			
			String headStr =  phone.substring(0, startCount);
			String tailStr = phone.substring(phoneLength-endCount);
			String middleStr = "";
			
			for(int i = 0; i < phoneLength-(startCount+endCount); i++){
				middleStr += "*";
			}
			return headStr + middleStr + tailStr;
		}
		return phone;
	}
	/**
	 * 检查字符串是否为空，包括:字符数为零"";空指针null;只包含空格字符串" ";字符串为"null"
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		return TextUtils.isEmpty(value)
					|| TextUtils.isEmpty(value.trim())
					|| value.trim().equalsIgnoreCase("null");
	}
	public static String makeMD5(String password) {
        MessageDigest md;
        try {
            // 生成一个MD5加密计算摘要
            md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(password.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String pwd = new BigInteger(1, md.digest()).toString(16);
            return pwd;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return password;
    }

    /**
     * 获取当前应用版本号
     * @param context
     * @return
     */
    public static int getVersionCode(Context context)
    {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }
	/**
	 * 上传购买记录
	 * @param contentId 包月id存 calObj. 按书传天翼阅读id
	 * @param price 价格：根据购买类型 传阅点还是话费。
	 * @param type 购买方式：1——按书购买。4——包月
	 * @param payType 购买类型：6——阅点支付；5——话费支付
	 */
	public static void recordBuyInfo(final String contentId, final String contentName,final String price,final int type,final int payType) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String calObj ="";
				String bookId ="";
				if (type == 1) {
					calObj = contentId;
					bookId = contentId;
				}else if(type == 4){
					calObj = contentId;
					bookId = "";
				}
                String leyueUserId = getUserId();
                try {
                    ApiProcess4Leyue.getInstance(MyAndroidApplication.getInstance()).getAddOrderDetail(leyueUserId, bookId, contentName,type,calObj, price, payType, ApiConfig.SOURCE_LEYUE);
                } catch (GsonResultException e) {
                }
            }
		}).start();
	}

    /**
     * 书籍对应的id文件是否存在本地
     * @param bookId
     * @return
     */
    public static boolean isBookExist(String bookId){
        String filePathString = "";
        filePathString = Constants.BOOKS_DOWNLOAD + bookId + ".epub";

        File file = new File(filePathString);
        return file.exists();
    }
    
    private static String formatPrice(int price){
		int temp = price;
		int tc = temp / 100;
		int tb = temp % 100;
		StringBuilder s = new StringBuilder();
		if (tc > 0){
			s.append(tc);
		}else {
			s.append("0");
		}
		if (tb > 0) {
			s.append(".");
			if (tb < 10) {
				s.append("0");
			}
			s.append(tb);
		} else {
			s.append(".00");
		}
		return s.toString();
	}
}
