package com.example.xxl.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xxl on 17-7-4.
 * 自定义ViewGroup,用来显示四个View,分别显示在左上，右上，左下和右下角
 */

public class CustomView extends ViewGroup {

    public CustomView(Context context) {
        this(context,null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写父类该方法，返回margin的实例，这样就为ViewGroup指定了layoutParamesMargin为marginLayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

    /**
     * 计算所有childView的宽和高，然后根据childView的计算结果，设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取此ViewGroup为其推荐的宽和高，以及计算模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heithtSize = MeasureSpec.getSize(heightMeasureSpec);

        Log.e("aaa","widthSize=="+widthSize+"heithtSize=="+heithtSize);
        // 计算所有childView的宽和高，非wrap_content情况
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        //记录如果wrap_content时的宽和高
        int width = 0;
        int height = 0;

        //上面childView的宽和下面childView的宽，取二者最大值
        int tWidth = 0;
        int bWidth = 0;

        //左边childView和右面childView的高，取二者之间的最大值
        int lHeight = 0;
        int rHeight = 0;

        int cCount = getChildCount();//获取childView数量

        int cWidth = 0;
        int cHeight = 0;

        MarginLayoutParams lp =null;

        for(int i = 0; i < cCount; i++) {//遍历所有子View
            View childView = getChildAt(i);//根据索引值获取ViewGroup里面的childView
            cWidth = getMeasuredWidth();
            cHeight = getMeasuredHeight();
            lp = (MarginLayoutParams) childView.getLayoutParams();
            if (i == 0 || i == 1) {
                tWidth += childView.getWidth() + lp.leftMargin +lp.rightMargin;
            }
            if (i == 2 || i == 3) {
                bWidth += childView.getWidth() +lp.leftMargin + lp.rightMargin;
            }
            if (i == 0 || i == 2) {
                lHeight += childView.getHeight() + lp.topMargin +lp.bottomMargin;
            }
            if (i == 1 || i == 3) {
                rHeight += childView.getHeight() + lp.topMargin +lp.bottomMargin;
            }

           width = Math.max(tWidth,bWidth);
           height = Math.max(lHeight,rHeight);

            Log.e("aaa",width+"");
            Log.e("aaa",height+"");
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ?widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heithtSize : height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        int cWidth = 0;
        int cHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();

            int cl = 0, ct = 0,cr = 0,cb = 0;
            switch (i) {
                case 0 :
                    cl = lp.leftMargin;
                    ct = lp.topMargin;
                    break;
                case 1 :
                    cl = getWidth() - cWidth - lp.rightMargin;
                    ct = lp.topMargin;
                    break;
                case 2 :
                    cl = lp.leftMargin;
                    ct = getHeight() - cHeight - lp.bottomMargin;
                    break;
                case 3 :
                    cl = getWidth() - cWidth - lp.rightMargin;
                    ct = getHeight() - cHeight - lp.bottomMargin;
                    break;
            }
            cr = cl + cWidth;
            cb = ct + cHeight;
            childView.layout(cl,ct,cr,cb);
        }
    }
}
