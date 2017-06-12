package com.lectek.android.lereader.utils;

import java.util.LinkedList;

import android.content.Context;
import android.text.style.ClickableSpan;
import android.view.View;

import com.lectek.android.lereader.ui.common.CommWebView;

/**
 * 处理TextView中的链接点击事件
 * 链接的类型包括：url，号码，email，地图
 * 这里只拦截url，即 http:// 开头的URI
 */
class CommURLSpan extends ClickableSpan {  
    private String mUrl;   					// 当前点击的实际链接
    Context context;
    private LinkedList<String> mUrls; // 根据需求，一个TextView中存在多个link的话，这个和我求有关，可已删除掉
    CommURLSpan(String url, LinkedList<String> urls,Context context) {  
    	mUrl = url; 
    	this.context = context;
    	mUrls = urls;
    }  
    @Override  
    public void onClick(View widget) {  
    	CommWebView.openMyWebView(context, mUrl, true, true);
    }  
}
