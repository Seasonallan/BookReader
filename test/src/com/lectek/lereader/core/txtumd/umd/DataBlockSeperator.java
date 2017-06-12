package com.lectek.lereader.core.txtumd.umd;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.zip.Inflater;

import android.util.SparseArray;

/** 
 *  
 * @author laijp
 * @date 2014-1-16
 * @email 451360508@qq.com
 */
public class DataBlockSeperator {
 
	private int contentLength;
	private int[] chapOff; 
	private ArrayList<BlockEntity> contentArr; 
	private String fileName; 
	private int mCurrentChapterIndex; 

	public DataBlockSeperator(int contentLength, int[] chapOff, ArrayList<BlockEntity> contentArr, String fileName) {
		this.fileName = fileName;
		this.contentArr = contentArr; 
		this.chapOff = chapOff; 
		this.contentLength = contentLength;
		this.mByteHashmaps = new SparseArray<SoftReference<byte[]>>();
		this.mByteMaps = new SparseArray<SoftReference<Byte>>();
	}

	public int getCurrentChapterIndex() {
		return mCurrentChapterIndex;
	}
	
	public void setCurrentChapterIndex(int index) {
		mCurrentChapterIndex = index;
	}
	
	public void release(){
		mByteMaps.clear();
		mByteHashmaps.clear();
	}
 
	private byte[] getDataBytesAtPosition(int position) throws Exception{ 
		SoftReference<byte[]> byteRef = mByteHashmaps.get(position); 
		if(byteRef != null && byteRef.get() != null){
			return byteRef.get();
		}else{
			mByteHashmaps.clear();
			InputStream is = new DataInputStream(new FileInputStream(fileName));  
			byte[] bytes = getDataBlockBytes(is, position);
			mByteHashmaps.put(position, new SoftReference<byte[]>(bytes)); 
			is.close();  
			return bytes;
		}  
	}
	
	private SparseArray<SoftReference<Byte>> mByteMaps;
	private SparseArray<SoftReference<byte[]>> mByteHashmaps;
	private int oneBlockSize = 0x8000;
	
	private byte[] currentReadBlock;
	private int currentBlock;
	public byte read(int position) throws Exception{
		int chapterStartIndex = chapOff[(int) mCurrentChapterIndex]; 
		int realPosition = chapterStartIndex + position;
		if(currentReadBlock == null || realPosition/oneBlockSize != currentBlock){
			return getByteAtPosition(position);
		}
		int bytePosition = realPosition % oneBlockSize; 
		return currentReadBlock[bytePosition]; 
	}
	/**
	 * 获取某位置的字节
	 */
	public byte getByteAtPosition(int position) throws Exception{
		int chapterStartIndex = chapOff[(int) mCurrentChapterIndex]; 
		int realPosition = chapterStartIndex + position;
		SoftReference<Byte> bteRef = mByteMaps.get(realPosition); 
		if(bteRef != null && bteRef.get() != null){
			return bteRef.get();
		} 
		if(oneBlockSize <= 0){
			InputStream is = new DataInputStream(new FileInputStream(fileName));  
			oneBlockSize = getDataBytesAtPosition(0).length;
			is.close();
		}
		int blockPosition = realPosition/oneBlockSize;
		int bytePosition = realPosition % oneBlockSize; 

		if(currentReadBlock != null && blockPosition == currentBlock){
			return currentReadBlock[bytePosition]; 
		}
		currentBlock = blockPosition;
		currentReadBlock = getDataBytesAtPosition(currentBlock); 
		byte res = currentReadBlock[bytePosition]; 
		mByteMaps.put(realPosition, new SoftReference<Byte>(res)); 
		return res; 
	}
	  
	/**
	 * 获取某位置的数据块 
	 */
	private byte[] getDataBlockBytes(InputStream is, int blockPosition) throws Exception{ 
		long skipNum = 0;
		long index = contentArr.get(blockPosition).index;
		skipNum = index - (blockPosition>=0?0:(contentArr.get(blockPosition-1).index +(int) contentArr.get(blockPosition-1).length));//nn;
		is.skip(skipNum);
		int length = (int) contentArr.get(blockPosition).length; 
		byte[] bytes = new byte[length];
		is.read(bytes);
		byte[] newBytes = new byte[0x8000];
		Inflater inflater1 = new Inflater();
		inflater1.setInput(bytes);
		inflater1.inflate(newBytes);
		int tempInflateLen = inflater1.getTotalOut();
		byte[] resByte = new byte[tempInflateLen];
		System.arraycopy(newBytes, 0, resByte, 0, tempInflateLen);  
		inflater1.end();
		byte[] res = IntergerUtil.getReverseBytes(resByte); 
		return res;
	}

	/**
	 * 获取某章节的字节数量
	 * @param chapterIndex
	 * @return
	 */
	public int getCurrentChapterLength(int chapterIndex) {
		int chapOffLen = chapOff.length;
		int chapterStartIndex = chapOff[(int) chapterIndex];
		int chapterEndIndex = 0;
		if (chapterIndex < chapOffLen - 1) {
			chapterEndIndex = this.chapOff[(int) chapterIndex + 1];
		} else {
			chapterEndIndex = this.contentLength;
		}  
		return chapterEndIndex - chapterStartIndex;
	}
 
}
