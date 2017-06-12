package com.lectek.lereader.core.bookformats.epub.paser;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;

import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.text.LinkedList;

public class NCXHandler extends DefaultHandler{
	private static final String TAG_NAV_MAP = "navMap";
	private static final String TAG_NAV_POINT = "navPoint";
	private static final String TAG_NAV_LABEL = "navLabel";
	private static final String TAG_TEXT = "text";
	private static final String TAG_CONTENT = "content";
	
	private ArrayList<Catalog> catalogs;
	private LinkedList<Catalog> catalogStack;
	private LinkedList<String> tagStack;
	private HashMap<String,Catalog> catalogHrefMap;

	public NCXHandler(){
		catalogStack = new LinkedList<Catalog>();
		tagStack = new LinkedList<String>();
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
		if (qName.equalsIgnoreCase(TAG_NAV_MAP)){
			catalogs = new ArrayList<Catalog>();
			catalogHrefMap = new HashMap<String, Catalog>();
		}else if(qName.equalsIgnoreCase(TAG_NAV_POINT)){
			Catalog parent = catalogStack.peekLast();
			String order = attributes.getValue("playOrder");
			int index = catalogs.size();
			if(!TextUtils.isEmpty(order)){
				index = Integer.valueOf(attributes.getValue("playOrder"));
			}
			Catalog catalog = new Catalog(parent,index);
			catalogs.add(catalog);
			catalogStack.addLast(catalog);
		}else if(qName.equalsIgnoreCase(TAG_NAV_LABEL)){
			Catalog catalog = catalogStack.peekLast();
			if(catalog != null){
				catalog.setHasLabel(true);
			}
		}else if(qName.equalsIgnoreCase(TAG_CONTENT)){
			Catalog catalog = catalogStack.peekLast();
			String href = attributes.getValue("src");
			if(catalog != null && !TextUtils.isEmpty(href)){
				catalog.setHref(href);
				catalogHrefMap.put(href, catalog);
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		tagStack.pollLast();
		if(qName.equalsIgnoreCase(TAG_NAV_POINT)){
			catalogStack.pollLast();
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		String qName = tagStack.getLast();
		if(TAG_TEXT.equalsIgnoreCase(qName)){
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
