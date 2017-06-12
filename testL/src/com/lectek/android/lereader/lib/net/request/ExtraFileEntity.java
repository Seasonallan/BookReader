package com.lectek.android.lereader.lib.net.request;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.entity.AbstractHttpEntity;

import android.text.TextUtils;

public class ExtraFileEntity extends AbstractHttpEntity implements Cloneable{
	private HashMap<String, File> mFileMap;
	private HashMap<String, String> mParamMap;
	private String end = "\r\n";
	private String twoHyphens = "--";
	private String boundary = "---------------------------7d4a6d158c9";
	private String newName = "image.jpg";
	
	public ExtraFileEntity(HashMap<String, String> extraFileData) throws UnsupportedEncodingException{
		if (extraFileData == null) {
            throw new IllegalArgumentException("requestData may not be null");
        }
		mFileMap = new HashMap<String, File>();
		mParamMap = new HashMap<String, String>();
		boolean isFile = false;
		for(Entry<String, String> entry: extraFileData.entrySet()){
			isFile = false;
			String dataString = entry.getValue() ;
			if(!TextUtils.isEmpty(dataString)){
				File file = new File(dataString);
				if(file.exists() && file.isFile()){
					mFileMap.put(entry.getKey(), file);
					isFile = true;
				}
				if(!isFile){
					mParamMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		extraFileData.clear();
		setContentType("multipart/form-data;charset=UTF-8;boundary="+boundary);
	}
	
	@Override
	public boolean isRepeatable() {
		return true;
	}

	@Override
	public long getContentLength() {
		return -1;
	}

	@Override
	public InputStream getContent() throws IOException,
			IllegalStateException {
		return null;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
		
		DataOutputStream ds = new DataOutputStream(outstream);
		StringBuffer params = new StringBuffer();
		// 上传的表单参数部分，格式请参考文章
		for (Entry<String, String> entry : mParamMap.entrySet()) {// 构建表单字段内容
			params.append("--");
			params.append(boundary);
			params.append("\r\n");
			params.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"\r\n\r\n");
			params.append(entry.getValue());
			params.append("\r\n");
		}
		params.append("--");      
		params.append(boundary);      
		params.append("\r\n");
		ds.write(params.toString().getBytes());
		ds.writeBytes(twoHyphens + boundary + end);
		
		for(Entry<String, File> entry: mFileMap.entrySet()){
			writeFile(ds,entry.getKey(),entry.getValue());
		}
		ds.flush();
	}
	
	private void writeFile(DataOutputStream outstream,String fileKey,File file) throws IOException{
		
		outstream.writeBytes("Content-Disposition: form-data; "
				+ "name=\"" + fileKey + "\";filename=\"" + newName + "\"" + end);
//		outstream.writeBytes("Content-Disposition: form-data;"
//				+ "name=\"" + fileKey + "\";" + end);
		outstream.writeBytes(end);
		/* 取得文件的FileInputStream */
		FileInputStream fStream = new FileInputStream(file);
		/* 设置每次写入1024bytes */
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		int length = -1;
		/* 从文件读取数据至缓冲区 */
		while ((length = fStream.read(buffer)) != -1) {
			/* 将资料写入DataOutputStream中 */
			outstream.write(buffer, 0, length);
		}
		outstream.writeBytes(end);
		outstream.writeBytes(twoHyphens + boundary + twoHyphens + end);
		/* close streams */
		fStream.close();
	}
	
	@Override
	public boolean isStreaming() {
		return false;
	}
	
}
