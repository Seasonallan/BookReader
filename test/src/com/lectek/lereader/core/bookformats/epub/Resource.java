package com.lectek.lereader.core.bookformats.epub;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.lectek.lereader.core.bookformats.util.IOUtil;

/** 保存EPUB的资源
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-21
 */
public class Resource implements Serializable {
	protected String href;
	protected String filePath;
	private static final long serialVersionUID = 2660177891925940292L;
	
	public Resource(String filePath, String href){
		this.filePath = filePath;
		this.href = href;
	}

	public byte[] getData() throws IOException{
		byte[] data = null;
		InputStream inputStream = getDataStream();
		if(inputStream != null){
			data = IOUtil.toByteArray(inputStream);
			inputStream.close();
		}
		return data;
	}

	public InputStream getDataStream() throws IOException{
		ZipFile zipFile = new ZipFile(filePath);
		ZipEntry zipEntry = null;
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		if(enumeration != null){
			for (;enumeration.hasMoreElements();) {
				zipEntry = enumeration.nextElement();
				String name = zipEntry.getName();
				if(zipEntry.isDirectory()){
					continue;
				}
				if(name.endsWith(href)){
					return new InteriorInputStream(zipFile.getInputStream(zipEntry), zipFile);
				}
			}
		}
		return null;
	}
	
	private class InteriorInputStream extends InputStream{
		private InputStream mInputStream;
		private ZipFile mZipFile;
		private InteriorInputStream(InputStream inputStream,ZipFile zipFile){
			mInputStream = inputStream;
			mZipFile = zipFile;
		}
		
		@Override
		public int read() throws IOException {
			return mInputStream.read();
		}

		@Override
		public int available() throws IOException {
			return mInputStream.available();
		}

		@Override
		public void close() throws IOException {
			mInputStream.close();
			mZipFile.close();
		}

		@Override
		public void mark(int readlimit) {
			mInputStream.mark(readlimit);
		}

		@Override
		public boolean markSupported() {
			return mInputStream.markSupported();
		}

		@Override
		public int read(byte[] b) throws IOException {
			return mInputStream.read(b);
		}

		@Override
		public int read(byte[] b, int offset, int length) throws IOException {
			return mInputStream.read(b, offset, length);
		}

		@Override
		public synchronized void reset() throws IOException {
			mInputStream.reset();
		}

		@Override
		public long skip(long byteCount) throws IOException {
			return mInputStream.skip(byteCount);
		}
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
}
