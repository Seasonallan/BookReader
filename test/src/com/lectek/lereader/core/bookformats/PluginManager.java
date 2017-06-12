package com.lectek.lereader.core.bookformats;

import java.io.File;

import android.text.TextUtils;

import com.lectek.lereader.core.bookformats.epub.EpubPlugin;

/** 插件管理；单实例
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-3-14
 */
public class PluginManager {
	private static PluginManager instance;
	/** 文件格式；CEB（电信）电子书格式 */
	public static final String EXTENSION_CEB = "ceb";
	/** 文件格式；EPUB电子书格式 */
	public static final String EXTENSION_EPUB = "epub";
	/** 文件格式；在线电子书 */
	public static final String EXTENSION_ONLINE = "online";
	/** 文件格式；在线SMIL格式，如：版式杂志、漫画 */
	public static final String EXTENSION_ONLINE_SMIL = "online_smil";
	/** 文件格式；在线流式杂志 */
	public static final String EXTENSION_ONLINE_STREAM_MAGAZINE = "online_stream_magazine";
	/** 文件格式；TXT格式 */
	public static final String EXTENSION_TXT = "txt";
	/** 文件格式；UMD格式 */
	public static final String EXTENSION_UMD = "umd";
	
	public static PluginManager instance() {
		if (instance == null) {
			instance = new PluginManager();
		}
		return instance;
	}
	
	private FormatPlugin getFormatPlugin(String filePath){
		String path = filePath.toLowerCase();
		if (path.endsWith(EXTENSION_EPUB)) {
			return new EpubPlugin(filePath);
		}
		return null;
	}
	
	/**
	 * 取得插件，获取书籍信息
	 * @param fileName
	 * @return
	 */
	public BookInfo getEpubBookInfoByPlugin(String filePath) throws Exception {
		if (TextUtils.isEmpty(filePath)) {
			throw new NullPointerException("书籍的文件名为空");
		}
		File file = new File(filePath);
		if(!file.exists() || !file.isFile()){
			throw new NullPointerException("不是正确的书籍文件");
		}
		FormatPlugin formatPlugin = getFormatPlugin(filePath);
		if (formatPlugin == null) {
			throw new NullPointerException("不支持阅读该格式文件");
		}
		try {
			return formatPlugin.getEpubBookInfo();
		} catch (NullPointerException e) {
			throw e;
		}
	}
	/**
	 * 取得插件
	 * 
	 * @param fileName
	 * @return
	 */
	public FormatPlugin getPlugin(String filePath,String secretKey) throws Exception {
		if (TextUtils.isEmpty(filePath)) {
			throw new NullPointerException("书籍的文件名为空");
		}
		File file = new File(filePath);
		if(!file.exists() || !file.isFile()){
			throw new NullPointerException("不是正确的书籍文件");
		}
		FormatPlugin formatPlugin = getFormatPlugin(filePath);
		if (formatPlugin == null) {
			throw new NullPointerException("不支持阅读该格式文件");
		}
		try {
			formatPlugin.init(secretKey);
		} catch (NullPointerException e) {
			throw e;
		}
		return formatPlugin;
	}
}
