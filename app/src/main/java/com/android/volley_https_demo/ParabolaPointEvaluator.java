package com.android.volley_https_demo;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
import android.util.Log;

/**
 * 需要根据具体的宽高度进行调整 参数
 */
public class ParabolaPointEvaluator implements TypeEvaluator<PointF> {
    private int mWidth;
    private int mHeight;
    private int direction = 1;
    private int v ;
    private int heightSqrt ;
    /**
     * 规则，越短的间隔时间 高度越高，速度越快，
     */
    private long intervalTime ;

    /**
     *  基准数100ms 系数1
     *  100ms-700ms 系数0.8-1
     *  大于700ms 系数1,
     */
    private float ratio = 1.0f;

    /**
     * 通过构造传入两个控制点
     */
    public ParabolaPointEvaluator(int mWidth, int height, int direction, long intervalTime) {
        this.mWidth = mWidth;
        this.mHeight = height;
        this.direction = direction;
        this.intervalTime = intervalTime;
        ratio = culRatio();
        v = (int)(mWidth / 4.0f / ratio);
        heightSqrt = (int)(Math.sqrt(this.mHeight) * ratio);
        Log.e("my","ratio:" + ratio + "  v:" + v + "  heightSqrt:" + heightSqrt);
    }

    /**
     *  基准数100ms 系数1
     *  100ms-700ms 系数0.5-1
     *  大于700ms 系数1,
     * @return
     */
    private float culRatio(){
        float ratio = 1.0f;
        if(intervalTime < 100){
            ratio = 1;
        } else if(intervalTime < 700){
            ratio = 0.5f + (700 - intervalTime) / 600 * 0.5f;
        } else {
            ratio = 1.0f;
        }
        return ratio;
    }

    /**
     * 利用 CalculateBezierPointForCubic 算法计算出三阶贝塞尔曲线上任意点
     */
    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        //4.1)计算x
        //1>创建一个点，抛物线是由无数个点组成的，需要计算每个点的坐标
        PointF pointF = new PointF();
        pointF.x = direction * v  * fraction * ratio  + mWidth / 2 ; //计算当前执行的距离
        //4.2)计算Y
        //y=(ax-b)(ax-b), b为Y轴上的顶点高度，a控制抛物线的曲率
//        pointF.y = ((v  * fraction * 20f)/5 -  38) * ((v  * fraction * 20f)/5  - 38) * ratio;
//        pointF.y = mHeight - 100 - 10 * fraction * 80;
        pointF.y = ((60f * fraction ) -  38) * ((fraction * 60f)  - 38);
        Log.e("my","fraction:" + fraction + " pointF.x:" + pointF.x + "  Math.sqrt(mHeight)：" + heightSqrt + "  pointFy：" + pointF.y);
        return pointF;
    }
}
