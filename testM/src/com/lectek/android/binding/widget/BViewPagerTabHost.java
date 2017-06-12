package com.lectek.android.binding.widget;

import gueei.binding.IBindableView;
import gueei.binding.ViewAttribute;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.lectek.android.widget.ViewPagerTabHost;

public class BViewPagerTabHost extends ViewPagerTabHost implements
		IBindableView<BViewPagerTabHost>{
	public BViewPagerTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
	}

	/**
	 * 建立绑定关系
	 */
	@Override
	public ViewAttribute<? extends View, ?> createViewAttribute(
			String attributeId) {
		if (attributeId.equals("offscreenPageLimit")) {
			return new OffscreenPageLimit(this);
		} else if (attributeId.equals("pagerTabHostAdapter")) {
			return new PagerTabHostAdapter(this);
		}
		return null;
	}

	/**
	 * 建立offscreenPageLimit的数据通道
	 */
	public static class OffscreenPageLimit extends ViewAttribute<BViewPagerTabHost, Integer>{
		public OffscreenPageLimit(BViewPagerTabHost view){
			super(Integer.class, view, "offscreenPageLimit");
		}
		
		@Override
		public Integer get() {
			return getView().getOffscreenPageLimit();
		}

		@Override
		protected void doSetAttributeValue(Object newValue) {
			if (newValue instanceof Integer) {
				getView().setOffscreenPageLimit((Integer) newValue);
			}
		}
	}
	
	public static class PagerTabHostAdapter extends ViewAttribute<BViewPagerTabHost, AbsPagerTabHostAdapter>{
		public PagerTabHostAdapter(BViewPagerTabHost view){
			super(AbsPagerTabHostAdapter.class, view, "pagerTabHostAdapter");
		}
		
		@Override
		protected void doSetAttributeValue(Object newValue) {
			if (newValue instanceof AbsPagerTabHostAdapter){
				getView().setAdapter((AbsPagerTabHostAdapter) newValue);
			}
		}

		@Override
		public AbsPagerTabHostAdapter get() {
			return getView().getAdapter();
		}
	};
}
