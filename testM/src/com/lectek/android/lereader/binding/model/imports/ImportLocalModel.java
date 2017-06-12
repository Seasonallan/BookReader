package com.lectek.android.lereader.binding.model.imports;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.text.TextUtils;
import android.text.format.Formatter;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.BaseLoadDataModel;
import com.lectek.android.lereader.data.FileData;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.PluginManager;

public class ImportLocalModel extends BaseLoadDataModel<ArrayList<FileData>>{
	public static final int LOAD_CURRENT_DIRECTORY = 1;
	public static final int LOAD_SCAN_FILES = 2;
	public static final String EXCEPTION_MSG_SDCARD_NOT_EXIST = "exception_msg_sdcard_not_exist";
	
	private int mScanNum = 0;
	private int mScanEpubNum = 0;
	private int mScanTxtNum = 0;
	private int mScanPdfNum = 0;
	private ArrayList<FileData> mScanList = new ArrayList<FileData>();
	
	private void initScanParams() {
		mScanNum = 0;
		mScanEpubNum = 0;
		mScanTxtNum = 0;
		mScanPdfNum = 0;
		mScanList.clear();
	}

	@Override
	protected ArrayList<FileData> onLoad(Object... params) throws Exception {
		ArrayList<FileData> fileDatas = null;
		if(params != null && params.length > 0) {
			int loadType = (Integer) params[0];
			String filePath = (String) params[1];
			if(loadType == LOAD_CURRENT_DIRECTORY) {
				fileDatas = loadCurrentDirectory(filePath);
			} else if (loadType == LOAD_SCAN_FILES) {
				fileDatas = scanFiles(filePath);
			}
			if(fileDatas != null && fileDatas.size() > 0) {
				Collections.sort(fileDatas, mComparator);
			}
			if(loadType == LOAD_CURRENT_DIRECTORY) {
				fileDatas = sortByFileType(fileDatas);
			}
		}
		return fileDatas;
	}
	
	/**
	 * 按照先文件夹后文件的方式排序
	 * @param fileDatas
	 * @return
	 */
	private ArrayList<FileData> sortByFileType(ArrayList<FileData> fileDatas) {
		if(fileDatas == null) {
			return null;
		}
		ArrayList<FileData> dirDatas = new ArrayList<FileData>();
		ArrayList<FileData> notDirDatas = new ArrayList<FileData>();
		for(FileData data : fileDatas) {
			File file = new File(data.mAbsolutePath);
			if(file.isDirectory()) {
				dirDatas.add(data);
			} else {
				notDirDatas.add(data);
			}
		}
		dirDatas.addAll(notDirDatas);
		return dirDatas;
	}
	
	/**
	 * 读取指定目录
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private ArrayList<FileData> loadCurrentDirectory(String filePath) throws Exception {
		if(!FileUtil.isSDcardExist()) {
			throw new Exception(EXCEPTION_MSG_SDCARD_NOT_EXIST);
		}
		File currFile = new File(filePath);
		if(!currFile.exists() || !currFile.isDirectory()) {
			return null;
		}
		ArrayList<FileData> tempFiles = null;
		String[] tempStrs = currFile.list(mDirFilter);
		if(tempStrs != null) {
			tempFiles = new ArrayList<FileData>();
			for(int i = 0; i < tempStrs.length; i++) {
				File f = new File(currFile, tempStrs[i]);
				String name = getName(f, tempStrs[i]);
				tempFiles.add(new FileData(f.getPath(), name, getFileInfo(f)));
			}
		}
		return tempFiles;
	}

	/**
	 * 扫描当前文件夹下的文件
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private ArrayList<FileData> scanFiles(String filePath) throws Exception {
		if(!FileUtil.isSDcardExist()) {
			throw new Exception(EXCEPTION_MSG_SDCARD_NOT_EXIST);
		}
		initScanParams();
		String[] tempStrs = new File(filePath).list(mDirFilter);
		scanFiles(tempStrs, filePath);
		return mScanList;
	}
	
	/**
	 * 扫描文件
	 * @param tempStrs
	 * @param currentDirectory
	 * @throws Exception
	 */
	private void scanFiles(String[] tempStrs, String currentDirectory) throws Exception{
		ArrayList<File> directorys = new ArrayList<File>();
		for (int i = 0; i < tempStrs.length; i++) {
			if(mScanListener != null) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mScanListener.onScan(++mScanNum);
					}
				});
			}
			String path = currentDirectory + File.separator + tempStrs[i];
			File file = new File(path);
			if(!file.isDirectory()){
				File f = new File(path);
				String name = getName(f, tempStrs[i]);
				FileData fileData = new FileData(path, name,getFileInfo(file));
				// TODO zxh 过滤已经在书架的书籍
				mScanList.add(fileData);
				checkFile(fileData);
			}else{
				directorys.add(file);
				path += "/";
			}
		}
		for (int i = 0; i < directorys.size(); i++) {
			File file = directorys.get(i);
			tempStrs = file.list(mDirFilter);
			scanFiles(tempStrs, file.getPath());
		}
	}
	
	private void checkFile(FileData fileData) {
		if(fileData == null) {
			return;
		}
		String path = fileData.mAbsolutePath;
		if(mScanListener != null) {
			if(FileUtil.isEpub(path)) {
				++mScanEpubNum;
			} else if (FileUtil.isTxt(path)) {
				++mScanTxtNum;
			} else if (FileUtil.isPdf(path)) {
				++mScanPdfNum;
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mScanListener.onFindFile(mScanEpubNum, mScanTxtNum, mScanPdfNum);
				}
			});
		}
	}
	
	/**
	 * 获取书名或去除后缀名的文件名
	 * @param file
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private String getName(File file, String fileName) throws Exception {
		if(file.isDirectory()) {
			return fileName;
		}
		if(Constants.BOOKS_DOWNLOAD.equals(FileUtil.getDirectoryName(file))) {
			BookInfo bookInfo = PluginManager.instance().getEpubBookInfoByPlugin(file.getPath());
			if(bookInfo != null) {
				if(!TextUtils.isEmpty(bookInfo.title)) {
					return bookInfo.title;
				}
			}
		}
		int i = fileName.lastIndexOf(".");
		if(i > -1 && i < (fileName.length() - 1)) {
			return fileName.substring(0, i);
		}
		return fileName;
	}
	
	private String getFileInfo(File file) {
		if(file.isDirectory()){
			String[] list = file.list(mDirFilter);
			return (list != null ? list.length : 0) + getContext().getString(R.string.import_local_file_num);
		}else{
			return getContext().getString(R.string.import_local_file_size) + Formatter.formatFileSize(getContext(), file.length());
		}
	}
	
	/**
	 * 文件过滤
	 */
	private FilenameFilter mDirFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {// 过滤除epub、txt、pdf以外的文件
			File mFile = new File(dir, name);
			if (!mFile.canRead()) {// 过滤不可读的文件
				return false;
			}
			name = name.toLowerCase();
			if (mFile.isDirectory()) {// 如果是文件夹
				return true;
			} else {// 非文件夹
//				if(mFile.length() < 1024) {	//小于1kb
//					return false;
//				}
				if (name.endsWith(".epub") || name.endsWith(".txt") || name.endsWith(".pdf") || name.endsWith(".umd")) {
					return true;
				}
			}
			return false;
		}
	};
	
	private Comparator<FileData> mComparator = new Comparator<FileData>() {
		@Override
		public int compare(FileData object1, FileData object2) {
			return object1.mName.compareToIgnoreCase(object2.mName);
		}
	};
	
	private OnScanListener mScanListener;
	
	public void setOnScanListener(OnScanListener onScanListener) {
		mScanListener = onScanListener;
	}
	
	public interface OnScanListener {
		public void onScan(int scanNum);
		public void onFindFile(int epubNum, int txtNum, int pdfNum);
		public void onCancelScan(List<FileData> fileDatas);
	}

}
