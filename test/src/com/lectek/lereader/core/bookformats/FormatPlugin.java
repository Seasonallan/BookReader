package com.lectek.lereader.core.bookformats;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.lectek.lereader.core.bookformats.epub.Resource;
import com.lectek.lereader.core.util.LogUtil;


/** 读取电子书内容基类，具体的书籍格式解析需继承此类实现具体的解析。
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-18
 */
public abstract class FormatPlugin {
	
	protected String contentId;
	protected long fileSize;//文件大小
	/** 是否系统默认支持的格式 */
	protected boolean isSystemFormat;
	/** 文件路径  */
	protected String filePath;
	/** 书籍格式 */
	protected String format = "unknow";
	/** 书籍信息 */
	protected BookInfo bookInfo;
	/** 书籍文件名 */
	protected String fileName;
	/** 书籍目录 */
	private ArrayList<Catalog> catalog = new ArrayList<Catalog>();
	/** 书籍章节ID列表 */
	private ArrayList<String> chapterIds = new ArrayList<String>();
	
	public FormatPlugin(String filePath){
		setFilePath(filePath);
	}
	
	/** 外部调用初始化资源
	 * @param secretKey 没有则传空 null
	 * @throws Exception
	 */
	public abstract void init(String secretKey) throws Exception;
	
	/** 
	 * 获取epub的书籍信息
	 * @throws Exception
	 */
	public abstract BookInfo getEpubBookInfo() throws Exception;

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/** 设置书籍的格式
	 * @param format the format to set
	 */
	protected abstract void setFormat(String format);

	/** 判断是否系统支持的格式
	 * @return the isSystemFormat
	 */
	public boolean isSystemFormat() {
		return isSystemFormat;
	}

	/** 设置是否系统支持的格式
	 * @param isSystemFormat the isSystemFormat to set
	 */
	public void setSystemFormat(boolean isSystemFormat) {
		this.isSystemFormat = isSystemFormat;
	}

	/** 获取书籍文件的路径
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/** 设置书籍文件的路径
	 * @param filePath the filePath to set
	 */
	protected void setFilePath(String filePath) {
		this.filePath = filePath;
		LogUtil.i("FormatPlugin", "set file path: " + filePath);
	}

	/** 获取书籍ID
	 * @return the contentId
	 */
	public String getContentId() {
		return contentId;
	}

	/** 文件大小
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/** 获取书籍信息
	 * @return the bookInfo
	 */
	public BookInfo getBookInfo() {
		return bookInfo;
	}

	/** 获取书籍目录；不为NULL，如果没有目录，SIZE为0
	 * @return the catalog
	 */
	public ArrayList<Catalog> getCatalog() {
		return catalog;
	}
	
	/**
	 * @return the chapterIds
	 */
	public ArrayList<String> getChapterIds() {
		return chapterIds;
	}

	protected void setCatalog(List<Catalog> catalogs){
		if(catalogs == null){
			return;
		}
		this.catalog.clear();
		this.catalog.addAll(catalogs);
	}
	
	protected void setChapterIds(List<String> chapterIds){
		if(chapterIds == null){
			return;
		}
		this.chapterIds.clear();
		this.chapterIds.addAll(chapterIds);
	}

	public abstract int getChapterIndex(Catalog catalog);
	
	public abstract Catalog getCatalogByIndex(int index);

	/** 获取章节内容
	 * @param chapterID
	 * @return
	 * @throws Exception
	 */
	public abstract Chapter getChapter(String chapterID) throws Exception;

	/**
	 *  释放解析的资源
	 */
	public abstract void recyle();

	public abstract Resource findResource(String path);

    /**
     * 获取书籍封面信息流
     * @return
     */
    public abstract InputStream getCoverStream();
}
