package com.android.volley_https_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import java.util.Random;

/**
 * 自定义点赞动画
 */
public class UserLikeAnimationView extends RelativeLayout {

    //存放心形图片的数组
    private Bitmap[] mDrawables;
    //设置不同的差值器，使动画看起来更具有随机性
    private Interpolator mLine = new LinearInterpolator();//线性
    private Interpolator mDec = new DecelerateInterpolator();//减速
    private Interpolator mAccAndDec = new AccelerateDecelerateInterpolator();//先加速再减速
    private DecelerateAccelerateInterpolator mDecAndAcc = new DecelerateAccelerateInterpolator();//先加速再减速

    //创建一个存放差速器的数组
    private Interpolator[] mInterpolator;
    //需要一个属性动画改变运动轨迹
    private ValueAnimator mValueAnimator;
    //图片真实的宽高
    private int mMPicWidth;
    private int mMPicHeight;
    //图片参数
    private LayoutParams mLayoutParams;
    //获取屏幕的宽高
    private int mHeight;
    private int mWidth;
    //上下文
    private Context mContext;
    //产生一个随机数
    Random mRandom = new Random();

    private static final int MSG_ADD_LIKE = 1;
    private static final int MSG_ADD_END = 100;
    private int count;
    private int repeatInterval = 1000;
    private int durationTime = 4000;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ADD_LIKE:
                    addLike();
                    count--;
                    if (count > 0) {
                        handler.sendEmptyMessageDelayed(MSG_ADD_LIKE, repeatInterval);
                    }
                    break;
                case MSG_ADD_END:
                    if (onAddMountAnimationListener != null) {
                        onAddMountAnimationListener.onAnimationEnd();
                    }
                    break;
            }
        }
    };

    private OnAddMountAnimationListener onAddMountAnimationListener;

    public interface OnAddMountAnimationListener {

        void onAnimationBegin(int count);

        void onAnimationProgress(int remainCount);

        void onAnimationEnd();

    }

    public void setOnAddMountAnimationListener(OnAddMountAnimationListener onAddMountAnimationListener) {
        this.onAddMountAnimationListener = onAddMountAnimationListener;
    }


    public UserLikeAnimationView(Context context) {
        this(context, null);
    }

    public UserLikeAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserLikeAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UserLikeAnimationView);
        int src1Id = a.getResourceId(R.styleable.UserLikeAnimationView_ulv_src1, 0);
        int src2Id = a.getResourceId(R.styleable.UserLikeAnimationView_ulv_src2, 0);
        int src3Id = a.getResourceId(R.styleable.UserLikeAnimationView_ulv_src3, 0);
        a.recycle();

        mContext = context;
        mDrawables = new Bitmap[3];
        //获取本地的图片资源
        Bitmap mRed = BitmapFactory.decodeResource(getResources(), src1Id);
        Bitmap mBlue = BitmapFactory.decodeResource(getResources(), src2Id);
        Bitmap mYellow = BitmapFactory.decodeResource(getResources(), src3Id);
        mDrawables[0] = mRed;
        mDrawables[1] = mBlue;
        mDrawables[2] = mYellow;
        //获取图片真实的宽高,用户后面的计算（这3张大小是一样的
        mMPicWidth = mBlue.getWidth();
        mMPicHeight = mBlue.getHeight();
        mLayoutParams = new LayoutParams(mMPicWidth, mMPicHeight);
        mLayoutParams.addRule(CENTER_HORIZONTAL, TRUE); //居中
        mLayoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);//父布局底部
        //将差速器添加到数组
        mInterpolator = new Interpolator[4];
        mInterpolator[0] = mLine;
        Interpolator acc = new AccelerateInterpolator();
        mInterpolator[1] = acc;
        mInterpolator[2] = mDec;
        mInterpolator[3] = mAccAndDec;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }
    float v ;

    /**
     * 该方法在外面用户自己调用开启
     */
    public void addHeart() {
//        v = mWidth / 2000f;
        v = 70;
        ImageView imageView = new ImageView(getContext());
        //随机选择一个图片
        imageView.setImageBitmap(mDrawables[mRandom.nextInt(3)]);
        imageView.setLayoutParams(mLayoutParams);
        addView(imageView);
        //获取随机动画
        testAdd(imageView);
//        Animator mAnimator = getTestAnimator(imageView);
//        mAnimator.addListener(new AnimatorEndListener(imageView));
//        //用户调用才开始执行动画
//        mAnimator.start();
    }


    long lastAddTime = 0;
    private void testAdd(final View target) {
        //t为三秒，代表总时间，总时间没必要改变
        final int duration = 2000;
        //3)ValueAnimator的作用是用来计算属性的，同时监听动画的执行过程，让我们自己来实现具体功能。
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(duration);//时间

        //1）给估值器设置一个默认的中心点对象，当估值器运行的时候，它会把每一个坐标的点(x,y)赋值到PointF上。
        valueAnimator.setObjectValues(new PointF(mWidth / 2, mHeight));//设置中心点
        //2）估值器返回的是当前的某个点,某个点的坐标,添加估值器就是为了计算每一个点的。
        //点的坐标要自己计算。自己计算抛物线的轨迹，自己计算每个点的(x,)坐标。
        //3)估值器：用来计算我们的view在屏幕中显示的位置(运动的轨迹：平移、缩放、抛物线等等)。
        long currentTime = System.currentTimeMillis();
        double random = Math.random();
        int direction = 1;
        if(random > 0.5){
            direction = 1;
        } else {
            direction = -1;
        }
        valueAnimator.setEvaluator(new ParabolaPointEvaluator(mWidth,mHeight,direction,currentTime - lastAddTime));
        lastAddTime = currentTime ;
        //执行动画效果
        valueAnimator.start();
        //使用估值器计算完抛物线轨迹，计算出每个点的轨迹后，添加位置的监听
        //让视图走起来,更新每个点，更新View的坐标
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                target.setX(pointF.x);
                target.setY(pointF.y);

            }

        });
        valueAnimator.setInterpolator(mDec);
        valueAnimator.addListener(new AnimatorEndListener(target));

    }


    /**
     * 该方法在外面用户自己调用开启
     * 如果n＜100，1s释放一个赞，一个赞出现到消失的时间是释放上升的时间平均是4s
     * b)	如果100≤n＜600，那么1/2s释放1个赞，一个赞从出现到消失的时间平均是3s，这样保证即使此时及以后没有任何人再点赞，那么这些赞也可以平均释放将近3min。
     * c)	如果n≥600，那么1/3s释放一个赞，一个赞从出现到消失的时间平均是2.5s。
     */
    public void addHeart(int count) {
        this.count = count;
        if (count > 0 && count < 100) {
            repeatInterval = 1000;
            durationTime = 4000;
        } else if (count < 600) {
            repeatInterval = 500;
            durationTime = 3000;
        } else {
            repeatInterval = 333;
            durationTime = 2500;
        }
        handler.sendEmptyMessageDelayed(MSG_ADD_LIKE, repeatInterval);
        if (onAddMountAnimationListener != null) {
            onAddMountAnimationListener.onAnimationBegin(count);
        }

    }

    private void addLike() {
        ImageView imageView = new ImageView(getContext());
        //随机选择一个图片
        imageView.setImageBitmap(mDrawables[mRandom.nextInt(3)]);
        imageView.setLayoutParams(mLayoutParams);
        addView(imageView);
        //获取随机动画
        Animator mAnimator = getDefaultAnimator(imageView);
        mAnimator.addListener(new AnimatorEndListener(imageView));
        //用户调用才开始执行动画
        mAnimator.start();
    }


    /**
     * 给图片设置动画
     *
     * @param imageView 需要做动画的控件，如图片
     * @return 返回一个动画的集合
     */
    private Animator getDefaultAnimator(View imageView) {
        //设置第一部分开始动画
        //设置移动动画
        ObjectAnimator rotaion = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0, -30, 0, 30, 0);//Y轴缩放动画;
        rotaion.setRepeatMode(ValueAnimator.REVERSE);
        ValueAnimator mBezierMovingValueAnimator = getBezierMovingValueAnimator(imageView);
        AnimatorSet mFinallyAnimSet = new AnimatorSet();
        mFinallyAnimSet.playTogether(mBezierMovingValueAnimator, rotaion);
        mFinallyAnimSet.setInterpolator(mInterpolator[0]);//选择一个允许差速器
        return mFinallyAnimSet;
    }


    /**
     * 给图片设置动画
     *
     * @param imageView 需要做动画的控件，如图片
     * @return 返回一个动画的集合
     */
    private Animator getAnimator(View imageView) {
        //设置第一部分开始动画
        AnimatorSet mAnimatorSet = getStartAnimator(imageView);
        //设置移动动画
        ValueAnimator mBezierMovingValueAnimator = getBezierMovingValueAnimator(imageView);

        AnimatorSet mFinallyAnimSet = new AnimatorSet();
        //顺序播放动画
        mFinallyAnimSet.play(mBezierMovingValueAnimator);
        mFinallyAnimSet.setInterpolator(mInterpolator[0]);//随机选择一个差速器
        mAnimatorSet.setTarget(imageView);

        return mFinallyAnimSet;
    }

    /**
     * 第一部分：设置控件开始显示的动画
     */
    private AnimatorSet getStartAnimator(View target) {
        ObjectAnimator mAlphaAnim = ObjectAnimator.ofFloat(target, View.ALPHA, 0.2f, 1f);//透明度
        ObjectAnimator mScaleXAnim = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.2f, 1f);//X轴缩放
        ObjectAnimator mScaleYAnim = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.2f, 1f);//Y轴缩放动画;
        AnimatorSet mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(500);
        mAnimatorSet.setInterpolator(mLine);
        mAnimatorSet.playTogether(mAlphaAnim, mScaleXAnim, mScaleYAnim);
        mAnimatorSet.setTarget(target);
        return mAnimatorSet;
    }

    /**
     * 第二部分：设置控件(图片)移动过程的轨迹动画(三阶贝塞尔曲线)
     *
     * @param target 目标控件
     */
    private ValueAnimator getBezierMovingValueAnimator(View target) {
        PicPointEvaluator picPointEvaluator = new PicPointEvaluator(getPointF(2), getPointF(1));
        mValueAnimator = ValueAnimator.ofObject(picPointEvaluator, new PointF((mWidth - mMPicWidth) / 2, mHeight - mMPicHeight), new PointF(mRandom.nextInt(mWidth), 0));
        mValueAnimator.setDuration(3000);
        mValueAnimator.setTarget(target);
        mValueAnimator.addUpdateListener(new LikeAnimatorUpdateListener(target));
        return mValueAnimator;
    }

    /**
     * 计算出三阶贝塞尔曲线所需要的两个控制点
     *
     * @param i 通过 i 来判断是那个点，因为第二个点在上面Y坐标需要相应的改变
     * @return 返回控制点
     */
    private PointF getPointF(int i) {
        //控制点的X范围为0到屏幕的宽度减去100
        int mFlagX = mRandom.nextInt(mWidth - 100);
        //控制点的Y坐标其实也并非要是屏幕高度的一半，自己看情况来做
        int mFlagY = mRandom.nextInt(mHeight - 100 / i);
        return new PointF(mFlagX, mFlagY);
    }

    /**
     * 实现动画运动过程中的监听
     */
    private class LikeAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        LikeAnimatorUpdateListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            PointF mPointF = (PointF) animation.getAnimatedValue();
            target.setX(mPointF.x);
            target.setY(mPointF.y);
            //在向上移动的过程中在做一个透明度减少的动画
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }

    /**
     * 动画执行过程中的监听
     */
    private class AnimatorEndListener extends AnimatorListenerAdapter {
        private View mView;

        AnimatorEndListener(View view) {
            mView = view;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为用户不停的点赞也就会不停的添加View，导致内存吃不消。
            //所以当一个动画中执行结束的时候，我们应该将View移除掉
            removeView(mView);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            super.onAnimationCancel(animation);
            //取消的时候也应该将控件移除 ，可以在这个位置增加一个—+1的动画
            removeView(mView);
        }
    }
}
