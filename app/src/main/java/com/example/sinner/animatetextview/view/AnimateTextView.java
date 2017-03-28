package com.example.sinner.animatetextview.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


import com.example.sinner.animatetextview.R;

import java.util.ArrayList;
import java.util.List;

/**This is a custom's textview which more powerful than last version,
 *
 * three types of showanimation: 1animate 2downcount 3cashout
 * 1 it comes true effect base on valueanimator's  updatelistener you can replace interpolator with yours
 * 2 just like a countdown; the value of duration is floating time
 * 3 cashout !  let's play slot machine!
 *
 *  这是一个增强版动态显示数字的自定义textview，
 * 分为3中模式 1：动态递减↗ → ↘增长 2.倒计时 固定间隔 3.水果机式逐位暂停
 * Created by win7 on 2017-03-25.
 */
public class AnimateTextView extends View {

    private int mTextSize = 16;
    private int mTextColor = 0;
    private int mStartCount = 0;
    private int mEndCount = 0;
    private Context mContext;
    private String str_num = "";
    private int duration = 1000;
    private final int MODE_DOWNCOUNT = 1, MODE_CASHOUT = 2, MODE_ANIMATE = 3;
    private int mCurrentMode = 3;
    private int mCurrentValue = -1;
    private StringBuffer stringBuffer, nb;
    private String textleft;
    private String textright;
    private List<Integer> nums;
    ValueAnimator va;
    OnResultListener callback;
    int lenght;

    public AnimateTextView(Context context) {
        super(context);
        this.mContext = context;
        init(null);
    }

    public AnimateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(attrs);
    }

    public AnimateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs);
    }


    public void SetResultListener(OnResultListener callback) {
        this.callback = callback;
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.AnimateTextView);
            mTextSize = a.getInteger(R.styleable.AnimateTextView_textSize, 16);
            mTextColor = a.getColor(R.styleable.AnimateTextView_textColor, getResources().getColor(android.R.color.black));
            mStartCount = a.getInteger(R.styleable.AnimateTextView_start, 0);
            mEndCount = a.getInteger(R.styleable.AnimateTextView_end, 0);
            duration = a.getInteger(R.styleable.AnimateTextView_duration, 1000);
            mCurrentMode = a.getInt(R.styleable.AnimateTextView_mode, 3);
            textleft = a.getString(R.styleable.AnimateTextView_textonleft);
            textright = a.getString(R.styleable.AnimateTextView_textonright);
            stringBuffer = new StringBuffer();
            nb = new StringBuffer();
            a.recycle();
        }
        //这个位置有点尴尬,直接自定义出来的好像这段代码提前判断了
        if (mEndCount <= mStartCount) {
            throw new RuntimeException("请确保start和end的值在正常可用范围内");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取测量模式
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        // 设置自定义的控件MyViewGroup的大小
        setMeasuredDimension(measureWidth, measureHeight);
    }


    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
            /**
             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
             * MeasureSpec.AT_MOST。
             *
             *
             * MeasureSpec.EXACTLY是精确尺寸，
             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
             *
             *
             * MeasureSpec.AT_MOST是最大尺寸，
             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
             *
             *
             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
             * 通过measure方法传入的模式。
             */
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = widthSize;
                break;
        }
        return result;
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;

        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }

    public void CancelCount() {
        if (va != null && va.isRunning()) va.cancel();
    }

    public void Stop() {
        if (va != null && va.isRunning()) va.end();
    }

    public void Resume() {
        if (va != null && !va.isRunning()) StartCount();
    }

    public void StartCount() {
        if (va != null && va.isRunning()) va.end();
        switch (mCurrentMode) {
            case MODE_ANIMATE://单纯动画
                va = ValueAnimator.ofInt(mStartCount, mEndCount);
                va.setDuration(duration);
                va.setInterpolator(new AccelerateDecelerateInterpolator());
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        str_num = String.valueOf((int) valueAnimator.getAnimatedValue());
                        postInvalidate();
                    }
                });
                va.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        if (callback != null) callback.OnStart();
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (callback != null) callback.OnEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                break;
            case MODE_DOWNCOUNT://倒计时模式
                va = ValueAnimator.ofInt(mStartCount, mEndCount);
                va.setRepeatMode(ValueAnimator.RESTART);
                va.setRepeatCount((mCurrentValue == -1 ? mEndCount : mCurrentValue) - mStartCount);
                str_num = "" + mEndCount;
                va.setDuration(duration);
                va.setInterpolator(new LinearInterpolator());
                va.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        Log.e("onAnimationStart", "--");
                        if (mCurrentValue == -1)
                            mCurrentValue = mEndCount;
                        if (callback != null) callback.OnStart();
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        Log.e("onAnimationEnd", "---");
                        postInvalidate();
                        if (callback != null) callback.OnEnd();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        Log.e("onAnimationCancel", "--");
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        Log.e("onAnimationRepeat", "--");
                        mCurrentValue--;
                        str_num = "" + mCurrentValue;
                        postInvalidate();
                    }
                });
                break;
            case MODE_CASHOUT://水果机效果
                str_num = String.valueOf(mEndCount);
                lenght = str_num.length();
                SetValue(mEndCount);
                va = ValueAnimator.ofInt(1, 100 * (lenght + 1));
                va.setInterpolator(new LinearInterpolator());
                va.setDuration(duration);
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        CurrentCount((int) valueAnimator.getAnimatedValue());
                    }
                });
                break;
        }

        va.start();
    }

    Interpolator in = new DecelerateInterpolator(1);

    /**
     * 计算当前值
     *
     * @param animatedValue
     */
    private void CurrentCount(int animatedValue) {
        int index = animatedValue / 100;
        nb.setLength(0);
        if (index < 1) {//全员加速状态；
            int part=100/nums.size();

            for (int i = 0; i < ((animatedValue/part)+1); i++) {//分批加入每一位数字
                nb.append("" + (int)( 10f*Math.random()));
            }
        } else {//
            for (int i = 0; i < nums.size(); i++) {
                if (i < index - 1) {//小于此位的数字不变
                    nb.append("" + nums.get(i));
                } else if (index - 1 == i) {//这位数字开始缓
                    int count = animatedValue % 100;
                    int x = (int) (Math.abs(in.getInterpolation(0.01f * count)) * 10);
                    //  Logs.e("--count:"+count,"--x"+x);
                    nb.append((nums.get(i) + x) % 10);
                } else {//高位继续动画
                    nb.append("" + ((nums.get(i) + animatedValue % 10) % 10));
                }
            }
        }
        postInvalidate();
    }

    /**
     * 设置当前结束位置的所有值
     */
    private void SetValue(int Count) {
        nums = new ArrayList<>();

        String count = String.valueOf(Count);
        for (int i = 0; i < count.length(); i++) {
            nums.add(Integer.parseInt("" + count.charAt(i)));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制文本
        Paint textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        String text = "0";
        textPaint.setTextSize(mTextSize);
        float textLength = textPaint.measureText(text);
        textPaint.setColor(mTextColor);

        switch (mCurrentMode) {
            case MODE_ANIMATE:
            case MODE_DOWNCOUNT:


                if ("".equals(str_num)) {
                    canvas.drawText(text, getWidth() / 2/*- AndroidUtil.px2dip(getContext(),mCenterTextSize)*(text.length()/2.0f+offset)*/, getHeight() / 2, textPaint);
                } else {
                    if ((textleft == null && textright == null)) {

                        canvas.drawText(str_num, getWidth() / 2/*- AndroidUtil.px2dip(getContext(),mCenterTextSize)*text.length()/2.0f*/, getHeight() / 2, textPaint);
                    } else {
                        stringBuffer.setLength(0);
                        if (textleft != null) stringBuffer.append(textleft);
                        stringBuffer.append(str_num);
                        if (textright != null) stringBuffer.append(textright);
                        canvas.drawText(stringBuffer.toString(), getWidth() / 2/*- AndroidUtil.px2dip(getContext(),mCenterTextSize)*text.length()/2.0f*/, getHeight() / 2, textPaint);
                    }
                }
                break;
            case MODE_CASHOUT:
                canvas.drawText(nb.toString(), getWidth() / 2/*- AndroidUtil.px2dip(getContext(),mCenterTextSize)*text.length()/2.0f*/, getHeight() / 2, textPaint);
                break;
        }
    }


    public interface OnResultListener {
        void OnStart();

        void OnEnd();
    }

    public interface OnTimeListener extends OnResultListener {
        void onRepeat();
    }
}
