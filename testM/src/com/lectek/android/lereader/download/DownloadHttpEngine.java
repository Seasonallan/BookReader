package com.lectek.android.lereader.download;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.TextUtils;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.download.HttpHandler;
import com.lectek.android.lereader.lib.net.http.AbsConnect;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.lib.utils.LogUtil;
import com.lectek.android.lereader.permanent.DownloadAPI;
import com.lectek.android.lereader.utils.Constants;

public class DownloadHttpEngine extends HttpHandler {
	public static final String TAG = "DownloadHttpEngine";
	/**
	 * 有声下载时作为音频ID使用
	 */
	public static final String CONTENT_ID = DownloadAPI.DATA0;
	public static final String URL = DownloadAPI.DATA1;
	public static final String CONTENT_NAME = DownloadAPI.DATA2;
	public static final String AUTHOR_NAME = DownloadAPI.DATA3;
	/**
	 * 有声下载时作为书籍ID使用
	 */
	public static final String CONTENT_TYPE = DownloadAPI.DATA4;
	public static final String TICKET_URL = DownloadAPI.DATA5;
	public static final String ICON_URL = DownloadAPI.DATA6;
	/**
	 * 下载类型
	 */
	public static final String DOWNLOAD_TYPE = DownloadAPI.DATA7;
	
	public static final String VOICE_NEXT_TITLE = DownloadAPI.DATA8;
	public static final String VOICE_AUDIO_NAME = DownloadAPI.DATA9;
	public static final String VOICE_LENGTH = DownloadAPI.DATA10;
	public static final String VOICE_POSITiON = DownloadAPI.DATA11;
	
	/**新增扩展类型--与书籍购买相关*/
	public static final String BOOK_IS_ORDER = DownloadAPI.DATA12;//用作存储 是否购买
	public static final String BOOK_FEE_START = DownloadAPI.DATA13;//用作存储购买点
	public static final String BOOK_PRICE = DownloadAPI.DATA14;//用作存储原价
	public static final String BOOK_PROMOTION_PRICE = DownloadAPI.DATA15;//用作存储优惠价
	public static final String BOOK_SECRET_KEY = DownloadAPI.DATA16;//用作书籍解密key
	public static final String BOOK_DOWNLOAD_FULL_VERSION = DownloadAPI.DATA17;//用作存储 是否需要下载完整版本书籍
	/**
	 * 下载类型:有声下载
	 */
	public static final String VALUE_DOWNLOAD_TYPE_VOICE = "VALUE_DOWNLOAD_TYPE_VOICE";
	/**
     *
	 * 下载类型：书籍下载默认是EPUB
	 */
	public static final String VALUE_DOWNLOAD_TYPE_BOOK = "VALUE_DOWNLOAD_TYPE_BOOK";
	/**
	 * Text或者UMD
	 */
	public static final String VALUE_DOWNLOAD_TYPE_TEXT_UND = "VALUE_DOWNLOAD_TYPE_TEXT_UND";
	/**
	 * PDF书籍
	 */
	public static final String VALUE_DOWNLOAD_TYPE_PDF = "VALUE_DOWNLOAD_TYPE_PDF";
	/**
	 * CEB书籍
	 */
	public static final String VALUE_DOWNLOAD_TYPE_CEB = "VALUE_DOWNLOAD_TYPE_CEB";
	
	private Context mContext;
	public DownloadHttpEngine(){
		FileUtil.createFileDir(Constants.BOOKS_DOWNLOAD);
		mContext = MyAndroidApplication.getInstance();
	}
	
	@Override
	public DownloadInputStream getDownloadFileStream(String url,
			long currentByteSize,long id){
		Info downloadInfo = getDownloadInfoById(id);
		if(downloadInfo == null){
			return null;
		}
		return downloadBookLeyue(url, currentByteSize, downloadInfo);
	}

	private DownloadInputStream downloadBookLeyue(String url,
			long currentByteSize,Info downloadInfo){
		if(downloadInfo == null || TextUtils.isEmpty(downloadInfo.url)){
			return null;
		}else{
			ContentValues values = new ContentValues();
			values.put(URL, downloadInfo.url);
//			values.put(DownloadAPI.FILE_BYTE_SIZE, downloadInfo.size);
			mContext.getContentResolver().update(DownloadAPI.CONTENT_URI_NOT_NOTICE, values,
					CONTENT_ID + " ='" +downloadInfo.contentID+"'", null);
		}
		//TODO:版权加密
//		try {
//			DRMSaxParser.getInstance(mContext).findContentTicket(downloadInfo.contentID);
//		} catch (Exception e) {
//		}
		
		return onDwnload(downloadInfo.url,currentByteSize,downloadInfo.size,true);
	}

	private DownloadInputStream onDwnload(String url,long currentByteSize,long saveTotalBytes,boolean isStrict){
		long totalBytes = -1;
		DefaultHttpClient client = null;
		InputStream is = null;
		try {
			client = AbsConnect.getDefaultHttpClient(mContext);
			HttpGet get = new HttpGet(url);
			get.setHeader(AbsConnect.HEADER_REQUEST_X_REFERRED, AbsConnect.X_REFERRED);
			if(currentByteSize != 0){
				get.setHeader("Range", "bytes=" + currentByteSize + "-");
			}
			for(Header header : get.getAllHeaders()){
				LogUtil.v(TAG, "reuest header: " + header.getName() + " value: " + header.getValue());
			}
			HttpResponse rp = client.execute(get);
			for(Header header : rp.getAllHeaders()){
				LogUtil.v(TAG, "respone header: " + header.getName() + " value: " + header.getValue());
			}
			int statusCode = rp.getStatusLine().getStatusCode();
			LogUtil.v(TAG, "status code " + statusCode);
			Header h = rp.getFirstHeader("Content-Length");
			boolean isConnectSucceed = statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_PARTIAL_CONTENT;
			
			if(h != null && h.getValue() != null && TextUtils.isDigitsOnly(h.getValue())){
				totalBytes = Integer.valueOf(h.getValue());
			}
			//模拟chunk模式
//			totalBytes = -1;
			if(totalBytes == -1 || totalBytes == 0 || !isConnectSucceed){//DownloadHttpEngine.java
				totalBytes = saveTotalBytes;
			}else{
				totalBytes = totalBytes + currentByteSize;
			}
			
			LogUtil.v(TAG, "total bytes " + totalBytes);
			if (isConnectSucceed) {
				is = rp.getEntity().getContent();
				return new DownloadInputStream(new ByteStreamRecorder(mContext, is, DOWNLOAD_STREAM_DATA1), totalBytes, isStrict);
			}
//			return new DownloadInputStream(is, totalBytes, isStrict);
			
		} catch (Exception e) {
			LogUtil.e("DownloadThread", "download err ", e);
		}
		return null;
	}

    public static final String DOWNLOAD_STREAM_DATA1 = "download_stream_data1";
    /**
     * 下载统计
     * @author chends
     *
     */
    public static class ByteStreamRecorder extends InputStream{

        private static final String VOICE_STREAM_RECORDER = "voice_stream_recorder";
        private SharedPreferences mSharedPref;
        private long size;
        private String mExtraName;

        private InputStream mInputStream;

        public ByteStreamRecorder(Context context, InputStream inputStream, String extraName) {
            mInputStream = inputStream;
            mSharedPref = context.getSharedPreferences(VOICE_STREAM_RECORDER, 0);
            mExtraName = extraName;
        }

        @Override
        public int read() throws IOException {
            int ch = mInputStream.read();
            size += (ch == -1 ? 0 : 1);
            return mInputStream.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            int result = mInputStream.read(b, 0, b.length);
            size += (Math.max(0, result));
            return result;
        }

        @Override
        public int read(byte[] b, int offset, int length) throws IOException {
            int result = mInputStream.read(b, offset, length);
            size += (Math.max(0, result));
            return result;
        }

        @Override
        public int available() throws IOException {
            return mInputStream.available();
        }

        @Override
        public void close() throws IOException {

            long streamSize = mSharedPref.getLong(mExtraName, 0);

            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putLong(mExtraName, size + streamSize);
            editor.commit();
            size = 0;

            if(mInputStream == null) {
                mInputStream.close();
            }
            super.close();
        }

        @Override
        public void mark(int readlimit) {
            mInputStream.mark(readlimit);
        }

        @Override
        public boolean markSupported() {
            return mInputStream.markSupported();
        }

        @Override
        public synchronized void reset() throws IOException {
            mInputStream.reset();
        }
        public long skip(long byteCount) throws IOException {
            return mInputStream.skip(byteCount);
        }
    }

	private Info getDownloadInfoById(long id){
		Cursor cursor = null;
		try{
            Info downloadInfo = null;
			cursor = MyAndroidApplication.getInstance().getContentResolver().query(DownloadAPI.CONTENT_URI,
					null, DownloadAPI._ID + "=" + id, null, null);
			if(cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()){
				downloadInfo = new Info();
				downloadInfo.contentID = cursor.getString(cursor.getColumnIndexOrThrow(CONTENT_ID));
				downloadInfo.url = cursor.getString(cursor.getColumnIndexOrThrow(URL));
/*				downloadInfo.contentName = cursor.getString(cursor.getColumnIndexOrThrow(CONTENT_NAME));
				downloadInfo.authorName = cursor.getString(cursor.getColumnIndexOrThrow(AUTHOR_NAME));
				downloadInfo.contentType = cursor.getString(cursor.getColumnIndexOrThrow(CONTENT_TYPE));
				downloadInfo.ticketURL = cursor.getString(cursor.getColumnIndexOrThrow(TICKET_URL));
				downloadInfo.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(DOWNLOAD_TYPE));*/
				downloadInfo.size = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadAPI.FILE_BYTE_SIZE));
				//downloadInfo.isOrder = cursor.getString(cursor.getColumnIndexOrThrow(DownloadAPI.DATA8));
			}
			
			return downloadInfo;
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return null;
	}

    private class Info{
        public String contentID;
        public String url;
        public long size;
    }

}
