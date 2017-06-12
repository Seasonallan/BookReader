package com.lectek.android.lereader.animation;

import android.view.View;
import android.view.animation.Animation;
import com.lectek.android.lereader.animation.factory.SimpleAnimationListener;
import com.lectek.android.lereader.animation.factory.SimpleAnimations;

/**
 * 控制View的显示和隐藏动画[淡入淡出]
 * @author Administrator
 *
 */
public class ViewAnimDecorator{

    /**
     * 显示视图
     * @param view
     * @param isStartAnim
     */
	public static void showView(View view,boolean isStartAnim){
		if(view == null){
			return;
		}
        view.setVisibility(View.VISIBLE);
		if(isStartAnim){
            Animation animation = SimpleAnimations.newAlphaAnimation(true);
            view.startAnimation(animation);
		}else{
			view.setAnimation(null);
		}
	}

    /**
     * 隐藏视图
     * @param view
     * @param isStartAnim
     */
	public static void hideView(final View view,boolean isStartAnim){
		if(view == null){
			return;
		}
		if(isStartAnim){
            Animation animation = SimpleAnimations.newAlphaAnimation(false);
			animation.setAnimationListener(new SimpleAnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
                    if(view == null){
                        view.setAnimation(null);
                        view.setVisibility(View.GONE);
                    }
				}
			});
			view.startAnimation(animation);
		}else{
            view.setAnimation(null);
            view.setVisibility(View.GONE);
		}
	}
}
