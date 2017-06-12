package com.lectek.lereader.core.bookformats.epub.paser;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;

import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.text.LinkedList;
import com.lectek.lereader.core.text.Util;

public class NAVHandler extends DefaultHandler{
	private static final String TAG_NAV = "nav";
	private static final String TAG_LI = "li";
	private static final String TAG_A = "a";
	
	private ArrayList<Catalog> catalogs;
	private LinkedList<Catalog> catalogStack;
	private LinkedList<String> tagStack;
	private HashMap<String,Catalog> catalogHrefMap;
	private String[] fileDirs;
	
	public NAVHandler(String filePath){
		catalogStack = new LinkedList<Catalog>();
		tagStack = new LinkedList<String>();
		fileDirs = filePath.split("/");
	}
	/**
	 * @return the catalogs
	 */
	public ArrayList<Catalog> getCatalogs() {
		return catalogs;
	}

	/**
	 * @return the catalogHrefMap
	 */
	public HashMap<String, Catalog> getCatalogHrefMap() {
		return catalogHrefMap;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		tagStack.addLast(qName);
		if (qName.equalsIgnoreCase(TAG_NAV)){
			catalogs = new ArrayList<Catalog>();
			catalogHrefMap = new HashMap<String, Catalog>();
		}else if(qName.equalsIgnoreCase(TAG_LI)){
			if(catalogs != null){
				Catalog parent = catalogStack.peekLast();
				int index = catalogs.size();
				Catalog catalog = new Catalog(parent,index);
				catalogs.add(catalog);
				catalogStack.addLast(catalog);
			}
		}else if(qName.equalsIgnoreCase(TAG_A)){
			if(catalogs != null){
				Catalog catalog = catalogStack.peekLast();
				if(catalog != null){
					catalog.setHasLabel(true);
					Integer bgColor = null;
					Integer textColor = null;
					ArrayList<String[]> css = Util.handlerInlineCss(attributes);
					if(css != null){
						for (String[] values : css) {
							if("background".equalsIgnoreCase(values[0])){
								bgColor = Util.parseHtmlColor(values[1]);
							}else if("color".equalsIgnoreCase(values[0])){
								textColor = Util.parseHtmlColor(values[1]);
							}
						}
					}
					catalog.setBgColor(bgColor);
					catalog.setTextColor(textColor);
					String href = attributes.getValue("href");
					int dirLayers = 0;
					int start = 0;
					for (;start < href.length();) {
						start = href.indexOf("..", start);
						if(start == -1){
							break;
						}else{
							dirLayers++;
							start += 3;
						}
					}
					for (int i = 0; i < dirLayers; i++) {
						String replace = "";
						int index = fileDirs.length - 2 - (dirLayers - i);
						if(index > 0 && index < fileDirs.length){
							replace = fileDirs[index] + "/";
						}
						href = href.replaceFirst("../",replace);
					}
					catalog.setHref(href);
					catalogHrefMap.put(href, catalog);
				}
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		tagStack.pollLast();
		if(qName.equalsIgnoreCase(TAG_LI)){
			catalogStack.pollLast();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		String qName = tagStack.getLast();
		if(TAG_A.equalsIgnoreCase(qName)){
			Catalog catalog = catalogStack.peekLast();
			if(catalog != null && catalog.hasLabel() && length > 0){
				String content = new String(ch, start, length);
				String contentOdl = catalog.getText();
				if(!TextUtils.isEmpty(contentOdl)){
					catalog.setText(contentOdl + content);
				}else{
					catalog.setText(content);
				}
			}
		}
	}
}
