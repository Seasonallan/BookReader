package com.lectek.lereader.core.text.html;

import java.util.ArrayList;
import java.util.List;

import com.lectek.lereader.core.text.html.HtmlParser.TagInfo;
import com.lectek.lereader.core.text.html.css.PropertyValue;
/**
 * Css提供者，负责获取解析CSS
 * @author lyw
 *
 */
public interface ICssProvider {
	/**
	 * 执行解析Css
	 */
	public void parse(ArrayList<String> paths);
	/**
	 * 获取Css样式内容
	 * @param classTag
	 * @return 
	 */
	public List<PropertyValue> getClassInfo(List<TagInfo> tagInfos);
}
