package com.lectek.android.lereader.animation.factory;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.lectek.android.app.BaseApplication;

/**
 * 四大动画
 * Created by Administrator on 14-7-2.
 */
public class Animations {

    private static Context getContext(){
        return BaseApplication.getInstance();
    }

    private static int getDuration(){
        return getContext().getResources().getInteger(android.R.integer.config_longAnimTime);
       // return 300;
    }

    /**
     * 通过anim资源ID获取动画
     * @param id
     * @return
     */
    public static Animation newAnimationFromXml(int id){
        return AnimationUtils.loadAnimation(getContext(), id);
    }

    /**
     * 创建一个位移动画[相对自身][时长400]
     */
    public static Animation newTranslateAnimation(float fromXValue, float toXValue, float fromYValue, float toYValue){
        return newTranslateAnimation(getDuration(), fromXValue, toXValue, fromYValue, toYValue);
    }

    /**
     * 创建一个位移动画[相对自身]
     */
    public static Animation newTranslateAnimation(int duration, float fromXValue, float toXValue, float fromYValue, float toYValue){
        return newTranslateAnimation(Animation.RELATIVE_TO_SELF, duration, fromXValue, toXValue, fromYValue, toYValue);
    }

    /**
     * 创建一个位移动画
     * @param animationType 相对位置类型，   如Animation.RELATIVE_TO_SELF
     *                      @see Animation.RELATIVE_TO_SELF
     * @param duration 动画时长
     * @param fromXValue 起始位置X
     * @param toXValue 终点位置X
     * @param fromYValue 起始位置Y
     * @param toYValue 终点位置Y
     * @return animation
     */
    public static Animation newTranslateAnimation(int animationType, int duration,  float fromXValue, float toXValue, float fromYValue, float toYValue){
        TranslateAnimation translateAnimation = new TranslateAnimation(animationType, fromXValue,animationType, toXValue,
                animationType, fromYValue,animationType, toYValue);
        translateAnimation.setDuration(duration);
        return translateAnimation;
    }
    /**
     * 创建一个透明动画[时长400]
     */
    public static Animation newAlphaAnimation(float fromAlpha, float toAlpha){
        return newAlphaAnimation(getDuration(), fromAlpha, toAlpha);
    }

    /**
     * 创建一个透明动画
     * @param duration 时长
     * @param fromAlpha 起始透明度
     * @param toAlpha 终点透明度
     * @return animation
     */
    public static Animation newAlphaAnimation(int duration, float fromAlpha, float toAlpha){
        AlphaAnimation translateAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        translateAnimation.setDuration(duration);
        return translateAnimation;
    }


    /**
     * 创建一个大小缩放动画[时长400]
     */
    public static Animation newScaleAnimation(float fromXValue, float toXValue, float fromYValue, float toYValue,
                                              float pivotXValue, float pivotYValue){
        return newScaleAnimation(getDuration(), fromXValue, toXValue, fromYValue, toYValue,
                pivotXValue,  pivotYValue);
    }

    /**
     * 创建一个大小缩放动画[相对自身][时长400]
     */
    public static Animation newScaleAnimation(int duration, float fromXValue, float toXValue, float fromYValue, float toYValue,
                                              float pivotXValue, float pivotYValue){
        return newScaleAnimation(duration, fromXValue, toXValue, fromYValue, toYValue, Animation.RELATIVE_TO_SELF,
                pivotXValue, Animation.RELATIVE_TO_SELF, pivotYValue);
    }

    /**
     * 创建一个大小缩放动画
     * @param duration 时长
     * @param fromXValue 起始X
     * @param toXValue 终点X
     * @param fromYValue 起始Y
     * @param toYValue 终点Y
     * @param pivotType  相对位置类型，   如Animation.RELATIVE_TO_SELF
     *                      @see Animation.RELATIVE_TO_SELF
     * @param pivotXValue 中心点X
     * @param pivotYType 相对位置类型，   如Animation.RELATIVE_TO_SELF
     *                      @see Animation.RELATIVE_TO_SELF
     * @param pivotYValue 中心点Y
     * @return animation
     */
    public static Animation newScaleAnimation(int duration,
                                              float fromXValue, float toXValue, float fromYValue, float toYValue,
                                              int pivotType, float pivotXValue, int pivotYType, float pivotYValue){
        ScaleAnimation scaleAnimation = new ScaleAnimation(fromXValue,toXValue,fromYValue,toYValue,
                pivotType, pivotXValue, pivotYType, pivotYValue);

        scaleAnimation.setDuration(duration);
        return scaleAnimation;
    }

    /**
     * 创建一个大小缩放动画[相对自身][时长400]
     */
    public static Animation newRotateAnimation(float fromDegrees, float toDegrees,
                                               float pivotXValue, float pivotYValue){
        return newRotateAnimation(getDuration(), fromDegrees, toDegrees, pivotXValue, pivotYValue);
    }


    /**
     * 创建一个大小缩放动画[相对自身]
     */
    public static Animation newRotateAnimation(int duration, float fromDegrees, float toDegrees,
                                                float pivotXValue, float pivotYValue){
        return newRotateAnimation(duration, fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF,
                pivotXValue, Animation.RELATIVE_TO_SELF, pivotYValue);
    }

    /**
     * 创建一个旋转动画
     * @param duration 时长
     * @param fromDegrees 起始角度
     * @param toDegrees 终点角度
     * @param pivotXType  相对位置类型，   如Animation.RELATIVE_TO_SELF
     *                      @see Animation.RELATIVE_TO_SELF
     * @param pivotXValue 中心点位置X
     * @param pivotYType 相对位置类型，   如Animation.RELATIVE_TO_SELF
     *                      @see Animation.RELATIVE_TO_SELF
     * @param pivotYValue 中心点位置Y
     * @return animation
     */
    public static Animation newRotateAnimation(int duration, float fromDegrees, float toDegrees,
                                               int pivotXType, float pivotXValue, int pivotYType, float pivotYValue){
        RotateAnimation scaleAnimation = new RotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue);
        scaleAnimation.setDuration(duration);
        return scaleAnimation;
    }
}
