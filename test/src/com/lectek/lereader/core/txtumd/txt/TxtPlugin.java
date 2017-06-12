package com.lectek.lereader.core.txtumd.txt;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lectek.lereader.core.txtumd.TxtUmdBasePlugin;
import com.lectek.lereader.core.txtumd.txt.ChapterControll.Chapter;

/**
 * TXT格式书籍解析
 * @author laijp
 * @date 2014-1-14
 * @email 451360508@qq.com
 */
public class TxtPlugin extends TxtUmdBasePlugin{ 
	
	private RandomAccessFile mRandomAccessFile;
	private FileChannel mFileChannel;
	private MappedByteBuffer mMappedByteBuffer = null; 
	private ChapterControll mChapterControll;

	public TxtPlugin(IScreenParam listener) {
		super(listener); 
	}  
	 
	private boolean isInit = false;
	public void openBook(String strFilePath, String chapterPath) throws IOException {
		File book_file = new File(strFilePath);
		mBufferLength = (int) book_file.length();
		mChapterControll= new ChapterControll(strFilePath, chapterPath); 
		mRandomAccessFile = new RandomAccessFile(book_file, "r");
		mFileChannel =  mRandomAccessFile.getChannel();
		mMappedByteBuffer = mFileChannel.map(
				FileChannel.MapMode.READ_ONLY, 0, mBufferLength);   
		byte[] encodings = new byte[400];
		mRandomAccessFile.read(encodings);
	    BytesEncodingDetect be = new BytesEncodingDetect();
	    int position = be.detectEncoding(encodings);
	    position = position==22?6:position;
	    mEncode = BytesEncodingDetect.nicename[position]; 
	    
	    findChapters();
	    isInit = true;
	} 

    @Override
    public boolean isInit(){
        return isInit;
    }
    
    int start = 0;
	private void findChapters(){
	    new Thread(){
	    	public void run(){ 
	    		try {
					List<Chapter> res = mChapterControll.readFile();
					if(res != null && res.size() > 0){
						start = Integer.MAX_VALUE;
						mChapterControll.addChapterList(res);
		    			ArrayList<String> chapters = new ArrayList<String>();
		    			for (int i = 0; i < res.size(); i++) { 
		    				chapters.add(res.get(i).cName);
						}
						mListener.onChapterFounded(chapters);  
						return; 
					}
				} catch (Exception e1) {
					//e1.printStackTrace();
				}
	    	    start = 0;
    			Pattern p = Pattern.compile("(^\\s*第)(.{1,9})[章节卷集部篇回](\\s*)(.*)(\n|\r|\r\n)"); 
    			ArrayList<String> chapters = new ArrayList<String>();
	    	    while (start < mBufferLength) {
	    			byte[] bts = readParagraphForward(start);
	    			String str;
					try {
						str = new String(bts, mEncode);
		    			Matcher matcher = p.matcher(str); 
		    			if (matcher.find()) {
		    				String name = matcher.group(0); 
		    				chapters.add(name);
		    				mChapterControll.addChapter(name, start); 
		    				/*if(chapters.size() == 20){
		    					mListener.onChapterFounded(chapters); 
				    			chapters = new ArrayList<String>();
		    				}*/
		    	        }
					} catch (UnsupportedEncodingException e) {
						return;
					}
	    			start += bts.length;
	    		}
	    	    start = mBufferLength;  
	    	    if(mChapterControll.getChapters().size() == 0){
	    	    	mChapterControll.addChapter(ChapterControll.DEFAULT_CHAPTER, 0);
	    	    	chapters.add(ChapterControll.DEFAULT_CHAPTER);
	    	    } 
				mListener.onChapterFounded(chapters);
	    	}
	    }.start(); 
	} 
	
	@Override
	public ArrayList<String> getChapterList() {
		return null;
	}
 
	@Override
	public String getCurrentChapterName(){ 
		if(mPageEnd > start){//目录未解析完毕
			return ChapterControll.DEFAULT_CHAPTER;
		}
		mChapterControll.setCurrentCHapterIndex(mChapterControll.getChapterIndexByPosition(mPageEnd));
		return mChapterControll.getCurrentChapterName();
	}

	@Override
	public int getCurrentChapterIndex(boolean realChapter){
		int chapterIndex = 0;
		if(realChapter){
			chapterIndex = mChapterControll.getChapterIndexByPosition(mPageEnd);
		}
		mChapterControll.setCurrentCHapterIndex(chapterIndex);
		return chapterIndex;
	}  
	
	/**
	 * 读取某章节某位置 
	 */
	@Override
	public void readChapterData(int chapterIndex, int wordPosition){ 
		if(chapterIndex > 0){
			wordPosition += mChapterControll.getChapterWordIndex(chapterIndex);
		} 
		mChapterControll.setCurrentCHapterIndex(chapterIndex);  
		super.readChapterData(chapterIndex, wordPosition);
	}
	
	@Override
	public int getChapterCount(){
		return super.getChapterCount();
	}  

	@Override
	public byte getContentByte(int position) {
		if(mMappedByteBuffer != null){
			return mMappedByteBuffer.get(position);
		}
		return 0x0a;
	}

	@Override
	public void release(){
		super.release();
		try {
			mFileChannel.close();
			mRandomAccessFile.close(); 
			if(start == mBufferLength){
				mChapterControll.save();
			}
		} catch (Exception e) {
		}
		start = Integer.MAX_VALUE;
		mMappedByteBuffer = null;
        System.gc();
	} 

}
