package com.lectek.lereader.core.txtumd.txt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 章节控制
 * 
 * @author laijp
 * @date 2014-1-26
 * @email 451360508@qq.com
 */
public class ChapterControll {
	public static class Chapter {
		public String cName;
		public int cPosition;
	}

	public static final String DEFAULT_CHAPTER = "正文";
	private List<Chapter> mChapters;
	private int mCurrentChapter;
	private String mFilePath; 
	private String mChapterPath;
 
	public ChapterControll(String filePath, String chapterPath) {
		this.mChapterPath = chapterPath;
		this.mFilePath = filePath;
		this.mChapters = new ArrayList<ChapterControll.Chapter>();
	}

	public void addChapterList(List<Chapter> chapter) {
		this.mChapters.addAll(chapter);
	}

	public void addChapter(String name, int position) {
		Chapter c = new Chapter();
		c.cName = name;
		c.cPosition = position;
		mChapters.add(c);
	}

	public int getNextChapterIndex() {
		if (mCurrentChapter < mChapters.size() - 1) {
			return mChapters.get(mCurrentChapter + 1).cPosition;
		}
		return 1;
	}

	public void setCurrentCHapterIndex(int index) {
		this.mCurrentChapter = index;
	}

	public int getCurrentChapterIndex() {
		return mCurrentChapter;
	}

	public int getCurrentChapterPosition() {
		if (mCurrentChapter >= mChapters.size()) {
			return 0;
		}
		return mChapters.get(mCurrentChapter).cPosition;
	}

	public List<Chapter> getChapters() {
		return mChapters;
	}

	/**
	 * 获取当前章节起始字节位置
	 * 
	 * @param chapterIndex
	 * @return
	 */
	public int getChapterWordIndex(int chapterIndex) {
		if (mChapters != null && mChapters.size() > chapterIndex) {
			return mChapters.get(chapterIndex).cPosition;
		}
		return 0;
	}

	/**
	 * 通过阅读位置获取当前所在章节
	 * 
	 * @param position
	 * @return
	 */
	public int getChapterIndexByPosition(int position) {
		if (mChapters != null && mChapters.size() > 0) {
			for (int i = 1; i < mChapters.size(); i++) {
				if (mChapters.get(i).cPosition > position) {
					return i - 1;
				}
			}
			return mChapters.size() - 1;
		}
		return 0;
	}

	/**
	 * 获取当前章节名
	 * @return
	 */
	public String getCurrentChapterName() {
		if (mChapters == null || mChapters.size() == 0) {
			return DEFAULT_CHAPTER;
		}
		return mChapters.get(mCurrentChapter).cName;
	}
 
	
	
	private String buildChapterStr() throws Exception{
		if(mChapters == null){
			return "";
		}
		JSONArray json = new JSONArray();
		for (int i = 0; i < mChapters.size(); i++) {
			JSONObject obj = new JSONObject();
			Chapter chapter = mChapters.get(i);
			obj.put("name", chapter.cName);
			obj.put("position", chapter.cPosition);
			json.put(obj);
		}
		return json.toString();
	}

	private List<Chapter> buildChapterList(String str) throws Exception{
		if(TextUtils.isEmpty(str)){
			return null;
		}
		List<Chapter> res = new ArrayList<ChapterControll.Chapter>();
		JSONArray json = new JSONArray(str);
		for (int i = 0; i < json.length(); i++) {
			JSONObject obj = json.getJSONObject(i);
			Chapter chapter = new Chapter();
			chapter.cName = obj.getString("name");
			chapter.cPosition = obj.getInt("position"); 
			res.add(chapter);
		}
		return res;
	}
	
	private File getSaveFile(){
		String chapterFileName = mFilePath.hashCode()+"";
		File saveDir = new File(mChapterPath+"chapter/"+chapterFileName);
		return saveDir;
	}
	
	public void save() throws Exception { 
		final File saveDir = getSaveFile();
		if(saveDir.exists() && saveDir.isFile() && saveDir.length() > 4){
			return;
		}
		new Thread(){
			public void run(){ 
				saveDir.getParentFile().mkdirs();  
				try {
					saveFile(saveDir, mChapters);
				} catch (Exception e) {
				}
			}
		}.start();
	}

	private void saveFile(File saveDir, List<Chapter> chapters) throws Exception{  
		saveDir.createNewFile(); // 创建新文件  
        BufferedWriter out = new BufferedWriter(new FileWriter(saveDir));  
        out.write(buildChapterStr()); // \r\n即为换行  
        out.flush(); // 把缓存区内容压入文件  
        out.close(); // 最后记得关闭文件   
	}
	
	public List<Chapter> readFile() throws Exception {
		File saveDir = getSaveFile();
		if (saveDir.exists() && saveDir.isFile() && saveDir.length() > 4) {
			StringBuffer res = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(saveDir));
			String tempString = null; 
			while ((tempString = reader.readLine()) != null) {
				res.append(tempString);
			}
			reader.close();
			return buildChapterList(res.toString());
		}
		return null;
	}
	
}
