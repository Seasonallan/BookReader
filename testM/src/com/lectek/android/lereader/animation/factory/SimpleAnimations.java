package com.lectek.android.lereader.animation.factory;

import android.view.animation.Animation;

/**
 * 简易动画
 * Created by Administrator on 14-7-2.
 */
public class SimpleAnimations {

    /**
     * 简易位移动画
     * @param positionType 类型，上下左右
     *                     @see IAnimationType
     * @param isShow 动画为出现true或消失false
     * @return Animation
     */
    public static Animation newTranslateAnimation(int positionType, boolean isShow){
        switch (positionType){
            case IAnimationType.LEFT:
                return Animations.newTranslateAnimation(isShow? -1f : 0f,
                        isShow? 0f : -1f, 0f, 0f);
            case IAnimationType.RIGHT:
                return Animations.newTranslateAnimation(isShow? 1f : 0f,
                        isShow? 0f : 1f, 0f, 0f);
            case IAnimationType.TOP:
                return Animations.newTranslateAnimation(0f, 0f, isShow? -1f : 0f,
                        isShow? 0f : -1f);
            case IAnimationType.BOTTOM:
                return Animations.newTranslateAnimation(0f, 0f, isShow? 1f : 0f,
                        isShow? 0f : 1f);
        }
        return Animations.newTranslateAnimation(0f, 0f, 0f, 0f);
    }

    /**
     * 简易淡入淡出动画
     * @param isShow 动画为出现true或消失false
     * @return
     */
    public static Animation newAlphaAnimation(boolean isShow){
        return Animations.newAlphaAnimation(isShow? 0f : 1f,  isShow? 1f : 0f);
    }


    /**
     * 简易放大缩小动画【围绕中心点放大缩小】
     * @param isShow 动画为出现true或消失false
     * @return
     */
    public static Animation newScaleAnimation(boolean isShow){
        return Animations.newScaleAnimation(isShow ? 0f : 1f, isShow ? 1f : 0f,
                isShow ? 0f : 1f, isShow ? 1f : 0f, 0.5f, 0.5f);
    }

    /**
     * 简易旋转动画【绕中心点90度】
     * @param isShow 动画为出现true或消失false
     * @return
     */
    public static Animation newRotateAnimation(boolean isShow){
        return Animations.newRotateAnimation(isShow ? -90 : 0, isShow ? 0 : 90,
                0.5f, 0.5f);
    }


}
