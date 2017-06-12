/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 */

package com.lectek.lereader.core.text.html.css;

import java.util.List;

import com.lectek.lereader.core.text.html.HtmlParser.TagInfo;

import android.text.TextUtils;


/**
 * Represents a CSS selector.
 * 
 * @author <a href="mailto:christoffer@christoffer.me">Christoffer Pettersson</a>
 */

public final class Selector {
	private String name;
//	private ArrayList<SelectorItem> mSelectorItems = new ArrayList<Selector.SelectorItem>();
	private Rule mRule;
	
	/**
	 * Creates a new selector.
	 * 
	 * @param name Selector name.
	 */

	public Selector(String name, Rule rule) {
		if(name.charAt(0) == 65279){
			name = name.substring(1, name.length());
		}
		this.name = name;
		this.mRule = rule;
		
//		mSelectorItems.clear();
//		String[] datas = name.split(" ");
//		if(datas != null){
//			for (String string : datas) {
//				mSelectorItems.add(new SelectorItem(string));
//			}
//		}
	}
	
//	public ArrayList<SelectorItem> getSelectorItems(){
//		return mSelectorItems;
//	}
	
	public Rule getRule() {
		return mRule;
	}
	
	/**
	 * 就算selector的权重.#id权重100,.class权重10,tag权重1
	 * @param selector
	 * @return
	 */
	public int getWeight() {
		int result = 0;
		
		if(!TextUtils.isEmpty(name)) {
			result = 1 + countRepeat(name, "#") * 100 + countRepeat(name, ".") * 10; 
		}
		
		return result;
	}
	
	/**
	 * 计算str在src中出现的次数
	 * @param src
	 * @param str
	 * @return
	 */
	private int countRepeat(String src, String str) {
		int result = 0;
		int index = src.indexOf(str);
		if (index >= 0) {  
			result = countRepeat(src.substring(index + str.length()), str) + 1;  
		}
		return result;
	}
	
	/**
	 * 是否是tag路径的css
	 * @param tagPath
	 * @return
	 */
	public boolean isTheSelector(List<TagInfo> tagPath) {
		boolean result = false;
		if(tagPath != null && !tagPath.isEmpty()) {
			TagInfo tag = tagPath.get(tagPath.size() - 1);
			if(!TextUtils.isEmpty(name)) {
				String[] items = name.split(" ");
				int index = items.length - 1;
				
				result = isTheCSS(items[index], tag);
				
				if(index > 0 && result) {//如果是复合选择器，检查父节点的css是否存在于选择器中
					int tagIndex = tagPath.size() - 2;
					for(index--; index >= 0; index--) {
						boolean match = false;
						while(!match && tagIndex >= 0) {
							tag = tagPath.get(tagIndex);
							match = isTheCSS(items[index], tag);
						
							tagIndex--;
						}
						
						if(tagIndex < 0) {
							break;
						}
					}
					
					result = index < 0;
				}
			}
		}
		return result;
	}
	
	private boolean isTheCSS(String css, TagInfo tag) {
		if(css.contains(".")) {
			return css.trim().equals("." + tag.getClazz());
		}else if(css.contains("#")) {
			return css.trim().equals("#" + tag.getId());
		}else {
			return (tag.getTag() != null && css.equalsIgnoreCase(tag.getTag()));
		}
	}
	
	public boolean isComplex() {
		return (!TextUtils.isEmpty(name) && name.trim().contains(" "));
	}
	
	@Override
	public String toString() {
		String[] items = (name + "").split(" ");
		StringBuilder sb = new StringBuilder();
		final int count = items.length;
		for (int i = 0; i < count; i++) {
			if(!items[i].contains(".") && !items[i].contains("#")) {
				items[i] = items[i].trim().toUpperCase();
			}
			
			sb.append(items[i]).append(" ");
		}
		
		return sb.toString().trim();
//		return name + "";
	}

	@Override
	public boolean equals(final Object object) {

		if (object instanceof Selector) {

			Selector target = (Selector) object;

			return target.name.equalsIgnoreCase(name);

		}

		return false;

	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
//	public class SelectorItem{
//		private ArrayList<String> mDatas = new ArrayList<String>();
//		private SelectorItem(String source){
//			source = source.replace(".", " .");
//			source = source.replace("#", " #");
//			addAll(source.split(" "));
//		}
//		
//		private void addAll(String [] datas){
//			for(String str : datas){
//				if(!TextUtils.isEmpty(str)){
//					mDatas.add(str);
//				}
//			}
//		}
//		
//		public ArrayList<String> getData(){
//			return mDatas;
//		}
//		
//		public boolean contains(String value){
//			return mDatas.contains(value);
//		}
//		
//		@Override
//		public String toString() {
//			return mDatas.toString();
//		}
//	}
}
