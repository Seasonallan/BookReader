package com.lectek.lereader.core.bookformats.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import com.lectek.lereader.core.util.LogUtil;

/** XML辅助工具类
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-21
 */
public final class XMLUtil {
	
	/**
	 * SAX解析XML
	 * 
	 * @param handler
	 * @param content
	 */
	public static boolean parserXml(DefaultHandler handler, byte[] content) {
		if(handler == null || content == null){
			return false;
		}
		try {
			InputStream is = new ByteArrayInputStream(content);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(is, handler);
			is.close();
			return true;
		} catch (Exception e) {
			LogUtil.e("XMLUtil", e);
		}
		return false;
	}

}
