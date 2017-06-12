/*
 * ========================================================
 * ClassName:RequestData4Leyue.java* 
 * Description:
 * Copyright (C) 
 * ========================================================
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *===================================================================*
 * Revision History
 *
 * Modification                    Tracking
 * Date         Author          Number       Description of changes
 *____________________________________________________________________
 * 
 * 2013-8-26     chendt          #00000       create
 */
package com.lectek.android.lereader.lib.net.request;

import java.util.HashMap;
/**
 *  HTTP请求信息
 * @author laijp
 * @date 2014-5-26
 * @email 451360508@qq.com
 */
public class RequestData4Leyue{
	
	/**
	 * 扩展文件数据，用于同时提交参数和文件，只支持post请求方式
	 */
	public HashMap<String,String> sendExtraFileData;
	
	// 请求头部信息
	public HashMap<String, String> headMessage = new HashMap<String, String>();
    // 请求
    public String action;
    // 请求方法，post/get/delete
    public String requestMethod;
    // 请求内容，存放于entity中
    public String sendData;
    // 请求名，用于打印信息
    public String actionName;
    //是否上传文件
    public boolean isUpLoadFile;
    // 天翼阅读授权码
    public String accessToken;
}
