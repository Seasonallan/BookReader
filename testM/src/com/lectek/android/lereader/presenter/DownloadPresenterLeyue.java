package com.lectek.android.lereader.presenter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.download.DownloadHttpEngine;
import com.lectek.android.lereader.download.DownloadService;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.net.response.ContentInfoLeyue;
import com.lectek.android.lereader.net.response.DownloadInfo;
import com.lectek.android.lereader.notification.DownloadNotification;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.lereader.utils.ToastUtil;
/**
 * 下载流程
 * @author linyiwei
 * @date 2012-02-29
 * @email 21551594@qq.com.com
 */
public class DownloadPresenterLeyue extends BasePresenter{
	public static final int SEARCH_TYPE_ALL = 6;
	public static final int SEARCH_TYPE_BOOK_NAME = 1;
	public static final int SEARCH_TYPE_AUTHOR = 2;
	public static final int SEARCH_TYPE_KEYWORD = 3;
	
	public static final String VOICE_CATALOG_POST_PATH = ".catalog";
	
	public static final String VOICE_POST_PATH = ".mp3";

	/**
	 * 初始化下载模块
	 */
	public static void initDownloadModule(final Context context){
//		DownloadAPI.Setting.mToastNoticeRunnable = new DownloadAPI.ToastNoticeRunnable() {
//			@Override
//			public void run(String msg) {
//			}
//		};
		
		DownloadAPI.Setting.mDownloadNotification = new DownloadNotification();
		DownloadAPI.Setting.mHttpHandler = DownloadHttpEngine.class;
		DownloadAPI.Setting.mMaxThreadSize = 5;
		DownloadAPI.Setting.mDBUpdateRunnable = new DownloadAPI.DBUpdateRunnable() {
			@Override
			public void onCreate(SQLiteDatabase db,String table) {
            	 ContentValues[] contentValuesArr = updateDB();
            	 for(int i = 0 ;contentValuesArr != null &&  i < contentValuesArr.length; i++){
                	 db.insert(table, "", contentValuesArr[i]);
            	 }
			}

			@Override
			public void onUpgrade(SQLiteDatabase db,String table) {
				ContentValues values = new ContentValues();
				values.put(DownloadHttpEngine.DOWNLOAD_TYPE,DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK);
				db.update(table, values, DownloadHttpEngine.DOWNLOAD_TYPE +" is null ", null);
			}
		};
		Intent intent = new Intent(context,DownloadService.class);
		intent.setAction(DownloadAPI.ACTION_START_DOWNLOAD);
		context.startService(intent);
	}

		
	/**
	 * 停止下载模块
	 * @param context
	 */
	public static void stopDownloadModule(Context context){
		Intent intent = new Intent(context,DownloadService.class);
		intent.setAction(DownloadAPI.ACTION_STOP_DOWNLOAD);
		context.stopService(intent);
	}
	
	/**
	 * 批量下载图书
	 * @param contentInfoList 需要下载的图书
	 * @param isAutoStart 标识是否自动重启已暂停的下载单元
	 * @param datchDownloadsUIRunnadle UI回调
	 */
	public static void bulkDownloadBooks(final ArrayList<ContentInfoLeyue> contentInfoList,
			final boolean isAutoStart,final IDatchDownloadsUIRunnadle datchDownloadsUIRunnadle){
		bulkDownload(contentInfoList, isAutoStart, datchDownloadsUIRunnadle, DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK);
	}
	
	/**
	 * 
	 * 批量下载流程（可在UI线程执行）
	 * @param contentInfoList
	 * @param isAutoStart
	 * @param datchDownloadsUIRunnadle
	 */
	public static void bulkDownload(final ArrayList<ContentInfoLeyue> contentInfoList,
			final boolean isAutoStart,final IDatchDownloadsUIRunnadle datchDownloadsUIRunnadle,final String type){

		try {
			if(contentInfoList == null){
				
				return;
				
			}
			//判断是否有SDCARD
			if(!FileUtil.isSDcardExist()){
				
				if(datchDownloadsUIRunnadle != null){
					 datchDownloadsUIRunnadle.onSDcardNotExist();
				}
				
				return;
			}
			
			//获取下载书籍列表中最小的文件大小
			long fileSize = 0;
			if (contentInfoList.size() >= 1){
				fileSize = contentInfoList.get(0).getFileSize();
				for(ContentInfoLeyue contentInfo : contentInfoList){
					fileSize = Math.min(fileSize, contentInfo.getFileSize());
				}
			}
			
			//如果存储空间小于最小的文件大小，调用没有空间的回调
			if (fileSize > FileUtil.getStorageSize()) {
				if(datchDownloadsUIRunnadle != null){
					 datchDownloadsUIRunnadle.onFreeNotEnough();
				}
				
				return;
			}
			new AsyncTask<Integer, Integer, Integer>(){
				int successSize = 0;
				int hasSize = 0;
				int finishedSize = 0;
				int state = -1;
				boolean isStop = false;
				@Override
				protected void onPostExecute(Integer result) {
					if(result < 0){
						if(datchDownloadsUIRunnadle != null){
							datchDownloadsUIRunnadle.onPostRun(result, result, result, result);
						}	
					}else{
						if(datchDownloadsUIRunnadle != null){
							datchDownloadsUIRunnadle.onPostRun(successSize, hasSize, finishedSize, state);
						}
					}
				}

				@Override
				protected void onPreExecute() {
					successSize = 0;
					hasSize = 0;
					finishedSize = 0;
					if(datchDownloadsUIRunnadle != null){
						isStop = datchDownloadsUIRunnadle.onPreRun();
					}
					if(isStop){
						this.cancel(true);
					}
				}

				@Override
				protected Integer doInBackground(Integer... params) {
					if(datchDownloadsUIRunnadle.onPreRunInThread()){
						return IDatchDownloadsUIRunnadle.RESULT_PRE_RUN_IN_THREAD_FAIL;
					}
					if(isStop ){
						return IDatchDownloadsUIRunnadle.RESULT_STOP;
					}
					ContentResolver resolver = getContentResolver();
					ArrayList<ContentValues> contentValuesList = new ArrayList<ContentValues>();
					for(int i = 0 ;i < contentInfoList.size();i ++){
						ContentInfoLeyue contentInfo = contentInfoList.get(i);
						if(contentInfo == null)continue;
						Cursor cursor = resolver.query(DownloadAPI.CONTENT_URI,null,DownloadHttpEngine.CONTENT_ID +" ='" + contentInfo.getBookId()+"'"
								, null, null);
						if(cursor == null)continue;
						if(!cursor.moveToFirst()){
							ContentValues values = null;
							if(DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_VOICE.equals(type)){
//								values = getVoiceDownloadContentValues(getDownloadInfo(contentInfo));
							}else{
								values = getBookDownloadContentValues(getDownloadInfo(contentInfo));
							}
							contentValuesList.add(values);
							successSize ++;
						}else{
							int state = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadAPI.STATE));
							if(isAutoStart && ( state == DownloadAPI.STATE_ONLINE || state == DownloadAPI.STATE_PAUSE || state == DownloadAPI.STATE_FAIL || state == DownloadAPI.STATE_FAIL_OUT_MEMORY)){
								ContentValues values = new ContentValues();
								values.put(DownloadAPI.STATE, DownloadAPI.STATE_START);
								resolver.update(DownloadAPI.CONTENT_URI, values,
										DownloadHttpEngine.CONTENT_ID + " ='" +contentInfo.getBookId()+"'", null);
							}
							if(state == DownloadAPI.STATE_FINISH){
								finishedSize ++;
							}else{
								hasSize ++;
							}
						}
						cursor.close();
					}
					ContentValues[] arrayValues = new ContentValues[contentValuesList.size()];
					contentValuesList.toArray(arrayValues);
					if(arrayValues != null && arrayValues.length > 0){
						resolver.bulkInsert(DownloadAPI.CONTENT_URI, arrayValues);
					}
					return 0;
				}
				
			}.execute();
		} catch (Exception e) {}
	
	}

	/**
	 * 删除流程（可在UI线程执行）
	 * @param curSelect
	 * @param deleteDownloadsUIRunnadle
	 */
	public static void runDeleteInUI(final ArrayList<DownloadInfo> curSelect
			,final DeleteDownloadsUIRunnadle deleteDownloadsUIRunnadle) {
		if(curSelect == null){
			return;
		}
		if(deleteDownloadsUIRunnadle != null){
			if( !deleteDownloadsUIRunnadle.onPreRun() ){
				return;
			}
		}
		new Thread(){
			public void run(){
				LinkedList<DownloadInfo> tempList = new LinkedList<DownloadInfo>(curSelect);
//				for(int i = 0 ;i < curSelect.size();i++){
//					if(isVoiceGroupDownloadInfo(curSelect.get(i))){
//						tempList.remove(i);
//						ArrayList<DownloadInfo> groupTempList = (ArrayList<DownloadInfo>) curSelect.get(i).tag;
//						if(groupTempList != null && groupTempList.size() > 0){
//							tempList.addAll(i, groupTempList);
//						}
//					}
//				}
				final int successSize = deleteDB(tempList);
				runInUI(new Runnable() {
					
					@Override
					public void run() {
						if(deleteDownloadsUIRunnadle != null){
							deleteDownloadsUIRunnadle.onPostRun(successSize);
						}
					}
				});
			}
		}.start();
	}
	 
	/**
	 * 读取下载单元列表流程（可在UI线程执行）
	 * @param where
	 * @param idMap
	 * @param loadDataUIRunnadle
	 */
	public static void loadDownLoadUnits(final String where, final HashMap<Long, DownloadInfo> idMap
			,final LoadDataUIRunnadle loadDataUIRunnadle, final String sortOrder){
		if(loadDataUIRunnadle != null){
			if( !loadDataUIRunnadle.onPreRun() ){
				return;
			}
		}
		new Thread(){
			private long delay = 0;
			public void run(){
				delay = System.currentTimeMillis();
				final ArrayList<DownloadInfo> datas = loadDownLoadUnits(where,idMap, sortOrder);
				delay = System.currentTimeMillis() - delay;
				if(delay < 500){
					try {
						sleep(500 - delay);
					} catch (InterruptedException e) {}
				}
				runInUI(new Runnable() {
					
					@Override
					public void run() {
						if(loadDataUIRunnadle != null){
							loadDataUIRunnadle.onPostRun(datas);
						}
					}
				});
			}
		}.start();
	}
	
	/**
	 * 重新开始下载（可在UI线程执行）
	 * @param contentID
	 */
	public static void reDownload(final String contentID){
		DownloadInfo downloadUnit = getDownloadUnitById(contentID);
		reDownload(downloadUnit);
	}
	
//	public static void reDownload(final DownloadInfo downloadUnit){
//		DownloadDataProvider.reDownload(downloadUnit);
//	}
	/**
	 * 启动暂停的下载单元（可在UI线程执行）
	 * @param contentID
	 */
	public static void resumeDownload(final String contentID){
		DownloadInfo downloadUnit = getDownloadUnitById(contentID);
		resumeDownload(downloadUnit);
	}
	
//	public static void resumeDownload(final DownloadInfo downloadUnit){
//		DownloadDataProvider.resumeDownload(downloadUnit);
//	}
//	
//	public static int getDownloadUnitStateById(final String contentID){
//		return DownloadDataProvider.getDownloadUnitStateById(contentID);
//	}
//	public static ArrayList<ContentInfoLeyue> searchBook(String keyWord, int searchType){
//		return DownloadDataProvider.searchBook(keyWord, searchType);
//	}
	
	/**
	 * 从旧版本数据库内容到
	 * @return 返回
	 */
	public static ContentValues[] updateDB(){
		return null;
	}
	/**
	 * 删除下载单元操作
	 * @param curSelect
	 * @return
	 */
	public static int deleteDB(final List<DownloadInfo> curSelect){
		if(curSelect == null || curSelect.size() == 0){
			return 0;
		}
		int successSize = 0;
		String ids = "";
		for(int i = 0 ;i < curSelect.size(); i ++){
			DownloadInfo downloadUnit = curSelect.get(i);
			if (downloadUnit != null) {
				// 删除下载数据库的数据
				ids += downloadUnit.id;
				if(i != curSelect.size() - 1 && curSelect.size() != 1){
					ids += ",";
				}
				if(downloadUnit.filePathLocation == null){
					downloadUnit.filePathLocation = "";
				}
				File file = new File(downloadUnit.filePathLocation);
				if(file.exists()){
					file.delete();
				}
			}
		}
		successSize = getContentResolver().delete(DownloadAPI.CONTENT_URI,
				DownloadAPI._ID + " IN (" + ids + ")", null);
		return successSize;
	}
    /**
     * 删除下载单元操作
     * @return
     */
    public static int deleteDB(String contentId){
        return getContentResolver().delete(DownloadAPI.CONTENT_URI,
                DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentId});
    }

	/**
	 * 删除下载单元操作
	 * @param curSelect
	 * @return
	 */
	public static int deleteDB(final DownloadInfo curSelect){
		if(curSelect == null){
			return 0;
		}
		int successSize = 0; 
		if (curSelect != null) {
			// 删除下载数据库的数据 
			if(curSelect.filePathLocation == null){
				curSelect.filePathLocation = "";
			}
			File file = new File(curSelect.filePathLocation);
			if(file.exists()){
				file.delete();
			}
		}	
		successSize = getContentResolver().delete(DownloadAPI.CONTENT_URI,
				DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{curSelect.contentID});
		return successSize;
	}
	
	/**
	 * 添加下载单元操作
	 * @param contentInfo
	 * @return
	 */
	public static void addBookDownload(DownloadInfo contentInfo) {
		ContentValues values = getBookDownloadContentValues(contentInfo);
		getContentResolver().insert(DownloadAPI.CONTENT_URI, values);
		return;
	}
	
	/**
	 * 更新是否需要下载完整版本状态
	 * @param isDownloadFullVersion 是否下载完整版
	 * @param contentID 书籍id
	 * @return
	 */
	public static int updateDownloadinfoFullVersionState(boolean isDownloadFullVersion,String contentID){
		if(TextUtils.isEmpty(contentID)){
			return -1;
		}
		ContentValues values = new ContentValues();
		values.put(DownloadHttpEngine.BOOK_DOWNLOAD_FULL_VERSION, isDownloadFullVersion);
		return getContentResolver().update(DownloadAPI.CONTENT_URI, values, DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentID});
	}
	
	public static String getDownloadinfoSecretKey(String contentID){
		if(TextUtils.isEmpty(contentID)){
			return null;
		}
		Cursor cursor = getContentResolver().query(DownloadAPI.CONTENT_URI, null, DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentID},null);
		if(cursor != null){
			if(cursor.moveToFirst()){
				return cursor.getString(cursor.getColumnIndex(DownloadHttpEngine.BOOK_SECRET_KEY));
			}
			cursor.close();
		}
		return null;
	}
	
	/**更新书籍解密key*/
	public static int updateDownloadinfoSecretKey(String key,String contentID){
		if(TextUtils.isEmpty(key) || TextUtils.isEmpty(contentID)){
			return -1;
		}
		ContentValues values = new ContentValues();
		values.put(DownloadHttpEngine.BOOK_SECRET_KEY, key);
		return getContentResolver().update(DownloadAPI.CONTENT_URI, values, DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentID});
	}
	
	/**修改旧版本，内置书籍的存储地址*/
	public static void updateOldDownloadinfo(){
		ContentValues values = new ContentValues();
		values.put(DownloadAPI.FILE_PATH, Constants.BOOKS_DOWNLOAD + "1000000126.epub");
		getContentResolver().update(DownloadAPI.CONTENT_URI, values, DownloadHttpEngine.CONTENT_ID + " = ? ", new String[]{"1000000126"});
		ContentValues values2 = new ContentValues();
		values.put(DownloadAPI.FILE_PATH, Constants.BOOKS_DOWNLOAD + "1000000068.epub");
		getContentResolver().update(DownloadAPI.CONTENT_URI, values2, DownloadHttpEngine.CONTENT_ID + " = ? ", new String[]{"1000000068"});
	}
	
	/**更新书籍价格信息*/
	public static int updateDownloadinfoBookPrice(String price,String promotionPrice,String isOrderNum,String contentID
            ,String url){
		if(TextUtils.isEmpty(contentID)){
			return -1;
		}
		if (TextUtils.isEmpty(price)) {
			price = "";
		}
		if (TextUtils.isEmpty(promotionPrice)) {
			promotionPrice = "";
		}
		if (TextUtils.isEmpty(isOrderNum)) {
			isOrderNum = "";
		}
		ContentValues values = new ContentValues();
		values.put(DownloadHttpEngine.BOOK_PRICE, price);
		values.put(DownloadHttpEngine.BOOK_PROMOTION_PRICE, promotionPrice);
		values.put(DownloadHttpEngine.BOOK_FEE_START, isOrderNum);
        if(!TextUtils.isEmpty(url)){
            values.put(DownloadHttpEngine.URL, url);
        }
		return getContentResolver().update(DownloadAPI.CONTENT_URI, values, DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentID});
	}
	
	public static int updateDownloadinfoLogoUrl(String url,String contentID){
		if(TextUtils.isEmpty(url) || TextUtils.isEmpty(contentID)){
			return 0;
		}
		ContentValues values = new ContentValues();
		values.put(DownloadHttpEngine.ICON_URL, url);
		return getContentResolver().update(DownloadAPI.CONTENT_URI, values, DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentID});
	}
	
	public static int updateDownloadinfoTime(String contentID){
		if(TextUtils.isEmpty(contentID)){
			return 0;
		}
		ContentValues values = new ContentValues();
		values.put(DownloadAPI.TIMES_TAMP, System.currentTimeMillis());
		return getContentResolver().update(DownloadAPI.CONTENT_URI, values,  DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentID});
	}

    /**
     * 添加书籍到数据库
     * @return
     */
    public static boolean addBookDownloadedInfo(Book mBook, String secretKey) {
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.authorName = mBook.getAuthor();
        downloadInfo.contentID = mBook.getBookId();
        downloadInfo.contentName = mBook.getBookName();
        downloadInfo.contentType = mBook.getBookType();
        downloadInfo.secret_key = secretKey;
        downloadInfo.price = mBook.getPrice();
        downloadInfo.promotionPrice = mBook.getPromotionPrice();
        downloadInfo.isOrderChapterNum = mBook.getFeeStart();
        downloadInfo.logoUrl = mBook.getCoverPath();
        downloadInfo.timestamp = System.currentTimeMillis();
        downloadInfo.isOrder = mBook.isOrder();
        downloadInfo.url = mBook.getPath();
        downloadInfo.downloadType = mBook.getBookFormatType();
        if(mBook.isOnline()){
            downloadInfo.state =  DownloadAPI.STATE_ONLINE;
        }else{
            downloadInfo.state =  DownloadAPI.STATE_FINISH;
        }
        ContentValues values = getBookHadDownloadedContentValues(downloadInfo);
        getContentResolver().insert(DownloadAPI.CONTENT_URI, values);
        return true;
    }


    /**
     * 添加书籍到数据库
     * @param contentInfo
     * @return
     */
    public static boolean addBookDownloadedInfoIfNotExit(DownloadInfo contentInfo) {
        if(!checkBookDownloadedExist(contentInfo.contentID)){
            ContentValues values = getBookHadDownloadedContentValues(contentInfo);
            getContentResolver().insert(DownloadAPI.CONTENT_URI, values);
            return true;
        }
        return false;
    }

    /**
     * 清空数据库
     * @return
     */
    public static int clearDownloadInfo() {
        return getContentResolver().delete(DownloadAPI.CONTENT_URI, null, null);
    }

	/**
	 * 检查数据库是否有存在该书籍
	 * @param contentID
	 * @return true:已存在; false:不存在
	 */
	public static boolean checkBookDownloadedExist(String contentID){
		boolean isExist = false;
		Cursor cursor = getContentResolver().query(DownloadAPI.CONTENT_URI, null, DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentID}, null);
		if(cursor != null){
			if(cursor.moveToFirst()){
				isExist = true;
			}
			cursor.close();
		}
		
		return isExist;
	}

	/**
	 * 添加已下载好的到数据库
	 * @param contentInfo
	 * @return
	 */
	public static void addLocalBook(DownloadInfo contentInfo) {
		boolean isExist = false;
		Cursor cursor = getContentResolver().query(DownloadAPI.CONTENT_URI, null, DownloadHttpEngine.CONTENT_ID + " = ?", new String[]{contentInfo.contentID}, null);
		if(cursor != null){
			if(cursor.moveToFirst()){
				isExist = true;
			}
			cursor.close();
		}
		if(!isExist) {
			ContentValues values = getLocalBookContentValues(contentInfo);
			getContentResolver().insert(DownloadAPI.CONTENT_URI, values);
		}
		return;
	}
	
	public static ContentValues getLocalBookContentValues(DownloadInfo contentInfo){
		ContentValues values = new ContentValues();
		values.put(DownloadAPI.STATE, contentInfo.state);
		values.put(DownloadHttpEngine.CONTENT_ID, contentInfo.contentID);
		values.put(DownloadHttpEngine.CONTENT_NAME, contentInfo.contentName);
		values.put(DownloadAPI.FILE_NAME, contentInfo.contentName);
		values.put(DownloadHttpEngine.AUTHOR_NAME, contentInfo.authorName);
		values.put(DownloadHttpEngine.CONTENT_TYPE, contentInfo.contentType);
		values.put(DownloadHttpEngine.ICON_URL, contentInfo.logoUrl);
		values.put(DownloadHttpEngine.DOWNLOAD_TYPE,contentInfo.downloadType);
		values.put(DownloadHttpEngine.URL, contentInfo.url);
		values.put(DownloadHttpEngine.BOOK_IS_ORDER, contentInfo.isOrder?"1":"0");//用于保存已下载 书籍的购买状态。0：未购买；1：已购买
		values.put(DownloadHttpEngine.BOOK_FEE_START, contentInfo.isOrderChapterNum);//用于保存购买点
		values.put(DownloadHttpEngine.BOOK_PRICE, contentInfo.price);//用于保存原价
		values.put(DownloadHttpEngine.BOOK_PROMOTION_PRICE, contentInfo.promotionPrice);//用于保存优惠价
		values.put(DownloadHttpEngine.BOOK_SECRET_KEY, contentInfo.secret_key);//用于保存解密key
		values.put(DownloadHttpEngine.BOOK_DOWNLOAD_FULL_VERSION, contentInfo.isDownloadFullVersonBook?"1":"0");//用于保存是否需要下载完整版状态。1：需要下载完整版本
		values.put(DownloadAPI.STATE,DownloadAPI.STATE_FINISH);
		values.put(DownloadAPI.FILE_PATH, contentInfo.filePathLocation);
		return values;
	}
	
//	public static FindCEBResult queryExistNum(String contentIDs, String filePaths){
//		LogUtil.i("DwonloadPresenter", "contentd ids: " + contentIDs);
//		FindCEBResult result = new FindCEBResult();
//		Cursor cursor = getContentResolver().query(DownloadAPI.CONTENT_URI, null, 
//				DownloadHttpEngine.CONTENT_ID + " in (" + contentIDs + ") OR " + DownloadAPI.FILE_PATH + " in (" + filePaths + ")", null, null);
//		if(cursor != null){
//			result.count = cursor.getCount();
//			String contentID = null;
//			while(cursor.moveToNext()){
//				contentID = cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.CONTENT_ID));
//				result.existCEBID.put(contentID, true);
//			}
//			cursor.close();
//		}
//		return result;
//	}
	
	/**构建下载信息实体*/
	private static DownloadInfo getDownloadInfo(ContentInfoLeyue contentInfo){
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.contentID = contentInfo.getBookId();
		downloadInfo.contentName = contentInfo.getBookName();
		downloadInfo.authorName = contentInfo.getAuthor();
		downloadInfo.contentType = contentInfo.getBookType();
		downloadInfo.logoUrl = contentInfo.getCoverPath();
		downloadInfo.url = contentInfo.getFilePath();
		downloadInfo.isOrder = contentInfo.isOrder();//购买；未购买
		downloadInfo.isOrderChapterNum = contentInfo.getFeeStart();//购买点
		downloadInfo.price = contentInfo.getPrice();//原价
		downloadInfo.promotionPrice = contentInfo.getPromotionPrice();//优惠价
		return downloadInfo;
	}
	
	public static int loadFinishId(String contentId){
		int id = -1;
		Cursor cursor  = getContentResolver().query(DownloadAPI.CONTENT_URI, null,DownloadHttpEngine.CONTENT_ID + "=?",
				new String[]{contentId}, null);
		if(cursor == null){
			return -1;
		}
		if(cursor.moveToFirst()){
			 int state = cursor.getInt(cursor.getColumnIndex(DownloadAPI.STATE));
			 if(state == DownloadAPI.STATE_FINISH){
				 id = cursor.getInt(cursor.getColumnIndex(DownloadAPI._ID));
			 }
		}
		cursor.close();
		return id;
	}
	
	/**组建更新下载 实体内容Values*/
	private static ContentValues getBookDownloadContentValues(DownloadInfo contentInfo){
		ContentValues values = new ContentValues();
		values.put(DownloadAPI.STATE, contentInfo.state);
		values.put(DownloadHttpEngine.CONTENT_ID, contentInfo.contentID);
		values.put(DownloadHttpEngine.CONTENT_NAME, contentInfo.contentName);
		values.put(DownloadAPI.FILE_NAME, contentInfo.contentName);
		values.put(DownloadHttpEngine.AUTHOR_NAME, contentInfo.authorName);
		values.put(DownloadHttpEngine.CONTENT_TYPE, contentInfo.contentType);
		values.put(DownloadHttpEngine.ICON_URL, contentInfo.logoUrl);
		values.put(DownloadHttpEngine.DOWNLOAD_TYPE, contentInfo.downloadType == null ? DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_BOOK:contentInfo.downloadType);
		values.put(DownloadAPI.FILE_PATH, Constants.BOOKS_DOWNLOAD + contentInfo.contentID + ".epub");
		values.put(DownloadHttpEngine.URL, contentInfo.url);
		values.put(DownloadHttpEngine.BOOK_IS_ORDER, contentInfo.isOrder?"1":"0");//用于保存已下载 书籍的购买状态。0：未购买；1：已购买
		values.put(DownloadHttpEngine.BOOK_FEE_START, contentInfo.isOrderChapterNum);//用于保存购买点
		values.put(DownloadHttpEngine.BOOK_PRICE, contentInfo.price);//用于保存原价
		values.put(DownloadHttpEngine.BOOK_PROMOTION_PRICE, contentInfo.promotionPrice);//用于保存优惠价
		values.put(DownloadHttpEngine.BOOK_SECRET_KEY, contentInfo.secret_key);//用于保存解密key
		values.put(DownloadHttpEngine.BOOK_DOWNLOAD_FULL_VERSION, contentInfo.isDownloadFullVersonBook?"1":"0");//用于保存是否需要下载完整版状态。1：需要下载完整版本
		return values;
	}
	
	public static ContentValues getBookHadDownloadedContentValues(DownloadInfo contentInfo){
		ContentValues contentValues = getBookDownloadContentValues(contentInfo);
		contentValues.put(DownloadAPI.FILE_BYTE_SIZE, contentInfo.size);
		contentValues.put(DownloadAPI.FILE_BYTE_CURRENT_SIZE, contentInfo.current_size);
		contentValues.put(DownloadHttpEngine.ICON_URL, contentInfo.logoUrl);
		return contentValues;
	}
	
	public static String getVoiceDownloadPath(String id){
		return Constants.BOOKS_DOWNLOAD + DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_VOICE + id + VOICE_POST_PATH;
	}
	
	public static String getVoiceDownloadCatalogPath(String bookId){
		return Constants.BOOKS_DOWNLOAD + DownloadHttpEngine.VALUE_DOWNLOAD_TYPE_VOICE + bookId + VOICE_CATALOG_POST_PATH;
	}
	
	/**
	 * 重新开始下载操作
	 * @param contentInfo
	 * @return
	 */
	public static void reDownload(DownloadInfo contentInfo){
		contentInfo.state = DownloadAPI.STATE_START;
		contentInfo.size = 0;
		contentInfo.current_size = 0;
		ContentValues values = new ContentValues();
		values.put(DownloadAPI.STATE, contentInfo.state);
		values.put(DownloadAPI.FILE_BYTE_SIZE, contentInfo.size);
		values.put(DownloadAPI.FILE_BYTE_CURRENT_SIZE, contentInfo.current_size);
		getContentResolver().update(DownloadAPI.CONTENT_URI, values,DownloadHttpEngine.CONTENT_ID+" ='"+contentInfo.contentID+"'",null);
	}
	
	/**
	 * 启动暂停的下载单元
	 * @param contentInfo
	 */
	public static int resumeDownload(DownloadInfo contentInfo) {
		ContentValues values = new ContentValues();
		values.put(DownloadAPI.STATE, DownloadAPI.STATE_START);
		int successSize = getContentResolver().update(DownloadAPI.CONTENT_URI, values,
				DownloadHttpEngine.CONTENT_ID+" ='"+contentInfo.contentID+"'", null);
		return successSize;
	}
	
	/**
	 * 读取下载单元操作
	 * @param where
	 * @param sortOrder 如果为空就用时间降序DownloadAPI.TIMES_TAMP + " DESC"
	 * @return
	 */
	public static ArrayList<DownloadInfo> loadDownLoadUnits(String where,HashMap<Long, DownloadInfo> idMap, String sortOrder){
		LinkedList<DownloadInfo> list = new LinkedList<DownloadInfo>();
		
//		String sortOrder = "";
		if(sortOrder == null) {
			sortOrder = DownloadAPI.TIMES_TAMP + " DESC";
		}
		Cursor cursor  = getContentResolver().query(DownloadAPI.CONTENT_URI, null,where,
				null, sortOrder);
		if(cursor != null){
			if(cursor.getCount() > 0){

				cursor.moveToLast();
				do {
					DownloadInfo downloadUnitInfo = getDownloadUnit(cursor);
					
					list.add(0,downloadUnitInfo);
					
				} while (cursor.moveToPrevious());
				
			}
			cursor.close();
		}
		return new ArrayList<DownloadInfo>(list);
	}
	
//	public static void sortVoiceGroupDownloadInfo(DownloadInfo groupdownloadUnitInfo){
//		if(!isVoiceGroupDownloadInfo(groupdownloadUnitInfo)){
//			return;
//		}
//		ArrayList<DownloadInfo> downloadUnitInfoList = (ArrayList<DownloadInfo>) groupdownloadUnitInfo.tag;
//		Collections.sort(downloadUnitInfoList, new Comparator<DownloadInfo>(){
//			@Override
//			public int compare(DownloadInfo object1, DownloadInfo object2) {
//				if(object1.position < object2.position){
//					return -1;
//				}else if(object1.position == object2.position){
//					return 0;
//				}else{
//					return 1;
//				}
//			}
//		});
//	}
	
	public static void computeVoiceGroupDownloadInfo(DownloadInfo groupdownloadUnitInfo){
//		ArrayList<DownloadInfo> downloadUnitInfoList = (ArrayList<DownloadInfo>) groupdownloadUnitInfo.tag;
//		boolean hasErr = false;
//		boolean hasStarting = false;
//		boolean hasPause = false;
//		for(DownloadInfo downloadInfo : downloadUnitInfoList){
//			if(downloadInfo.state == DownloadAPI.STATE_START 
//					|| downloadInfo.state == DownloadAPI.STATE_STARTING){
//				hasStarting = true;
//				break;
//			}
//			if(downloadInfo.state == DownloadAPI.STATE_FAIL || downloadInfo.state == DownloadAPI.STATE_FAIL_OUT_MEMORY){
//				hasErr = true;
//			}
//			if(downloadInfo.state == DownloadAPI.STATE_PAUSE){
//				hasPause = true;
//			}
//		}
//		if(hasStarting){
//			groupdownloadUnitInfo.state = DownloadAPI.STATE_STARTING;
//		}else if(hasErr){
//			groupdownloadUnitInfo.state = DownloadAPI.STATE_FAIL;
//		}else if(hasPause){
//			groupdownloadUnitInfo.state = DownloadAPI.STATE_PAUSE;
//		}else{
//			groupdownloadUnitInfo.state = DownloadAPI.STATE_FINISH;
//		}
	}
	
//	public static DownloadInfo findVoiceGroupDownloadInfoById(long id,List<DownloadInfo> allDownloadUnitInfoList){
//		if(allDownloadUnitInfoList == null || allDownloadUnitInfoList.size() == 0){
//			return null;
//		}
//		for(DownloadInfo downloadInfoGroup : allDownloadUnitInfoList){
//			if(!isVoiceGroupDownloadInfo(downloadInfoGroup)){
//				continue;
//			}
//			ArrayList<DownloadInfo> downloadUnitInfoList = (ArrayList<DownloadInfo>) downloadInfoGroup.tag;
//			for(DownloadInfo downloadInfo : downloadUnitInfoList){
//				if(downloadInfo.id == id){
//					return downloadInfoGroup;
//				}
//			}
//		}
//		return null;
//	}
	
	/**
	 * 根据id读取下载单元
	 * @param id
	 * @return
	 */
	public static DownloadInfo getDownloadUnitById(long id){
		DownloadInfo downloadUnitInfo = null;
		Cursor cursor = getContentResolver().query(DownloadAPI.CONTENT_URI, null,DownloadAPI._ID+"="+id,
				null, null);
		if(cursor == null){
			return downloadUnitInfo;
		}
		if(cursor.moveToFirst()){
			 downloadUnitInfo = getDownloadUnit(cursor);
		}
		cursor.close();
		return downloadUnitInfo;
	}
	
	/**
	 * 根据contentId读取下载单元
	 * @param contentId
	 * @return
	 */
	public static DownloadInfo getDownloadUnitById(String contentId){
		DownloadInfo downloadUnitInfo = null;
		Cursor cursor  = getContentResolver().query(DownloadAPI.CONTENT_URI, null
				,DownloadHttpEngine.CONTENT_ID+"='"+contentId+"'",null, null);
		if(cursor == null){
			return downloadUnitInfo;
		}
		if(cursor.moveToFirst()){
			 downloadUnitInfo = getDownloadUnit(cursor);
		}
		cursor.close();
		return downloadUnitInfo;
	}
	
	/**
	 * 根据contentId读取下载单元的下载状态
	 * @param contentId
	 * @return
	 */
	public static int getDownloadUnitStateById(String contentId){
		int state = -1;
		Cursor cursor  = getContentResolver().query(DownloadAPI.CONTENT_URI, null
				,DownloadHttpEngine.CONTENT_ID+"='"+contentId+"'",null, null);
		if(cursor == null){
			return state;
		}
		if(cursor.moveToFirst()){
			 state = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadAPI.STATE));
		}
		cursor.close();
		return state;
	}
	
	/**
	 * 读取cursor的数据并填充到DownloadInfo
	 * @param cursor
	 * @return
	 */
	public static DownloadInfo getDownloadUnit(Cursor cursor){
		DownloadInfo downloadInfo = new DownloadInfo(
				cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI._ID)),
				cursor.getInt(cursor.getColumnIndexOrThrow(DownloadAPI.STATE)),
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.ICON_URL)),
				cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_BYTE_CURRENT_SIZE)),
				cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_BYTE_SIZE)),
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.CONTENT_NAME)),
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.CONTENT_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_PATH)),
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.AUTHOR_NAME)),
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.BOOK_IS_ORDER)),//存储书籍购买状态
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.BOOK_FEE_START)),//存储书籍购买点
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.BOOK_PRICE)),//存储原价
				cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.BOOK_PROMOTION_PRICE)));//存储优惠价
		downloadInfo.secret_key = cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.BOOK_SECRET_KEY));//用于保存解密key
		downloadInfo.downloadType = cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.DOWNLOAD_TYPE));
		downloadInfo.contentType = cursor.getString(cursor.getColumnIndexOrThrow(DownloadHttpEngine.CONTENT_TYPE));
		downloadInfo.timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.TIMES_TAMP));
		downloadInfo.isDownloadFullVersonBook = cursor.getInt(
									cursor.getColumnIndexOrThrow(DownloadHttpEngine.BOOK_DOWNLOAD_FULL_VERSION)) ==0?false:true;
		return downloadInfo;
	}
	
	/**
	 * 根据关键字搜索下载单元
	 * @return
	 */
	public static ArrayList<DownloadInfo> searchBook(String keyWord, int searchType) {
		ArrayList<DownloadInfo> list = new ArrayList<DownloadInfo>();
		if (keyWord == null) {
			return list;
		}
		String where = null;
		String[] selectionArgs = null;
		if (searchType == SEARCH_TYPE_ALL || searchType == SEARCH_TYPE_KEYWORD) {
			where = DownloadHttpEngine.CONTENT_NAME + " like ? OR "
					+ DownloadHttpEngine.AUTHOR_NAME + " like ?";
			selectionArgs = new String[] { "%" + keyWord + "%",
					"%" + keyWord + "%" };
		} else if (searchType == SEARCH_TYPE_BOOK_NAME) {
			where = DownloadHttpEngine.CONTENT_NAME + " like ?";
			selectionArgs = new String[] { "%" + keyWord + "%" };
		} else if (searchType == SEARCH_TYPE_AUTHOR) {
			where = DownloadHttpEngine.AUTHOR_NAME + " like ?";
			selectionArgs = new String[] { "%" + keyWord + "%" };
		}
		if (TextUtils.isEmpty(where) || selectionArgs == null) {
			return list;
		}

		DownloadInfo downInfo = null;
		Cursor cursor = getContentResolver().query(
				DownloadAPI.CONTENT_URI, null, where, selectionArgs,
				null);
		if (cursor != null) {
			while(cursor.moveToNext()) {
				downInfo = getDownloadUnit(cursor);
				list.add(downInfo);
			}
			cursor.close();
			cursor = null;
		}
		return list;
	}
	
	/**
	 * 读取cursor的数据并填充到ContentInfo
	 * @param cursor
	 * @return
	 */
	public static ContentInfoLeyue getContentInfo(Cursor cursor){
		DownloadInfo downloadInfo = getDownloadUnit(cursor);
		ContentInfoLeyue contentInfo = new ContentInfoLeyue();
		contentInfo = new ContentInfoLeyue();
		contentInfo.setBookId(downloadInfo.contentID);
		contentInfo.setBookName(downloadInfo.contentName);
		contentInfo.setAuthor(downloadInfo.authorName);
		contentInfo.setIntroduce("无");
		contentInfo.setCoverPath(downloadInfo.logoUrl);
		return contentInfo;
	}
	
	public interface DownloadTipRunnable{
		public void postTip(int successSize,int hasSize,int finishedSize);
	}
	
	public interface IDatchDownloadsUIRunnadle{
		public static int RESULT_STOP = -1;
		public static int RESULT_PRE_RUN_IN_THREAD_FAIL = -2;
		public void onSDcardNotExist();
		public void onFreeNotEnough();
		public boolean onPreRun();
		public boolean onPreRunInThread();
		public void onPostRun(int successSize, int hasSize, int finishedSize, int state);
	}
	
	public interface LoadDataUIRunnadle{
		public boolean onPreRun();
		public void onPostRun(ArrayList<DownloadInfo> datas);
	}
	
	public interface DeleteDownloadsUIRunnadle{
		public boolean onPreRun();
		public void onPostRun(int successSize);
	}
	
	public static class DatchDownloadsUIRunnadle implements IDatchDownloadsUIRunnadle{
		private Context mContext;
		public DatchDownloadsUIRunnadle(Context context){
			mContext = context;
		}
		@Override
		public void onSDcardNotExist() {
			ToastUtil.showToast(mContext, R.string.sdcard_no_exist_download_tip);
		}

		@Override
		public void onFreeNotEnough() {
			ToastUtil.showToast(mContext, R.string.sdcard_no_exist_Free_Not_Enough_tip);	
		}

		@Override
		public boolean onPreRun() {
			return false;
		}

	
		
		@Override
		public boolean onPreRunInThread() {
			return false;
		}
		
		@Override
		public void onPostRun(int successSize, int hasSize, int finishedSize,
				int state) {
			
			
		}
	}
}
