package com.lectek.lereader.core.bookformats.epub.paser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** 解析container.xml
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-21
 */
public class ContainerHandler extends DefaultHandler {
	
	private String opfFilePath;

	/**
	 * @return the opfFilePath
	 */
	public String getOpfFilePath() {
		return opfFilePath;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equalsIgnoreCase("rootfile")){
			opfFilePath = attributes.getValue("full-path");
		}
	}

}
