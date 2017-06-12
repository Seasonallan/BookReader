package com.lectek.android.lereader.lib.cache.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * 软引用列表
 * @author chends@lectek.com
 * @param <T>
 */
public class SoftReferenceQueue<T> {

	private ReferenceQueue<T> mGCQueue = new ReferenceQueue<T>();
	private Hashtable<Integer, SoftReference<T>> mRefQueue = new Hashtable<Integer, SoftReference<T>>();
	
	/**
	 * 增加对象的软引用
	 * @param object
	 */
	public void addReference(T object) {
		clearGCItem();
		if(!mRefQueue.contains(object.hashCode())) {
			mRefQueue.put(new Integer(object.hashCode()), new SoftReference<T>(object, mGCQueue));
		}
	}
	
	/**
	 * 移除对象的软引用
	 * @param object
	 */
	public void removeReference(T object) {
		SoftReference<T> soft = mRefQueue.remove(object.hashCode());
		if(soft != null) {
			soft.clear();
		}
	}
	
	private void clearGCItem() {
		T item = null;
		while ((item = (T)mGCQueue.poll()) != null) {       
			mRefQueue.remove(item.hashCode());
	    } 
	}
	
	/**
	 * 获取软引用列表
	 * @return
	 */
	public Iterator<SoftReference<T>> getSoftReferencesIterator() {
		return mRefQueue.values().iterator();
	}
}
