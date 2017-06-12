package com.lectek.android.lereader.animation.factory;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * android:interpolator
 interpolator 被用来修饰动画效果，定义动画的变化率，可以使存在的动画效果可以 accelerated(加速)，decelerated(减速),repeated(重复),bounced(弹跳)等。
 * Created by Administrator on 14-7-2.
 */
public class Interpolators {
    /**
     * 以常量速率改变
     * @return
     */
    public static Interpolator newLinearInterpolator(){
         return new LinearInterpolator();
    }

    /**
     * 在动画开始的地方速率改变比较慢，然后开始加速
     * @return
     */
    public static Interpolator newAccelerateInterpolator(){
        return new AccelerateInterpolator();
    }

    /**
     * 在动画开始与结束的地方速率改变比较慢，在中间的时候加速减速
     * @return
     */
    public static Interpolator newAccelerateDecelerateInterpolator  (){
        return new AccelerateDecelerateInterpolator();
    }

    /**
     * 开始的时候向后然后向前甩
     * @return
     */
    public static Interpolator newAnticipateInterpolator(){
        return new AnticipateInterpolator();
    }

    /**
     * 开始的时候向后然后向前甩一定值后返回最后的值
     * @return
     */
    public static Interpolator newAnticipateOvershootInterpolator(){
        return new AnticipateOvershootInterpolator();
    }

    /**
     * 动画结束的时候弹起
     * @return
     */
    public static Interpolator newBounceInterpolator(){
        return new BounceInterpolator();
    }


    /**
     * 动画循环播放特定的次数，速率改变沿着正弦曲线
     * @return
     */
    public static Interpolator newCycleInterpolator(float cycles){
        return new CycleInterpolator(cycles);
    }

    /**
     * 在动画开始的地方快然后慢
     * @return
     */
    public static Interpolator DecelerateInterpolator(){
        return new DecelerateInterpolator();
    }

    /**
     *  向前甩一定值后再回到原来位置
     * @return
     */
    public static Interpolator newOvershootInterpolator(){
        return new OvershootInterpolator();
    }

}
