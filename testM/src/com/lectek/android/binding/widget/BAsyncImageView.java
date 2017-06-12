package com.lectek.android.binding.widget;

import gueei.binding.IBindableView;
import gueei.binding.ViewAttribute;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.lectek.android.widget.AsyncImageView;

public class BAsyncImageView extends AsyncImageView implements IBindableView<BViewPagerTabHost>{
	public BAsyncImageView(Context context) {
		super(context);
	}

	public BAsyncImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public ViewAttribute<? extends View, ?> createViewAttribute(
			String attributeId) {
		if (attributeId.equals("defaultImgRes")) {
			return new DefaultImgRes(this);
		} 
		else if (attributeId.equals("imageUrl")) {
			return new ImageUrl(this);
		}else if (attributeId.equals("cornerDegree")) {
			return new CornerDegree(this);
		}
		return null;
	}

	/**
	 * 建立offscreenPageLimit的数据通道
	 */
	public static class DefaultImgRes extends ViewAttribute<BAsyncImageView, Integer>{
		
		public DefaultImgRes(BAsyncImageView view){
			super(Integer.class, view, "defaultImgRes");
		}
		
		@Override
		public Integer get() {
			return null;
		}

		@Override
		protected void doSetAttributeValue(Object newValue) {
			if(newValue == null){
				getView().setDefaultImgRes(0);
			}
			if (newValue instanceof Integer) {
					getView().setDefaultImgRes((Integer) newValue);
			}
		}
	}
	
	/**
	 * 建立offscreenPageLimit的数据通道
	 */
	public static class ImageUrl extends ViewAttribute<BAsyncImageView, String>{
		
		public ImageUrl(BAsyncImageView view){
			super(String.class, view, "imageUrl");
		}
		
		public ImageUrl(BAsyncImageView view, boolean aIsCircle){
			super(String.class, view, "imageUrl");
		}
		
		public ImageUrl(BAsyncImageView view, boolean aIsCircle, float aCorner){
			super(String.class, view, "imageUrl");
		}
		
		
		@Override
		public String get() {
			return null;
		}

		@Override
		protected void doSetAttributeValue(Object newValue) {
			if (newValue == null) {
				getView().setImageUrl(null);
			}else {
				getView().setImageUrl((String)newValue);
			}
		}
	}
	
	/**
	 * 设置圆角属性的数据通道
	 */
	public static class CornerDegree extends ViewAttribute<BAsyncImageView, Object>{

		public CornerDegree(BAsyncImageView view){
			super(Object.class, view, "cornerDegree");
		}
		
		@Override
		protected void doSetAttributeValue(Object arg0) {
			if(arg0 == null) {
				getView().setFilletedCornerDegree(0);
			}else if(arg0 instanceof Integer){
				getView().setFilletedCornerDegree(((Integer)arg0).intValue());
			}else if(arg0 instanceof String) {
				String res = (String)arg0;
				if(res.startsWith("@integer")) {
					getView().getResources().getIdentifier(res.substring(1), "integer", getView().getContext().getPackageName());
				}
			}
		}

		@Override
		public Object get() {
			return null;
		}
		
	}
}
