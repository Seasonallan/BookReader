package com.lectek.android.lereader.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.lectek.android.widget.ViewPagerTabHost;


/** 
 *  
 * @author laijp
 * @date 2014-7-2
 * @email 451360508@qq.com
 */
public class GapRelativeLayout extends RelativeLayout implements ViewPagerTabHost.ViewPagerChild {


    public GapRelativeLayout(Context context) {
        super(context);
    }

    public GapRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GapRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canScroll(ViewPager viewPager, int dx, int x, int y) {
        return true;
    }
}
