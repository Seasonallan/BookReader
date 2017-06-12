package com.lectek.android.lereader.lib.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lectek.android.lereader.lib.utils.FileUtil;

/**
 * @author linyiwei
 * @email 21551594@qq.com
 * @date 2011-11-1
 */
public abstract class HttpHandler{
	
	public class DownloadResultCode{
		public static final int DOWNLOAD_RES_CODE_SUCCESS = 1;
		public static final int DOWNLOAD_RES_CODE_FAILURE_FILE = 2;
		public static final int DOWNLOAD_RES_CODE_FAILURE_FILE_OUT_MEMORY = 3;
		public static final int DOWNLOAD_RES_CODE_FAILURE_NETWORK = 4;
	}
	
	
	/**
	 * 缓冲大小
	 */
	private int BUFFER_SIZE = 10240;
	/**
	 * 是否停止
	 */
	private boolean isStop = false;
	/**
	 * 下载监听接口
	 */
	private OnDownloadListener mOnDownloadListener;
	private InputStream mInputStream;
	/**
	 * 得到下载文件的输入流，需要模块外实现
	 * @return
	 */
	public abstract DownloadInputStream getDownloadFileStream(String url, long currentByteSize,long id);
	
	
	public class DownloadInputStream{
		
		/**
		 *
		 */
		public DownloadInputStream(InputStream mInputStream, long mFileSize,boolean isStrict) {
			this.mInputStream = mInputStream;
			this.mFileSize = mFileSize;
			this.isStrict = isStrict;
		}
		private InputStream mInputStream;
		private long mFileSize = 0;
		private boolean isStrict = false;
	} 
	
	public void setIsStop(boolean isStop){
		this.isStop = isStop;
	}
	
	public void setOnDownloadListener(OnDownloadListener downloadListener){
		mOnDownloadListener = downloadListener;
	}
	
	public int startDownload(String url, String path, long currentByteSize,long ByteSize,long id){
		if(!FileUtil.checkPath(path, true)){
			return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE;
		}
		if(FileUtil.getStorageSize() < BUFFER_SIZE){
			return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE_OUT_MEMORY;
		}
		File file = new File(path);
		if(!file.exists()){
			file = new File((String) path.subSequence(0, path.lastIndexOf(".")) + ".tmp");
		}else{
			boolean isSucce = file.renameTo(new File((String) path.subSequence(0, path.lastIndexOf(".")) + ".tmp"));
			file = new File((String) path.subSequence(0, path.lastIndexOf(".")) + ".tmp");
		}
		boolean append = true;
		FileOutputStream outputStream = null;
//		BufferedInputStream bis = null;
		mInputStream = null;
		try{
			try{
				if(file.isFile() && file.exists() && ByteSize != 0){
					currentByteSize = file.length();
				}else{
					currentByteSize = 0;
					append = false;
				}
				outputStream = new FileOutputStream(file,append);
			}catch (Exception e) {
				e.printStackTrace();
				if(mOnDownloadListener != null){
					mOnDownloadListener.onDownloadErr(id,null);
				}
				return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE;
			}
			if(mOnDownloadListener != null && ByteSize == file.length() && ByteSize != 0){
				file.renameTo(new File(path));
				mOnDownloadListener.onDownloadProgressChange(id, ByteSize ,ByteSize);
				mOnDownloadListener.onDownloadFinish(id);
				return DownloadResultCode.DOWNLOAD_RES_CODE_SUCCESS;
			}
			DownloadInputStream mDownloadInputStream = getDownloadFileStream( url,currentByteSize,id);
			if(mDownloadInputStream == null){
				if(mOnDownloadListener != null){
					mOnDownloadListener.onDownloadErr(id,null);
				}
				return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_NETWORK;
			}

			mInputStream = mDownloadInputStream.mInputStream;
			long size = mDownloadInputStream.mFileSize;
			
			if(mOnDownloadListener != null && size == file.length() && size != 0 
					|| ( !mDownloadInputStream.isStrict && size <= file.length() && size != 0)){
				file.renameTo(new File(path));
				mOnDownloadListener.onDownloadProgressChange(id, size ,size);
				mOnDownloadListener.onDownloadFinish(id);
				return DownloadResultCode.DOWNLOAD_RES_CODE_SUCCESS;
			}
			
			if(mDownloadInputStream.mInputStream == null || mDownloadInputStream.mFileSize == 0){
				if(mOnDownloadListener != null){
					mOnDownloadListener.onDownloadErr(id,null);
				}
				return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_NETWORK;
			}
			
			if(FileUtil.getStorageSize() < size){
				return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE_OUT_MEMORY;
			}
			
			if(mOnDownloadListener != null){
				mOnDownloadListener.onDownloading(id,file.length(),size);
			}
//			bis = new BufferedInputStream(mInputStream);
			
			long count = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
		    int n = 0;
		    while (-1 != (n = mInputStream.read(buffer, 0, BUFFER_SIZE)) && !isStop) {
		    	count += n;
		    	outputStream.write(buffer, 0, n);
				if(mOnDownloadListener != null){
					if(!mOnDownloadListener.onDownloadProgressChange(id, count + currentByteSize ,size)){
						break;
					}
				}
				Thread.sleep(20L);
		    }
		    if(-1 == n){
		    	 if( count + currentByteSize == size || !mDownloadInputStream.isStrict){
		    		 	file.renameTo(new File(path));
		    		 	if(mOnDownloadListener != null){
		    				mOnDownloadListener.onDownloadFinish(id);
		    			}
				    	return DownloadResultCode.DOWNLOAD_RES_CODE_SUCCESS;
				    }else{
				    	file.delete();
				    	if(mOnDownloadListener != null){
							mOnDownloadListener.onDownloadErr(id,null);
						}
				    	return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE;
				    }
		    }
		    return DownloadResultCode.DOWNLOAD_RES_CODE_SUCCESS;
		}catch(IOException e){
			e.printStackTrace();
			if(mOnDownloadListener != null){
				mOnDownloadListener.onDownloadErr(id,e);
			}
			if(FileUtil.getStorageSize() < BUFFER_SIZE){
				return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE_OUT_MEMORY;
			}
			return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_NETWORK;
		}catch(Exception e){
			e.printStackTrace();
			if(mOnDownloadListener != null){
				mOnDownloadListener.onDownloadErr(id,e);
			}
			if(FileUtil.getStorageSize() < BUFFER_SIZE){
				return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE_OUT_MEMORY;
			}
			return DownloadResultCode.DOWNLOAD_RES_CODE_FAILURE_FILE;
		}finally{
			try {
//				 bis.close();
				new Thread("关闭输出流线程"){
					@Override
					public void run(){
						InputStream inputStream = mInputStream;
						if(inputStream != null){
							try {
								//TODO  开发初期的时候发现关闭流的时间需要很久，待验证是否还存在同样问题
								inputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}.start();
				if(outputStream != null){
					outputStream.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
	
}
