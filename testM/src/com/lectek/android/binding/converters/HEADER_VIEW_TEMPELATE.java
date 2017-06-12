/*
 * ========================================================
 * ClassName:HEADER_VIEW_TEMPELATE.java* 
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
 * 2013-9-16     chendt          #00000       create
 */
package com.lectek.android.binding.converters;

import gueei.binding.Binder.InflateResult;
import gueei.binding.Converter;
import gueei.binding.IObservable;
import gueei.binding.viewAttributes.templates.Layout;

/**
 * For Header View Layout Template. 
 * @说明 用于ListView 中指定第一项为HeaderView布局。
 * （由于Android_Binding对于ListView
 *  HeaderView的支持缺失，为避免AB繁复的接口调用方式，采用第一项作为FirstItem的解决方案） <br/>
 * You can supply only two templates to it, and it will output them by specified rule, according to position <br/>
 * For example, you provide 2 templates to it, binding:itemTemplate="ALT(A,B)" then <br/>
 * the first item in ListView will have A template, and remaining will have B.
 * 
 * @usage layout layout ...
 *  
 * @arg layout gueei.binding.viewAttributes.templates.Layout Note layout can be supplied in @package:layout/id format
 * 
 * @return gueei.binding.viewAttributes.templates.Layout
 * 
 * @author andy
 *
 */
public class HEADER_VIEW_TEMPELATE extends Converter<Layout> {
	public HEADER_VIEW_TEMPELATE(IObservable<?>[] dependents) {
		super(Layout.class, dependents);
	}

	@Override
	public Layout calculateValue(Object... args) throws Exception {
		Layout[] ids = new Layout[args.length];
		for (int i=0; i<args.length; i++){
			ids[i] = ((Layout)args[i]);
		}
		return new Header_Layout(ids);
	}
	
	private static class Header_Layout extends Layout{
		private Layout[] mLayouts;
		
		@Override
		public void onAfterInflate(InflateResult result, int pos) {
			int idx = pos % mLayouts.length;
			mLayouts[idx].onAfterInflate(result, pos);
		}

		public Header_Layout(Layout[] layouts) {
			super(layouts[0].getDefaultLayoutId());
			mLayouts = layouts;
		}

		@Override
		public int getLayoutTypeId(int pos) {
			if(pos == 0){
				return 0;
			}else
				return 1;
		}

		@Override
		public int getLayoutId(int pos) {
			if(pos == 0) return mLayouts[0].getDefaultLayoutId();
			else return mLayouts[1].getDefaultLayoutId();
		}

		@Override
		public int getTemplateCount() {
			return mLayouts.length;
		}
	}
}

