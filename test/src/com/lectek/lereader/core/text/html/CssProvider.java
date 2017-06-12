package com.lectek.lereader.core.text.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.text.TextUtils;

import com.lectek.lereader.core.text.LinkedList;
import com.lectek.lereader.core.text.html.HtmlParser.TagInfo;
import com.lectek.lereader.core.text.html.css.CSSParser;
import com.lectek.lereader.core.text.html.css.PropertyValue;
import com.lectek.lereader.core.text.html.css.Rule;
import com.lectek.lereader.core.text.html.css.Selector;

public class CssProvider implements ICssProvider {
	private static final String TAG = CssProvider.class.getSimpleName();
//	private List<Rule> mRules;
	private ICssLoader mICssLoader;
	
	private HashMap<String, Selector> mAllSimpleSelectors = new HashMap<String, Selector>();
	private ArrayList<Selector> mAllComplexSelectors = new ArrayList<Selector>();
	
	public CssProvider(ICssLoader cssLoader){
//		mRules = new ArrayList<Rule>();
		mICssLoader = cssLoader;
	} 
	
	@Override
	public void parse(ArrayList<String> paths) {
		if(mICssLoader == null || paths == null || paths.isEmpty()){
			return;
		}
//		mRules.clear();
		mAllSimpleSelectors.clear();
		mAllComplexSelectors.clear();
		try {
			for(String path : paths){
				String source = mICssLoader.load(path);
				List<Rule> rules = CSSParser.parse(source);
//				if(rules != null && !rules.isEmpty()){
//					mRules.addAll(rules);
//				}
				
				for (Rule rule : rules) {
					List<Selector> selectors = rule.getSelectors();
					for (Selector selector : selectors) {
						if(selector.isComplex()) {
							mAllComplexSelectors.add(selector);
						}else {
							mAllSimpleSelectors.put(selector.toString(), selector);
						}
					}
					
//					mRules.add(rule);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class SortByWeight implements Comparator<Selector> {

		@Override
		public int compare(Selector object1, Selector object2) {
			return (object1 != null && object2 != null) ? (object1.getWeight() - object2.getWeight()) : 0;
		}
		
	}
	
//	private boolean Debug = true;
//	private boolean rebuild = true;
//	private static long totalTime = 0;
//	private static int count = 0;
	
	@Override
	public List<PropertyValue> getClassInfo(List<TagInfo> tagInfos) {
//		CssProvider.count++;
//		
//		long startTime = System.currentTimeMillis();
		
		LinkedList<PropertyValue> propertyValues = new LinkedList<PropertyValue>();
//		if(rebuild && tagInfos != null && !tagInfos.isEmpty()) {
		if(tagInfos != null && !tagInfos.isEmpty()) {
			
			ArrayList<Selector> relateSelectors = new ArrayList<Selector>();
			
			//添加叶子选择器
			TagInfo leaf = tagInfos.get(tagInfos.size() - 1);
			
			Selector simpleSelector = mAllSimpleSelectors.get("." + leaf.getClazz());
			if(simpleSelector != null) {
				relateSelectors.add(simpleSelector);
			}
			
			simpleSelector = mAllSimpleSelectors.get("#" + leaf.getId());
			if(simpleSelector != null) {
				relateSelectors.add(simpleSelector);
			}
			
			if(!TextUtils.isEmpty(leaf.getTag())) {
				simpleSelector = mAllSimpleSelectors.get(leaf.getTag().trim().toUpperCase());
				if(simpleSelector != null) {
					relateSelectors.add(simpleSelector);
				}
			}
			
			//查找复合选择器
			for (Selector item : mAllComplexSelectors) {
				Selector theSelector = null;
				
				if(item.isTheSelector(tagInfos) && (theSelector == null || item.getWeight() >= theSelector.getWeight())) {
					theSelector = item;
				}
				
				if(theSelector != null) {
					relateSelectors.add(theSelector);
				}
			}
			
			//根据Selector的weight进行从低到高的排序
			Collections.sort(relateSelectors, new SortByWeight());
	
			//获取属性
			for (Selector selector : relateSelectors) {
				propertyValues.addAll(selector.getRule().getPropertyValues());
			}
		}	
//		}else {
//		int maxLevel = 0;
//		Rule rule;
//		for(int location = 0;location < mRules.size();location++){
////			LogUtil.v(TAG, "扫描选择器 location="+location +" 匹配标签："+tagInfos.get(tagInfos.size() - 1).getTag());
//			rule = mRules.get(location);
//			for(Selector selector : rule.getSelectors()){
//				ArrayList<SelectorItem> selectorItems = selector.getSelectorItems();
//				if(selectorItems != null){
////					LogUtil.v(TAG, "选择器: size="+selectorItems.size() +" 内容="+selectorItems);
//					SelectorItem selectorItem = null;
//					int i = selectorItems.size() - 1;
//					int level = 0;
//					for(int j = 1;i >= 0 && tagInfos.size() - j >= 0;i--){
//						selectorItem = selectorItems.get(i);
//						if(i == selectorItems.size() - 1){
//							if((level = matching(tagInfos.get(tagInfos.size() - j),selectorItem)) <= 0){
//								break;
//							}else{
////								LogUtil.v(TAG, "匹配当前标签 level="+level+" selectorItem="+selectorItem);
//							}
//						}else{
//							int parentLevel = 0;
//							for(j++;tagInfos.size() - j >= 0;j++){
//								parentLevel = matching(tagInfos.get(tagInfos.size() - j),selectorItem);
//								if(parentLevel > 0){
//									level += parentLevel;
//									break;
//								}
//							}
//							if(parentLevel <= 0){
//								level = 0;
//								break;
//							}else{
////								LogUtil.v(TAG, "匹配父标签 parentLevel="+parentLevel +" level="+level+" 父标签匹配位置"+(tagInfos.size() - j));
//							}
//						}
//					}
////					LogUtil.v(TAG, "当前选择器 location="+location+" 匹配的最终结果 level"+level);
//					if(level > 0){
//						//XXX 优先级排序
//						int index = 0;
//						if(maxLevel < level){
//							maxLevel = level;
//							index = propertyValues.isEmpty() ? 0 : propertyValues.size() - 1;
//						}
//						propertyValues.addAll(index, mRules.get(location).getPropertyValues());
//					}
//				}
//			}
//		}
//		}
		
//		if(Debug) {
//			long used = System.currentTimeMillis() - startTime;
//			LogUtil.e(TAG, String.format("tag time:%d total:%d count:%d", used, totalTime += used, CssProvider.count));
//		}
		
		return propertyValues;
	}
	
//	private int matching(TagInfo tagInfo,SelectorItem selectorItem){
//		int level = 0;
//		try {
//			String [] keys = new String[]{
//				tagInfo.getTag(),
//				"."+tagInfo.getClazz(),
//				"#"+tagInfo.getId(),
//			};
//			boolean isMatching;
//			for (String targetKey : selectorItem.getData()) {
//				isMatching = false;
//				String key = null;
//				for (int i = 0;i < keys.length;i++) {
//					key = keys[i];
//					if(i == 0 ? targetKey.equalsIgnoreCase(key) : targetKey.equals(key)){
//						isMatching = true;
//					}
//					if(isMatching){
//						switch (i) {
//						case 0:
//							level += 1;
//							break;
//						case 1:
//							level += 10;
//							break;
//						case 2:
//							level += 100;
//							break;
//						}
//						break;
//					}
//				}
//				if(!isMatching){
//					level = 0;
//					break;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return level;
//	}
	
	public interface ICssLoader{
		public String load(String path);
	}
}
