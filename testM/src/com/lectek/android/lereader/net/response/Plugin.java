package com.lectek.android.lereader.net.response;

import com.lectek.android.lereader.lib.storage.dbase.BaseDao;
import com.lectek.android.lereader.lib.storage.dbase.iterface.Json;

/**
 *  插件
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class Plugin extends BaseDao{

	@Json(name = "pluginId")
	public int pluginId;

	@Json(name = "clientName")
	public String clientName;
	
	@Json(name = "clientVersion")
	public String clientVersion;
	
	@Json(name = "pluginName")
	public String pluginName;
	
	@Json(name = "pluginVersion")
	public String pluginVersion;
	
	@Json(name = "pluginPackName")
	public String pluginPackName;
	
	@Json(name = "pluginDesc")
	public String pluginDesc;
	
	@Json(name = "pluginIconUrl")
	public String pluginIconUrl;
	
	@Json(name = "pluginFileUrl")
	public String pluginFileUrl;
	
	@Json(name = "recordTime")
	public String recordTime;

}
