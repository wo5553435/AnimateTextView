package com.example.sinner.animatetextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import java.math.BigDecimal;

/**
 * Created by win7 on 2016-10-28.
 */

public class AnimateTextView extends View {

    //动画位置百分比进度
    private float mCurNumber;
    //是否计算（显示）时带上小数点
    private boolean isHasdot=false;
    //小数点后位数
    private int dotsize=0;
    //实际百分比进度
    private int mPercent;

    private float maxnumber=0;

    private float splitnumber=0;

    private float offset=0;//字体偏移量 if you dont want  to the number show in center

    private int DefaultColor=Color.WHITE;
    private int color=DefaultColor;
    private String str_num="";
    private int mCenterTextSize=14;
    private Context context;
    public AnimateTextView(Context context) {
        this(context,null);
    }

    public AnimateTextView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.context=context;
        init();
    }

    public AnimateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        this.context=context;init();

    }

    public AnimateTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init() {
        this.mCenterTextSize=Integer.parseInt(context.getString(R.string.animate_textsize));
        //  getWidth()/2- AndroidUtil.dip2px(getContext(),mCenterTextSize)*("3212".length()/2.0f);
        switch (mCenterTextSize){
            case 50:
                offset=-0.5f;
                break;
            case 100:
                offset=2.0f;
                break;
        }
    }

   /* public void setMaxNumber(int maxNumber){
        this.maxnumber=maxNumber;
        setPercent(maxNumber);
    }*/

    /**
     *设置最大数
     * @param maxnumber 最大数
     * @param isfloat 是否保留小数点
     * @param  dotmaxsize 小数点后保留几位
     */
    public void setMaxNumber(float maxnumber,boolean isfloat,int dotmaxsize){
        this.maxnumber=maxnumber;
        this.isHasdot=isfloat;
        this.dotsize=dotmaxsize;
        this.splitnumber=maxnumber/100;
        this.mPercent=(int)(maxnumber/100)+1;
        setPercent(maxnumber);

    }



    public void setPercent(float percent){
        setCurPercent(percent);
    }


    //外部设置百分比数
    public void setPercent(int percent) {
        if (percent > 100) {
            throw new IllegalArgumentException("percent must less than 100!");
        }

        setCurPercent(percent);
    }



    //内部设置百分比 用于动画效果
    private void setCurPercent(float percent) {
        if(percent<10){
            mCurNumber=maxnumber;
            postInvalidate();
            return;
        }
        //mPercent = (int)percent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int sleepTime = 1;
                for(int i =0;i<100;i++){
                    if(i%20 == 0){
                        sleepTime+=1;
                    }else if(i>75){//快接近时加速延迟更新时间  let the acceleration slow down
                        sleepTime+=2;
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mCurNumber = i*splitnumber;
                    if(i==99)
                        mCurNumber=maxnumber;
                    postInvalidate();
                }
            }

        }).start();

    }

    public void  SetText(String  string){
        str_num=string;
        postInvalidate();
    }

    //内部设置百分比 用于动画效果
    private void setCurPercent(int percent) {

        mPercent = percent;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int sleepTime = 1;
                for(int i =0;i<mPercent;i++){
                    if(i%20 == 0){
                        sleepTime+=2;
                    }else if(i>(mPercent-20)){//快接近时加速延迟更新时间  let the acceleration slow down
                        sleepTime+=3;
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mCurNumber = i*mPercent;
                    postInvalidate();
                }
            }

        }).start();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        // 设置自定义的控件MyViewGroup的大小
        setMeasuredDimension(measureWidth, measureHeight);
        //获取测量大小
        /*int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
        }

        if(widthMode == MeasureSpec.AT_MOST&&heightMode ==MeasureSpec.AT_MOST){

        }

        setMeasuredDimension(mWidth,mHeight);*/
    }

    public void setTextSize(int size){
        this.mCenterTextSize=size;
    }

    public void setTextColor(int color) {
        this.color=color;
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

    BigDecimal bd;

    @Override
    protected void onDraw(Canvas canvas) {



        //绘制文本
        Paint textPaint = new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        String text="";
        if(!isHasdot){
            text = ""+(int)mCurNumber;
        }else{
            bd=new BigDecimal(mCurNumber);
            text=""+bd.setScale(dotsize,BigDecimal.ROUND_HALF_UP);
        }
        textPaint.setTextSize(mCenterTextSize);

        float textLength = textPaint.measureText(text);

        textPaint.setColor(color);
//        if(text=="0"){
//
//        }
//        else



        if("".equals(str_num)){
            //字体大小偏移量

            canvas.drawText(text,getWidth()/2/*- AndroidUtil.px2dip(getContext(),mCenterTextSize)*(text.length()/2.0f+offset)*/, 3*getHeight()/4, textPaint);
        }else{
            canvas.drawText(str_num,getWidth()/2/*- AndroidUtil.px2dip(getContext(),mCenterTextSize)*text.length()/2.0f*/, 3*getHeight()/4, textPaint);
        }


    }

}
