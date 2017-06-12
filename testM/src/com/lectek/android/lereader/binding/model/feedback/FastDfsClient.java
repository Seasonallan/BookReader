package com.lectek.android.lereader.binding.model.feedback;

import android.util.Log;

import com.lectek.android.lereader.lib.utils.Base64Util;
import com.lectek.android.lereader.utils.Constants;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjx
 *
 */
public class FastDfsClient {
	
	public FastDfsClient() {
	}
	
	public String upload(String path) throws ConnectTimeoutException, Exception {
		Log.d(getClass().getSimpleName(),"upload:" + path);
		
		// 返回保存文件ID
		String fileId = "";
		// 获取文件类型
		String fileExtName = "";
		if (path.contains(".")) {
			fileExtName = path.substring(path.lastIndexOf(".") + 1);
		}
		// 设置元信息
		NameValuePair[] metaList = new NameValuePair[2];
		metaList[0] = new NameValuePair("fileName", path);
		metaList[1] = new NameValuePair("fileExtName", fileExtName);

		try {
			// 建立连接
			String configPath  = Constants.BOOKS_TEMP_FEEDBACK_IMAGE+"fdfs_client.conf";
			ClientGlobal.init(configPath);
			TrackerClient tracker = new TrackerClient();
			TrackerServer trackerServer = tracker.getConnection();
			StorageServer storageServer = null;
			StorageClient1 client = new StorageClient1(trackerServer, storageServer);
			fileId = client.upload_file1(path, fileExtName, metaList);
			trackerServer.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return fileId;
	}
	
	private List<BasicNameValuePair> getDefaultHeaderList() {
		List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
		list.add(new BasicNameValuePair("Authorization", getAuthorization()));
		list.add(new BasicNameValuePair("version","1"));// 修改为现有版本
		list.add(new BasicNameValuePair("appId","9"));
		return list;
	}
	
	private String getAuthorization() {
		// 改为现有账号
		return "Basic " + new String(Base64Util.encode("guozb@lectek.com" + ":"
                + 123456));
	}
}
