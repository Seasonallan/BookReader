package com.lectek.android.update;

import android.text.TextUtils;

/**
 * 客户端信息
 * 
 * @author mingkg21
 * @date 2010-4-16
 * @email mingkg21@gmail.com
 */
public final class ClientInfo {

	/** 客户端升级版本号 */
	public String updateVersion;
	/** 客户端升级地址 */
	public String updateURL;
	/** 是否强制升级 */
	public boolean mustUpdate;
	/** 客户端升级版本信息 */
	public String updateMessage = "暂无";
	/** 客户端安装包完整性校验码 */
	public String MD5Code;

	public boolean silenceUpdate;

	public int updateSize;

	public String getUpdateMessage() {
		if (!TextUtils.isEmpty(updateMessage)) {
			StringBuilder sb = new StringBuilder();
			String[] temps = updateMessage.split("\\\\n");
			int len = temps.length;
			for (int i = 0; i < len; ++i) {
				sb.append(temps[i].trim());
				if (i != (len - 1)) {
					sb.append("\n");
				}
			}
			return sb.toString();
		}
		return updateMessage;
	}

}
